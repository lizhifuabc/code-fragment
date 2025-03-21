package io.github.lizhifuabc.tree.service.adjacency.impl;

import io.github.lizhifuabc.tree.common.TreeException;
import io.github.lizhifuabc.tree.domain.adjacency.AdjacencyNode;
import io.github.lizhifuabc.tree.mapper.adjacency.AdjacencyNodeMapper;
import io.github.lizhifuabc.tree.service.adjacency.AdjacencyNodeService;
import io.github.lizhifuabc.tree.service.adjacency.AdjacencyNodeTree;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 邻接表模型服务实现
 *
 * @author lizhifu
 * @since 2023/7/1
 */
@Service
public class AdjacencyNodeServiceImpl implements AdjacencyNodeService {

    private final AdjacencyNodeMapper adjacencyNodeMapper;

    public AdjacencyNodeServiceImpl(AdjacencyNodeMapper adjacencyNodeMapper) {
        this.adjacencyNodeMapper = adjacencyNodeMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdjacencyNode createNode(AdjacencyNode node) {
        // 设置节点层级
        if (node.getParentId() == null) {
            // 根节点
            node.setLevel(0);
        } else {
            // 非根节点，查询父节点
            AdjacencyNode parentNode = adjacencyNodeMapper.selectById(node.getParentId());
            if (parentNode == null) {
                throw new TreeException("父节点不存在");
            }
            // 子节点层级 = 父节点层级 + 1
            node.setLevel(parentNode.getLevel() + 1);
        }

        // 插入节点
        adjacencyNodeMapper.insert(node);
        return node;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdjacencyNode updateNode(AdjacencyNode node) {
        // 查询原节点
        AdjacencyNode oldNode = adjacencyNodeMapper.selectById(node.getId());
        if (oldNode == null) {
            throw new TreeException("节点不存在");
        }

        // 如果修改了父节点，需要重新计算层级
        if (node.getParentId() != null && !node.getParentId().equals(oldNode.getParentId())) {
            // 查询新父节点
            AdjacencyNode parentNode = adjacencyNodeMapper.selectById(node.getParentId());
            if (parentNode == null) {
                throw new TreeException("父节点不存在");
            }
            // 子节点层级 = 父节点层级 + 1
            node.setLevel(parentNode.getLevel() + 1);

            // TODO: 如果有子节点，需要递归更新子节点的层级
        } else {
            // 保持原层级
            node.setLevel(oldNode.getLevel());
        }

        // 更新节点
        adjacencyNodeMapper.update(node);
        return node;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteNode(Long id) {
        // 查询是否有子节点
        List<AdjacencyNode> children = adjacencyNodeMapper.selectByParentId(id);
        if (!children.isEmpty()) {
            throw new TreeException("该节点下有子节点，不能删除");
        }

        // 删除节点
        return adjacencyNodeMapper.delete(id) > 0;
    }

    @Override
    public AdjacencyNode getNode(Long id) {
        return adjacencyNodeMapper.selectById(id);
    }

    @Override
    public List<AdjacencyNode> getAllNodes() {
        return adjacencyNodeMapper.selectAll();
    }

    @Override
    public List<AdjacencyNode> getChildNodes(Long parentId) {
        return adjacencyNodeMapper.selectByParentId(parentId);
    }

    @Override
    public List<AdjacencyNode> getDescendants(Long id) {
        return adjacencyNodeMapper.selectDescendants(id);
    }

    @Override
    public List<AdjacencyNodeTree> buildTree() {
        // 获取所有节点
        List<AdjacencyNode> allNodes = adjacencyNodeMapper.selectAll();
        
        // 转换为树节点
        List<AdjacencyNodeTree> treeNodes = allNodes.stream()
                .map(AdjacencyNodeTree::fromEntity)
                .collect(Collectors.toList());
        
        // 构建节点ID到树节点的映射
        Map<Long, AdjacencyNodeTree> nodeMap = new HashMap<>();
        for (AdjacencyNodeTree treeNode : treeNodes) {
            nodeMap.put(treeNode.getId(), treeNode);
        }
        
        // 构建树结构
        List<AdjacencyNodeTree> rootNodes = new ArrayList<>();
        for (AdjacencyNodeTree treeNode : treeNodes) {
            if (treeNode.getParentId() == null) {
                // 根节点
                rootNodes.add(treeNode);
            } else {
                // 非根节点，添加到父节点的子节点列表
                AdjacencyNodeTree parentNode = nodeMap.get(treeNode.getParentId());
                if (parentNode != null) {
                    parentNode.getChildren().add(treeNode);
                }
            }
        }
        
        return rootNodes;
    }
}