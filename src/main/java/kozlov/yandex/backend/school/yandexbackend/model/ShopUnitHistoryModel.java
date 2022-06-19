package kozlov.yandex.backend.school.yandexbackend.model;

import kozlov.yandex.backend.school.yandexbackend.enums.ShopUnitType;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GeneratorType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name = "ShopUnitHistory")
public class ShopUnitHistoryModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    private String originId;

    private Long price;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ShopUnitType type;

    private LocalDateTime date;

    private UUID parentId;
}
