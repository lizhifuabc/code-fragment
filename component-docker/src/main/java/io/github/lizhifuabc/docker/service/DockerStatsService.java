package io.github.lizhifuabc.docker.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.BlkioStatEntry;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.StatisticNetworksConfig;
import com.github.dockerjava.api.model.Statistics;
import io.github.lizhifuabc.docker.config.DockerClientFactory;
import io.github.lizhifuabc.docker.model.DockerStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Docker资源监控服务
 *
 * @author lizhifu
 * @since 2024/3/13
 */
@Slf4j
@Service
public class DockerStatsService {

    private final DockerClientFactory dockerClientFactory;

    public DockerStatsService(DockerClientFactory dockerClientFactory) {
        this.dockerClientFactory = dockerClientFactory;
    }

    /**
     * 定时采集容器资源使用统计信息
     */
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void collectStats() {
        dockerClientFactory.getServers().forEach(serverName -> {
            try {
                List<DockerStats> stats = getContainerStats(serverName);
                // TODO: 存储统计信息，可以使用数据库或时序数据库
                log.info("采集到{}个容器的资源使用统计信息, 服务器: {}", stats.size(), serverName);
            } catch (Exception e) {
                log.error("采集容器资源使用统计信息失败, 服务器: {}", serverName, e);
            }
        });
    }

    /**
     * 获取指定服务器上所有容器的资源使用统计信息
     *
     * @param serverName 服务器名称
     * @return 容器资源使用统计信息列表
     */
    public List<DockerStats> getContainerStats(String serverName) {
        DockerClient client = dockerClientFactory.getClient(serverName);
        List<DockerStats> statsList = new ArrayList<>();

        // 获取所有运行中的容器
        List<Container> containers = client.listContainersCmd().exec();

        for (Container container : containers) {
            try {
                // 使用异步回调方式获取容器统计信息
                Statistics[] statsHolder = new Statistics[1];
                ResultCallback<Statistics> callback = new ResultCallback<Statistics>() {
                    @Override
                    public void onStart(Closeable closeable) {
                    }

                    @Override
                    public void onNext(Statistics statistics) {
                        statsHolder[0] = statistics;
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        log.error("获取容器{}的统计信息失败", container.getId(), throwable);
                    }

                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void close() throws IOException {
                    }
                };
                
                try {
                    client.statsCmd(container.getId()).exec(callback);
                    Thread.sleep(5000); // 等待5秒钟收集统计信息
                    callback.close();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("等待统计信息时被中断", e);
                }
                
                if (statsHolder[0] == null) {
                    log.warn("获取容器{}的统计信息超时", container.getId());
                    continue;
                }
                
                Statistics statistics = statsHolder[0];
                DockerStats stats = convertToDockerStats(statistics, container, serverName);
                statsList.add(stats);
            } catch (Exception e) {
                log.error("获取容器{}的资源使用统计信息失败", container.getId(), e);
            }
        }

        return statsList;
    }

    /**
     * 将Docker API返回的统计信息转换为自定义的统计信息对象
     *
     * @param statistics Docker API返回的统计信息
     * @param container  容器信息
     * @param serverName 服务器名称
     * @return 自定义的统计信息对象
     */
    private DockerStats convertToDockerStats(Statistics statistics, Container container, String serverName) {
        DockerStats stats = new DockerStats();
        stats.setContainerId(container.getId());
        stats.setContainerName(container.getNames()[0].substring(1)); // 移除开头的'/'
        stats.setServerName(serverName);
        stats.setTimestamp(System.currentTimeMillis());

        // 计算CPU使用率
        long cpuDelta = 0;
        long systemDelta = 0;
        
        // 直接从Statistics对象获取CPU使用信息
        try {
            // 在3.4.2版本中，直接使用getCpuStats()和getPreCpuStats()获取CPU统计信息
            if (statistics.getCpuStats() != null && statistics.getPreCpuStats() != null) {
                if (statistics.getCpuStats().getCpuUsage() != null && 
                    statistics.getPreCpuStats().getCpuUsage() != null) {
                    Long currentCpuUsage = statistics.getCpuStats().getCpuUsage().getTotalUsage();
                    Long preCpuUsage = statistics.getPreCpuStats().getCpuUsage().getTotalUsage();
                    Long currentSystemCpuUsage = statistics.getCpuStats().getSystemCpuUsage();
                    Long preSystemCpuUsage = statistics.getPreCpuStats().getSystemCpuUsage();
                    
                    if (currentCpuUsage != null && preCpuUsage != null && 
                        currentSystemCpuUsage != null && preSystemCpuUsage != null) {
                        cpuDelta = currentCpuUsage - preCpuUsage;
                        systemDelta = currentSystemCpuUsage - preSystemCpuUsage;
                    }
                    
                    double cpuUsage = 0.0;
                    if (systemDelta > 0 && cpuDelta > 0) {
                        Long numCPUs = statistics.getCpuStats().getOnlineCpus();
                        if (numCPUs != null && numCPUs > 0) {
                            cpuUsage = (cpuDelta / (double) systemDelta) * numCPUs * 100.0;
                        }
                    }
                    stats.setCpuUsage(cpuUsage);
                }
            }
        } catch (Exception e) {
            log.warn("计算容器{}的CPU使用率时发生异常", container.getId(), e);
            stats.setCpuUsage(0.0);
        }

        // 设置内存使用情况
        if (statistics.getMemoryStats() != null) {
            stats.setMemoryUsage(statistics.getMemoryStats().getUsage());
            stats.setMemoryLimit(statistics.getMemoryStats().getLimit());
            if (stats.getMemoryLimit() > 0) {
                stats.setMemoryUsagePercent((double) stats.getMemoryUsage() / stats.getMemoryLimit() * 100);
            }
        }

        // 设置网络IO
        Map<String, StatisticNetworksConfig> networks = statistics.getNetworks();
        if (networks != null && !networks.isEmpty()) {
            try {
                long rxBytes = 0;
                long txBytes = 0;
                for (Map.Entry<String, StatisticNetworksConfig> entry : networks.entrySet()) {
                    StatisticNetworksConfig network = entry.getValue();
                    if (network != null) {
                        Long rx = network.getRxBytes();
                        Long tx = network.getTxBytes();
                        rxBytes += rx != null ? rx : 0;
                        txBytes += tx != null ? tx : 0;
                    }
                }
                stats.setNetworkInput(rxBytes);
                stats.setNetworkOutput(txBytes);
            } catch (Exception e) {
                log.warn("处理容器{}的网络统计信息时发生异常", container.getId(), e);
                stats.setNetworkInput(0L);
                stats.setNetworkOutput(0L);
            }
        } else {
            stats.setNetworkInput(0L);
            stats.setNetworkOutput(0L);
        }

        // 设置块设备IO
        try {
            if (statistics.getBlkioStats() != null) {
                List<BlkioStatEntry> ioServiceBytesRecursive = statistics.getBlkioStats().getIoServiceBytesRecursive();
                if (ioServiceBytesRecursive != null) {
                    stats.setBlockRead(ioServiceBytesRecursive
                            .stream()
                            .filter(bytes -> "Read".equalsIgnoreCase(bytes.getOp()))
                            .mapToLong(bytes -> bytes.getValue())
                            .sum());
                    stats.setBlockWrite(ioServiceBytesRecursive
                            .stream()
                            .filter(bytes -> "Write".equalsIgnoreCase(bytes.getOp()))
                            .mapToLong(bytes -> bytes.getValue())
                            .sum());
                } else {
                    stats.setBlockRead(0L);
                    stats.setBlockWrite(0L);
                }
            } else {
                stats.setBlockRead(0L);
                stats.setBlockWrite(0L);
            }
        } catch (Exception e) {
            log.warn("处理容器{}的块设备IO统计信息时发生异常", container.getId(), e);
            stats.setBlockRead(0L);
            stats.setBlockWrite(0L);
        }

        return stats;
    }
}