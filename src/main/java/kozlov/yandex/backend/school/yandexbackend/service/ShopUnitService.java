package kozlov.yandex.backend.school.yandexbackend.service;

import kozlov.yandex.backend.school.yandexbackend.dto.*;
import kozlov.yandex.backend.school.yandexbackend.enums.ShopUnitType;
import kozlov.yandex.backend.school.yandexbackend.exception.NotFoundException;
import kozlov.yandex.backend.school.yandexbackend.jpa.repository.ShopUnitHistoryRepositoryInterface;
import kozlov.yandex.backend.school.yandexbackend.jpa.repository.ShopUnitRepositoryInterface;
import kozlov.yandex.backend.school.yandexbackend.model.ShopUnitModel;
import kozlov.yandex.backend.school.yandexbackend.utils.BeanUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class ShopUnitService implements ShopUnitServiceInterface {

    @Autowired
    private ShopUnitRepositoryInterface shopUnitRepository;

    @Autowired
    private ShopUnitHistoryRepositoryInterface shopUnitHistoryRepository;


    @Autowired
    private ModelMapper modelMapper;

    //данный метод расскладывает пришедшее тело в стек по иерархии , где последний положенный элемент окажется самым высоким в дереве
    public void putToStackOfModel(Map<UUID, ShopUnitModel> all, Map<UUID, List<ShopUnitModel>> childrenMap,
                                  ShopUnitModel current, Stack<List<ShopUnitModel>> stack, List<ShopUnitModel> list) {

        list.add(current);
        if (childrenMap.containsKey(current.getId())) {
            List<ShopUnitModel> forLowerLevel = new ArrayList<>();
            //проходимся по детям
            childrenMap.get(current.getId()).forEach(item -> {
                putToStackOfModel(all, childrenMap, item, stack, forLowerLevel);
            });
            childrenMap.remove(current.getId());
            stack.push(forLowerLevel);
        }
    }

    @Override
    public Boolean importShopUnit(ImportShopUnitDto importShopUnitDto) {
        Map<UUID, ShopUnitModel> rootLevelMap = new HashMap<>();
        Map<UUID, ShopUnitModel> all = new HashMap<>();
        Map<UUID, List<ShopUnitModel>> childrenMap = new HashMap<>();

        //Складываем все по map - чтобы потом пользоваться ими, мапим результаты
        importShopUnitDto.getItems().forEach(item -> {
            var model = mapToShopUnitModel(item, importShopUnitDto.getUpdateDate());
            rootLevelMap.put(item.getId(), model);
            all.put(item.getId(), model);
        });

        /*
           Здесь будет три мапы
           rootLevelMap - в ней останутся только мапы , у которых нет родителей
           childrenMap - здесь будем хранить детей
           all - все мапы, чтобы доставать модели
        */
        importShopUnitDto.getItems().forEach(item -> {
            if (item.getParentId() != null) {
                if (childrenMap.containsKey(item.getParentId())) {
                    childrenMap.get(item.getParentId()).add(all.get(item.getId()));
                } else {
                    childrenMap.put(item.getParentId(), new ArrayList<>(List.of(new ShopUnitModel[]{all.get(item.getId())})));
                }
                rootLevelMap.remove(item.getId());
            }
        });


        Stack<List<ShopUnitModel>> stack = new Stack<>();
        List<ShopUnitModel> rootLevel = new ArrayList<>();
        rootLevelMap.keySet().forEach(id -> {

            putToStackOfModel(all, childrenMap, all.get(id), stack, rootLevel);

            //добавляем наш рут уровень
            //System.out.println(rootLevel);
            stack.push(rootLevel);

        });

        //проверяем не осталось ли пустых родитиелей ( если пришли только их дети в теле ,
        // но самих родитиелей в теле запроса не было)
        // если есть, то кладем их в стек , так как их родители должны быть в базе
        List<ShopUnitModel> orphans = new ArrayList<>();

        childrenMap.keySet().forEach(parent -> {
            orphans.addAll(childrenMap.get(parent));
        });
        stack.push(orphans);

        shopUnitRepository.migrateToHistory(new ArrayList<>(all.keySet()));

        while (!stack.empty()) {
            // мапим каждую отдельно , потому что hibernate испытывает трудности с этим  - не смог исправить
            List<ShopUnitModel> shopUnitModels = stack.pop();
            for (ShopUnitModel model : shopUnitModels) {
                if (all.containsKey(model.getParentId())) {
                    model.setParent(all.get(model.getParentId()));
                } else if (model.getParentId() != null) {
                    model.setParent(
                            ShopUnitModel.builder().id(model.getParentId()).build()
                    );
                }
            }
            shopUnitRepository.saveAll(shopUnitModels);
        }

        //здесь мы получаем id сущностей , которые тоже нужно обновить , потому что они родители
        var parentsUUIDS = shopUnitRepository.getAllParents(new ArrayList<>(all.keySet()));


        shopUnitRepository.migrateToHistory(parentsUUIDS);

        shopUnitRepository.updateDateByIds(parentsUUIDS, importShopUnitDto.getUpdateDate());

        return true;
    }

    // данный метод мапит дто в сущность и проставляет дату
    private ShopUnitModel mapToShopUnitModel(ShopUnitDto shopUnitDto, LocalDateTime updateDate) {
        var model = modelMapper.map(shopUnitDto, ShopUnitModel.class);
        model.setDate(updateDate);
        return model;
    }


    @Override
    public Boolean deleteShopUnit(UUID id) {
        try {
            shopUnitRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Not found");
        }
        return true;
    }


    @Override
    public ResponseNodeShopUnitDto getShopUnitModelWithChildren(UUID id) throws IllegalAccessException, InstantiationException {
        var map = shopUnitRepository.getAllWithChildrenAndAveragePrice(id);
        var models = BeanUtils.toList(map, RecursiveShopUnitDto.class);

        return mapToOneObject(models);
    }

    //здесь происходит маппинг пришедшего ответа от рекурсивного запроса в одну дто , которая удволетваряет спецификации
    public ResponseNodeShopUnitDto mapToOneObject(List<RecursiveShopUnitDto> list) {
        Map<UUID, ResponseNodeShopUnitDto> map = new HashMap<>();
        ResponseNodeShopUnitDto result = new ResponseNodeShopUnitDto();
        if (list.size() == 0) {
            throw new NotFoundException("Not found");
        }
        list.forEach(dto -> {
            System.out.println(dto.getLevel());
            if (dto.getLevel().equals(0)) {
                result.setId(UUID.fromString(dto.getId()));
                result.setDate(dto.getDate().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDateTime());
                if (dto.getPrice() == null) {
                    result.setPrice(null);
                } else {
                    result.setPrice(dto.getPrice().longValue());
                }
                if (dto.getParent_id() == null) {
                    result.setParentId(null);
                } else {
                    result.setParentId(UUID.fromString(dto.getParent_id()));
                }

                result.setName(dto.getName());
                result.setType(ShopUnitType.valueOf(dto.getType()));
                if (ShopUnitType.CATEGORY.equals(result.getType())) {
                    result.setChildren(new ArrayList<>());
                }
                map.put(result.getId(), result);


            } else {
                var current_dto = new ResponseNodeShopUnitDto();
                current_dto.setId(UUID.fromString(dto.getId()));
                current_dto.setDate(dto.getDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime());
                if (dto.getPrice() == null) {
                    current_dto.setPrice(null);
                } else {
                    current_dto.setPrice(dto.getPrice().longValue());
                }
                if (dto.getParent_id() == null) {
                    current_dto.setParentId(null);
                } else {
                    current_dto.setParentId(UUID.fromString(dto.getParent_id()));
                }
                current_dto.setName(dto.getName());
                current_dto.setType(ShopUnitType.valueOf(dto.getType()));
                if (ShopUnitType.CATEGORY.equals(current_dto.getType())) {
                    current_dto.setChildren(new ArrayList<>());
                }
                map.put(current_dto.getId(), current_dto);
                if (dto.getParent_id() != null) {
                    var parent = map.get(UUID.fromString(dto.getParent_id()));
                    parent.getChildren().add(current_dto);
                }
                System.out.println(current_dto.getName());
                System.out.println(current_dto.getDate());
            }
        });
        return result;
    }


    // возвращает скидки(самый последний обновленный элемент)
    @Override
    public ReturnSalesDto getSales(LocalDateTime date) {
        var salesUnitsModel = shopUnitRepository.findAllUnitsByDateBetween(date.minusHours(24), date);
        List<UUID> notIds = new ArrayList<>();
        var salesUnitsDto = salesUnitsModel.stream().map(obj -> {
            notIds.add(obj.getId());
            return modelMapper.map(obj, ReturnItemDto.class);
        });
        var salesHistoryUnitsModel = shopUnitHistoryRepository.findAllUnitsHistoryByDateBetween(date.minusHours(24), date, notIds);
        var salesHistoryUnitsDto = salesHistoryUnitsModel.stream().map(
                obj -> {
                    System.out.println(obj);
                    return modelMapper.map(obj, ReturnItemDto.class);
                }
        );
        ReturnSalesDto returnSalesDto = new ReturnSalesDto();

        returnSalesDto.setItems(Stream.concat(salesUnitsDto, salesHistoryUnitsDto).collect(Collectors.toList()));
        return returnSalesDto;
    }

    // не смог придумать запрос
    @Override
    public ReturnSalesDto getStatistics(UUID id, LocalDateTime dateStart, LocalDateTime dateEnd) {
        // ReturnItemDto
        var inHistoryModels = shopUnitHistoryRepository.findAllUnitsByDateAndIdBetween(dateStart, dateEnd, id);
        var mainModel = shopUnitRepository.findAllUnitsByDateBetweenAndIdWithAvg(dateStart, dateEnd, id);
        var result = new ArrayList<ReturnItemDto>();

        result.add(modelMapper.map(mainModel, ReturnItemDto.class));
        result.addAll(inHistoryModels.stream().map(
                        mdl -> modelMapper.map(mdl, ReturnItemDto.class)
                ).collect(Collectors.toList())
        );
        return ReturnSalesDto.builder().items(result).build();
    }
}
