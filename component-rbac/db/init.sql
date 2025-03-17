drop database if exists component_rbac;

-- 创建数据库 component_rbac，设置默认字符集为 utf8mb4，校验规则为 utf8mb4_general_ci
create database component_rbac default character set utf8mb4 collate utf8mb4_general_ci;

set names utf8mb4;
set foreign_key_checks = 0;

use component_rbac;


-- ----------------------------
-- 租户表
-- ----------------------------
create table sys_tenant
(
    tenant_id         bigint    not null        comment '租户id',
    contact_user_name varchar(20)                   comment '联系人',
    contact_phone     varchar(20)                   comment '联系电话',
    company_name      varchar(30)                   comment '企业名称',
    license_number    varchar(30)                   comment '统一社会信用代码',
    address           varchar(200)                  comment '地址',
    intro             varchar(200)                  comment '企业简介',
    domain            varchar(200)                  comment '域名',
    remark            varchar(200)                  comment '备注',
    expire_time       datetime                      comment '过期时间',
    account_count     int           default -1      comment '用户数量（-1不限制）',
    disabled_flag tinyint NOT NULL DEFAULT 0 COMMENT '是否禁用',
    deleted_flag tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
    create_dept       bigint                    comment '创建部门',
    create_by         bigint                    comment '创建者',
    update_by         bigint                    comment '更新者',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    create_time datetime not null default current_timestamp comment '创建时间',
    primary key (tenant_id)
) engine=innodb comment = '租户表';

-- ----------------------------
-- 租户套餐表
-- ----------------------------
create table sys_tenant_package (
    tenant_package_id       bigint     not null    comment '租户套餐id',
    package_name            varchar(20)                comment '套餐名称',
    remark                  varchar(200)               comment '备注',
    menu_check_strictly     tinyint     default 1   comment '菜单树选择项是否关联显示',
    disabled_flag tinyint NOT NULL DEFAULT 0 COMMENT '是否禁用',
    deleted_flag tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
    create_dept             bigint                 comment '创建部门',
    create_by               bigint                 comment '创建者',
    update_by               bigint                 comment '更新者',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    create_time datetime not null default current_timestamp comment '创建时间',
    primary key (tenant_package_id)
) engine=innodb comment = '租户套餐表';

-- ----------------------------
-- 租户套餐和菜单关联表  租户套餐1-N菜单
-- ----------------------------
create table sys_tenant_package_menu (
   tenant_package_id   bigint not null comment '租户套餐ID',
   menu_id   bigint not null comment '菜单ID',
   primary key(tenant_package_id,menu_id)
) engine=innodb comment = '租户套餐和菜单关联表';


-- ----------------------------
-- 租户和租户套餐关联表  租户1-N租户套餐
-- ----------------------------
create table sys_tenant_package_ref (
    tenant_package_id   bigint not null comment '租户套餐ID',
    tenant_id   bigint not null comment '租户ID',
    primary key(tenant_package_id,tenant_id)
) engine=innodb comment = '租户和租户套餐关联表';

-- ----------------------------
-- 部门表
-- ----------------------------
create table sys_dept (
    dept_id           bigint      not null                   comment '部门id',
    tenant_id         bigint      not null                   comment '租户id',
    parent_id         bigint      default 0                  comment '父部门id',
    ancestors         varchar(500)    default ''                 comment '祖级列表',
    dept_name         varchar(30)     default ''                 comment '部门名称',
    dept_category     varchar(100)    default null               comment '部门类别编码',
    order_num         int          default 0                  comment '显示顺序',
    leader            bigint      default null               comment '负责人',
    phone             varchar(11)     default null               comment '联系电话',
    email             varchar(50)     default null               comment '邮箱',
    disabled_flag tinyint NOT NULL DEFAULT 0 COMMENT '是否禁用',
    deleted_flag tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
    create_dept       bigint      default null               comment '创建部门',
    create_by         bigint      default null               comment '创建者',
    update_by         bigint      default null               comment '更新者',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    create_time datetime not null default current_timestamp comment '创建时间',
    primary key (dept_id)
) engine=innodb comment = '部门表';

-- ----------------------------
-- 用户信息表
-- ----------------------------
create table sys_user (
    user_id           bigint      not null                   comment '用户ID',
    tenant_id         bigint      not null                   comment '租户id',
    dept_id           bigint      default null               comment '部门ID',
    user_name         varchar(30)     not null                   comment '用户账号',
    nick_name         varchar(30)     not null                   comment '用户昵称',
    user_type         varchar(10)     default 'sys_user'         comment '用户类型（sys_user系统用户）',
    email             varchar(50)     default ''                 comment '用户邮箱',
    phone             varchar(11)     default ''                 comment '手机号码',
    gender              tinyint NOT NULL DEFAULT 0 COMMENT '性别',
    avatar            bigint                                 comment '头像地址',
    password          varchar(100)    default ''                 comment '密码',
    disabled_flag       tinyint NOT NULL DEFAULT 0 COMMENT '是否禁用',
    deleted_flag        tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
    login_ip          varchar(128)    default ''                 comment '最后登录IP',
    login_date        datetime                                   comment '最后登录时间',
    create_dept       bigint      default null               comment '创建部门',
    create_by         bigint      default null               comment '创建者',
    update_by         bigint      default null               comment '更新者',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    create_time datetime not null default current_timestamp comment '创建时间',
    remark            varchar(500)    default null               comment '备注',
    primary key (user_id)
) engine=innodb comment = '用户信息表';

-- ----------------------------
-- 岗位信息表
-- ----------------------------
create table sys_post
(
    post_id       bigint      not null                   comment '岗位ID',
    tenant_id     bigint      not null                   comment '租户id',
    dept_id       bigint      not null                   comment '部门id',
    post_code     varchar(64)     not null                   comment '岗位编码',
    post_category varchar(100)    default null               comment '岗位类别编码',
    post_name     varchar(50)     not null                   comment '岗位名称',
    post_sort     int          not null                   comment '显示顺序',
    disabled_flag tinyint NOT NULL DEFAULT 0 COMMENT '是否禁用',
    create_dept   bigint      default null               comment '创建部门',
    create_by     bigint      default null               comment '创建者',
    update_by     bigint      default null               comment '更新者',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    create_time datetime not null default current_timestamp comment '创建时间',
    remark        varchar(500)    default null               comment '备注',
    primary key (post_id)
) engine=innodb comment = '岗位信息表';

-- ----------------------------
-- 角色信息表
-- ----------------------------
create table sys_role (
    role_id              bigint      not null                   comment '角色ID',
    tenant_id            bigint      not null                   comment '租户id',
    role_name            varchar(30)     not null                   comment '角色名称',
    role_key             varchar(100)    not null                   comment '角色权限字符串',
    role_sort            int          not null                   comment '显示顺序',
    menu_check_strictly  tinyint      default 1                  comment '菜单树选择项是否关联显示',
    dept_check_strictly  tinyint      default 1                  comment '部门树选择项是否关联显示',
    disabled_flag tinyint NOT NULL DEFAULT 0 COMMENT '是否禁用',
    deleted_flag         tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
    create_dept          bigint      default null               comment '创建部门',
    create_by            bigint      default null               comment '创建者',
    update_by            bigint      default null               comment '更新者',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    create_time datetime not null default current_timestamp comment '创建时间',
    remark               varchar(500)    default null               comment '备注',
    primary key (role_id)
) engine=innodb comment = '角色信息表';

create table sys_role_data_scope  (
  role_data_scope_id bigint not null auto_increment,
  data_scope_type int not null comment '数据范围类型',
  view_type int not null comment '数据可见范围类型',
  role_id bigint not null comment '角色id',
  update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  create_time datetime not null default current_timestamp comment '创建时间',
  primary key (role_data_scope_id)
) engine=innodb comment = '角色的数据范围';


-- ----------------------------
-- 菜单权限表
-- ----------------------------
create table sys_menu (
    menu_id           bigint      not null                   comment '菜单ID',
    menu_name         varchar(50)     not null                   comment '菜单名称',
    parent_id         bigint      default 0                  comment '父菜单ID',
    order_num         int          default 0                  comment '显示顺序',
    path              varchar(200)    default ''                 comment '路由地址',
    component         varchar(255)    default null               comment '组件路径',
    query_param       varchar(255)    default null               comment '路由参数',
    frame_flag          tinyint NOT NULL DEFAULT 0 COMMENT '是否为外链',
    cache_flag          tinyint NOT NULL DEFAULT 0 COMMENT '是否缓存',
    menu_type           int NOT NULL COMMENT '类型',
    visible_flag        tinyint NOT NULL DEFAULT 1 COMMENT '显示状态',
    disabled_flag       tinyint NOT NULL DEFAULT 0 COMMENT '是否禁用',
    perms             varchar(100)    default null               comment '权限标识',
    icon              varchar(100)    default '#'                comment '菜单图标',
    create_dept       bigint      default null               comment '创建部门',
    create_by         bigint      default null               comment '创建者',
    update_by         bigint      default null               comment '更新者',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    create_time datetime not null default current_timestamp comment '创建时间',
    remark            varchar(500)    default ''                 comment '备注',
    primary key (menu_id)
) engine=innodb comment = '菜单权限表';


-- ----------------------------
-- 用户和角色关联表  用户N-1角色
-- ----------------------------
create table sys_user_role (
    user_id   bigint not null comment '用户ID',
    role_id   bigint not null comment '角色ID',
    tenant_id         bigint      not null                   comment '租户id',
    primary key(tenant_id,user_id, role_id)
) engine=innodb comment = '用户和角色关联表';

-- ----------------------------
-- 角色和菜单关联表  角色1-N菜单
-- ----------------------------
create table sys_role_menu (
    role_id   bigint not null comment '角色ID',
    menu_id   bigint not null comment '菜单ID',
    tenant_id         bigint      not null                   comment '租户id',
    primary key(tenant_id,role_id, menu_id)
) engine=innodb comment = '角色和菜单关联表';


-- ----------------------------
-- 角色和部门关联表  角色1-N部门
-- ----------------------------
create table sys_role_dept (
    role_id   bigint not null comment '角色ID',
    dept_id   bigint not null comment '部门ID',
    tenant_id         bigint      not null                   comment '租户id',
    primary key(tenant_id,role_id, dept_id)
) engine=innodb comment = '角色和部门关联表';

-- ----------------------------
-- 用户与岗位关联表  用户1-N岗位
-- ----------------------------
create table sys_user_post
(
    user_id   bigint not null comment '用户ID',
    post_id   bigint not null comment '岗位ID',
    tenant_id         bigint      not null                   comment '租户id',
    primary key (tenant_id,user_id, post_id)
) engine=innodb comment = '用户与岗位关联表';