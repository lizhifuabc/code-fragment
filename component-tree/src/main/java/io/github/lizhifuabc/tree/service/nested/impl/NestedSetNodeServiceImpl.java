package io.github.lizhifuabc.tree.service.nested.impl;

import io.github.lizhifuabc.tree.common.TreeException;
import io.github.lizhifuabc.tree.domain.nested.NestedSetNode;
import io.github.lizhifuabc.tree.mapper.nested.NestedSetNodeMapper;
import io.github.lizhifuabc.tree.service.nested.NestedSetNodeService;
import io.github.lizhifuabc.tree.service.nested.NestedSetNodeTree;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 嵌套集模型服务实现
 *
 * @author lizhifu
 * @since 2023/7/1
 */
@Service
public class NestedSetNodeServiceImpl implements NestedSetNodeService {

    private final NestedSetNodeMapper nestedSetNodeMapper;

    public NestedSetNodeServiceImpl(NestedSetNodeMapper nestedSetNodeMapper) {
        this.nestedSetNodeMapper = nestedSetNodeMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NestedSetNode createRootNode(NestedSetNode node) {
        // 检查是否已存在根节点
        List<NestedSetNode> allNodes = nestedSetNodeMapper.selectAll();
        if (!allNodes.isEmpty()) {
            // 如果已有节点，检查是否有根节点
            boolean hasRoot = allNodes.stream().anyMatch(n -> n.getLevel() == 0);
            if (hasRoot) {
                throw new TreeException("根节点已存在");
            }
        }
        
        // 设置根节点的左右值和层级
        node.setLft(1);
        node.setRgt(2);
        node.setLevel(0);
        
        // 插入节点
        nestedSetNodeMapper.insert(node);
        return node;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NestedSetNode createChildNode(NestedSetNode node, Long parentId) {
        // 查询父节点
        NestedSetNode parentNode = nestedSetNodeMapper.selectById(parentId);
        if (parentNode == null) {
            throw new TreeException("父节点不存在");
        }
        
        // 获取父节点的右值
        int parentRgt = parentNode.getRgt();
        
        // 更新所有右值大于等于父节点右值的节点的右值（右移2个位置）
        nestedSetNodeMapper.updateRightValues(parentRgt, 2);
        
        // 更新所有左值大于父节点右值的节点的左值（右移2个位置）
        nestedSetNodeMapper.updateLeftValues(parentRgt, 2);
        
        // 设置新节点的左右值和层级
        node.setLft(parentRgt); // 新节点的左值等于原父节点的右值
        node.setRgt(parentRgt + 1); // 新节点的右值等于左值+1
        node.setLevel(parentNode.getLevel() + 1); // 层级为父节点层级+1
        
        // 插入节点
        nestedSetNodeMapper.insert(node);
        return node;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NestedSetNode updateNode(NestedSetNode node) {
        // 查询原节点
        NestedSetNode oldNode = nestedSetNodeMapper.selectById(node.getId());
        if (oldNode == null) {
            throw new TreeException("节点不存在");
        }
        
        // 保持左右值和层级不变
        node.setLft(oldNode.getLft());
        node.setRgt(oldNode.getRgt());
        node.setLevel(oldNode.getLevel());
        
        // 更新节点
        nestedSetNodeMapper.update(node);
        return node;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteNode(Long id) {
        // 查询节点
        NestedSetNode node = nestedSetNodeMapper.selectById(id);
        if (node == null) {
            throw new TreeException("节点不存在");
        }
        
        // 计算节点包含的元素个数（包括自身）
        int width = node.getRgt() - node.getLft() + 1;
        
        // 删除节点及其所有子节点
        List<NestedSetNode> descendants = nestedSetNodeMapper.selectDescendants(id);
        for (NestedSetNode descendant : descendants) {
            nestedSetNodeMapper.delete(descendant.getId());
        }
        nestedSetNodeMapper.delete(id);
        
        // 更新右值大于被删除节点右值的节点的右值（左移width个位置）
        nestedSetNodeMapper.updateRightValues(node.getRgt(), -width);
        
        // 更新左值大于被删除节点右值的节点的左值（左移width个位置）
        nestedSetNodeMapper.updateLeftValues(node.getRgt(), -width);
        
        return true;
    }

    @Override
    public NestedSetNode getNode(Long id) {
        return nestedSetNodeMapper.selectById(id);
    }

    @Override
    public List<NestedSetNode> getAllNodes() {
        return nestedSetNodeMapper.selectAll();
    }

    @Override
    public List<NestedSetNode> getChildNodes(Long parentId) {
        return nestedSetNodeMapper.selectByParentId(parentId);
    }

    @Override
    public List<NestedSetNode> getDescendants(Long id) {
        return nestedSetNodeMapper.selectDescendants(id);
    }

    @Override
    public List<NestedSetNode> getAncestors(Long id) {
        return nestedSetNodeMapper.selectAncestors(id);
    }

    @Override
    public List<NestedSetNodeTree> buildTree() {
        // 获取所有节点并按左值排序
        List<NestedSetNode> allNodes = nestedSetNodeMapper.selectAll();
        
        // 如果没有节点，返回空列表
        if (allNodes.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 转换为树节点
        List<NestedSetNodeTree> treeNodes = allNodes.stream()
                .map(NestedSetNodeTree::fromEntity)
                .collect(Collectors.toList());
        
        // 构建节点ID到树节点的映射
        Map<Long, NestedSetNodeTree> nodeMap = new HashMap<>();
        for (NestedSetNodeTree treeNode : treeNodes) {
            nodeMap.put(treeNode.getId(), treeNode);
        }
        
        // 构建树结构
        List<NestedSetNodeTree> rootNodes = new ArrayList<>();
        for (NestedSetNodeTree treeNode : treeNodes) {
            if (treeNode.getLevel() == 0) {
                // 根节点
                rootNodes.add(treeNode);
            } else {
                // 非根节点，找到父节点
                for (NestedSetNodeTree potentialParent : treeNodes) {
                    // 父节点的左值小于当前节点的左值，右值大于当前节点的右值，且层级比当前节点小1
                    if (potentialParent.getLft() < treeNode.getLft() && 
                        potentialParent.getRgt() > treeNode.getRgt() && 
                        potentialParent.getLevel() == treeNode.getLevel() - 1) {
                        potentialParent.getChildren().add(treeNode);
                        break;
                    }
                }
            }
        }
        
        return rootNodes;
    }
}