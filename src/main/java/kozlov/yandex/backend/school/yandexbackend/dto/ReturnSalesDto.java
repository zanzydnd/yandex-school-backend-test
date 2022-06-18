package kozlov.yandex.backend.school.yandexbackend.dto;

import kozlov.yandex.backend.school.yandexbackend.enums.ShopUnitType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReturnSalesDto {

    private List<ReturnItemDto> items;
}

