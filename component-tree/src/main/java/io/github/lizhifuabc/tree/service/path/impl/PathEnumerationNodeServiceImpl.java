package io.github.lizhifuabc.tree.service.path.impl;

import io.github.lizhifuabc.tree.common.TreeException;
import io.github.lizhifuabc.tree.domain.path.PathEnumerationNode;
import io.github.lizhifuabc.tree.mapper.path.PathEnumerationNodeMapper;
import io.github.lizhifuabc.tree.service.path.PathEnumerationNodeService;
import io.github.lizhifuabc.tree.service.path.PathEnumerationNodeTree;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 路径枚举模型服务实现
 *
 * @author lizhifu
 * @since 2023/7/1
 */
@Service
public class PathEnumerationNodeServiceImpl implements PathEnumerationNodeService {

    private final PathEnumerationNodeMapper pathEnumerationNodeMapper;

    public PathEnumerationNodeServiceImpl(PathEnumerationNodeMapper pathEnumerationNodeMapper) {
        this.pathEnumerationNodeMapper = pathEnumerationNodeMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PathEnumerationNode createNode(PathEnumerationNode node, Long parentId) {
        // 设置节点路径和层级
        if (parentId == null) {
            // 根节点
            node.setPath("/");
            node.setLevel(0);
        } else {
            // 非根节点，查询父节点
            PathEnumerationNode parentNode = pathEnumerationNodeMapper.selectById(parentId);
            if (parentNode == null) {
                throw new TreeException("父节点不存在");
            }
            // 子节点路径 = 父节点路径 + 父节点ID + /
            node.setPath(parentNode.getPath() + parentId + "/");
            // 子节点层级 = 父节点层级 + 1
            node.setLevel(parentNode.getLevel() + 1);
        }

        // 插入节点
        pathEnumerationNodeMapper.insert(node);
        return node;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PathEnumerationNode updateNode(PathEnumerationNode node) {
        // 查询原节点
        PathEnumerationNode oldNode = pathEnumerationNodeMapper.selectById(node.getId());
        if (oldNode == null) {
            throw new TreeException("节点不存在");
        }

        // 路径和层级不允许修改，保持原值
        node.setPath(oldNode.getPath());
        node.setLevel(oldNode.getLevel());

        // 更新节点
        pathEnumerationNodeMapper.update(node);
        return node;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteNode(Long id) {
        // 查询是否有子节点
        List<PathEnumerationNode> children = pathEnumerationNodeMapper.selectByParentId(id);
        if (!children.isEmpty()) {
            throw new TreeException("该节点下有子节点，不能删除");
        }

        // 删除节点
        return pathEnumerationNodeMapper.delete(id) > 0;
    }

    @Override
    public PathEnumerationNode getNode(Long id) {
        return pathEnumerationNodeMapper.selectById(id);
    }

    @Override
    public List<PathEnumerationNode> getAllNodes() {
        return pathEnumerationNodeMapper.selectAll();
    }

    @Override
    public List<PathEnumerationNode> getChildNodes(Long parentId) {
        return pathEnumerationNodeMapper.selectByParentId(parentId);
    }

    @Override
    public List<PathEnumerationNode> getDescendants(Long id) {
        return pathEnumerationNodeMapper.selectDescendants(id);
    }

    @Override
    public List<PathEnumerationNodeTree> buildTree() {
        // 获取所有节点
        List<PathEnumerationNode> allNodes = pathEnumerationNodeMapper.selectAll();
        
        // 转换为树节点
        List<PathEnumerationNodeTree> treeNodes = allNodes.stream()
                .map(PathEnumerationNodeTree::fromEntity)
                .collect(Collectors.toList());
        
        // 构建节点ID到树节点的映射
        Map<Long, PathEnumerationNodeTree> nodeMap = new HashMap<>();
        for (PathEnumerationNodeTree treeNode : treeNodes) {
            nodeMap.put(treeNode.getId(), treeNode);
        }
        
        // 构建树结构
        List<PathEnumerationNodeTree> rootNodes = new ArrayList<>();
        for (PathEnumerationNodeTree treeNode : treeNodes) {
            if (treeNode.getLevel() == 0) {
                // 根节点
                rootNodes.add(treeNode);
            } else {
                // 非根节点，找到父节点并添加到父节点的子节点列表
                String path = treeNode.getPath();
                // 从路径中提取父节点ID
                int lastSlashIndex = path.lastIndexOf('/', path.length() - 2);
                String parentIdStr = path.substring(lastSlashIndex + 1, path.length() - 1);
                Long parentId = Long.parseLong(parentIdStr);
                
                PathEnumerationNodeTree parentNode = nodeMap.get(parentId);
                if (parentNode != null) {
                    parentNode.getChildren().add(treeNode);
                }
            }
        }
        
        return rootNodes;
    }
}