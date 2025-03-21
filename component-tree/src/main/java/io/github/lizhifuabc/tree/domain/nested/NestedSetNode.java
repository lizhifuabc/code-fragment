package io.github.lizhifuabc.tree.domain.nested;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 嵌套集模型实体类
 *
 * @author lizhifu
 * @since 2023/7/1
 */
@Data
public class NestedSetNode {
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
     * 是否禁用
     */
    private Integer disabledFlag;

    /**
     * 是否删除
     */
    private Integer deletedFlag;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}