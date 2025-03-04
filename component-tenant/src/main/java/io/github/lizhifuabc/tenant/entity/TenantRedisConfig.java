package io.github.lizhifuabc.tenant.entity;

import io.github.lizhifuabc.tenant.enums.RedisTenantType;
import lombok.Data;

@Data
public class TenantRedisConfig {
    private String tenantId;
    private RedisTenantType type;
    // 独立实例配置
    private String host;
    private int port;
    private String password;
    // Database配置
    private int database;
    // Key前缀配置
    private String keyPrefix;
    // 集群配置
    private String[] clusterNodes;

    // 哨兵配置
    private String masterName;
    private String[] sentinelNodes;
}