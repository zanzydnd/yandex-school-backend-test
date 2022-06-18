package kozlov.yandex.backend.school.yandexbackend.model;

import kozlov.yandex.backend.school.yandexbackend.enums.ShopUnitType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "ShopUnit")
public class ShopUnitModel {
    @Id
    @Type(type="pg-uuid")
    private UUID id;

    @NotNull
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private ShopUnitModel parent;

    private Long price;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ShopUnitType type;

    private Date date;

}
