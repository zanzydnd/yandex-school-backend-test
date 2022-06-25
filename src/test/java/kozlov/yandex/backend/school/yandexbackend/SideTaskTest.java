package kozlov.yandex.backend.school.yandexbackend;

import com.fasterxml.jackson.core.JsonProcessingException;
import kozlov.yandex.backend.school.yandexbackend.dto.*;
import kozlov.yandex.backend.school.yandexbackend.enums.ShopUnitType;
import kozlov.yandex.backend.school.yandexbackend.jpa.repository.ShopUnitHistoryRepositoryInterface;
import kozlov.yandex.backend.school.yandexbackend.jpa.repository.ShopUnitRepositoryInterface;
import kozlov.yandex.backend.school.yandexbackend.model.ShopUnitModel;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SideTaskTest extends AbstractTest {
    @Autowired
    private MockMvc mockMvc;

    public void prepareData() throws Exception {
        LocalDateTime updateDate = Instant.parse("2020-12-01T00:00:00.000Z").atZone(ZoneId.of("UTC")).toLocalDateTime();

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

        LocalDateTime updateDateTime = Instant.parse("2020-12-02T00:00:00.000Z").atZone(ZoneId.of("UTC"))
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
    }

    @Test
    public void AtestSales() throws Exception {
        this.prepareData();

        var response = this.mockMvc.perform(
                get("/sales?date=2020-12-03T00:00:00.000Z")
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        var result = mapFromJson(response, ReturnSalesDto.class);

        var expected = new ReturnSalesDto();

        expected.setItems(List.of(new ReturnItemDto[]{ReturnItemDto
                .builder().id(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a555")).name("Updated")
                .parentId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a333"))
                .price((long) 1)
                .type(ShopUnitType.OFFER)
                .date(Instant.parse("2020-12-02T00:00:00.000Z").atZone(ZoneId.of("UTC")).toLocalDateTime())
                .build()}));

        Assert.assertEquals(expected, result);

    }

    @Test
    public void BtestSalesInvalid() throws Exception {
        this.mockMvc.perform(
                get("/sales?date=T21:12:01.000Z2022-05-28")
        ).andExpect(status().isBadRequest());

        this.mockMvc.perform(
                get("/sales?date=2022-05-2821:12:01.000Z")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void CtestStatistics() throws Exception {
        var response = this.mockMvc.perform(
                get("/node/3fa85f64-5717-4562-b3fc-2c963f66a111/statistic?dateStart=2020-12-01T10:05:23.653Z&dateEnd=2020-12-02T10:05:23.653Z")
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        var result = mapFromJson(response, ReturnSalesDto.class);

        var expected = ReturnSalesDto.builder().items(
                List.of(new ReturnItemDto[]{ReturnItemDto
                        .builder().id(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a111")).name("Head")
                        .parentId(null)
                        .price((long) 367)
                        .type(ShopUnitType.CATEGORY)
                        .date(Instant.parse("2020-12-02T00:00:00.000Z").atZone(ZoneId.of("UTC")).toLocalDateTime())
                        .build()})
        ).build();

        Assert.assertEquals(expected, result);

        response = this.mockMvc.perform(
                get("/node/3fa85f64-5717-4562-b3fc-2c963f66a111/statistic?dateStart=2020-12-01T00:00:00.000Z&dateEnd=2020-12-02T00:00:00.000Z")
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        result = mapFromJson(response, ReturnSalesDto.class);

        expected = ReturnSalesDto.builder().items(
                List.of(new ReturnItemDto[]{ReturnItemDto
                        .builder().id(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a111")).name("Head")
                        .parentId(null)
                        .price((long) 433)
                        .type(ShopUnitType.CATEGORY)
                        .date(Instant.parse("2020-12-01T00:00:00.000Z").atZone(ZoneId.of("UTC")).toLocalDateTime())
                        .build()})
        ).build();

        Assert.assertEquals(expected, result);


        response = this.mockMvc.perform(
                get("/node/3fa85f64-5717-4562-b3fc-2c963f66a111/statistic?dateStart=2020-12-01T00:00:00.000Z&dateEnd=2020-12-02T01:00:00.000Z")
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        result = mapFromJson(response, ReturnSalesDto.class);

        expected = ReturnSalesDto.builder().items(
                List.of(new ReturnItemDto[]{
                        ReturnItemDto
                                .builder().id(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a111")).name("Head")
                                .parentId(null)
                                .price((long) 367)
                                .type(ShopUnitType.CATEGORY)
                                .date(Instant.parse("2020-12-02T00:00:00.000Z").atZone(ZoneId.of("UTC")).toLocalDateTime())
                                .build(),
                        ReturnItemDto
                                .builder().id(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a111")).name("Head")
                                .parentId(null)
                                .price((long) 433)
                                .type(ShopUnitType.CATEGORY)
                                .date(Instant.parse("2020-12-01T00:00:00.000Z").atZone(ZoneId.of("UTC")).toLocalDateTime())
                                .build()})
        ).build();

        Assert.assertEquals(expected, result);

    }

    @Test
    public void DtestStatisticsNotFound() throws Exception {
        this.mockMvc.perform(
                get("/node/3fa85f64-5717-4562-b3fc-2c963f66a321/statistic?dateStart=2020-12-03T00:00:00.000Z&dateEnd=2020-12-03T00:00:00.000Z")
        ).andExpect(status().isNotFound());
    }

    @Test
    public void EtestStatisticsInvalid() throws Exception {
        this.mockMvc.perform(
                get("/node/3fzxca1/statistic?dateStart=2020-12-03T00:00:00.000Z&dateEnd=2020-12-03T00:00:00.000Z")
        ).andExpect(status().isBadRequest());

        this.mockMvc.perform(
                get("/node/3fa85f64-5717-4562-b3fc-2c963f66a111/statistic?dateStart=2019-12-01T10:05:253&dateEnd=2020-12-02T10:05:23.653Z")
        ).andExpect(status().isBadRequest());

        this.mockMvc.perform(
                get("/node/3fa85f64-5717-4562-b3fc-2c963f66a111/statistic?dateStart=2019-12-01T10:05:23.653Z&dateEnd=2020-12-0210:05:23.653Z")
        ).andExpect(status().isBadRequest());
    }

}
