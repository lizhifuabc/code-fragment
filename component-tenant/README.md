# Redis 多租户组件

## 项目说明
本组件实现了 Redis 多租户数据隔离功能，支持以下隔离策略：
- 键前缀隔离
- 数据库隔离
- 实例隔离
- 哨兵模式
- 集群模式

## 使用方式
1. 引入依赖
2. 配置租户信息
3. 接入拦截器

## 核心类说明
- TenantContextHolder: 租户上下文管理
- TenantInterceptor: 租户信息拦截器
- RedisConfiguration: Redis 配置类
- TenantConfigService: 租户配置服务