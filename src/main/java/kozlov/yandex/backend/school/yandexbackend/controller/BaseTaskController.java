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
        shopUnitService.importShopUnit(shopUnitDto);
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
