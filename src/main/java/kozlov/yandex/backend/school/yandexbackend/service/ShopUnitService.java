package kozlov.yandex.backend.school.yandexbackend.service;

import kozlov.yandex.backend.school.yandexbackend.dto.*;
import kozlov.yandex.backend.school.yandexbackend.enums.ShopUnitType;
import kozlov.yandex.backend.school.yandexbackend.exception.BusinessLogicException;
import kozlov.yandex.backend.school.yandexbackend.exception.NotFoundException;
import kozlov.yandex.backend.school.yandexbackend.jpa.repository.ShopUnitHistoryRepositoryInterface;
import kozlov.yandex.backend.school.yandexbackend.jpa.repository.ShopUnitRepositoryInterface;
import kozlov.yandex.backend.school.yandexbackend.model.ShopUnitModel;
import kozlov.yandex.backend.school.yandexbackend.utils.BeanUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;
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

    @Override
    //родителем товара или категории может быть только категория
    // тут можно будет собрать все parentId и общим запросом посмотреть по длине результата есть ли среди parentId товар , а не категория.
    public Boolean importShopUnit(ImportShopUnitDto importShopUnitDto) {

        var map = importShopUnitDto.getItems()
                .stream()
                .map(dto -> mapToShopUnitModel(dto, importShopUnitDto.getUpdateDate()))
                .collect(Collectors.toMap(ShopUnitModel::getId, Function.identity()));

        var result = new ArrayList<ShopUnitModel>();

        importShopUnitDto.getItems()
                .forEach(dto -> {
                    var model = map.get(dto.getId());
                    if(ShopUnitType.OFFER.equals(dto.getType())){
                        if(dto.getPrice() == null || dto.getPrice() < 0) throw new BusinessLogicException("Can't set price for category");
                    }
                    else {
                        if(dto.getPrice() != null) throw  new BusinessLogicException("Can't set price for category");
                    }

                    if (Objects.nonNull(dto.getParentId())) {
                        var parent = map.get(dto.getParentId());
                        if (Objects.nonNull(parent)) {
                            if (ShopUnitType.OFFER.equals(parent.getType())) {
                                throw new BusinessLogicException("some text");
                            }
                            model.setParent(parent);
                            if (!result.contains(parent)) {
                                result.add(parent);
                            }
                        } else {
                            model.setParent(ShopUnitModel.builder()
                                    .id(dto.getParentId())
                                    .build());
                        }
                    }

                    if (!result.contains(model)) {
                        result.add(model);
                    }
                });


        shopUnitRepository.migrateToHistory(new ArrayList<>(map.keySet()));
        shopUnitRepository.saveAllAndFlush(result);
        var parentsUUIDS = shopUnitRepository.getAllParents(new ArrayList<>(map.keySet()));

        shopUnitRepository.migrateToHistory(parentsUUIDS);

        shopUnitRepository.updateDateByIds(parentsUUIDS, importShopUnitDto.getUpdateDate());

        return true;
    }

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
                result.setPrice(dto.getPrice().longValue());
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
                current_dto.setPrice(dto.getPrice().longValue());
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

    @Override
    public ReturnSalesDto getStatistics(UUID id, Date dateStart, Date dateEnd) {
        return null;
    }
}
