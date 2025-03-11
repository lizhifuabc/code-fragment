package io.github.lizhifuabc.docker.model;

import lombok.Data;

/**
 * Docker容器资源使用统计信息
 *
 * @author lizhifu
 * @since 2024/3/13
 */
@Data
public class DockerStats {
    /**
     * 容器ID
     */
    private String containerId;

    /**
     * 容器名称
     */
    private String containerName;

    /**
     * CPU使用率（百分比）
     */
    private Double cpuUsage;

    /**
     * 内存使用量（字节）
     */
    private Long memoryUsage;

    /**
     * 内存限制（字节）
     */
    private Long memoryLimit;

    /**
     * 内存使用率（百分比）
     */
    private Double memoryUsagePercent;

    /**
     * 网络输入（字节）
     */
    private Long networkInput;

    /**
     * 网络输出（字节）
     */
    private Long networkOutput;

    /**
     * 块设备读取（字节）
     */
    private Long blockRead;

    /**
     * 块设备写入（字节）
     */
    private Long blockWrite;

    /**
     * 采集时间戳
     */
    private Long timestamp;

    /**
     * 服务器名称
     */
    private String serverName;
}