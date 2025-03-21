package io.github.lizhifuabc.tree;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 树形结构应用启动类
 *
 * @author lizhifu
 * @since 2023/7/1
 */
@SpringBootApplication
@MapperScan("io.github.lizhifuabc.tree.mapper")
public class TreeApplication {
    public static void main(String[] args) {
        SpringApplication.run(TreeApplication.class, args);
    }
}