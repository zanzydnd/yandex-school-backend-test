package kozlov.yandex.backend.school.yandexbackend;

import kozlov.yandex.backend.school.yandexbackend.controller.BaseTaskController;
import kozlov.yandex.backend.school.yandexbackend.dto.ImportShopUnitDto;
import kozlov.yandex.backend.school.yandexbackend.dto.ShopUnitDto;
import kozlov.yandex.backend.school.yandexbackend.enums.ShopUnitType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

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

    @Test
    public void importValidData() throws Exception {
        LocalDateTime updateDate = new Date().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();


    }

    @Test
    public void importsInvalidBodyTest() throws Exception {
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
