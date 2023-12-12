create table if not exists subscriber
(
    id                  bigint primary key,
    bot_state           varchar(50),
    first_name          varchar(100),
    count_equipment     numeric not null,
    day_mailing         numeric not null,
    deleted             boolean not null,
    registration        timestamp not null,
    activity            timestamp
);