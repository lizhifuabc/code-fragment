package io.github.lizhifuabc.tenant.config;

import io.github.lizhifuabc.tenant.context.TenantContextHolder;
import io.github.lizhifuabc.tenant.entity.TenantRedisConfig;
import io.github.lizhifuabc.tenant.enums.RedisTenantType;
import io.github.lizhifuabc.tenant.service.TenantConfigService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态 Redis 连接工厂
 * 支持多租户的动态 Redis 连接创建，可根据租户配置动态切换不同的 Redis 连接方式
 * 
 * 支持的租户模式：
 * 1. PREFIX: 使用 key 前缀区分租户数据
 * 2. DATABASE: 使用不同的 database 区分租户数据
 * 3. INSTANCE: 每个租户使用独立的 Redis 实例
 * 4. CLUSTER: 使用 Redis 集群模式
 * 5. SENTINEL: 使用 Redis 哨兵模式（待实现）
 *
 * @author lizhifu
 */
public class DynamicLettuceConnectionFactory implements RedisConnectionFactory, InitializingBean, DisposableBean {
    
    /**
     * 租户配置服务
     */
    private final TenantConfigService tenantConfigService;

    /**
     * 租户连接工厂缓存
     * key: 租户ID
     * value: 对应的连接工厂实例
     */
    private final Map<String, LettuceConnectionFactory> factoryCache = new ConcurrentHashMap<>();

    /**
     * 构造方法
     *
     * @param tenantConfigService 租户配置服务
     */
    public DynamicLettuceConnectionFactory(TenantConfigService tenantConfigService) {
        this.tenantConfigService = tenantConfigService;
    }

    /**
     * 获取 Redis 连接
     * 根据当前租户上下文获取对应的连接，如果连接不存在则创建新的连接
     *
     * @return Redis 连接
     * @throws IllegalStateException 如果无法获取租户ID或创建连接失败
     */
    @Override
    @NonNull
    public RedisConnection getConnection() {
        String tenantId = getTenantIdWithValidation();
        LettuceConnectionFactory factory = factoryCache.computeIfAbsent(tenantId, this::createConnectionFactory);
        RedisConnection connection = factory.getConnection();
        validateConnection(connection);
        return connection;
    }

    /**
     * 获取并验证租户ID
     *
     * @return 租户ID
     * @throws IllegalStateException 如果租户ID为空
     */
    private String getTenantIdWithValidation() {
        String tenantId = TenantContextHolder.getTenantId();
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new IllegalStateException("当前上下文中未找到租户ID");
        }
        return tenantId;
    }

    /**
     * 验证 Redis 连接
     *
     * @param connection Redis连接
     * @throws IllegalStateException 如果连接为空或无效
     */
    private void validateConnection(RedisConnection connection) {
        if (connection == null) {
            throw new IllegalStateException("Redis连接创建失败");
        }
        try {
            // 尝试进行简单的ping操作验证连接
            connection.ping();
        } catch (Exception e) {
            throw new IllegalStateException("Redis连接验证失败: " + e.getMessage());
        }
    }

    /**
     * 创建租户专用的连接工厂
     * 根据租户配置创建对应类型的连接工厂
     *
     * @param tenantId 租户ID
     * @return Lettuce连接工厂实例
     * @throws IllegalArgumentException 当租户配置的Redis类型不支持时抛出
     * @throws IllegalStateException 当连接工厂创建失败时抛出
     */
    private LettuceConnectionFactory createConnectionFactory(String tenantId) {
        try {
            TenantRedisConfig config = tenantConfigService.getTenantConfig(tenantId);
            if (config == null) {
                throw new IllegalStateException("未找到租户[" + tenantId + "]的Redis配置");
            }

            LettuceConnectionFactory factory;
            switch (config.getType()) {
                case PREFIX -> factory = createDefaultConnectionFactory(config);
                case DATABASE -> factory = createDatabaseConnectionFactory(config);
                case INSTANCE -> factory = createInstanceConnectionFactory(config);
                case CLUSTER -> factory = createClusterConnectionFactory(config);
                default -> throw new IllegalArgumentException("不支持的Redis租户类型");
            }

            factory.afterPropertiesSet();
            return factory;
        } catch (Exception e) {
            throw new IllegalStateException("创建租户[" + tenantId + "]的Redis连接工厂失败: " + e.getMessage(), e);
        }
    }

    /**
     * 销毁连接工厂
     * 清理所有租户的连接资源
     */
    @Override
    public void destroy() {
        for (Map.Entry<String, LettuceConnectionFactory> entry : factoryCache.entrySet()) {
            try {
                entry.getValue().destroy();
            } catch (Exception e) {
                // 记录日志但不中断清理过程
                System.err.println("清理租户[" + entry.getKey() + "]的Redis连接工厂失败: " + e.getMessage());
            }
        }
        factoryCache.clear();
    }

    /**
     * 获取 Redis 集群连接
     * 仅在租户配置为集群模式时可用
     *
     * @return Redis 集群连接
     * @throws UnsupportedOperationException 当租户未配置集群模式时抛出
     */
    @Override
    @NonNull
    public RedisClusterConnection getClusterConnection() {
        String tenantId = TenantContextHolder.getTenantId();
        TenantRedisConfig config = tenantConfigService.getTenantConfig(tenantId);
        
        if (config.getType() == RedisTenantType.CLUSTER) {
            LettuceConnectionFactory factory = factoryCache.computeIfAbsent(tenantId, this::createConnectionFactory);
            return factory.getClusterConnection();
        }
        
        throw new UnsupportedOperationException("当前租户未配置集群模式");
    }

    /**
     * 获取 Redis 哨兵连接
     * 仅在租户配置为哨兵模式时可用
     *
     * @return Redis 哨兵连接
     * @throws UnsupportedOperationException 当租户未配置哨兵模式时抛出
     */
    @Override
    @NonNull
    public RedisSentinelConnection getSentinelConnection() {
        String tenantId = TenantContextHolder.getTenantId();
        TenantRedisConfig config = tenantConfigService.getTenantConfig(tenantId);
        
        if (config.getType() == RedisTenantType.SENTINEL) {
            LettuceConnectionFactory factory = factoryCache.computeIfAbsent(tenantId, this::createConnectionFactory);
            return factory.getSentinelConnection();
        }
        
        throw new UnsupportedOperationException("当前租户未配置哨兵模式");
    }

    /**
     * 创建默认连接工厂
     * 用于 PREFIX 模式，使用配置文件中的 Redis 配置
     *
     * @param config 租户Redis配置
     * @return Lettuce连接工厂实例
     */
    private LettuceConnectionFactory createDefaultConnectionFactory(TenantRedisConfig config) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(config.getHost());
        configuration.setPort(config.getPort());
        configuration.setPassword(config.getPassword());
        configuration.setDatabase(config.getDatabase());
        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);
        factory.afterPropertiesSet();
        return factory;
    }

    /**
     * 创建独立实例模式的连接工厂
     * 每个租户使用独立的 Redis 实例
     *
     * @param config 租户Redis配置
     * @return Lettuce连接工厂实例
     */
    private LettuceConnectionFactory createInstanceConnectionFactory(TenantRedisConfig config) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(config.getHost());
        configuration.setPort(config.getPort());
        configuration.setPassword(config.getPassword());
        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);
        factory.afterPropertiesSet();
        return factory;
    }

    /**
     * 创建集群模式的连接工厂
     * 支持 Redis 集群配置
     *
     * @param config 租户Redis配置
     * @return Lettuce连接工厂实例
     */
    private LettuceConnectionFactory createClusterConnectionFactory(TenantRedisConfig config) {
        RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration();
        for (String node : config.getClusterNodes()) {
            String[] parts = node.split(":");
            clusterConfig.clusterNode(parts[0], Integer.parseInt(parts[1]));
        }
        LettuceConnectionFactory factory = new LettuceConnectionFactory(clusterConfig);
        factory.afterPropertiesSet();
        return factory;
    }

    /**
     * 异常转换
     * 将 Redis 异常转换为 Spring DAO 异常
     *
     * @param ex 运行时异常
     * @return 转换后的数据访问异常
     */
    @Override
    public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
        String tenantId = TenantContextHolder.getTenantId();
        LettuceConnectionFactory factory = factoryCache.get(tenantId);
        if (factory != null) {
            return factory.translateExceptionIfPossible(ex);
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() {
        // 仅做必要的初始化检查
        if (tenantConfigService == null) {
            throw new IllegalStateException("TenantConfigService must not be null");
        }
    }

    // 修改 createDatabaseConnectionFactory 方法，添加基础配置
    private LettuceConnectionFactory createDatabaseConnectionFactory(TenantRedisConfig config) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(config.getHost());
        configuration.setPort(config.getPort());
        configuration.setPassword(config.getPassword());
        configuration.setDatabase(config.getDatabase());
        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);
        factory.afterPropertiesSet();
        return factory;
    }

    /**
     * 获取管道和事务结果转换标志
     *
     * @return 是否转换管道和事务结果
     */
    @Override
    public boolean getConvertPipelineAndTxResults() {
        return true;
    }
}