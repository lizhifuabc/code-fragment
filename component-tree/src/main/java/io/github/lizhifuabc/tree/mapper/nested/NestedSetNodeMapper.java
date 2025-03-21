package io.github.lizhifuabc.tree.mapper.nested;

import io.github.lizhifuabc.tree.domain.nested.NestedSetNode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 嵌套集模型Mapper接口
 *
 * @author lizhifu
 * @since 2023/7/1
 */
public interface NestedSetNodeMapper {
    /**
     * 插入节点
     *
     * @param node 节点
     * @return 影响行数
     */
    int insert(NestedSetNode node);

    /**
     * 更新节点
     *
     * @param node 节点
     * @return 影响行数
     */
    int update(NestedSetNode node);

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
    NestedSetNode selectById(@Param("id") Long id);

    /**
     * 查询所有节点
     *
     * @return 节点列表
     */
    List<NestedSetNode> selectAll();

    /**
     * 查询子节点
     * 子节点的左值在父节点的左值和右值之间，且层级比父节点大1
     *
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    List<NestedSetNode> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 查询所有子孙节点
     * 子孙节点的左值在父节点的左值和右值之间
     *
     * @param id 节点ID
     * @return 子孙节点列表
     */
    List<NestedSetNode> selectDescendants(@Param("id") Long id);
    
    /**
     * 查询节点的所有祖先节点
     * 祖先节点的左值小于当前节点的左值，右值大于当前节点的右值
     *
     * @param id 节点ID
     * @return 祖先节点列表
     */
    List<NestedSetNode> selectAncestors(@Param("id") Long id);
    
    /**
     * 更新节点的左值和右值
     * 用于在插入和删除节点时调整其他节点的左右值
     *
     * @param lft 左值起点
     * @param increment 增量值（可为负数）
     * @return 影响行数
     */
    int updateLeftValues(@Param("lft") Integer lft, @Param("increment") Integer increment);
    
    /**
     * 更新节点的右值
     * 用于在插入和删除节点时调整其他节点的右值
     *
     * @param rgt 右值起点
     * @param increment 增量值（可为负数）
     * @return 影响行数
     */
    int updateRightValues(@Param("rgt") Integer rgt, @Param("increment") Integer increment);
}