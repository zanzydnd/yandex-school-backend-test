package kozlov.yandex.backend.school.yandexbackend.model;

import kozlov.yandex.backend.school.yandexbackend.enums.ShopUnitType;
import kozlov.yandex.backend.school.yandexbackend.exception.BusinessLogicException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.procedure.spi.ParameterRegistrationImplementor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Type(type = "pg-uuid")
    private UUID id;

    @NotNull
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private ShopUnitModel parent;

    private Long price;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ShopUnitType type;

    private LocalDateTime date;
    @Transient
    private UUID parentId;

    @PrePersist
    public void validate() {

        if (parent != null && !ShopUnitType.CATEGORY.equals(parent.getType())) {
            throw new BusinessLogicException("Offer cant be a parent");
        }

        if (ShopUnitType.CATEGORY.equals(type) && price != null)
            throw new BusinessLogicException("cant set price for category");

        if (ShopUnitType.OFFER.equals(type) && (price == null || price < 0))
            throw new BusinessLogicException("invalid price for offer");
    }

    }
