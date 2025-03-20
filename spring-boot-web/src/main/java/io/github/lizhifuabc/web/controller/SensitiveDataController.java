package io.github.lizhifuabc.web.controller;

import io.github.lizhifuabc.web.common.Result;
import io.github.lizhifuabc.web.model.UserInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据脱敏控制器
 * 演示数据脱敏功能
 *
 * @author lizhifu
 * @since 2025/3/19
 */
@RestController
@RequestMapping("/api/sensitive")
public class SensitiveDataController {

    /**
     * 获取用户信息（自动脱敏）
     *
     * @return 脱敏后的用户信息
     */
    @GetMapping("/user")
    public Result<UserInfo> getUserInfo() {
        // 创建用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId("10001");
        userInfo.setName("张三");
        userInfo.setMobile("18888888888");
        userInfo.setEmail("zhangsan@example.com");
        userInfo.setIdCard("310123199001011234");
        userInfo.setBankCard("6222021234567890123");
        userInfo.setAddress("上海市浦东新区张江高科技园区");
        
        // 返回用户信息（会自动脱敏）
        return Result.success(userInfo);
    }
}