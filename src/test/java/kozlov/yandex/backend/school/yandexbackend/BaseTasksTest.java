package kozlov.yandex.backend.school.yandexbackend;

import kozlov.yandex.backend.school.yandexbackend.controller.BaseTaskController;
import kozlov.yandex.backend.school.yandexbackend.dto.ImportShopUnitDto;
import kozlov.yandex.backend.school.yandexbackend.dto.ShopUnitDto;
import kozlov.yandex.backend.school.yandexbackend.enums.ShopUnitType;
import kozlov.yandex.backend.school.yandexbackend.jpa.repository.ShopUnitRepositoryInterface;
import kozlov.yandex.backend.school.yandexbackend.model.ShopUnitModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.Assert;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class BaseTasksTest extends AbstractTest {

    @Autowired
    private BaseTaskController controller;

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ShopUnitRepositoryInterface shopUnitRepository;


    @Test
    public void importValidData() throws Exception {

        //2022-05-08 08:32:23
        LocalDateTime updateDate = new Date(1651987943).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

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
        LocalDateTime date = new Date().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        ImportShopUnitDto importShopUnitDto = ImportShopUnitDto.builder().items(items)
                .updateDate(date).build();

        this.mockMvc.perform(
                post("/imports")
                        .contentType("application/json")
                        .content(super.mapToJson(importShopUnitDto))
        ).andExpect(status().isOk());

        ShopUnitModel headModel = shopUnitRepository
                .getReferenceById(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a111"));

        Assert.assertEquals(headModel.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a111"));
        Assert.assertNull(headModel.getPrice());
        Assert.assertEquals(headModel.getDate(), date);
        Assert.assertEquals(headModel.getType(), ShopUnitType.CATEGORY);
        Assert.assertEquals(headModel.getName(), "Head");
        Assert.assertNull(headModel.getParent());

        ShopUnitModel nodeModel = shopUnitRepository
                .getReferenceById(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a222"));

        Assert.assertEquals(nodeModel.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a222"));
        Assert.assertNull(nodeModel.getPrice());
        Assert.assertEquals(nodeModel.getDate(), date);
        Assert.assertEquals(nodeModel.getType(), ShopUnitType.CATEGORY);
        Assert.assertEquals(nodeModel.getName(), "Node");
        Assert.assertEquals(nodeModel.getParent(), headModel);


        ShopUnitModel nodeModel2 = shopUnitRepository
                .getReferenceById(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a333"));

        Assert.assertEquals(nodeModel2.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a333"));
        Assert.assertNull(nodeModel2.getPrice());
        Assert.assertEquals(nodeModel2.getDate(), date);
        Assert.assertEquals(nodeModel2.getType(), ShopUnitType.CATEGORY);
        Assert.assertEquals(nodeModel2.getName(), "Node2");
        Assert.assertEquals(nodeModel2.getParent(), nodeModel);

        ShopUnitModel leafModel = shopUnitRepository
                .getReferenceById(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a444"));

        Assert.assertEquals(leafModel.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a444"));
        Assert.assertEquals(java.util.Optional.ofNullable(leafModel.getPrice()), (long) 200);
        Assert.assertEquals(leafModel.getDate(), date);
        Assert.assertEquals(leafModel.getType(), ShopUnitType.OFFER);
        Assert.assertEquals(leafModel.getName(), "leaf");
        Assert.assertEquals(leafModel.getParent(), nodeModel);


        ShopUnitModel leafModel2 = shopUnitRepository
                .getReferenceById(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a555"));

        Assert.assertEquals(leafModel2.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a555"));
        Assert.assertEquals(java.util.Optional.ofNullable(leafModel2.getPrice()), (long) 100);
        Assert.assertEquals(leafModel2.getDate(), date);
        Assert.assertEquals(leafModel2.getType(), ShopUnitType.OFFER);
        Assert.assertEquals(leafModel2.getName(), "leaf2");
        Assert.assertEquals(leafModel2.getParent(), nodeModel2);

        ShopUnitModel leafModel3 = shopUnitRepository
                .getReferenceById(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a666"));

        Assert.assertEquals(leafModel2.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a666"));
        Assert.assertEquals(java.util.Optional.ofNullable(leafModel2.getPrice()), (long) 1000);
        Assert.assertEquals(leafModel2.getDate(), date);
        Assert.assertEquals(leafModel2.getType(), ShopUnitType.OFFER);
        Assert.assertEquals(leafModel2.getName(), "leaf3");
        Assert.assertEquals(leafModel2.getParent(), headModel);


        // update !!!!

        //2022-06-08 08:32:23
        LocalDateTime updateDateTime = new Date(1654666343).toInstant()
                .atZone(ZoneId.systemDefault())
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
                        .content(super.mapToJson(importShopUnitDto))
        ).andExpect(status().isOk());

        ShopUnitModel leaf2UpdateModel = shopUnitRepository.getReferenceById(
                UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a555"));
        ShopUnitModel node2UpdateModel = shopUnitRepository.getReferenceById(
                UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a333"));
        ShopUnitModel nodeUpdateModel = shopUnitRepository.getReferenceById(
                UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a222"));
        ShopUnitModel headUpdateModel = shopUnitRepository.getReferenceById(
                UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a111"));


        Assert.assertEquals(leaf2UpdateModel.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a555"));
        Assert.assertEquals(java.util.Optional.ofNullable(leaf2UpdateModel.getPrice()), (long) 1);
        Assert.assertEquals(leaf2UpdateModel.getDate(), updateDateTime);
        Assert.assertEquals(leaf2UpdateModel.getType(), ShopUnitType.OFFER);
        Assert.assertEquals(leaf2UpdateModel.getName(), "leaf2");
        Assert.assertEquals(leaf2UpdateModel.getParent(), node2UpdateModel);

        Assert.assertEquals(headUpdateModel.getId(), UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a111"));
        Assert.assertNull(headUpdateModel.getPrice());
        Assert.assertEquals(headUpdateModel.getDate(), updateDateTime);
        Assert.assertEquals(headUpdateModel.getType(), ShopUnitType.CATEGORY);
        Assert.assertEquals(headUpdateModel.getName(), "Head");
        Assert.assertNull(headUpdateModel.getParent());

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
    }

    @Test
    public void importsInvalidBodyTest() throws Exception {
        //родитель не товар

        //цена товара не может быть null и должна быть больше либо равна нулю.
        ShopUnitDto shopUnitDto = ShopUnitDto
                .builder()
                .id(UUID.randomUUID())
                .name("Name")
                .price((long) -1)
                .type(ShopUnitType.OFFER).build();

        ImportShopUnitDto invalidPriceOfferDto = ImportShopUnitDto.builder().build();
        List list = new ArrayList<ShopUnitDto>();
        list.add(shopUnitDto);
        invalidPriceOfferDto.setItems(list);
        invalidPriceOfferDto.setUpdateDate(new Date().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime());

        this.mockMvc.perform(
                post("/imports")
                        .contentType("application/json")
                        .content(super.mapToJson(invalidPriceOfferDto))
        ).andExpect(status().isBadRequest()).andReturn();

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

        this.mockMvc.perform(
                post("/imports")
                        .contentType("application/json")
                        .content(super.mapToJson(invalidPriceOfferDto))
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
        invalidPriceOfferDto.setUpdateDate(new Date().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime());

        this.mockMvc.perform(
                post("/imports")
                        .contentType("application/json")
                        .content(super.mapToJson(invalidPriceOfferDto))
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
        invalidPriceOfferDto.setUpdateDate(new Date().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime());

        this.mockMvc.perform(
                post("/imports")
                        .contentType("application/json")
                        .content(super.mapToJson(invalidPriceOfferDto))
        ).andExpect(status().isBadRequest()).andReturn();
    }

}
