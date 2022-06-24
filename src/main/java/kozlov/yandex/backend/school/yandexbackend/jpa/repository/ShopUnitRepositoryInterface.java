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
            value = "insert into shop_unit_history (date, name, price, type, origin_id, parent_id)\n" +
                    "\n" +
                    "\n" +
                    "    (select sp.date,\n" +
                    "            sp.name,\n" +
                    "            (\n" +
                    "                (\n" +
                    "                    WITH RECURSIVE unit_tree as (\n" +
                    "                        SELECT s1.id,\n" +
                    "                               s1.name,\n" +
                    "                               s1.price,\n" +
                    "                               s1.parent_id,\n" +
                    "                               s1.type,\n" +
                    "                               s1.date,\n" +
                    "                               0                       as level,\n" +
                    "                               array [s1.id] as path\n" +
                    "                        FROM shop_unit s1\n" +
                    "                        WHERE s1.id = sp.id\n" +
                    "\n" +
                    "                        UNION ALL\n" +
                    "\n" +
                    "                        SELECT s2.id,\n" +
                    "                               s2.name,\n" +
                    "                               s2.price,\n" +
                    "                               s2.parent_id,\n" +
                    "                               s2.type,\n" +
                    "                               s2.date,\n" +
                    "                               level + 1,\n" +
                    "                               ut.path || s2.id as path --generate the path for every unit so that we can check if it is a child of another element\n" +
                    "                        FROM shop_unit s2\n" +
                    "                                 JOIN unit_tree ut ON ut.id = s2.parent_id\n" +
                    "                    )\n" +
                    "                    SELECT case when ut.type = 'CATEGORY' then ap.avg_price else ut.price end as price\n" +
                    "                    FROM unit_tree ut\n" +
                    "                             LEFT JOIN LATERAL (\n" +
                    "                        SELECT avg(ut2.price) avg_price\n" +
                    "                        FROM unit_tree ut2\n" +
                    "                        WHERE ut.level < ut2.level\n" +
                    "                          and ut.id = any (path)\n" +
                    "                        GROUP BY ut.id\n" +
                    "                        ) ap ON TRUE\n" +
                    "                    where ut.level = 0\n" +
                    "                )\n" +
                    "            ) as price,\n" +
                    "            sp.type,\n" +
                    "            sp.id,\n" +
                    "            sp.parent_id\n" +
                    "     from shop_unit sp\n" +
                    "     where id in :ids" +
                    "    );",
            nativeQuery = true
    )
    int migrateToHistory(@Param("ids") List<UUID> ids);

    @Modifying
    @Transactional
    @Query(value = "update shop_unit set date=:updateDate where id in :ids", nativeQuery = true)
    int updateDateByIds(@Param("ids") List<UUID> ids, @Param("updateDate") LocalDateTime updateDate);

    @Query(value = "WITH RECURSIVE unit_tree as (\n" +
            "    SELECT s1.id,\n" +
            "           s1.name,\n" +
            "           s1.price,\n" +
            "           s1.parent_id,\n" +
            "           s1.type,\n" +
            "           s1.date,\n" +
            "           0 as level,\n" +
            "           array [s1.id] as path\n" +
            "    FROM shop_unit s1\n" +
            "    WHERE s1.id = :headId\n" +
            "\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT s2.id,\n" +
            "           s2.name,\n" +
            "           s2.price,\n" +
            "           s2.parent_id,\n" +
            "           s2.type,\n" +
            "           s2.date,\n" +
            "           level + 1,\n" +
            "           ut.path || s2.id as path --generate the path for every unit so that we can check if it is a child of another element\n" +
            "    FROM shop_unit s2\n" +
            "             JOIN unit_tree ut ON ut.id = s2.parent_id\n" +
            ")\n" +
            "\n" +
            "SELECT cast(ut.id as varchar) as id ,\n" +
            "       ut.name as name,\n" +
            "       cast(ut.parent_id as varchar ) as parent_id,\n" +
            "       ut.type as type ,\n" +
            "       case when ut.type = 'CATEGORY' then ap.avg_price else ut.price end as price,\n" +
            "       ut.level as level,\n" +
            "       ut.date ,\n" +
            "       ut.path \n" +
            "\n" +
            "FROM unit_tree ut\n" +
            "         -- The JOIN LATERAL subquery roughly means \"for each row of ut run this query\"\n" +
            "         -- Must be a LEFT JOIN LATERAL in order to keep rows of ut that have no children.\n" +
            "         LEFT JOIN LATERAL (\n" +
            "    SELECT avg(ut2.price) avg_price\n" +
            "    FROM unit_tree ut2\n" +
            "    WHERE ut.level < ut2.level \n" +
            "      and ut.id = any (path) \n" +
            "    GROUP BY ut.id\n" +
            "    ) ap ON TRUE\n" +
            "\n" +
            "ORDER BY level;", nativeQuery = true)
    List<Map<String, Object>> getAllWithChildrenAndAveragePrice(@Param("headId") UUID id);

    @Query(
            nativeQuery = true,
            value = "with RECURSIVE name_tree as\n" +
                    "                   (\n" +
                    "                       SELECT id, parent_id\n" +
                    "                       FROM shop_unit\n" +
                    "                       WHERE id IN (\n" +
                    "                           SELECT parent_id\n" +
                    "                           FROM shop_unit\n" +
                    "                           WHERE id in :ids)\n" +
                    "\n" +
                    "                       union all\n" +
                    "\n" +
                    "                       select c.id, c.parent_id\n" +
                    "                       from shop_unit c\n" +
                    "                                join name_tree p on C.id = P.parent_id\n" +
                    "                   )\n" +
                    "select distinct(cast(id as varchar)) \n" +
                    "from name_tree where id not in :ids ;"
    )
    List<UUID> getAllParents(@Param("ids") List<UUID> ids);

    @Query(value = "select * from shop_unit as shp where shp.date <= :before and shp.date>= :after and shp.type = 'OFFER'", nativeQuery = true)
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
    ShopUnitModel findAllUnitsByDateBetweenAndIdWithAvg(@Param("after") LocalDateTime after, @Param("before") LocalDateTime before, @Param("id") UUID id);

}
