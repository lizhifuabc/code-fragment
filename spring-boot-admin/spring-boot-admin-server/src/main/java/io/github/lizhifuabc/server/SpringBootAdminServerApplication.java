package io.github.lizhifuabc.server;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Admin Server
 *
 * @author lizhifu
 * @since 2025/2/24
 */
@SpringBootApplication
@EnableAdminServer // 开启admin服务端
public class SpringBootAdminServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootAdminServerApplication.class, args);
    }
}
