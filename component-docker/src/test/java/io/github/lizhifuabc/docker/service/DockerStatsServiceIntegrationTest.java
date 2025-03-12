package io.github.lizhifuabc.docker.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Container;
import io.github.lizhifuabc.docker.ComponentDockerApplication;
import io.github.lizhifuabc.docker.config.DockerClientFactory;
import io.github.lizhifuabc.docker.model.DockerStats;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Docker资源监控服务集成测试
 * 该测试类不使用mock，直接连接到实际的Docker环境进行测试
 * 
 * @author lizhifu
 * @since 2024/3/13
 */
@SpringBootTest(classes = ComponentDockerApplication.class)
@Tag("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DockerStatsServiceIntegrationTest {

    @Resource
    private DockerStatsService dockerStatsService;
    
    @Resource
    private DockerClientFactory dockerClientFactory;
    
    @Resource
    private DockerContainerService dockerContainerService;
    
    private static final String TEST_SERVER = "remote";
    private static final String TEST_IMAGE = "hello-world";
    private static final String TEST_CONTAINER = "test-stats-container";
    private static String containerId;
    
    @BeforeAll
    static void checkDockerAvailable() {
        // 此方法会在所有测试方法执行前运行，用于检查Docker环境是否可用
        // 如果Docker环境不可用，则跳过所有测试
        // Assumptions.assumeTrue(isDockerAvailable(), "Docker环境不可用，跳过测试");
    }
    
    private static boolean isDockerAvailable() {
        try {
            // 使用docker info命令检查Docker是否可用
            Process process = Runtime.getRuntime().exec("docker info");
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }
    
//    @BeforeEach
    void setUp() {
        // 确保测试容器不存在
        try {
            List<Container> containers = dockerContainerService.listContainers(true);
            System.out.println(containers.size());
            containers.stream()
                    .filter(c -> {
                        String[] names = c.getNames();
                        return names != null && names.length > 0 && names[0].contains(TEST_CONTAINER);
                    })
                    .forEach(c -> {
                        try {
                            dockerContainerService.stopContainer(c.getId());
                            dockerContainerService.removeContainer(c.getId());
                        } catch (Exception e) {
                            // 忽略异常
                        }
                    });
        } catch (Exception e) {
            // 忽略异常
        }
    }
    
    @Test
    @Order(1)
    @DisplayName("测试创建容器并获取统计信息")
    void testCreateContainerAndGetStats() throws InterruptedException {
        List<Container> containers = dockerContainerService.listContainers(true);
        // 创建测试容器
        try {
            // 拉取镜像（如果需要）
            DockerClient client = dockerClientFactory.getClient(TEST_SERVER);
            client.pullImageCmd(TEST_IMAGE).start().awaitCompletion(30, TimeUnit.SECONDS);
            
            // 创建并启动容器
            CreateContainerResponse container = dockerContainerService.createContainer(TEST_IMAGE, TEST_CONTAINER);
            containerId = container.getId();
            assertNotNull(containerId, "容器创建失败");
            
            dockerContainerService.startContainer(containerId);
            assertTrue(dockerContainerService.containerExists(containerId), "容器不存在");
            
            // 等待容器启动
            TimeUnit.SECONDS.sleep(5);
            
            // 获取容器统计信息
            List<DockerStats> statsList = dockerStatsService.getContainerStats(TEST_SERVER);
            
            // 验证结果
            assertNotNull(statsList, "获取容器统计信息失败");
            
            // 打印统计信息
            for (DockerStats stats : statsList) {
                System.out.println("容器ID: " + stats.getContainerId());
                System.out.println("容器名称: " + stats.getContainerName());
                System.out.println("CPU使用率: " + stats.getCpuUsage() + "%");
                System.out.println("内存使用量: " + formatBytes(stats.getMemoryUsage()));
                System.out.println("内存限制: " + formatBytes(stats.getMemoryLimit()));
                System.out.println("内存使用率: " + stats.getMemoryUsagePercent() + "%");
                System.out.println("网络输入: " + formatBytes(stats.getNetworkInput()));
                System.out.println("网络输出: " + formatBytes(stats.getNetworkOutput()));
                System.out.println("块设备读取: " + formatBytes(stats.getBlockRead()));
                System.out.println("块设备写入: " + formatBytes(stats.getBlockWrite()));
                System.out.println("-----------------------------------");
            }
        } finally {
            // 清理测试容器
            if (containerId != null) {
                try {
                    dockerContainerService.stopContainer(containerId);
                    dockerContainerService.removeContainer(containerId);
                } catch (Exception e) {
                    // 忽略异常
                }
            }
        }
    }
    
    @Test
    @Order(2)
    @DisplayName("测试collectStats方法")
    void testCollectStats() {
        // 测试定时任务方法
        dockerStatsService.collectStats();
        // 由于collectStats方法主要是日志输出，这里只验证方法不抛出异常
    }
    
    /**
     * 格式化字节数为可读的字符串
     *
     * @param bytes 字节数
     * @return 格式化后的字符串
     */
    private String formatBytes(Long bytes) {
        if (bytes == null) {
            return "0 B";
        }
        if (bytes < 1024) {
            return bytes + " B";
        }
        if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        }
        if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024));
        }
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }
    
    @AfterEach
    void tearDown() {
        // 确保测试容器被清理
        if (containerId != null) {
            try {
                dockerContainerService.stopContainer(containerId);
                dockerContainerService.removeContainer(containerId);
            } catch (Exception e) {
                // 忽略异常
            }
        }
    }
}