package io.github.lizhifuabc.web.version;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * API版本请求条件
 * 用于匹配请求中的版本号
 *
 * @author lizhifu
 * @since 2025/3/19
 */
public class ApiVersionRequestCondition implements RequestCondition<ApiVersionRequestCondition> {
    
    /**
     * 版本号正则表达式
     * 匹配路径中的版本号，例如：/v1/user
     */
    private static final Pattern VERSION_PATTERN = Pattern.compile("/v(\\d+\\.\\d+\\.\\d+)");
    
    /**
     * 版本号
     */
    private final String apiVersion;

    public ApiVersionRequestCondition(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    @Override
    public ApiVersionRequestCondition combine(ApiVersionRequestCondition other) {
        // 采用最新版本
        return new ApiVersionRequestCondition(other.apiVersion);
    }

    @Override
    public ApiVersionRequestCondition getMatchingCondition(HttpServletRequest request) {
        Matcher matcher = VERSION_PATTERN.matcher(request.getRequestURI());
        if (matcher.find()) {
            String version = matcher.group(1);
            // 如果请求的版本号大于等于当前版本号，则匹配成功
            if (compareVersion(version, apiVersion) >= 0) {
                return this;
            }
        }
        return null;
    }

    @Override
    public int compareTo(ApiVersionRequestCondition other, HttpServletRequest request) {
        // 版本号越大优先级越高
        return compareVersion(other.apiVersion, this.apiVersion);
    }

    /**
     * 比较版本号
     *
     * @param v1 版本号1
     * @param v2 版本号2
     * @return 比较结果
     */
    private int compareVersion(String v1, String v2) {
        String[] version1 = v1.split("\\.");
        String[] version2 = v2.split("\\.");
        
        int len = Math.max(version1.length, version2.length);
        for (int i = 0; i < len; i++) {
            int num1 = i < version1.length ? Integer.parseInt(version1[i]) : 0;
            int num2 = i < version2.length ? Integer.parseInt(version2[i]) : 0;
            
            if (num1 != num2) {
                return num1 - num2;
            }
        }
        
        return 0;
    }
}