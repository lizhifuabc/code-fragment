package io.github.lizhifuabc.tree.service.nested;

import io.github.lizhifuabc.tree.domain.nested.NestedSetNode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 嵌套集模型树节点
 *
 * @author lizhifu
 * @since 2023/7/1
 */
@Data
public class NestedSetNodeTree {
    /**
     * 节点ID
     */
    private Long id;

    /**
     * 左值
     */
    private Integer lft;

    /**
     * 右值
     */
    private Integer rgt;

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
    private List<NestedSetNodeTree> children = new ArrayList<>();

    /**
     * 从实体转换为树节点
     *
     * @param node 实体
     * @return 树节点
     */
    public static NestedSetNodeTree fromEntity(NestedSetNode node) {
        NestedSetNodeTree tree = new NestedSetNodeTree();
        tree.setId(node.getId());
        tree.setLft(node.getLft());
        tree.setRgt(node.getRgt());
        tree.setName(node.getName());
        tree.setLevel(node.getLevel());
        tree.setDescription(node.getDescription());
        return tree;
    }
}