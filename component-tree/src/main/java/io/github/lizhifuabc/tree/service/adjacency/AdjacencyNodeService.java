package io.github.lizhifuabc.tree.service.adjacency;

import io.github.lizhifuabc.tree.domain.adjacency.AdjacencyNode;

import java.util.List;

/**
 * 邻接表模型服务接口
 *
 * @author lizhifu
 * @since 2023/7/1
 */
public interface AdjacencyNodeService {
    /**
     * 创建节点
     *
     * @param node 节点
     * @return 创建后的节点
     */
    AdjacencyNode createNode(AdjacencyNode node);

    /**
     * 更新节点
     *
     * @param node 节点
     * @return 更新后的节点
     */
    AdjacencyNode updateNode(AdjacencyNode node);

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
    AdjacencyNode getNode(Long id);

    /**
     * 获取所有节点
     *
     * @return 节点列表
     */
    List<AdjacencyNode> getAllNodes();

    /**
     * 获取子节点
     *
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    List<AdjacencyNode> getChildNodes(Long parentId);

    /**
     * 获取所有子孙节点
     *
     * @param id 节点ID
     * @return 子孙节点列表
     */
    List<AdjacencyNode> getDescendants(Long id);
    
    /**
     * 构建树结构
     *
     * @return 树结构
     */
    List<AdjacencyNodeTree> buildTree();
}