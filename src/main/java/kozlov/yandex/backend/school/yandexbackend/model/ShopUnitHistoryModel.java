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

    @Column(name = "origin_id")
    private UUID originId;

    private Long price;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ShopUnitType type;

    private LocalDateTime date;

    @Column(name = "parent_id")
    private UUID parentId;
}
