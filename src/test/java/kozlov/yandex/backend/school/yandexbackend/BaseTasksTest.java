package kozlov.yandex.backend.school.yandexbackend;

import kozlov.yandex.backend.school.yandexbackend.controller.BaseTaskController;
import kozlov.yandex.backend.school.yandexbackend.dto.ImportShopUnitDto;
import kozlov.yandex.backend.school.yandexbackend.dto.ResponseNodeShopUnitDto;
import kozlov.yandex.backend.school.yandexbackend.dto.ShopUnitDto;
import kozlov.yandex.backend.school.yandexbackend.enums.ShopUnitType;
import kozlov.yandex.backend.school.yandexbackend.jpa.repository.ShopUnitRepositoryInterface;
import kozlov.yandex.backend.school.yandexbackend.model.ShopUnitModel;
import org.checkerframework.checker.units.qual.A;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.Assert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseTasksTest extends AbstractTest {



    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ShopUnitRepositoryInterface shopUnitRepository;


    @Test
    public void AimportValidData() throws Exception {

        LocalDateTime updateDate = Instant.parse("2019-12-01T10:05:23.653Z").atZone(ZoneId.of("UTC")).toLocalDateTime();

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


        ShopUnitDto single = ShopUnitDto.builder()
                .id(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a123"))
                .type(ShopUnitType.CATEGORY)
                .name("Single")
                .build();

        items.add(single);
        items.add(leaf2);
        items.add(leaf3);
        items.add(leaf1);
        items.add(node2);
        items.add(node);
        items.add(head);


        ImportShopUnitDto importShopUnitDto = ImportShopUnitDto.builder().items(items)
                .updateDate(updateDate).build();

        this.mockMvc.perform(
                post("/imports")
                        .contentType("application/json")
                        .content(super.mapToJson(importShopUnitDto))
        ).andExpect(status().isOk());

        ShopUnitModel headModel = shopUnitRepository
                .findById(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a111")).orElseThrow();

        Assert.assertEquals(headModel.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a111"));
        Assert.assertNull(headModel.getPrice());
        Assert.assertEquals(headModel.getDate(), updateDate);
        Assert.assertEquals(headModel.getType(), ShopUnitType.CATEGORY);
        Assert.assertEquals(headModel.getName(), "Head");
        Assert.assertNull(headModel.getParent());

        ShopUnitModel nodeModel = shopUnitRepository
                .findById(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a222")).orElseThrow();

        Assert.assertEquals(nodeModel.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a222"));
        Assert.assertNull(nodeModel.getPrice());
        Assert.assertEquals(nodeModel.getDate(), updateDate);
        Assert.assertEquals(nodeModel.getType(), ShopUnitType.CATEGORY);
        Assert.assertEquals(nodeModel.getName(), "Node");
        Assert.assertEquals(nodeModel.getParent(), headModel);


        ShopUnitModel nodeModel2 = shopUnitRepository
                .findById(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a333")).orElseThrow();

        Assert.assertEquals(nodeModel2.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a333"));
        Assert.assertNull(nodeModel2.getPrice());
        Assert.assertEquals(nodeModel2.getDate(), updateDate);
        Assert.assertEquals(nodeModel2.getType(), ShopUnitType.CATEGORY);
        Assert.assertEquals(nodeModel2.getName(), "Node2");
        Assert.assertEquals(nodeModel2.getParent(), nodeModel);

        ShopUnitModel leafModel = shopUnitRepository
                .findById(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a444")).orElseThrow();

        Assert.assertEquals(leafModel.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a444"));
        Assert.assertEquals(leafModel.getPrice(), Long.valueOf(100));
        Assert.assertEquals(leafModel.getDate(), updateDate);
        Assert.assertEquals(leafModel.getType(), ShopUnitType.OFFER);
        Assert.assertEquals(leafModel.getName(), "leaf");
        Assert.assertEquals(leafModel.getParent(), nodeModel);


        ShopUnitModel leafModel2 = shopUnitRepository
                .findById(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a555")).orElseThrow();

        Assert.assertEquals(leafModel2.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a555"));
        Assert.assertEquals(leafModel2.getPrice(), Long.valueOf(200));
        Assert.assertEquals(leafModel2.getDate(), updateDate);
        Assert.assertEquals(leafModel2.getType(), ShopUnitType.OFFER);
        Assert.assertEquals(leafModel2.getName(), "leaf2");
        Assert.assertEquals(leafModel2.getParent(), nodeModel2);

        ShopUnitModel leafModel3 = shopUnitRepository
                .findById(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a666")).orElseThrow();

        Assert.assertEquals(leafModel3.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a666"));
        Assert.assertEquals(leafModel3.getPrice(), Long.valueOf(1000));
        Assert.assertEquals(leafModel3.getDate(), updateDate);
        Assert.assertEquals(leafModel3.getType(), ShopUnitType.OFFER);
        Assert.assertEquals(leafModel3.getName(), "leaf3");
        Assert.assertEquals(leafModel3.getParent(), headModel);

        ShopUnitModel singleModel = shopUnitRepository
                .findById(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a123")).orElseThrow();


        Assert.assertEquals(singleModel.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a123"));
        Assert.assertNull(singleModel.getPrice());
        Assert.assertEquals(singleModel.getDate(), updateDate);
        Assert.assertEquals(singleModel.getType(), ShopUnitType.CATEGORY);
        Assert.assertEquals(singleModel.getName(), "Single");
        Assert.assertNull(singleModel.getParent());


        // update !!!!


        LocalDateTime updateDateTime = Instant.parse("2020-12-01T10:05:23.653Z").atZone(ZoneId.of("UTC"))
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

        this.mockMvc.perform(
                        post("/imports")
                                .contentType("application/json")
                                .content(super.mapToJson(importShopUnitDto)))
                .andExpect(status().isOk());

        ShopUnitModel leaf2UpdateModel = shopUnitRepository.findById(
                UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a555")).orElseThrow();
        ShopUnitModel node2UpdateModel = shopUnitRepository.findById(
                UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a333")).orElseThrow();
        ShopUnitModel nodeUpdateModel = shopUnitRepository.findById(
                UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a222")).orElseThrow();
        ShopUnitModel headUpdateModel = shopUnitRepository.findById(
                UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a111")).orElseThrow();

        Assert.assertEquals(leaf2UpdateModel.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a555"));
        Assert.assertEquals(leaf2UpdateModel.getPrice(), Long.valueOf(1));
        Assert.assertEquals(leaf2UpdateModel.getDate(), updateDateTime);
        Assert.assertEquals(leaf2UpdateModel.getType(), ShopUnitType.OFFER);
        Assert.assertEquals(leaf2UpdateModel.getName(), "Updated");
        Assert.assertEquals(leaf2UpdateModel.getParent(), node2UpdateModel);

        Assert.assertEquals(nodeUpdateModel.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a222"));
        Assert.assertNull(nodeUpdateModel.getPrice());
        Assert.assertEquals(nodeUpdateModel.getDate(), updateDateTime);
        Assert.assertEquals(nodeUpdateModel.getType(), ShopUnitType.CATEGORY);
        Assert.assertEquals(nodeUpdateModel.getName(), "Node");
        Assert.assertEquals(nodeUpdateModel.getParent(), headUpdateModel);

        Assert.assertEquals(node2UpdateModel.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a333"));
        Assert.assertNull(node2UpdateModel.getPrice());
        Assert.assertEquals(node2UpdateModel.getDate(), updateDateTime);
        Assert.assertEquals(node2UpdateModel.getType(), ShopUnitType.CATEGORY);
        Assert.assertEquals(node2UpdateModel.getName(), "Node2");
        Assert.assertEquals(node2UpdateModel.getParent(), nodeUpdateModel);

        Assert.assertEquals(headUpdateModel.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a111"));
        Assert.assertNull(headUpdateModel.getPrice());
        Assert.assertEquals(headUpdateModel.getDate(), updateDateTime);
        Assert.assertEquals(headUpdateModel.getType(), ShopUnitType.CATEGORY);
        Assert.assertEquals(headUpdateModel.getName(), "Head");
        Assert.assertNull(headUpdateModel.getParent());

    }

    @Test
    public void BgetElementInfoInvalidId() throws Exception {
        this.mockMvc.perform(get("/nodes/asd")
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void CgetElementInfoNotFound() throws Exception {
        this.mockMvc.perform(get("/nodes/" + "3fa85f64-5717-4562-b3fc-2c963f66a999")
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    public void DgetElement() throws Exception {
        // цена категории - это средняя цена всех её товаров,
        // включая товары дочерних категорий.
        // Если категория не содержит товаров цена равна null.
        // При обновлении цены товара, средняя цена категории,
        // которая содержит этот товар, тоже обновляется.

        LocalDateTime dateUpdated = Instant.parse("2020-12-01T10:05:23.653Z").atZone(ZoneId.of("UTC"))
                .toLocalDateTime();

        LocalDateTime date = Instant.parse("2019-12-01T10:05:23.653Z").atZone(ZoneId.of("UTC"))
                .toLocalDateTime();

        var leaf3 = ResponseNodeShopUnitDto
                .builder()
                .id(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a666"))
                .name("leaf3")
                .date(date)
                .price((long) 1000)
                .parentId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a111"))
                .type(ShopUnitType.OFFER).build();


        var leaf2 = ResponseNodeShopUnitDto
                .builder()
                .id(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a555"))
                .name("Updated")
                .date(dateUpdated)
                .price((long) 1)
                .parentId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a333"))
                .type(ShopUnitType.OFFER).build();

        var leaf1 = ResponseNodeShopUnitDto
                .builder()
                .id(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a444"))
                .name("leaf")
                .date(date)
                .price((long) 100)
                .parentId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a222"))
                .type(ShopUnitType.OFFER).build();

        var node2 = ResponseNodeShopUnitDto
                .builder()
                .id(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a333"))
                .name("Node2")
                .date(dateUpdated)
                .parentId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a222"))
                .price((long) 1)
                .children(Collections.singletonList(leaf2))
                .type(ShopUnitType.CATEGORY).build();

        var node = ResponseNodeShopUnitDto
                .builder()
                .id(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a222"))
                .name("Node")
                .date(dateUpdated)
                .parentId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a111"))
                .price((long) 50)
                .children(List.of(new ResponseNodeShopUnitDto[]{leaf1, node2}))
                .type(ShopUnitType.CATEGORY).build();


        ResponseNodeShopUnitDto father = ResponseNodeShopUnitDto
                .builder()
                .id(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a111"))
                .name("Head")
                .date(dateUpdated)
                .price((long) 367)
                .children(List.of(new ResponseNodeShopUnitDto[]{leaf3, node}))
                .type(ShopUnitType.CATEGORY).build();


        String response = this.mockMvc.perform(get("/nodes/" + "3fa85f64-5717-4562-b3fc-2c963f66a111")
                .contentType("application/json")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        var result = mapFromJson(response, ResponseNodeShopUnitDto.class);

        Assert.assertEquals(father, result);

        response = this.mockMvc.perform(get("/nodes/" + "3fa85f64-5717-4562-b3fc-2c963f66a222")
                .contentType("application/json")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        result = mapFromJson(response, ResponseNodeShopUnitDto.class);


        Assert.assertEquals(node, result);

        // проверка на пустой массив у категории без детей
        var single = ResponseNodeShopUnitDto.builder()
                .id(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a123"))
                .type(ShopUnitType.CATEGORY)
                .children(new ArrayList<>())
                .name("Single")
                .date(date)
                .build();

        response = this.mockMvc.perform(get("/nodes/" + "3fa85f64-5717-4562-b3fc-2c963f66a123")
                .contentType("application/json")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        result = mapFromJson(response, ResponseNodeShopUnitDto.class);


        Assert.assertEquals(single, result);
    }


    @Test
    public void EimportsInvalidBodyTest() throws Exception {

        LocalDateTime updateDate = Instant.parse("2019-12-01T10:05:23.653Z").atZone(ZoneId.of("UTC")).toLocalDateTime();
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
        invalidParentOfferDto.setUpdateDate(updateDate);

        this.mockMvc.perform(
                post("/imports")
                        .contentType("application/json")
                        .content(super.mapToJson(invalidParentOfferDto))
        ).andExpect(status().isBadRequest());

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
        invalidPriceOfferDto.setUpdateDate(updateDate);

        this.mockMvc.perform(
                post("/imports")
                        .contentType("application/json")
                        .content(super.mapToJson(invalidPriceOfferDto))
        ).andExpect(status().isBadRequest());

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
        invalidPriceOfferDto.setUpdateDate(updateDate);

        this.mockMvc.perform(
                post("/imports")
                        .contentType("application/json")
                        .content(super.mapToJson(invalidPriceCategoryDto))
        ).andExpect(status().isBadRequest()).andReturn();

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
        invalidPriceOfferDto.setUpdateDate(updateDate);

        this.mockMvc.perform(
                post("/imports")
                        .contentType("application/json")
                        .content(super.mapToJson(invalidNotUniqeIdDto))
        ).andExpect(status().isBadRequest()).andReturn();


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
        invalidPriceOfferDto.setUpdateDate(updateDate);

        this.mockMvc.perform(
                post("/imports")
                        .contentType("application/json")
                        .content(super.mapToJson(invalidNameDto))
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void FdeleteInvalidTest() throws Exception {
        this.mockMvc.perform(
                delete("/delete/asd")
                        .contentType("application/json")
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void GdeleteNotFoundTest() throws Exception {
        this.mockMvc.perform(
                delete("/delete/3fa85f64-5717-4562-b3fc-2c963f66a343")
                        .contentType("application/json")
        ).andExpect(status().isNotFound()).andReturn();
    }

//    @Test
//    public void HdeleteTest() throws Exception {
//        this.mockMvc.perform(
//                delete("/delete/3fa85f64-5717-4562-b3fc-2c963f66a222")
//                        .contentType("application/json")
//        ).andExpect(status().isOk()).andReturn();
//
//        this.mockMvc.perform(
//                delete("/delete/3fa85f64-5717-4562-b3fc-2c963f66a444")
//                        .contentType("application/json")
//        ).andExpect(status().isNotFound()).andReturn();
//
//        this.mockMvc.perform(
//                delete("/delete/3fa85f64-5717-4562-b3fc-2c963f66a333")
//                        .contentType("application/json")
//        ).andExpect(status().isNotFound()).andReturn();
//
//        this.mockMvc.perform(
//                delete("/delete/3fa85f64-5717-4562-b3fc-2c963f66a222")
//                        .contentType("application/json")
//        ).andExpect(status().isNotFound()).andReturn();
//
//
//    }
}
