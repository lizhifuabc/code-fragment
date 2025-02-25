package io.github.lizhifuabc.extension.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * hello
 *
 * @author lizhifu
 * @since 2025/2/25
 */
@RestController("/")
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
