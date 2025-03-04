package io.github.lizhifuabc.tenant.service;

import io.github.lizhifuabc.tenant.entity.TenantRedisConfig;

public interface TenantConfigService {
    /**
     * 获取租户Redis配置
     * @param tenantId 租户ID
     * @return Redis配置
     */
    TenantRedisConfig getTenantConfig(String tenantId);
}