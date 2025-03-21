package io.github.lizhifuabc.tree.mapper.path;

import io.github.lizhifuabc.tree.domain.path.PathEnumerationNode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 路径枚举模型Mapper接口
 *
 * @author lizhifu
 * @since 2023/7/1
 */
public interface PathEnumerationNodeMapper {
    /**
     * 插入节点
     *
     * @param node 节点
     * @return 影响行数
     */
    int insert(PathEnumerationNode node);

    /**
     * 更新节点
     *
     * @param node 节点
     * @return 影响行数
     */
    int update(PathEnumerationNode node);

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
    PathEnumerationNode selectById(@Param("id") Long id);

    /**
     * 查询所有节点
     *
     * @return 节点列表
     */
    List<PathEnumerationNode> selectAll();

    /**
     * 根据父节点ID查询子节点
     * 通过路径匹配查找直接子节点
     *
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    List<PathEnumerationNode> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 查询节点的所有子孙节点
     * 通过路径前缀匹配查找所有子孙节点
     *
     * @param id 节点ID
     * @return 子孙节点列表
     */
    List<PathEnumerationNode> selectDescendants(@Param("id") Long id);
    
    /**
     * 根据路径查询节点
     *
     * @param path 路径
     * @return 节点
     */
    PathEnumerationNode selectByPath(@Param("path") String path);
}