-- apply changes
create table rc_hotbars (
  id                            integer auto_increment not null,
  name                          varchar(128),
  display_name                  varchar(128),
  holder_id                     integer,
  position                      integer not null,
  active                        tinyint(1) default 0 not null,
  constraint pk_rc_hotbars primary key (id)
);

create table rc_hotbars_holders (
  id                            integer auto_increment not null,
  player                        varchar(255),
  player_id                     varchar(40),
  active_hotbar                 integer not null,
  constraint uq_rc_hotbars_holders_player_id unique (player_id),
  constraint pk_rc_hotbars_holders primary key (id)
);

create table rc_hotbars_slots (
  id                            integer auto_increment not null,
  name                          varchar(128),
  item                          varchar(128),
  position                      integer not null,
  hotbar_id                     integer,
  constraint pk_rc_hotbars_slots primary key (id)
);

create table rc_hotbars_slots_data (
  id                            integer auto_increment not null,
  slot_id                       integer,
  data_key                      varchar(255),
  data_value                    varchar(255),
  constraint pk_rc_hotbars_slots_data primary key (id)
);

create index ix_rc_hotbars_holder_id on rc_hotbars (holder_id);
alter table rc_hotbars add constraint fk_rc_hotbars_holder_id foreign key (holder_id) references rc_hotbars_holders (id) on delete restrict on update restrict;

create index ix_rc_hotbars_slots_hotbar_id on rc_hotbars_slots (hotbar_id);
alter table rc_hotbars_slots add constraint fk_rc_hotbars_slots_hotbar_id foreign key (hotbar_id) references rc_hotbars (id) on delete restrict on update restrict;

create index ix_rc_hotbars_slots_data_slot_id on rc_hotbars_slots_data (slot_id);
alter table rc_hotbars_slots_data add constraint fk_rc_hotbars_slots_data_slot_id foreign key (slot_id) references rc_hotbars_slots (id) on delete restrict on update restrict;

