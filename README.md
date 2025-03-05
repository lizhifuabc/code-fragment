# code-fragment

记录自己的代码

## Spring Boot 使用

[spring-boot-admin监控](spring-boot-admin) 

- 使用 https://github.com/codecentric/spring-boot-admin
- 使用 Nacos 

[spring-boot-redis 使用Redis](spring-boot-redis)

- guava
- Redis BitMap 工具类

[spring-boot-virtualthreads 虚拟线程](spring-boot-virtualthreads)

- 虚拟线程是在请求后立即创建和使用的，因为从资源的角度来看，它们非常便宜。
- 对于 CPU 密集型作，虚拟线程并不合适，因为此类任务所需的阻塞最少。

 [spring-boot-extension 内置扩展](spring-boot-extension) 

- CommonsRequestLoggingFilter 记录请求的参数、请求体、请求头和客户端信息。
- OncePerRequestFilter 确保在一次请求的生命周期内，无论请求 forwarding 或 including，过滤器逻辑只执行一次。避免重复处理请求或响应非常有用。
- ContentCachingRequestWrapper 请求包装器，用于缓存请求的输入流。允许多次读取请求体，多次处理请求数据（如日志记录和业务处理）时有用。
- ContentCachingResponseWrapper 响应包装器，用于缓存响应的输出流。允许在响应提交给客户端之前修改响应体，需要对响应内容进行后处理（如添加额外的头部信息、修改响应体）时非常有用。

 [spring-boot-init 常用初始化](spring-boot-init) 

单个 Bean 内部的初始化顺序 ：构造方法 → @PostConstruct 方法 → afterPropertiesSet() 方法。

@PostConstruct <---InitializingBean.afterPropertiesSet<---@Bean 的 initMethod<---SmartInitializingSingleton.afterSingletonsInstantiated<---@EventListener (如 ContextRefreshedEvent)<---ApplicationRunner.run<---CommandLineRunner.run<---ApplicationListener (如 ApplicationReadyEvent)

- @PostConstruct 依赖注入完成后立即执行，执行顺序较早
- InitializingBean 依赖注入完成后调用，执行顺序 < @PostConstruct
- initMethod 执行顺序在 `@PostConstruct` 和 `InitializingBean` 之后
- CommandLineRunner 应用完全启动后，执行顺序在所有 Spring 组件初始化完成之后。
- ApplicationRunner 执行顺序与 CommandLineRunner 相同
- @EventListener 适用于需要在特定生命周期事件发生时执行的初始化逻辑，以监听各种生命周期事件，如 `ContextRefreshedEvent`、`ContextClosedEvent` 等。
- SmartInitializingSingleton 在所有单例 bean 初始化完成后执行。适用于需要确保所有单例 bean 都已准备好时执行的初始化逻辑。
- ApplicationListener 用于在应用完全启动后执行逻辑。可以监听各种 Spring 事件，提供灵活的初始化时机。

 [spring-boot-log日志模块](spring-boot-log) 

- 结构化日志
- 自定义日志结构化
- MDC打印

 [spring-boot-minio](spring-boot-minio) 

- 文件分片上传

## 业务组件

 [component-tenant 多租户](component-tenant) 

redis 方案：

- 小型系统建议使用 Key前缀方案
- 中型系统可以考虑 Database方案
- 大型系统或企业级应用推荐独立实例或Cluster方案

 [component-idempotent 幂等](component-idempotent) 

- 前端传输 token 参数校验
- 使用用户标识、请求路径和请求方法的组合来生成幂等标识
- 业务完成之后自动删除 key
- TODO 处理SpEL表达式

 [component-rbac 后台管理系统](component-rbac) 

TODO

