package io.github.lizhifuabc.tree.mapper.closure;

import io.github.lizhifuabc.tree.domain.closure.ClosurePath;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 闭包表模型路径Mapper接口
 *
 * @author lizhifu
 * @since 2023/7/1
 */
public interface ClosurePathMapper {
    /**
     * 插入路径
     *
     * @param path 路径
     * @return 影响行数
     */
    int insert(ClosurePath path);

    /**
     * 删除路径
     *
     * @param ancestorId 祖先节点ID
     * @param descendantId 后代节点ID
     * @return 影响行数
     */
    int delete(@Param("ancestorId") Long ancestorId, @Param("descendantId") Long descendantId);

    /**
     * 删除与节点相关的所有路径
     *
     * @param nodeId 节点ID
     * @return 影响行数
     */
    int deleteByNodeId(@Param("nodeId") Long nodeId);

    /**
     * 查询节点的所有祖先节点ID
     *
     * @param descendantId 后代节点ID
     * @return 祖先节点ID列表
     */
    List<Long> selectAncestorIds(@Param("descendantId") Long descendantId);

    /**
     * 查询节点的所有后代节点ID
     *
     * @param ancestorId 祖先节点ID
     * @return 后代节点ID列表
     */
    List<Long> selectDescendantIds(@Param("ancestorId") Long ancestorId);

    /**
     * 查询节点的直接子节点ID
     *
     * @param parentId 父节点ID
     * @return 子节点ID列表
     */
    List<Long> selectChildIds(@Param("parentId") Long parentId);

    /**
     * 批量插入路径
     *
     * @param paths 路径列表
     * @return 影响行数
     */
    int batchInsert(List<ClosurePath> paths);
}