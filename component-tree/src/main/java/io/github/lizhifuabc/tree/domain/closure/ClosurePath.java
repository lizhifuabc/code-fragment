package io.github.lizhifuabc.tree.domain.closure;

import lombok.Data;

/**
 * 闭包表模型路径实体类
 *
 * @author lizhifu
 * @since 2023/7/1
 */
@Data
public class ClosurePath {
    /**
     * 祖先节点ID
     */
    private Long ancestorId;

    /**
     * 后代节点ID
     */
    private Long descendantId;

    /**
     * 距离，0表示自己，1表示直接父子关系，以此类推
     */
    private Integer distance;
}