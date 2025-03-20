package io.github.lizhifuabc.web.security;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * IP白名单服务
 * 用于限制只有特定IP可以访问API接口
 *
 * @author lizhifu
 * @since 2025/3/19
 */
@Service
public class IpWhitelistService {

    /**
     * IP白名单集合
     * 实际项目中可以从配置文件或数据库中加载
     */
    private final Set<String> whitelistedIps = new HashSet<>();

    /**
     * 是否启用IP白名单
     */
    private boolean enabled = true;

    /**
     * 构造函数，初始化白名单
     */
    public IpWhitelistService() {
        // 添加默认白名单IP，实际项目中应从配置或数据库加载
        whitelistedIps.addAll(Arrays.asList(
                "127.0.0.1",
                "192.168.1.1",
                "10.0.0.1"
        ));
    }

    /**
     * 检查IP是否在白名单中
     *
     * @param ip 请求IP
     * @return 是否在白名单中
     */
    public boolean isIpAllowed(String ip) {
        // 如果未启用白名单，则所有IP都允许访问
        if (!enabled) {
            return true;
        }
        return whitelistedIps.contains(ip);
    }

    /**
     * 添加IP到白名单
     *
     * @param ip IP地址
     */
    public void addIp(String ip) {
        whitelistedIps.add(ip);
    }

    /**
     * 从白名单中移除IP
     *
     * @param ip IP地址
     */
    public void removeIp(String ip) {
        whitelistedIps.remove(ip);
    }

    /**
     * 设置是否启用IP白名单
     *
     * @param enabled 是否启用
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 获取所有白名单IP
     *
     * @return 白名单IP集合
     */
    public Set<String> getWhitelistedIps() {
        return new HashSet<>(whitelistedIps);
    }
}