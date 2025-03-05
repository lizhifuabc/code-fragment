package io.github.lizhifuabc.rbac.security.service;

import io.github.lizhifuabc.rbac.security.config.SecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.regex.Pattern;

/**
 * 密码安全服务
 * 密码必须为长度8-20位且必须包含大小写字母、数字、特殊符号（如：@#$%^&*()_+-=）等三种字符
 * 要求用户对口令（密码）定期更换，默认：更换周期为6个月，更新的口令（密码） 5 次内不能重复
 * 密码加密保存，默认：使用 md5 + salt(加盐) 存储
 * <a href="https://smartadmin.vip/views/level3protect/smart-admin/function.html">密码安全服务</a>
 * @author lizhifu
 * @since 2024/3/5
 */
@Slf4j
@Service
public class SecurityPasswordService {
    @Autowired
    private SecurityProperties securityProperties;

    /**
     * 验证密码复杂度
     * @param password 密码
     * @return true:符合要求 false:不符合要求
     */
    public boolean validatePasswordComplexity(String password) {
        if (!securityProperties.isPasswordComplexityEnabled()) {
            return true;
        }
        return Pattern.matches(securityProperties.getPasswordPattern(), password);
    }

    /**
     * 生成加盐密码
     * @param password 原始密码
     * @param salt 盐值（通常使用用户唯一标识，如用户名或ID）
     * @return 加密后的密码
     */
    public String encryptPassword(String password, String salt) {
        String saltedPassword = String.format(securityProperties.getPasswordSaltFormat(), salt) + password;
        return DigestUtils.md5DigestAsHex(saltedPassword.getBytes());
    }

    /**
     * 验证密码是否正确
     * @param inputPassword 输入的密码
     * @param salt 盐值
     * @param encryptedPassword 已加密的密码
     * @return true:正确 false:错误
     */
    public boolean verifyPassword(String inputPassword, String salt, String encryptedPassword) {
        String calculatedPassword = encryptPassword(inputPassword, salt);
        return calculatedPassword.equals(encryptedPassword);
    }

    /**
     * 检查密码是否需要更新
     * @param lastPasswordChangeTime 上次修改密码时间（时间戳）
     * @return true:需要更新 false:不需要更新
     */
    public boolean isPasswordExpired(long lastPasswordChangeTime) {
        if (securityProperties.getRegularChangePasswordDays() <= 0) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        long passwordAgeDays = (currentTime - lastPasswordChangeTime) / (1000 * 60 * 60 * 24);
        return passwordAgeDays >= securityProperties.getRegularChangePasswordDays();
    }

    /**
     * 检查新密码是否在历史密码中
     * @param newPassword 新密码
     * @param salt 盐值
     * @param passwordHistory 历史密码列表（已加密）
     * @return true:在历史记录中 false:不在历史记录中
     */
    public boolean isPasswordInHistory(String newPassword, String salt, String[] passwordHistory) {
        if (passwordHistory == null || passwordHistory.length == 0) {
            return false;
        }
        String encryptedNewPassword = encryptPassword(newPassword, salt);
        for (String historicalPassword : passwordHistory) {
            if (historicalPassword.equals(encryptedNewPassword)) {
                return true;
            }
        }
        return false;
    }
}