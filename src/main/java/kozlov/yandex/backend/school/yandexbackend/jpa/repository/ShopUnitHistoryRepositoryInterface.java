package kozlov.yandex.backend.school.yandexbackend.jpa.repository;

import kozlov.yandex.backend.school.yandexbackend.model.ShopUnitHistoryModel;
import kozlov.yandex.backend.school.yandexbackend.model.ShopUnitModel;
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
    List<ShopUnitHistoryModel> findAllByOriginId(UUID originId);

    @Query(value = """ 
            select shp.id,
                   cast(shp.origin_id as varchar) as origin_id,
                   cast(shp.parent_id as varchar) as parent_id,
                   shp.type,
                   shp.date,
                   shp.price,
                   shp.name
            from (select shp1.id,
                   cast(shp1.origin_id as varchar) as origin_id,
                   cast(shp1.parent_id as varchar) as parent_id,
                   shp1.type,
                   shp1.date,
                   shp1.price,
                   shp1.name,
                   row_number() over(partition by shp1.origin_id
                                    order by shp1.date desc) seq
                   from shop_unit_history as shp1
                   where shp1.date <= :before
                    and shp1.date >= :after
                    and shp1.type = 'OFFER'
                    and shp1.origin_id NOT IN :ids
                ) as shp
            where seq = 1;
            """
            , nativeQuery = true)
    List<ShopUnitHistoryModel> findAllUnitsHistoryByDateBetween(@Param("after") LocalDateTime after, @Param("before") LocalDateTime before, @Param("ids") List<UUID> ids);

    @Query(value = "select id, cast(shp.origin_id as varchar) as origin_id, cast(shp.parent_id as varchar) as parent_id, type, shp.date, shp.price, shp.name from shop_unit_history as shp where shp.date < :before and shp.date >= :after and shp.origin_id = :id order by shp.date", nativeQuery = true)
    List<ShopUnitHistoryModel> findAllUnitsByDateAndIdBetween(@Param("after") LocalDateTime after, @Param("before") LocalDateTime before, @Param("id") UUID id);
}
// http://127.0.0.1:8080/node/3fa85f64-5717-4562-b3fc-2c963f66a111/statistic?dateStart=2019-12-01T10:05:23.653Z&dateEnd=2020-12-02T10:05:23.653Z