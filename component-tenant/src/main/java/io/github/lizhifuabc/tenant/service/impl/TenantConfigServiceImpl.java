package io.github.lizhifuabc.tenant.service.impl;

import io.github.lizhifuabc.tenant.entity.TenantRedisConfig;
import io.github.lizhifuabc.tenant.enums.RedisTenantType;
import io.github.lizhifuabc.tenant.service.TenantConfigService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 租户配置服务实现类
 *
 * @author lizhifu
 */
@Service
public class TenantConfigServiceImpl implements TenantConfigService {
    
    @Value("${spring.data.redis.host}")
    private String redisHost;
    
    @Value("${spring.data.redis.port}")
    private int redisPort;
    
    @Value("${spring.data.redis.database}")
    private int redisDatabase;
    
    @Value("${spring.data.redis.password}")
    private String redisPassword;
    
    private final Map<String, TenantRedisConfig> tenantConfigs = new HashMap<>();

    @Override
    public TenantRedisConfig getTenantConfig(String tenantId) {
        // 从缓存中获取配置，如果不存在则创建新的配置
        return tenantConfigs.computeIfAbsent(tenantId, this::createConfig);
    }

    /**
     * 创建租户配置
     * 这里使用简单的策略：
     * - tenant1: 使用 key 前缀方式
     * - tenant2: 使用 database 方式
     * - tenant3: 使用独立实例方式
     * 实际应用中可以从数据库或配置中心获取配置
     */
    private TenantRedisConfig createConfig(String tenantId) {
        TenantRedisConfig config = new TenantRedisConfig();
        config.setTenantId(tenantId);
        
        switch (tenantId) {
            case "tenant1" -> {
                config.setType(RedisTenantType.PREFIX);
                config.setKeyPrefix(tenantId + ":");
                config.setHost(redisHost);
                config.setPort(redisPort);
                config.setPassword(redisPassword);
                config.setDatabase(redisDatabase);
            }
            case "tenant2" -> {
                config.setType(RedisTenantType.DATABASE);
                config.setHost(redisHost);
                config.setPort(redisPort);
                config.setPassword(redisPassword);
                config.setDatabase(redisDatabase + 1); // 使用下一个数据库
            }
            case "tenant3" -> {
                config.setType(RedisTenantType.INSTANCE);
                config.setHost(redisHost);
                config.setPort(redisPort);
                config.setPassword(redisPassword);
                config.setDatabase(0);
            }
            default -> {
                // 默认使用 key 前缀方式
                config.setType(RedisTenantType.PREFIX);
                config.setKeyPrefix(tenantId + ":");
                config.setHost(redisHost);
                config.setPort(redisPort);
                config.setPassword(redisPassword);
                config.setDatabase(redisDatabase);
            }
        }
        
        return config;
    }
}