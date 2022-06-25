package kozlov.yandex.backend.school.yandexbackend.jpa.repository;

import kozlov.yandex.backend.school.yandexbackend.dto.RecursiveShopUnitDto;
import kozlov.yandex.backend.school.yandexbackend.dto.ReturnSalesDto;
import kozlov.yandex.backend.school.yandexbackend.model.ShopUnitModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShopUnitRepositoryInterface extends JpaSpecificationExecutor<ShopUnitModel>, JpaRepository<ShopUnitModel, UUID> {

//    @Modifying
//    @Transactional
//    @Query(value = "insert into shop_unit_history (date, name, price, type, origin_id,parent_id) (select shop_unit.date,\n" +
//            "                                                                           shop_unit.name,\n" +
//            "                                                                           shop_unit.price,\n" +
//            "                                                                           shop_unit.type,\n" +
//            "                                                                           shop_unit.id, \n" +
//            "                                                                           shop_unit.parent_id \n" +
//            "                                                                    from shop_unit\n" +
//            "                                                                    where shop_unit.id in :ids);", nativeQuery = true)
//    int migrateToHistory(@Param("ids") List<UUID> ids);

    @Transactional
    @Modifying
    @Query(
            value = """
                    insert into shop_unit_history (date, name, price, type, origin_id, parent_id)
                        (select sp.date,
                                sp.name,
                                (
                                    (
                                        WITH RECURSIVE unit_tree as (
                                            SELECT s1.id,
                                                   s1.name,
                                                   s1.price,
                                                   s1.parent_id,
                                                   s1.type,
                                                   s1.date,
                                                   0                       as level,
                                                   array [s1.id] as path
                                            FROM shop_unit s1
                                            WHERE s1.id = sp.id

                                            UNION ALL

                                            SELECT s2.id,
                                                   s2.name,
                                                   s2.price,
                                                   s2.parent_id,
                                                   s2.type,
                                                   s2.date,
                                                   level + 1,
                                                   ut.path || s2.id as path --generate the path for every unit so that we can check if it is a child of another element
                                            FROM shop_unit s2
                                                     JOIN unit_tree ut ON ut.id = s2.parent_id
                                        )
                                        SELECT case when ut.type = 'CATEGORY' then ap.avg_price else ut.price end as price
                                        FROM unit_tree ut
                                                 LEFT JOIN LATERAL (
                                            SELECT avg(ut2.price) avg_price
                                            FROM unit_tree ut2
                                            WHERE ut.level < ut2.level
                                              and ut.id = any (path)
                                            GROUP BY ut.id
                                            ) ap ON TRUE
                                        where ut.level = 0
                                    )
                                ) as price,
                                sp.type,
                                sp.id,
                                sp.parent_id
                         from shop_unit sp
                         where id in :ids    );""",
            nativeQuery = true
    )
    int migrateToHistory(@Param("ids") List<UUID> ids);

    @Modifying
    @Transactional
    @Query(value = "update shop_unit set date=:updateDate where id in :ids", nativeQuery = true)
    int updateDateByIds(@Param("ids") List<UUID> ids, @Param("updateDate") LocalDateTime updateDate);

    @Query(value = """
            WITH RECURSIVE unit_tree as (
                SELECT s1.id,
                       s1.name,
                       s1.price,
                       s1.parent_id,
                       s1.type,
                       s1.date,
                       0 as level,
                       array [s1.id] as path
                FROM shop_unit s1
                WHERE s1.id = :headId

                UNION ALL

                SELECT s2.id,
                       s2.name,
                       s2.price,
                       s2.parent_id,
                       s2.type,
                       s2.date,
                       level + 1,
                       ut.path || s2.id as path --generate the path for every unit so that we can check if it is a child of another element
                FROM shop_unit s2
                         JOIN unit_tree ut ON ut.id = s2.parent_id
            )

            SELECT cast(ut.id as varchar) as id ,
                   ut.name as name,
                   cast(ut.parent_id as varchar ) as parent_id,
                   ut.type as type ,
                   case when ut.type = 'CATEGORY' then ap.avg_price else ut.price end as price,
                   ut.level as level,
                   ut.date ,
                   ut.path

            FROM unit_tree ut
                     -- The JOIN LATERAL subquery roughly means "for each row of ut run this query"
                     -- Must be a LEFT JOIN LATERAL in order to keep rows of ut that have no children.
                     LEFT JOIN LATERAL (
                SELECT avg(ut2.price) avg_price
                FROM unit_tree ut2
                WHERE ut.level < ut2.level
                  and ut.id = any (path)
                GROUP BY ut.id
                ) ap ON TRUE

            ORDER BY level;""", nativeQuery = true)
    List<Map<String, Object>> getAllWithChildrenAndAveragePrice(@Param("headId") UUID id);

    @Query(
            nativeQuery = true,
            value = """
                    with RECURSIVE name_tree as
                                       (
                                           SELECT id, parent_id
                                           FROM shop_unit
                                           WHERE id IN (
                                               SELECT parent_id
                                               FROM shop_unit
                                               WHERE id in :ids)

                                           union all

                                           select c.id, c.parent_id
                                           from shop_unit c
                                                    join name_tree p on C.id = P.parent_id
                                       )
                    select distinct(cast(id as varchar))
                    from name_tree where id not in :ids ;"""
    )
    List<UUID> getAllParents(@Param("ids") List<UUID> ids);

    @Query(value = "select * from shop_unit as shp where shp.date <= :before and shp.date>= :after and shp.type = 'OFFER'", nativeQuery = true)
    @Transactional
    List<ShopUnitModel> findAllUnitsByDateBetween(@Param("after") LocalDateTime after, @Param("before") LocalDateTime before);

    @Query(
            value = """
                    WITH RECURSIVE unit_tree as (
                        SELECT s1.id,
                               s1.name,
                               s1.price,
                               s1.parent_id,
                               s1.type,
                               s1.date,
                               0 as level,
                               array [s1.id] as path
                        FROM shop_unit s1
                        WHERE s1.id = :id

                        UNION ALL

                        SELECT s2.id,
                               s2.name,
                               s2.price,
                               s2.parent_id,
                               s2.type,
                               s2.date,
                               level + 1,
                               ut.path || s2.id as path 
                        FROM shop_unit s2
                                 JOIN unit_tree ut ON ut.id = s2.parent_id
                    )

                    SELECT cast(ut.id as varchar) as id ,
                           ut.name as name,
                           cast(ut.parent_id as varchar) as parent_id,
                           ut.type as type ,
                           case when ut.type = 'CATEGORY' then ap.avg_price else ut.price end as price,
                           ut.date
                    FROM unit_tree ut
                             LEFT JOIN LATERAL (
                        SELECT avg(ut2.price) avg_price
                        FROM unit_tree ut2
                        WHERE ut.level < ut2.level
                          and ut.id = any (path)
                        GROUP BY ut.id
                        ) ap ON TRUE
                    where ut.level = 0 and ut.date >= :after and ut.date < :before ;""",
            nativeQuery = true
    )
    @Transactional
    ShopUnitModel findAllUnitsByDateBetweenAndIdWithAvg(@Param("after") LocalDateTime after, @Param("before") LocalDateTime before, @Param("id") UUID id);

}
