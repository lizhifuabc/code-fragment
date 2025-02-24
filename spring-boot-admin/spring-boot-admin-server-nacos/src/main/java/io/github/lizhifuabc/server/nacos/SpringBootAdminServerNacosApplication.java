package io.github.lizhifuabc.server.nacos;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Spring Boot Admin Client
 *
 * @author lizhifu
 * @since 2025/2/24
 */
@SpringBootApplication
@EnableAdminServer // 开启admin服务端
@EnableDiscoveryClient
public class SpringBootAdminServerNacosApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootAdminServerNacosApplication.class, args);
    }
}
