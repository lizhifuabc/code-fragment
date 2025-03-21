package io.github.lizhifuabc.tree.service.path;

import io.github.lizhifuabc.tree.domain.path.PathEnumerationNode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 路径枚举模型树节点
 *
 * @author lizhifu
 * @since 2023/7/1
 */
@Data
public class PathEnumerationNodeTree {
    /**
     * 节点ID
     */
    private Long id;

    /**
     * 路径
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
    private List<PathEnumerationNodeTree> children = new ArrayList<>();

    /**
     * 从实体转换为树节点
     *
     * @param node 实体
     * @return 树节点
     */
    public static PathEnumerationNodeTree fromEntity(PathEnumerationNode node) {
        PathEnumerationNodeTree tree = new PathEnumerationNodeTree();
        tree.setId(node.getId());
        tree.setPath(node.getPath());
        tree.setName(node.getName());
        tree.setLevel(node.getLevel());
        tree.setDescription(node.getDescription());
        return tree;
    }
}