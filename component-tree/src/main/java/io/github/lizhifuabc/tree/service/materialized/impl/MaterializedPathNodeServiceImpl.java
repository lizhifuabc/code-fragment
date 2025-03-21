package io.github.lizhifuabc.tree.service.materialized.impl;

import io.github.lizhifuabc.tree.common.TreeException;
import io.github.lizhifuabc.tree.domain.materialized.MaterializedPathNode;
import io.github.lizhifuabc.tree.mapper.materialized.MaterializedPathNodeMapper;
import io.github.lizhifuabc.tree.service.materialized.MaterializedPathNodeService;
import io.github.lizhifuabc.tree.service.materialized.MaterializedPathNodeTree;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 物化路径模型服务实现
 *
 * @author lizhifu
 * @since 2023/7/1
 */
@Service
public class MaterializedPathNodeServiceImpl implements MaterializedPathNodeService {

    private final MaterializedPathNodeMapper materializedPathNodeMapper;

    public MaterializedPathNodeServiceImpl(MaterializedPathNodeMapper materializedPathNodeMapper) {
        this.materializedPathNodeMapper = materializedPathNodeMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MaterializedPathNode createNode(MaterializedPathNode node, Long parentId) {
        // 设置节点路径和层级
        if (parentId == null) {
            // 根节点
            node.setPath("001");
            node.setLevel(0);
        } else {
            // 非根节点，查询父节点
            MaterializedPathNode parentNode = materializedPathNodeMapper.selectById(parentId);
            if (parentNode == null) {
                throw new TreeException("父节点不存在");
            }
            
            // 查询同级节点数量，用于生成新节点的路径
            List<MaterializedPathNode> siblings = materializedPathNodeMapper.selectByParentId(parentId);
            int siblingCount = siblings.size() + 1;
            
            // 生成新节点的路径编码（三位数，不足补0）
            String childCode = String.format("%03d", siblingCount);
            
            // 子节点路径 = 父节点路径 + . + 子节点编码
            node.setPath(parentNode.getPath() + "." + childCode);
            
            // 子节点层级 = 父节点层级 + 1
            node.setLevel(parentNode.getLevel() + 1);
        }

        // 插入节点
        materializedPathNodeMapper.insert(node);
        return node;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MaterializedPathNode updateNode(MaterializedPathNode node) {
        // 查询原节点
        MaterializedPathNode oldNode = materializedPathNodeMapper.selectById(node.getId());
        if (oldNode == null) {
            throw new TreeException("节点不存在");
        }

        // 路径和层级不允许修改，保持原值
        node.setPath(oldNode.getPath());
        node.setLevel(oldNode.getLevel());

        // 更新节点
        materializedPathNodeMapper.update(node);
        return node;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteNode(Long id) {
        // 查询节点
        MaterializedPathNode node = materializedPathNodeMapper.selectById(id);
        if (node == null) {
            throw new TreeException("节点不存在");
        }

        // 查询是否有子节点
        List<MaterializedPathNode> descendants = materializedPathNodeMapper.selectByPathPrefix(node.getPath() + ".");
        if (!descendants.isEmpty()) {
            throw new TreeException("该节点下有子节点，不能删除");
        }

        // 删除节点
        return materializedPathNodeMapper.delete(id) > 0;
    }

    @Override
    public MaterializedPathNode getNode(Long id) {
        return materializedPathNodeMapper.selectById(id);
    }

    @Override
    public List<MaterializedPathNode> getAllNodes() {
        return materializedPathNodeMapper.selectAll();
    }

    @Override
    public List<MaterializedPathNode> getChildNodes(Long parentId) {
        return materializedPathNodeMapper.selectByParentId(parentId);
    }

    @Override
    public List<MaterializedPathNode> getDescendants(Long id) {
        // 查询节点
        MaterializedPathNode node = materializedPathNodeMapper.selectById(id);
        if (node == null) {
            throw new TreeException("节点不存在");
        }

        // 查询所有以该节点路径为前缀的节点
        return materializedPathNodeMapper.selectByPathPrefix(node.getPath() + ".");
    }

    @Override
    public List<MaterializedPathNode> getAncestors(Long id) {
        // 查询节点
        MaterializedPathNode node = materializedPathNodeMapper.selectById(id);
        if (node == null) {
            throw new TreeException("节点不存在");
        }

        // 查询所有祖先节点
        return materializedPathNodeMapper.selectAncestorsByPath(node.getPath());
    }

    @Override
    public List<MaterializedPathNodeTree> buildTree() {
        // 获取所有节点并按路径排序
        List<MaterializedPathNode> allNodes = materializedPathNodeMapper.selectAll();
        
        // 如果没有节点，返回空列表
        if (allNodes.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 转换为树节点
        List<MaterializedPathNodeTree> treeNodes = allNodes.stream()
                .map(MaterializedPathNodeTree::fromEntity)
                .collect(Collectors.toList());
        
        // 构建节点ID到树节点的映射
        Map<Long, MaterializedPathNodeTree> nodeMap = new HashMap<>();
        for (MaterializedPathNodeTree treeNode : treeNodes) {
            nodeMap.put(treeNode.getId(), treeNode);
        }
        
        // 构建树结构
        List<MaterializedPathNodeTree> rootNodes = new ArrayList<>();
        for (MaterializedPathNodeTree treeNode : treeNodes) {
            if (treeNode.getLevel() == 0) {
                // 根节点
                rootNodes.add(treeNode);
            } else {
                // 非根节点，找到父节点
                String parentPath = treeNode.getPath().substring(0, treeNode.getPath().lastIndexOf("."));
                for (MaterializedPathNodeTree potentialParent : treeNodes) {
                    if (potentialParent.getPath().equals(parentPath)) {
                        potentialParent.getChildren().add(treeNode);
                        break;
                    }
                }
            }
        }
        
        return rootNodes;
    }
}