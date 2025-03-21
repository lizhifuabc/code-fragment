package io.github.lizhifuabc.tree.mapper.closure;

import io.github.lizhifuabc.tree.domain.closure.ClosureNode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 闭包表模型节点Mapper接口
 *
 * @author lizhifu
 * @since 2023/7/1
 */
public interface ClosureNodeMapper {
    /**
     * 插入节点
     *
     * @param node 节点
     * @return 影响行数
     */
    int insert(ClosureNode node);

    /**
     * 更新节点
     *
     * @param node 节点
     * @return 影响行数
     */
    int update(ClosureNode node);

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
    ClosureNode selectById(@Param("id") Long id);

    /**
     * 查询所有节点
     *
     * @return 节点列表
     */
    List<ClosureNode> selectAll();
}