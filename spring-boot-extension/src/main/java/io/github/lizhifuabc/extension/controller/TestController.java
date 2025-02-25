package io.github.lizhifuabc.extension.controller;

import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @PostMapping("/json")
    public TestResponse testJson(@RequestBody TestRequest request) {
        TestResponse response = new TestResponse();
        response.setMessage("收到请求：" + request.getName());
        response.setCode("200");
        return response;
    }

    @Data
    public static class TestRequest {
        private String name;
        private String message;
    }

    @Data
    public static class TestResponse {
        private String message;
        private String code;
    }
}