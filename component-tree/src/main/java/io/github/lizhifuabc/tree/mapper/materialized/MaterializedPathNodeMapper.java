package io.github.lizhifuabc.tree.mapper.materialized;

import io.github.lizhifuabc.tree.domain.materialized.MaterializedPathNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物化路径模型Mapper接口
 *
 * @author lizhifu
 * @since 2023/7/1
 */
@Mapper
public interface MaterializedPathNodeMapper {
    /**
     * 插入节点
     *
     * @param node 节点
     * @return 影响行数
     */
    int insert(MaterializedPathNode node);

    /**
     * 更新节点
     *
     * @param node 节点
     * @return 影响行数
     */
    int update(MaterializedPathNode node);

    /**
     * 删除节点
     *
     * @param id 节点ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 根据ID查询节点
     *
     * @param id 节点ID
     * @return 节点
     */
    MaterializedPathNode selectById(Long id);

    /**
     * 查询所有节点
     *
     * @return 节点列表
     */
    List<MaterializedPathNode> selectAll();

    /**
     * 根据父节点ID查询子节点
     *
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    List<MaterializedPathNode> selectByParentId(Long parentId);

    /**
     * 根据路径前缀查询子孙节点
     *
     * @param pathPrefix 路径前缀
     * @return 子孙节点列表
     */
    List<MaterializedPathNode> selectByPathPrefix(@Param("pathPrefix") String pathPrefix);

    /**
     * 根据路径查询祖先节点
     *
     * @param path 路径
     * @return 祖先节点列表
     */
    List<MaterializedPathNode> selectAncestorsByPath(@Param("path") String path);
}