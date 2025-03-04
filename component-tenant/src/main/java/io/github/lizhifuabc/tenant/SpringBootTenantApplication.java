package io.github.lizhifuabc.tenant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 多租户启动类
 *
 * @author lizhifu
 * @since 2024/2/24
 */
@SpringBootApplication
public class SpringBootTenantApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootTenantApplication.class, args);
    }
}