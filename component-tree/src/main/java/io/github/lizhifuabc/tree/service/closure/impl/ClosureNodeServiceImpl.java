package io.github.lizhifuabc.tree.service.closure.impl;

import io.github.lizhifuabc.tree.common.TreeException;
import io.github.lizhifuabc.tree.domain.closure.ClosureNode;
import io.github.lizhifuabc.tree.domain.closure.ClosurePath;
import io.github.lizhifuabc.tree.mapper.closure.ClosureNodeMapper;
import io.github.lizhifuabc.tree.mapper.closure.ClosurePathMapper;
import io.github.lizhifuabc.tree.service.closure.ClosureNodeService;
import io.github.lizhifuabc.tree.service.closure.ClosureNodeTree;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 闭包表模型服务实现
 *
 * @author lizhifu
 * @since 2023/7/1
 */
@Service
public class ClosureNodeServiceImpl implements ClosureNodeService {

    private final ClosureNodeMapper closureNodeMapper;
    private final ClosurePathMapper closurePathMapper;

    public ClosureNodeServiceImpl(ClosureNodeMapper closureNodeMapper, ClosurePathMapper closurePathMapper) {
        this.closureNodeMapper = closureNodeMapper;
        this.closurePathMapper = closurePathMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClosureNode createNode(ClosureNode node, Long parentId) {
        // 设置节点层级
        if (parentId == null) {
            // 根节点
            node.setLevel(0);
        } else {
            // 非根节点，查询父节点
            ClosureNode parentNode = closureNodeMapper.selectById(parentId);
            if (parentNode == null) {
                throw new TreeException("父节点不存在");
            }
            // 子节点层级 = 父节点层级 + 1
            node.setLevel(parentNode.getLevel() + 1);
        }

        // 插入节点
        closureNodeMapper.insert(node);

        // 插入路径关系
        List<ClosurePath> paths = new ArrayList<>();

        // 1. 自身到自身的路径，距离为0
        ClosurePath selfPath = new ClosurePath();
        selfPath.setAncestorId(node.getId());
        selfPath.setDescendantId(node.getId());
        selfPath.setDistance(0);
        paths.add(selfPath);

        if (parentId != null) {
            // 2. 获取父节点的所有祖先节点ID
            List<Long> ancestorIds = closurePathMapper.selectAncestorIds(parentId);
            
            // 3. 为每个祖先节点创建到新节点的路径
            for (Long ancestorId : ancestorIds) {
                // 查询祖先到父节点的距离
                int distance = 0;
                for (ClosurePath path : paths) {
                    if (path.getAncestorId().equals(ancestorId) && path.getDescendantId().equals(parentId)) {
                        distance = path.getDistance();
                        break;
                    }
                }
                
                // 创建祖先到新节点的路径，距离 = 祖先到父节点的距离 + 1
                ClosurePath ancestorPath = new ClosurePath();
                ancestorPath.setAncestorId(ancestorId);
                ancestorPath.setDescendantId(node.getId());
                ancestorPath.setDistance(distance + 1);
                paths.add(ancestorPath);
            }
        }

        // 批量插入路径
        if (!paths.isEmpty()) {
            closurePathMapper.batchInsert(paths);
        }

        return node;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClosureNode updateNode(ClosureNode node) {
        // 查询原节点
        ClosureNode oldNode = closureNodeMapper.selectById(node.getId());
        if (oldNode == null) {
            throw new TreeException("节点不存在");
        }

        // 更新节点信息（不改变层级和路径关系）
        closureNodeMapper.update(node);
        return node;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteNode(Long id) {
        // 查询是否有子节点
        List<Long> childIds = closurePathMapper.selectChildIds(id);
        if (!childIds.isEmpty()) {
            throw new TreeException("该节点下有子节点，不能删除");
        }

        // 删除节点
        closureNodeMapper.delete(id);

        // 删除与该节点相关的所有路径
        closurePathMapper.deleteByNodeId(id);

        return true;
    }

    @Override
    public ClosureNode getNode(Long id) {
        return closureNodeMapper.selectById(id);
    }

    @Override
    public List<ClosureNode> getAllNodes() {
        return closureNodeMapper.selectAll();
    }

    @Override
    public List<ClosureNode> getChildNodes(Long parentId) {
        // 获取直接子节点ID
        List<Long> childIds = closurePathMapper.selectChildIds(parentId);
        if (childIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 查询子节点信息
        List<ClosureNode> childNodes = new ArrayList<>();
        for (Long childId : childIds) {
            ClosureNode childNode = closureNodeMapper.selectById(childId);
            if (childNode != null) {
                childNodes.add(childNode);
            }
        }

        return childNodes;
    }

    @Override
    public List<ClosureNode> getDescendants(Long id) {
        // 获取所有后代节点ID
        List<Long> descendantIds = closurePathMapper.selectDescendantIds(id);
        if (descendantIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 查询后代节点信息
        List<ClosureNode> descendants = new ArrayList<>();
        for (Long descendantId : descendantIds) {
            if (!descendantId.equals(id)) { // 排除自身
                ClosureNode descendant = closureNodeMapper.selectById(descendantId);
                if (descendant != null) {
                    descendants.add(descendant);
                }
            }
        }

        return descendants;
    }

    @Override
    public List<ClosureNode> getAncestors(Long id) {
        // 获取所有祖先节点ID
        List<Long> ancestorIds = closurePathMapper.selectAncestorIds(id);
        if (ancestorIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 查询祖先节点信息
        List<ClosureNode> ancestors = new ArrayList<>();
        for (Long ancestorId : ancestorIds) {
            if (!ancestorId.equals(id)) { // 排除自身
                ClosureNode ancestor = closureNodeMapper.selectById(ancestorId);
                if (ancestor != null) {
                    ancestors.add(ancestor);
                }
            }
        }

        return ancestors;
    }

    @Override
    public List<ClosureNodeTree> buildTree() {
        // 获取所有节点
        List<ClosureNode> allNodes = closureNodeMapper.selectAll();
        if (allNodes.isEmpty()) {
            return new ArrayList<>();
        }

        // 转换为树节点
        Map<Long, ClosureNodeTree> treeMap = new HashMap<>();
        for (ClosureNode node : allNodes) {
            treeMap.put(node.getId(), ClosureNodeTree.fromEntity(node));
        }

        // 构建树结构
        List<ClosureNodeTree> rootNodes = new ArrayList<>();
        for (ClosureNode node : allNodes) {
            if (node.getLevel() == 0) {
                // 根节点
                rootNodes.add(treeMap.get(node.getId()));
            } else {
                // 非根节点，查找其父节点
                List<Long> parentIds = closurePathMapper.selectAncestorIds(node.getId());
                for (Long parentId : parentIds) {
                    if (!parentId.equals(node.getId())) { // 排除自身
                        ClosureNodeTree parentTree = treeMap.get(parentId);
                        ClosureNodeTree childTree = treeMap.get(node.getId());
                        if (parentTree != null && childTree != null) {
                            // 检查是否为直接父子关系（距离为1）
                            // 这里简化处理，通过层级判断
                            if (parentTree.getLevel() == childTree.getLevel() - 1) {
                                parentTree.getChildren().add(childTree);
                            }
                        }
                    }
                }
            }
        }

        return rootNodes;
    }
}