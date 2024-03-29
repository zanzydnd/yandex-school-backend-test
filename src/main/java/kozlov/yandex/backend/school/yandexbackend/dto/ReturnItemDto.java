package kozlov.yandex.backend.school.yandexbackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import kozlov.yandex.backend.school.yandexbackend.enums.ShopUnitType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime date;

}