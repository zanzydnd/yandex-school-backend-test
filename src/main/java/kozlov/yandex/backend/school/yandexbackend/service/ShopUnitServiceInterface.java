package kozlov.yandex.backend.school.yandexbackend.service;

import kozlov.yandex.backend.school.yandexbackend.dto.ImportShopUnitDto;
import kozlov.yandex.backend.school.yandexbackend.dto.ResponseNodeShopUnitDto;
import kozlov.yandex.backend.school.yandexbackend.dto.ReturnSalesDto;
import kozlov.yandex.backend.school.yandexbackend.dto.ShopUnitDto;
import kozlov.yandex.backend.school.yandexbackend.model.ShopUnitModel;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ShopUnitServiceInterface {

    Boolean importShopUnit(ImportShopUnitDto importShopUnitDto);

    Boolean deleteShopUnit(UUID id);

    ResponseNodeShopUnitDto getShopUnitModelWithChildren(UUID id) throws IllegalAccessException, InstantiationException;

    ReturnSalesDto getSales(LocalDateTime date);

    ReturnSalesDto getStatistics(UUID id, LocalDateTime dateStart, LocalDateTime dateEnd);
}
