-- 租户配置表示例
CREATE TABLE tenant_redis_config (
         tenant_id VARCHAR(32) PRIMARY KEY,
         type VARCHAR(20),
         host VARCHAR(100),
         port INT,
         password VARCHAR(100),
         database INT,
         key_prefix VARCHAR(50),
         cluster_nodes TEXT
);

-- 示例数据
INSERT INTO tenant_redis_config VALUES
        ('tenant1', 'PREFIX', NULL, NULL, NULL, NULL, 'T1:', NULL),
        ('tenant2', 'DATABASE', NULL, NULL, NULL, 1, NULL, NULL),
        ('tenant3', 'INSTANCE', '192.168.1.10', 6379, 'pass123', NULL, NULL, NULL),
        ('tenant4', 'CLUSTER', NULL, NULL, NULL, NULL, NULL, '192.168.1.1:6379,192.168.1.2:6379');