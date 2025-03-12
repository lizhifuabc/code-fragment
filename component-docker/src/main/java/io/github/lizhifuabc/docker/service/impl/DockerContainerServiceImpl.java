package io.github.lizhifuabc.docker.service.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import io.github.lizhifuabc.docker.config.DockerClientFactory;
import io.github.lizhifuabc.docker.service.DockerContainerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Docker容器操作服务实现
 *
 * @author lizhifu
 * @since 2024/3/13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DockerContainerServiceImpl implements DockerContainerService {

    private final DockerClientFactory dockerClientFactory;

    @Override
    public CreateContainerResponse createContainer(String imageName, String containerName) {
        return createContainer("remote", imageName, containerName);
    }
    
    /**
     * 在指定服务器上创建容器
     *
     * @param serverName 服务器名称
     * @param imageName 镜像名称
     * @param containerName 容器名称
     * @return 容器创建响应
     */
    public CreateContainerResponse createContainer(String serverName, String imageName, String containerName) {
        DockerClient dockerClient = dockerClientFactory.getClient(serverName);
        return dockerClient.createContainerCmd(imageName)
                .withName(containerName)
                .exec();
    }

    @Override
    public void startContainer(String containerId) {
        startContainer("remote", containerId);
    }
    
    /**
     * 在指定服务器上启动容器
     *
     * @param serverName 服务器名称
     * @param containerId 容器ID
     */
    public void startContainer(String serverName, String containerId) {
        DockerClient dockerClient = dockerClientFactory.getClient(serverName);
        dockerClient.startContainerCmd(containerId).exec();
    }

    @Override
    public void stopContainer(String containerId) {
        stopContainer("remote", containerId);
    }
    
    /**
     * 在指定服务器上停止容器
     *
     * @param serverName 服务器名称
     * @param containerId 容器ID
     */
    public void stopContainer(String serverName, String containerId) {
        DockerClient dockerClient = dockerClientFactory.getClient(serverName);
        dockerClient.stopContainerCmd(containerId)
                .withTimeout(10)
                .exec();
    }

    @Override
    public void removeContainer(String containerId) {
        removeContainer("remote", containerId);
    }
    
    /**
     * 在指定服务器上删除容器
     *
     * @param serverName 服务器名称
     * @param containerId 容器ID
     */
    public void removeContainer(String serverName, String containerId) {
        DockerClient dockerClient = dockerClientFactory.getClient(serverName);
        dockerClient.removeContainerCmd(containerId)
                .withForce(true)
                .withRemoveVolumes(true)
                .exec();
    }

    @Override
    public List<Container> listContainers(boolean showAll) {
        return listContainers("remote", showAll);
    }
    
    /**
     * 获取指定服务器上的容器列表
     *
     * @param serverName 服务器名称
     * @param showAll 是否显示所有容器（包括已停止的）
     * @return 容器列表
     */
    public List<Container> listContainers(String serverName, boolean showAll) {
        DockerClient dockerClient = dockerClientFactory.getClient(serverName);
        return dockerClient.listContainersCmd()
                .withShowAll(showAll)
                .exec();
    }

    @Override
    public boolean containerExists(String containerId) {
        return containerExists("remote", containerId);
    }
    
    /**
     * 检查指定服务器上的容器是否存在
     *
     * @param serverName 服务器名称
     * @param containerId 容器ID
     * @return 是否存在
     */
    public boolean containerExists(String serverName, String containerId) {
        try {
            DockerClient dockerClient = dockerClientFactory.getClient(serverName);
            InspectContainerResponse response = dockerClient.inspectContainerCmd(containerId).exec();
            return response != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getContainerLogs(String containerId) {
        return getContainerLogs("remote", containerId);
    }
    
    /**
     * 获取指定服务器上的容器日志
     *
     * @param serverName 服务器名称
     * @param containerId 容器ID
     * @return 容器日志
     */
    public String getContainerLogs(String serverName, String containerId) {
        StringBuilder logs = new StringBuilder();
        try {
            DockerClient dockerClient = dockerClientFactory.getClient(serverName);
            dockerClient.logContainerCmd(containerId)
                    .withStdOut(true)
                    .withStdErr(true)
                    .withTail(100)
                    .exec(new LogContainerResultCallback() {
                        @Override
                        public void onNext(Frame frame) {
                            logs.append(new String(frame.getPayload())).append("\n");
                        }
                    }).awaitCompletion(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("获取容器日志失败", e);
        }
        return logs.toString();
    }
}