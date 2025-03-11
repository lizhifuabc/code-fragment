package io.github.lizhifuabc.docker.service;

import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Container;

import java.util.List;

/**
 * Docker容器操作服务接口
 *
 * @author lizhifu
 * @since 2024/3/13
 */
public interface DockerContainerService {
    /**
     * 创建容器
     *
     * @param imageName 镜像名称
     * @param containerName 容器名称
     * @return 容器创建响应
     */
    CreateContainerResponse createContainer(String imageName, String containerName);

    /**
     * 启动容器
     *
     * @param containerId 容器ID
     */
    void startContainer(String containerId);

    /**
     * 停止容器
     *
     * @param containerId 容器ID
     */
    void stopContainer(String containerId);

    /**
     * 删除容器
     *
     * @param containerId 容器ID
     */
    void removeContainer(String containerId);

    /**
     * 获取所有容器列表
     *
     * @param showAll 是否显示所有容器（包括已停止的）
     * @return 容器列表
     */
    List<Container> listContainers(boolean showAll);

    /**
     * 检查容器是否存在
     *
     * @param containerId 容器ID
     * @return 是否存在
     */
    boolean containerExists(String containerId);

    /**
     * 获取容器日志
     *
     * @param containerId 容器ID
     * @return 容器日志
     */
    String getContainerLogs(String containerId);
}