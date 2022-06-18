package kozlov.yandex.backend.school.yandexbackend.dto;

import kozlov.yandex.backend.school.yandexbackend.enums.ShopUnitType;
import kozlov.yandex.backend.school.yandexbackend.validator.IsCorrectUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Validated
@IsCorrectUnit
public class ShopUnitDto {
    @NotNull
    private UUID id;

    @NotNull
    private String name;
    private UUID parentId;

    private Long price;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ShopUnitType type;
}
