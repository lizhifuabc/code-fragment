package io.github.lizhifuabc.tree.service.adjacency;

import io.github.lizhifuabc.tree.domain.adjacency.AdjacencyNode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 邻接表模型树节点
 *
 * @author lizhifu
 * @since 2023/7/1
 */
@Data
public class AdjacencyNodeTree {
    /**
     * 节点ID
     */
    private Long id;

    /**
     * 父节点ID
     */
    private Long parentId;

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
    private List<AdjacencyNodeTree> children = new ArrayList<>();

    /**
     * 从实体转换为树节点
     *
     * @param node 实体
     * @return 树节点
     */
    public static AdjacencyNodeTree fromEntity(AdjacencyNode node) {
        AdjacencyNodeTree tree = new AdjacencyNodeTree();
        tree.setId(node.getId());
        tree.setParentId(node.getParentId());
        tree.setName(node.getName());
        tree.setLevel(node.getLevel());
        tree.setDescription(node.getDescription());
        return tree;
    }
}