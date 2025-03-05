package io.github.lizhifuabc.rbac.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 三级等保安全配置属性
 *
 * @author lizhifu
 * @since 2024/2/24
 */
@Data
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {
    /**
     * 开启双因子登录，默认：开启
     */
    private boolean twoFactorLoginEnabled = false;

    /**
     * 连续登录失败次数则锁定，-1表示不受限制，可以一直尝试登录
     */
    private int loginFailMaxTimes = -1;

    /**
     * 连续登录失败锁定时间（单位：秒），-1表示不锁定，建议锁定30分钟
     */
    private int loginFailLockSeconds = 1800;

    /**
     * 最低活跃时间（单位：秒），超过此时间没有操作系统就会被冻结，默认-1 代表不限制，永不冻结; 默认 30分钟
     */
    private int loginActiveTimeoutSeconds = -1;

    /**
     * 密码复杂度 是否开启，默认：开启
     */
    private boolean passwordComplexityEnabled = true;

    /**
     * 定期修改密码时间间隔（默认：天），默认：建议90天更换密码
     */
    private int regularChangePasswordDays = 90;

    /**
     * 定期修改密码不允许相同次数，默认：3次以内密码不能相同
     */
    private int regularChangePasswordNotAllowRepeatTimes = 3;

    /**
     * 文件大小限制，单位 mb ，(默认：50 mb)
     */
    private long maxUploadFileSizeMb = 50;

    /**
     * 文件检测，默认：不开启
     */
    private boolean fileDetectFlag = false;

    /**
     * 密码长度8-20位且包含大小写字母、数字、特殊符号三种及以上组合
     */
    private String passwordPattern = "^(?![a-zA-Z]+$)(?![A-Z0-9]+$)(?![A-Z\\W_!@#$%^&*`~()-+=]+$)(?![a-z0-9]+$)(?![a-z\\W_!@#$%^&*`~()-+=]+$)(?![0-9\\W_!@#$%^&*`~()-+=]+$)[a-zA-Z0-9\\W_!@#$%^&*`~()-+=]*$";

    /**
     * 密码格式提示信息
     */
    private String passwordFormatMsg = "密码必须为长度8-20位且必须包含大小写字母、数字、特殊符号（如：@#$%^&*()_+-=）等三种字符";

    /**
     * 密码加盐格式
     */
    private String passwordSaltFormat = "component_%s_rbac_$^&*";
}