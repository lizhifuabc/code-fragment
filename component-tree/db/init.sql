drop database if exists component_tree;

-- 创建数据库 component_tree，设置默认字符集为 utf8mb4，校验规则为 utf8mb4_general_ci
create database component_tree default character set utf8mb4 collate utf8mb4_general_ci;

set names utf8mb4;
set foreign_key_checks = 0;

use component_tree;

-- ----------------------------
-- 1. 邻接表模型 (Adjacency List Model)
-- 最简单的树形结构实现，每个节点记录其父节点ID
-- ----------------------------
create table tree_adjacency_list
(
    id          bigint       not null auto_increment comment '节点ID',
    parent_id   bigint       comment '父节点ID，根节点为null',
    name        varchar(100) not null comment '节点名称',
    level       int          not null comment '节点层级，根节点为0',
    description varchar(200) comment '节点描述',
    disabled_flag tinyint not null default 0 comment '是否禁用',
    deleted_flag tinyint not null default 0 comment '是否删除',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    create_time datetime not null default current_timestamp comment '创建时间',
    primary key (id),
    index idx_parent_id (parent_id)
) engine=innodb comment = '邻接表模型';

-- ----------------------------
-- 2. 路径枚举模型 (Path Enumeration Model)
-- 每个节点存储从根到当前节点的完整路径
-- ----------------------------
create table tree_path_enumeration
(
    id          bigint       not null auto_increment comment '节点ID',
    path        varchar(255) not null comment '路径，格式如：/1/2/3/',
    name        varchar(100) not null comment '节点名称',
    level       int          not null comment '节点层级，根节点为0',
    description varchar(200) comment '节点描述',
    disabled_flag tinyint not null default 0 comment '是否禁用',
    deleted_flag tinyint not null default 0 comment '是否删除',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    create_time datetime not null default current_timestamp comment '创建时间',
    primary key (id),
    index idx_path (path)
) engine=innodb comment = '路径枚举模型';

-- ----------------------------
-- 3. 嵌套集模型 (Nested Set Model)
-- 使用左值和右值表示节点在树中的位置
-- ----------------------------
create table tree_nested_set
(
    id          bigint       not null auto_increment comment '节点ID',
    name        varchar(100) not null comment '节点名称',
    lft         int          not null comment '左值',
    rgt         int          not null comment '右值',
    level       int          not null comment '节点层级，根节点为0',
    description varchar(200) comment '节点描述',
    disabled_flag tinyint not null default 0 comment '是否禁用',
    deleted_flag tinyint not null default 0 comment '是否删除',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    create_time datetime not null default current_timestamp comment '创建时间',
    primary key (id),
    index idx_lft_rgt (lft, rgt)
) engine=innodb comment = '嵌套集模型';

-- ----------------------------
-- 4. 闭包表模型 (Closure Table Model)
-- 存储树中所有可能的路径
-- ----------------------------
create table tree_closure_node
(
    id          bigint       not null auto_increment comment '节点ID',
    name        varchar(100) not null comment '节点名称',
    level       int          not null comment '节点层级，根节点为0',
    description varchar(200) comment '节点描述',
    disabled_flag tinyint not null default 0 comment '是否禁用',
    deleted_flag tinyint not null default 0 comment '是否删除',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    create_time datetime not null default current_timestamp comment '创建时间',
    primary key (id)
) engine=innodb comment = '闭包表模型-节点表';

create table tree_closure_path
(
    ancestor_id   bigint not null comment '祖先节点ID',
    descendant_id bigint not null comment '后代节点ID',
    distance      int    not null comment '距离，0表示自己',
    primary key (ancestor_id, descendant_id),
    index idx_descendant (descendant_id)
) engine=innodb comment = '闭包表模型-路径表';

-- ----------------------------
-- 5. 物化路径模型 (Materialized Path Model)
-- 类似路径枚举，但使用特定分隔符和固定长度存储
-- ----------------------------
create table tree_materialized_path
(
    id          bigint       not null auto_increment comment '节点ID',
    path        varchar(255) not null comment '物化路径，格式如：001.002.003',
    name        varchar(100) not null comment '节点名称',
    level       int          not null comment '节点层级，根节点为0',
    description varchar(200) comment '节点描述',
    disabled_flag tinyint not null default 0 comment '是否禁用',
    deleted_flag tinyint not null default 0 comment '是否删除',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    create_time datetime not null default current_timestamp comment '创建时间',
    primary key (id),
    index idx_path (path)
) engine=innodb comment = '物化路径模型';