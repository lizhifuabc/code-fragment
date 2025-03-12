package io.github.lizhifuabc.docker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Docker控制组件启动类
 *
 * @author lizhifu
 * @since 2024/3/13
 */
@SpringBootApplication
public class ComponentDockerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ComponentDockerApplication.class, args);
    }
}