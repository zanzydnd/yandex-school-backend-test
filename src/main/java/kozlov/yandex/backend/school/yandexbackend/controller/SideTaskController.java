package kozlov.yandex.backend.school.yandexbackend.controller;

import kozlov.yandex.backend.school.yandexbackend.service.ShopUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@RestController
public class SideTaskController {

    @Autowired
    private ShopUnitService service;

    @GetMapping("/sales")
    public ResponseEntity<?> getListOfGoods(@RequestParam(name = "date", required = true)
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                    LocalDateTime date) {
        return ResponseEntity.ok(service.getSales(date));
    }

    @GetMapping("/node/{id}/statistic")
    public ResponseEntity<?> getStats(@PathVariable(required = true) UUID id, @RequestParam(name = "dateStart", required = true)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Date dateStart, @RequestParam(name = "dateEnd", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                              Date dateEnd) {
        return ResponseEntity.ok(service.getStatistics(id, dateStart, dateEnd));
    }
}
