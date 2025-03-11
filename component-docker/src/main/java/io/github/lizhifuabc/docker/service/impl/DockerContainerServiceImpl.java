package io.github.lizhifuabc.docker.service.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import io.github.lizhifuabc.docker.service.DockerContainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Docker容器操作服务实现
 *
 * @author lizhifu
 * @since 2024/3/13
 */
@Service
@RequiredArgsConstructor
public class DockerContainerServiceImpl implements DockerContainerService {

    private final DockerClient dockerClient;

    @Override
    public CreateContainerResponse createContainer(String imageName, String containerName) {
        return dockerClient.createContainerCmd(imageName)
                .withName(containerName)
                .exec();
    }

    @Override
    public void startContainer(String containerId) {
        dockerClient.startContainerCmd(containerId).exec();
    }

    @Override
    public void stopContainer(String containerId) {
        dockerClient.stopContainerCmd(containerId)
                .withTimeout(10)
                .exec();
    }

    @Override
    public void removeContainer(String containerId) {
        dockerClient.removeContainerCmd(containerId)
                .withForce(true)
                .withRemoveVolumes(true)
                .exec();
    }

    @Override
    public List<Container> listContainers(boolean showAll) {
        return dockerClient.listContainersCmd()
                .withShowAll(showAll)
                .exec();
    }

    @Override
    public boolean containerExists(String containerId) {
        try {
            InspectContainerResponse response = dockerClient.inspectContainerCmd(containerId).exec();
            return response != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getContainerLogs(String containerId) {
        StringBuilder logs = new StringBuilder();
        try {
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