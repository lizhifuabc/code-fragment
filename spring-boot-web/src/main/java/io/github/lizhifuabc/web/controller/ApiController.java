package io.github.lizhifuabc.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * api 版本控制
 *
 * @author lizhifu
 * @since 2025/3/19
 */
@RestController
@RequestMapping("/api")
public class ApiController {
//    @GetMapping(value = "/query", version = "1.0.0")
    public ResponseEntity<String> queryV1() {
        return ResponseEntity.ok("query api v1.0.0...");
    }

//    @GetMapping(value = "/query", version = "2.0.0")
    public ResponseEntity<String> queryV2() {
        return ResponseEntity.ok("query api v2.0.0...");
    }
}
