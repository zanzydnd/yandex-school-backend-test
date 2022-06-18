package kozlov.yandex.backend.school.yandexbackend.dto;

import kozlov.yandex.backend.school.yandexbackend.enums.ShopUnitType;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SqlResultSetMapping(
        name = "recursiveShopUnitModel",
        entities = {
                @EntityResult(
                        entityClass = RecursiveShopUnitDto.class,
                        fields = {
                                @FieldResult(name = "id", column = "id"),
                                @FieldResult(name = "name", column = "name"),
                                @FieldResult(name = "price", column = "price"),
                                @FieldResult(name = "parentId", column = "parent_id"),
                                @FieldResult(name = "date", column = "date"),
                                @FieldResult(name = "path", column = "path"),
                                @FieldResult(name = "level", column = "level"),
                                @FieldResult(name = "type", column = "type"),
                        }
                )
        }
)
public class RecursiveShopUnitDto {
    String id;
    String name;
    BigDecimal price;
    String parent_id;

    String type;
    Date date;

    Integer level;

    UUID[] path;
}
