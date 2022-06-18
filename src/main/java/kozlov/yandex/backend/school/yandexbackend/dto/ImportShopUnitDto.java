package kozlov.yandex.backend.school.yandexbackend.dto;

import kozlov.yandex.backend.school.yandexbackend.validator.IsCorrectUnit;
import kozlov.yandex.backend.school.yandexbackend.validator.IsCorrectUnitList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class ImportShopUnitDto {
    @IsCorrectUnitList
    private List<@Valid ShopUnitDto> items;
    @NotNull
    private Date updateDate;
}
