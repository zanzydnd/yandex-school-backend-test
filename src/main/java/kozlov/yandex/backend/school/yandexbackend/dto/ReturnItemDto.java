package kozlov.yandex.backend.school.yandexbackend.dto;

import kozlov.yandex.backend.school.yandexbackend.enums.ShopUnitType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReturnItemDto {
    private UUID id;

    private String name;
    private UUID parentId;

    private Long price;

    @Enumerated(EnumType.STRING)
    private ShopUnitType type;
    private Date date;

}