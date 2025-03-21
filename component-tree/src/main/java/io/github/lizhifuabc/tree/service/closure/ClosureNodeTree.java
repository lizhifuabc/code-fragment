package io.github.lizhifuabc.tree.service.closure;

import io.github.lizhifuabc.tree.domain.closure.ClosureNode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 闭包表模型树节点
 *
 * @author lizhifu
 * @since 2023/7/1
 */
@Data
public class ClosureNodeTree {
    /**
     * 节点ID
     */
    private Long id;

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
    private List<ClosureNodeTree> children = new ArrayList<>();

    /**
     * 从实体转换为树节点
     *
     * @param node 实体
     * @return 树节点
     */
    public static ClosureNodeTree fromEntity(ClosureNode node) {
        ClosureNodeTree tree = new ClosureNodeTree();
        tree.setId(node.getId());
        tree.setName(node.getName());
        tree.setLevel(node.getLevel());
        tree.setDescription(node.getDescription());
        return tree;
    }
}