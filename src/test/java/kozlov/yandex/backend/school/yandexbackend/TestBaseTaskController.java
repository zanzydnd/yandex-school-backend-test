package kozlov.yandex.backend.school.yandexbackend;

import kozlov.yandex.backend.school.yandexbackend.dto.ImportShopUnitDto;
import kozlov.yandex.backend.school.yandexbackend.dto.ResponseNodeShopUnitDto;
import kozlov.yandex.backend.school.yandexbackend.dto.ShopUnitDto;
import kozlov.yandex.backend.school.yandexbackend.enums.ShopUnitType;
import kozlov.yandex.backend.school.yandexbackend.jpa.repository.ShopUnitRepositoryInterface;
import kozlov.yandex.backend.school.yandexbackend.model.ShopUnitModel;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestBaseTaskController extends AbstractTestIT {

    private final ShopUnitRepositoryInterface shopUnitRepositoryInterface;

    private final TestRestTemplate testRestTemplate;


    @Autowired
    public TestBaseTaskController(ShopUnitRepositoryInterface shopUnitRepositoryInterface, TestRestTemplate testRestTemplate) {
        this.shopUnitRepositoryInterface = shopUnitRepositoryInterface;
        this.testRestTemplate = testRestTemplate;
    }

    @Test
    public void testValidImport() {
        LocalDateTime updateDate = Instant.parse("2019-12-01T10:05:23.653Z").atZone(ZoneId.systemDefault()).toLocalDateTime();

        List<ShopUnitDto> items = new ArrayList<>();

        ShopUnitDto head = ShopUnitDto
                .builder()
                .id(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a111"))
                .name("Head")
                .price(null)
                .type(ShopUnitType.CATEGORY).build();


        ShopUnitDto node = ShopUnitDto
                .builder()
                .id(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a222"))
                .name("Node")
                .parentId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a111"))
                .price(null)
                .type(ShopUnitType.CATEGORY).build();

        ShopUnitDto node2 = ShopUnitDto
                .builder()
                .id(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a333"))
                .name("Node2")
                .parentId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a222"))
                .price(null)
                .type(ShopUnitType.CATEGORY).build();

        ShopUnitDto leaf1 = ShopUnitDto
                .builder()
                .id(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a444"))
                .name("leaf")
                .price((long) 100)
                .parentId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a222"))
                .type(ShopUnitType.OFFER).build();

        ShopUnitDto leaf2 = ShopUnitDto
                .builder()
                .id(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a555"))
                .name("leaf2")
                .price((long) 200)
                .parentId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a333"))
                .type(ShopUnitType.OFFER).build();

        ShopUnitDto leaf3 = ShopUnitDto
                .builder()
                .id(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a666"))
                .name("leaf3")
                .price((long) 1000)
                .parentId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a111"))
                .type(ShopUnitType.OFFER).build();

        items.add(leaf2);
        items.add(leaf3);
        items.add(leaf1);
        items.add(node2);
        items.add(node);
        items.add(head);


        ImportShopUnitDto importShopUnitDto = ImportShopUnitDto.builder().items(items)
                .updateDate(updateDate).build();

        var result = testRestTemplate.postForEntity("/imports", importShopUnitDto, null);
        assertEquals(HttpStatus.OK, result.getStatusCode());


        ShopUnitModel headModel = shopUnitRepositoryInterface
                .findById(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a111")).orElseThrow();

        assertEquals(headModel.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a111"));
        assertNull(headModel.getPrice());
        assertEquals(headModel.getDate(), updateDate);
        assertEquals(headModel.getType(), ShopUnitType.CATEGORY);
        assertEquals(headModel.getName(), "Head");
        assertNull(headModel.getParent());

        ShopUnitModel nodeModel = shopUnitRepositoryInterface
                .findById(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a222")).orElseThrow();

        assertEquals(nodeModel.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a222"));
        assertNull(nodeModel.getPrice());
        assertEquals(nodeModel.getDate(), updateDate);
        assertEquals(nodeModel.getType(), ShopUnitType.CATEGORY);
        assertEquals(nodeModel.getName(), "Node");
        assertEquals(nodeModel.getParent(), headModel);


        ShopUnitModel nodeModel2 = shopUnitRepositoryInterface
                .findById(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a333")).orElseThrow();

        assertEquals(nodeModel2.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a333"));
        assertNull(nodeModel2.getPrice());
        assertEquals(nodeModel2.getDate(), updateDate);
        assertEquals(nodeModel2.getType(), ShopUnitType.CATEGORY);
        assertEquals(nodeModel2.getName(), "Node2");
        assertEquals(nodeModel2.getParent(), nodeModel);

        ShopUnitModel leafModel = shopUnitRepositoryInterface
                .findById(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a444")).orElseThrow();

        assertEquals(leafModel.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a444"));
        assertEquals(leafModel.getPrice(), Long.valueOf(100));
        assertEquals(leafModel.getDate(), updateDate);
        assertEquals(leafModel.getType(), ShopUnitType.OFFER);
        assertEquals(leafModel.getName(), "leaf");
        assertEquals(leafModel.getParent(), nodeModel);


        ShopUnitModel leafModel2 = shopUnitRepositoryInterface
                .findById(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a555")).orElseThrow();

        assertEquals(leafModel2.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a555"));
        assertEquals(leafModel2.getPrice(), Long.valueOf(200));
        assertEquals(leafModel2.getDate(), updateDate);
        assertEquals(leafModel2.getType(), ShopUnitType.OFFER);
        assertEquals(leafModel2.getName(), "leaf2");
        assertEquals(leafModel2.getParent(), nodeModel2);

        ShopUnitModel leafModel3 = shopUnitRepositoryInterface
                .findById(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a666")).orElseThrow();

        assertEquals(leafModel3.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a666"));
        assertEquals(leafModel3.getPrice(), Long.valueOf(1000));
        assertEquals(leafModel3.getDate(), updateDate);
        assertEquals(leafModel3.getType(), ShopUnitType.OFFER);
        assertEquals(leafModel3.getName(), "leaf3");
        assertEquals(leafModel3.getParent(), headModel);


        // update !!!!

        //2022-06-08 08:32:23
        LocalDateTime updateDateTime = Instant.parse("2020-12-01T10:05:23.653Z").atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        ShopUnitDto leaf2Update = ShopUnitDto.builder().id(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a555"))
                .price((long) 1)
                .name("Updated")
                .type(ShopUnitType.OFFER)
                .parentId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a333"))
                .build();

        items = new ArrayList<>();
        items.add(leaf2Update);

        importShopUnitDto = ImportShopUnitDto.builder().items(items)
                .updateDate(updateDateTime).build();

        result = testRestTemplate.postForEntity("/imports", importShopUnitDto, null);
        assertEquals(HttpStatus.OK, result.getStatusCode());

        ShopUnitModel leaf2UpdateModel = shopUnitRepositoryInterface.findById(
                UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a555")).orElseThrow();
        ShopUnitModel node2UpdateModel = shopUnitRepositoryInterface.findById(
                UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a333")).orElseThrow();
        ShopUnitModel nodeUpdateModel = shopUnitRepositoryInterface.findById(
                UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a222")).orElseThrow();
        ShopUnitModel headUpdateModel = shopUnitRepositoryInterface.findById(
                UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a111")).orElseThrow();

        assertEquals(leaf2UpdateModel.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a555"));
        assertEquals(leaf2UpdateModel.getPrice(), Long.valueOf(1));
        assertEquals(leaf2UpdateModel.getDate(), updateDateTime);
        assertEquals(leaf2UpdateModel.getType(), ShopUnitType.OFFER);
        assertEquals(leaf2UpdateModel.getName(), "Updated");
        assertEquals(leaf2UpdateModel.getParent(), node2UpdateModel);

        assertEquals(nodeUpdateModel.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a222"));
        assertNull(nodeUpdateModel.getPrice());
        assertEquals(nodeUpdateModel.getDate(), updateDateTime);
        assertEquals(nodeUpdateModel.getType(), ShopUnitType.CATEGORY);
        assertEquals(nodeUpdateModel.getName(), "Node");
        assertEquals(nodeUpdateModel.getParent(), headUpdateModel);

        assertEquals(node2UpdateModel.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a333"));
        assertNull(node2UpdateModel.getPrice());
        assertEquals(node2UpdateModel.getDate(), updateDateTime);
        assertEquals(node2UpdateModel.getType(), ShopUnitType.CATEGORY);
        assertEquals(node2UpdateModel.getName(), "Node2");
        assertEquals(node2UpdateModel.getParent(), nodeUpdateModel);

        assertEquals(headUpdateModel.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a111"));
        assertNull(headUpdateModel.getPrice());
        assertEquals(headUpdateModel.getDate(), updateDateTime);
        assertEquals(headUpdateModel.getType(), ShopUnitType.CATEGORY);
        assertEquals(headUpdateModel.getName(), "Head");

    }


    @Test
    public void sendBadRequestImport() {
        //родитель не товар

        ShopUnitDto shopUnitDtoParent = ShopUnitDto
                .builder()
                .id(UUID.randomUUID())
                .name("Name")
                .price(null)
                .type(ShopUnitType.CATEGORY).build();

        ShopUnitDto shopUnitDtoChild = ShopUnitDto
                .builder()
                .id(UUID.randomUUID())
                .parentId(shopUnitDtoParent.getParentId())
                .name("Name")
                .price((long) -1)
                .type(ShopUnitType.OFFER).build();

        List list = new ArrayList<ShopUnitDto>();
        list.add(shopUnitDtoChild);
        list.add(shopUnitDtoParent);

        ImportShopUnitDto invalidParentOfferDto = ImportShopUnitDto.builder().build();
        invalidParentOfferDto.setItems(list);
        invalidParentOfferDto.setUpdateDate(new Date().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime());

        var result = testRestTemplate.postForEntity("/imports", invalidParentOfferDto, null);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());

        //цена товара не может быть null и должна быть больше либо равна нулю.
        ShopUnitDto shopUnitDto = ShopUnitDto
                .builder()
                .id(UUID.randomUUID())
                .name("Name")
                .price((long) -1)
                .type(ShopUnitType.OFFER).build();

        ImportShopUnitDto invalidPriceOfferDto = ImportShopUnitDto.builder().build();
        list = new ArrayList<ShopUnitDto>();
        list.add(shopUnitDto);
        invalidPriceOfferDto.setItems(list);
        invalidPriceOfferDto.setUpdateDate(new Date().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime());

        result = testRestTemplate.postForEntity("/imports", invalidPriceOfferDto, null);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());

        // у категорий поле price должно содержать null
        ImportShopUnitDto invalidPriceCategoryDto = ImportShopUnitDto.builder().build();

        shopUnitDto = ShopUnitDto
                .builder()
                .id(UUID.randomUUID())
                .name("Name")
                .price((long) 1)
                .type(ShopUnitType.CATEGORY).build();

        list = new ArrayList<ShopUnitDto>();
        list.add(shopUnitDto);
        invalidPriceOfferDto.setItems(list);
        invalidPriceOfferDto.setUpdateDate(new Date().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime());

        result = testRestTemplate.postForEntity("/imports", invalidPriceCategoryDto, null);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());

        //в одном запросе не может быть двух элементов с одинаковым id
        ImportShopUnitDto invalidNotUniqeIdDto = ImportShopUnitDto.builder().build();

        shopUnitDto = ShopUnitDto
                .builder()
                .id(UUID.randomUUID())
                .name("Name")
                .price(null)
                .type(ShopUnitType.CATEGORY).build();

        ShopUnitDto shopUnitDto2 = ShopUnitDto
                .builder()
                .id(shopUnitDto.getId())
                .name("Name")
                .price(null)
                .type(ShopUnitType.CATEGORY).build();

        list = new ArrayList<ShopUnitDto>();
        list.add(shopUnitDto);
        list.add(shopUnitDto2);
        invalidPriceOfferDto.setItems(list);
        invalidPriceOfferDto.setUpdateDate(new Date().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime());

        result = testRestTemplate.postForEntity("/imports", invalidNotUniqeIdDto, null);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());


        // имя не null
        ImportShopUnitDto invalidNameDto = ImportShopUnitDto.builder().build();

        shopUnitDto = ShopUnitDto
                .builder()
                .id(UUID.randomUUID())
                .name(null)
                .price(null)
                .type(ShopUnitType.CATEGORY).build();

        list = new ArrayList<ShopUnitDto>();
        list.add(shopUnitDto);
        invalidPriceOfferDto.setItems(list);
        invalidPriceOfferDto.setUpdateDate(new Date().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime());

        result = testRestTemplate.postForEntity("/imports", invalidNameDto, null);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

}
