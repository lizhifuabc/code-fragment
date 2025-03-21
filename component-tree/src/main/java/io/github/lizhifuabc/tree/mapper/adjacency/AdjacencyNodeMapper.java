package io.github.lizhifuabc.tree.mapper.adjacency;

import io.github.lizhifuabc.tree.domain.adjacency.AdjacencyNode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 邻接表模型Mapper接口
 *
 * @author lizhifu
 * @since 2023/7/1
 */
public interface AdjacencyNodeMapper {
    /**
     * 插入节点
     *
     * @param node 节点
     * @return 影响行数
     */
    int insert(AdjacencyNode node);

    /**
     * 更新节点
     *
     * @param node 节点
     * @return 影响行数
     */
    int update(AdjacencyNode node);

    /**
     * 删除节点（逻辑删除）
     *
     * @param id 节点ID
     * @return 影响行数
     */
    int delete(@Param("id") Long id);

    /**
     * 根据ID查询节点
     *
     * @param id 节点ID
     * @return 节点
     */
    AdjacencyNode selectById(@Param("id") Long id);

    /**
     * 查询所有节点
     *
     * @return 节点列表
     */
    List<AdjacencyNode> selectAll();

    /**
     * 根据父节点ID查询子节点
     *
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    List<AdjacencyNode> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 查询节点的所有子孙节点
     *
     * @param id 节点ID
     * @return 子孙节点列表
     */
    List<AdjacencyNode> selectDescendants(@Param("id") Long id);
}