# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table usuario_dto (
  id                            bigint auto_increment not null,
  nick                          varchar(255),
  age                           integer,
  version                       bigint not null,
  when_created                  timestamp not null,
  when_updated                  timestamp not null,
  constraint pk_usuario_dto primary key (id)
);


# --- !Downs

drop table if exists usuario_dto;

