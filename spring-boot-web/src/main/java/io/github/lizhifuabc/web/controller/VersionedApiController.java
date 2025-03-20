package io.github.lizhifuabc.web.controller;

import io.github.lizhifuabc.web.common.Result;
import io.github.lizhifuabc.web.version.ApiVersion;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 版本控制API示例
 * 演示API版本控制功能
 *
 * @author lizhifu
 * @since 2025/3/19
 */
@RestController
@RequestMapping("/api")
public class VersionedApiController {

    /**
     * V1版本接口
     */
    @ApiVersion("1.0.0")
    @GetMapping("/v1/query")
    public Result<String> queryV1() {
        return Result.success("这是API v1.0.0版本的查询接口");
    }

    /**
     * V2版本接口
     */
    @ApiVersion("2.0.0")
    @GetMapping("/v2/query")
    public Result<String> queryV2() {
        return Result.success("这是API v2.0.0版本的查询接口，提供了更多功能");
    }
}