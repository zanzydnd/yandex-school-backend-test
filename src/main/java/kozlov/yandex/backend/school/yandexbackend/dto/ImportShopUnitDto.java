package kozlov.yandex.backend.school.yandexbackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import kozlov.yandex.backend.school.yandexbackend.validator.IsCorrectUnit;
import kozlov.yandex.backend.school.yandexbackend.validator.IsCorrectUnitList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updateDate;
}
