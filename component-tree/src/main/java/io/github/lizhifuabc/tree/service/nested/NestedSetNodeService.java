package io.github.lizhifuabc.tree.service.nested;

import io.github.lizhifuabc.tree.domain.nested.NestedSetNode;

import java.util.List;

/**
 * 嵌套集模型服务接口
 *
 * @author lizhifu
 * @since 2023/7/1
 */
public interface NestedSetNodeService {
    /**
     * 创建根节点
     *
     * @param node 节点
     * @return 创建后的节点
     */
    NestedSetNode createRootNode(NestedSetNode node);
    
    /**
     * 创建子节点
     *
     * @param node 节点
     * @param parentId 父节点ID
     * @return 创建后的节点
     */
    NestedSetNode createChildNode(NestedSetNode node, Long parentId);

    /**
     * 更新节点
     *
     * @param node 节点
     * @return 更新后的节点
     */
    NestedSetNode updateNode(NestedSetNode node);

    /**
     * 删除节点
     *
     * @param id 节点ID
     * @return 是否删除成功
     */
    boolean deleteNode(Long id);

    /**
     * 获取节点
     *
     * @param id 节点ID
     * @return 节点
     */
    NestedSetNode getNode(Long id);

    /**
     * 获取所有节点
     *
     * @return 节点列表
     */
    List<NestedSetNode> getAllNodes();

    /**
     * 获取子节点
     *
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    List<NestedSetNode> getChildNodes(Long parentId);

    /**
     * 获取所有子孙节点
     *
     * @param id 节点ID
     * @return 子孙节点列表
     */
    List<NestedSetNode> getDescendants(Long id);
    
    /**
     * 获取所有祖先节点
     *
     * @param id 节点ID
     * @return 祖先节点列表
     */
    List<NestedSetNode> getAncestors(Long id);
    
    /**
     * 构建树结构
     *
     * @return 树结构
     */
    List<NestedSetNodeTree> buildTree();
}