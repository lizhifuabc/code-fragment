package io.github.lizhifuabc.tenant.enums;

public enum RedisTenantType {
    PREFIX,     // Key前缀方案
    DATABASE,   // Database方案
    INSTANCE,   // 独立实例方案
    SENTINEL,   // 哨兵方案
    CLUSTER     // 集群方案
}