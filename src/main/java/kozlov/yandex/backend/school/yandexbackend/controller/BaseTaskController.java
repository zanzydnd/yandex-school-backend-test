package kozlov.yandex.backend.school.yandexbackend.controller;

import kozlov.yandex.backend.school.yandexbackend.dto.ImportShopUnitDto;
import kozlov.yandex.backend.school.yandexbackend.service.ShopUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Validated
@RestController
public class BaseTaskController {

    @Autowired
    private ShopUnitService shopUnitService;

    @PostMapping("/imports")
    public ResponseEntity<?> importGoods(@RequestBody @Valid ImportShopUnitDto shopUnitDto) {
        /*
        uuid товара или категории является уникальным среди товаров и категорий -
        родителем товара или категории может быть только категория
        принадлежность к категории определяется полем parentId -
        товар или категория могут не иметь родителя -
        название элемента не может быть null -
        у категорий поле price должно содержать null -
        при обновлении товара/категории обновленными считаются все их параметры -
        при обновлении параметров элемента обязательно обновляется поле date в соответствии с временем обновления -
         */

        Instant start = Instant.now();
        shopUnitService.importShopUnit(shopUnitDto);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        System.out.println("All: " + shopUnitDto.getItems().size());
        System.out.println("Time taken: " + timeElapsed.toMillis() + " milliseconds");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteElement(@PathVariable(required = true) UUID id) {

        shopUnitService.deleteShopUnit(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/nodes/{id}")
    public ResponseEntity<?> getElementInfo(@PathVariable(required = true) UUID id) throws IllegalAccessException, InstantiationException {
        var result = shopUnitService.getShopUnitModelWithChildren(id);
        return ResponseEntity.ok(result);
    }
}
