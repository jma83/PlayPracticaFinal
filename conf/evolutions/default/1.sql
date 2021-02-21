# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table ingredient (
  id                            bigint auto_increment not null,
  title_xml                     varchar(255),
  name                          varchar(255),
  description                   varchar(255),
  quantity                      float,
  measure                       varchar(255),
  version                       bigint not null,
  when_created                  timestamp not null,
  when_updated                  timestamp not null,
  constraint pk_ingredient primary key (id)
);

create table ingredient_tag (
  ingredient_id                 bigint not null,
  tag_id                        bigint not null,
  constraint pk_ingredient_tag primary key (ingredient_id,tag_id)
);

create table recipe (
  id                            bigint auto_increment not null,
  title_xml                     varchar(255),
  name                          varchar(255),
  description                   varchar(255),
  visibility                    boolean,
  author_id                     bigint,
  version                       bigint not null,
  when_created                  timestamp not null,
  when_updated                  timestamp not null,
  constraint pk_recipe primary key (id)
);

create table recipe_tag (
  recipe_id                     bigint not null,
  tag_id                        bigint not null,
  constraint pk_recipe_tag primary key (recipe_id,tag_id)
);

create table recipe_ingredient (
  recipe_id                     bigint not null,
  ingredient_id                 bigint not null,
  constraint pk_recipe_ingredient primary key (recipe_id,ingredient_id)
);

create table recipe_book (
  id                            bigint auto_increment not null,
  title_xml                     varchar(255),
  name                          varchar(255),
  review                        varchar(255),
  version                       bigint not null,
  when_created                  timestamp not null,
  when_updated                  timestamp not null,
  constraint pk_recipe_book primary key (id)
);

create table recipe_book_recipe (
  recipe_book_id                bigint not null,
  recipe_id                     bigint not null,
  constraint pk_recipe_book_recipe primary key (recipe_book_id,recipe_id)
);

create table tag (
  id                            bigint auto_increment not null,
  title_xml                     varchar(255),
  tag_name                      varchar(255),
  version                       bigint not null,
  when_created                  timestamp not null,
  when_updated                  timestamp not null,
  constraint pk_tag primary key (id)
);

create table user (
  id                            bigint auto_increment not null,
  title_xml                     varchar(255),
  username                      varchar(255),
  email                         varchar(255),
  birthdate                     timestamp,
  age                           integer,
  country                       varchar(255),
  language                      varchar(255),
  recipe_book_id                bigint,
  user_token_id                 bigint,
  version                       bigint not null,
  when_created                  timestamp not null,
  when_updated                  timestamp not null,
  constraint uq_user_recipe_book_id unique (recipe_book_id),
  constraint uq_user_user_token_id unique (user_token_id),
  constraint pk_user primary key (id)
);

create table user_token (
  id                            bigint auto_increment not null,
  title_xml                     varchar(255),
  token                         varchar(255),
  visible                       boolean,
  version                       bigint not null,
  when_created                  timestamp not null,
  when_updated                  timestamp not null,
  constraint pk_user_token primary key (id)
);

create index ix_ingredient_tag_ingredient on ingredient_tag (ingredient_id);
alter table ingredient_tag add constraint fk_ingredient_tag_ingredient foreign key (ingredient_id) references ingredient (id) on delete restrict on update restrict;

create index ix_ingredient_tag_tag on ingredient_tag (tag_id);
alter table ingredient_tag add constraint fk_ingredient_tag_tag foreign key (tag_id) references tag (id) on delete restrict on update restrict;

create index ix_recipe_author_id on recipe (author_id);
alter table recipe add constraint fk_recipe_author_id foreign key (author_id) references user (id) on delete restrict on update restrict;

create index ix_recipe_tag_recipe on recipe_tag (recipe_id);
alter table recipe_tag add constraint fk_recipe_tag_recipe foreign key (recipe_id) references recipe (id) on delete restrict on update restrict;

create index ix_recipe_tag_tag on recipe_tag (tag_id);
alter table recipe_tag add constraint fk_recipe_tag_tag foreign key (tag_id) references tag (id) on delete restrict on update restrict;

create index ix_recipe_ingredient_recipe on recipe_ingredient (recipe_id);
alter table recipe_ingredient add constraint fk_recipe_ingredient_recipe foreign key (recipe_id) references recipe (id) on delete restrict on update restrict;

create index ix_recipe_ingredient_ingredient on recipe_ingredient (ingredient_id);
alter table recipe_ingredient add constraint fk_recipe_ingredient_ingredient foreign key (ingredient_id) references ingredient (id) on delete restrict on update restrict;

create index ix_recipe_book_recipe_recipe_book on recipe_book_recipe (recipe_book_id);
alter table recipe_book_recipe add constraint fk_recipe_book_recipe_recipe_book foreign key (recipe_book_id) references recipe_book (id) on delete restrict on update restrict;

create index ix_recipe_book_recipe_recipe on recipe_book_recipe (recipe_id);
alter table recipe_book_recipe add constraint fk_recipe_book_recipe_recipe foreign key (recipe_id) references recipe (id) on delete restrict on update restrict;

alter table user add constraint fk_user_recipe_book_id foreign key (recipe_book_id) references recipe_book (id) on delete restrict on update restrict;

alter table user add constraint fk_user_user_token_id foreign key (user_token_id) references user_token (id) on delete restrict on update restrict;


# --- !Downs

alter table ingredient_tag drop constraint if exists fk_ingredient_tag_ingredient;
drop index if exists ix_ingredient_tag_ingredient;

alter table ingredient_tag drop constraint if exists fk_ingredient_tag_tag;
drop index if exists ix_ingredient_tag_tag;

alter table recipe drop constraint if exists fk_recipe_author_id;
drop index if exists ix_recipe_author_id;

alter table recipe_tag drop constraint if exists fk_recipe_tag_recipe;
drop index if exists ix_recipe_tag_recipe;

alter table recipe_tag drop constraint if exists fk_recipe_tag_tag;
drop index if exists ix_recipe_tag_tag;

alter table recipe_ingredient drop constraint if exists fk_recipe_ingredient_recipe;
drop index if exists ix_recipe_ingredient_recipe;

alter table recipe_ingredient drop constraint if exists fk_recipe_ingredient_ingredient;
drop index if exists ix_recipe_ingredient_ingredient;

alter table recipe_book_recipe drop constraint if exists fk_recipe_book_recipe_recipe_book;
drop index if exists ix_recipe_book_recipe_recipe_book;

alter table recipe_book_recipe drop constraint if exists fk_recipe_book_recipe_recipe;
drop index if exists ix_recipe_book_recipe_recipe;

alter table user drop constraint if exists fk_user_recipe_book_id;

alter table user drop constraint if exists fk_user_user_token_id;

drop table if exists ingredient;

drop table if exists ingredient_tag;

drop table if exists recipe;

drop table if exists recipe_tag;

drop table if exists recipe_ingredient;

drop table if exists recipe_book;

drop table if exists recipe_book_recipe;

drop table if exists tag;

drop table if exists user;

drop table if exists user_token;

