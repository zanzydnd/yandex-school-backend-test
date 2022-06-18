create table shop_unit
(
    id         varchar(255) not null,
    created_at timestamp,
    name       varchar(255) not null,
    price      int4,
    type       varchar(255) not null,
    updated_at timestamp,
    parent_id  varchar(255),
    primary key (id)
)
alter table if exists shop_unit add constraint FKl2nx2b5fsamdqyir6lif6n7as foreign key (parent_id) references shop_unit;
