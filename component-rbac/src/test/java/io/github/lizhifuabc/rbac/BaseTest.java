package io.github.lizhifuabc.rbac;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * 测试基类
 *
 * @author lizhifu
 * @since 2024/2/24
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public abstract class BaseTest {
    
    @BeforeEach
    public void setUp() {
        log.info("开始测试...");
    }
}