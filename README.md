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

