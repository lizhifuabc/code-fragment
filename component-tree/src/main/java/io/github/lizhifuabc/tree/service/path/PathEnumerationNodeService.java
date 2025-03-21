package io.github.lizhifuabc.tree.service.path;

import io.github.lizhifuabc.tree.domain.path.PathEnumerationNode;

import java.util.List;

/**
 * 路径枚举模型服务接口
 *
 * @author lizhifu
 * @since 2023/7/1
 */
public interface PathEnumerationNodeService {
    /**
     * 创建节点
     *
     * @param node 节点
     * @param parentId 父节点ID，如果为null则创建根节点
     * @return 创建后的节点
     */
    PathEnumerationNode createNode(PathEnumerationNode node, Long parentId);

    /**
     * 更新节点
     *
     * @param node 节点
     * @return 更新后的节点
     */
    PathEnumerationNode updateNode(PathEnumerationNode node);

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
    PathEnumerationNode getNode(Long id);

    /**
     * 获取所有节点
     *
     * @return 节点列表
     */
    List<PathEnumerationNode> getAllNodes();

    /**
     * 获取子节点
     *
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    List<PathEnumerationNode> getChildNodes(Long parentId);

    /**
     * 获取所有子孙节点
     *
     * @param id 节点ID
     * @return 子孙节点列表
     */
    List<PathEnumerationNode> getDescendants(Long id);
    
    /**
     * 构建树结构
     *
     * @return 树结构
     */
    List<PathEnumerationNodeTree> buildTree();
}