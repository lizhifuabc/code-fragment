package io.github.lizhifuabc.tree.service.materialized;

import io.github.lizhifuabc.tree.domain.materialized.MaterializedPathNode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 物化路径模型树节点
 *
 * @author lizhifu
 * @since 2023/7/1
 */
@Data
public class MaterializedPathNodeTree {
    /**
     * 节点ID
     */
    private Long id;

    /**
     * 物化路径
     */
    private String path;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 节点层级
     */
    private Integer level;

    /**
     * 节点描述
     */
    private String description;

    /**
     * 子节点列表
     */
    private List<MaterializedPathNodeTree> children = new ArrayList<>();

    /**
     * 从实体转换为树节点
     *
     * @param node 实体
     * @return 树节点
     */
    public static MaterializedPathNodeTree fromEntity(MaterializedPathNode node) {
        MaterializedPathNodeTree tree = new MaterializedPathNodeTree();
        tree.setId(node.getId());
        tree.setPath(node.getPath());
        tree.setName(node.getName());
        tree.setLevel(node.getLevel());
        tree.setDescription(node.getDescription());
        return tree;
    }
}