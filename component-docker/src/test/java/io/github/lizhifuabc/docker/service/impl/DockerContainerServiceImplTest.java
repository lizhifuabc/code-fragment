package io.github.lizhifuabc.docker.service.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.Container;
import io.github.lizhifuabc.docker.config.DockerClientFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Docker容器操作服务实现类测试
 *
 * @author lizhifu
 * @since 2024/3/13
 */
@ExtendWith(MockitoExtension.class)
public class DockerContainerServiceImplTest {

    @Mock
    private DockerClientFactory dockerClientFactory;

    @Mock
    private DockerClient dockerClient;

    @Mock
    private CreateContainerCmd createContainerCmd;

    @Mock
    private StartContainerCmd startContainerCmd;

    @Mock
    private StopContainerCmd stopContainerCmd;

    @Mock
    private RemoveContainerCmd removeContainerCmd;

    @Mock
    private ListContainersCmd listContainersCmd;

    @Mock
    private InspectContainerCmd inspectContainerCmd;

    @Mock
    private LogContainerCmd logContainerCmd;

    @InjectMocks
    private DockerContainerServiceImpl dockerContainerService;

    @BeforeEach
    void setUp() {
        when(dockerClientFactory.getClient(anyString())).thenReturn(dockerClient);
    }

    @Test
    void testCreateContainer() {
        String imageName = "test-image";
        String containerName = "test-container";
        CreateContainerResponse expectedResponse = mock(CreateContainerResponse.class);

        when(dockerClient.createContainerCmd(imageName)).thenReturn(createContainerCmd);
        when(createContainerCmd.withName(containerName)).thenReturn(createContainerCmd);
        when(createContainerCmd.exec()).thenReturn(expectedResponse);

        CreateContainerResponse response = dockerContainerService.createContainer(imageName, containerName);

        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(createContainerCmd).withName(containerName);
        verify(createContainerCmd).exec();
    }

    @Test
    void testStartContainer() {
        String containerId = "test-container-id";

        when(dockerClient.startContainerCmd(containerId)).thenReturn(startContainerCmd);

        dockerContainerService.startContainer(containerId);

        verify(startContainerCmd).exec();
    }

    @Test
    void testStopContainer() {
        String containerId = "test-container-id";

        when(dockerClient.stopContainerCmd(containerId)).thenReturn(stopContainerCmd);
        when(stopContainerCmd.withTimeout(anyInt())).thenReturn(stopContainerCmd);

        dockerContainerService.stopContainer(containerId);

        verify(stopContainerCmd).withTimeout(10);
        verify(stopContainerCmd).exec();
    }

    @Test
    void testRemoveContainer() {
        String containerId = "test-container-id";

        when(dockerClient.removeContainerCmd(containerId)).thenReturn(removeContainerCmd);
        when(removeContainerCmd.withForce(true)).thenReturn(removeContainerCmd);
        when(removeContainerCmd.withRemoveVolumes(true)).thenReturn(removeContainerCmd);

        dockerContainerService.removeContainer(containerId);

        verify(removeContainerCmd).withForce(true);
        verify(removeContainerCmd).withRemoveVolumes(true);
        verify(removeContainerCmd).exec();
    }

    @Test
    void testListContainers() {
        List<Container> expectedContainers = Collections.singletonList(mock(Container.class));

        when(dockerClient.listContainersCmd()).thenReturn(listContainersCmd);
        when(listContainersCmd.withShowAll(true)).thenReturn(listContainersCmd);
        when(listContainersCmd.exec()).thenReturn(expectedContainers);

        List<Container> containers = dockerContainerService.listContainers(true);

        assertNotNull(containers);
        assertEquals(expectedContainers, containers);
        verify(listContainersCmd).withShowAll(true);
        verify(listContainersCmd).exec();
    }

    @Test
    void testContainerExists() {
        String containerId = "test-container-id";
        InspectContainerResponse response = mock(InspectContainerResponse.class);

        when(dockerClient.inspectContainerCmd(containerId)).thenReturn(inspectContainerCmd);
        when(inspectContainerCmd.exec()).thenReturn(response);

        boolean exists = dockerContainerService.containerExists(containerId);

        assertTrue(exists);
        verify(inspectContainerCmd).exec();
    }

    @Test
    void testContainerExistsWhenException() {
        String containerId = "test-container-id";

        when(dockerClient.inspectContainerCmd(containerId)).thenReturn(inspectContainerCmd);
        when(inspectContainerCmd.exec()).thenThrow(new RuntimeException("Container not found"));

        boolean exists = dockerContainerService.containerExists(containerId);

        assertFalse(exists);
        verify(inspectContainerCmd).exec();
    }
}