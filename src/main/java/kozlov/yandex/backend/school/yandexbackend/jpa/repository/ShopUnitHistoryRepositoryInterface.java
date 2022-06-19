package kozlov.yandex.backend.school.yandexbackend.jpa.repository;

import kozlov.yandex.backend.school.yandexbackend.model.ShopUnitHistoryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShopUnitHistoryRepositoryInterface extends JpaSpecificationExecutor<ShopUnitHistoryModel>, JpaRepository<ShopUnitHistoryModel, Long> {
    @Query(value = "select * from shop_unit_history as shp where shp.date <= :before and shp.date>= :after and shp.type = 'OFFER' and shp.origin_id NOT IN :ids", nativeQuery = true)
    List<ShopUnitHistoryModel> findAllUnitsHistoryByDateBetween(@Param("after") LocalDateTime after, @Param("before") LocalDateTime before, @Param("ids") List<UUID> ids);

}
