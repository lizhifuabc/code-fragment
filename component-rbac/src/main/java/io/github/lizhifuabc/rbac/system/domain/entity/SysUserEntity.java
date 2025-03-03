package io.github.lizhifuabc.rbac.system.domain.entity;

import cn.xbatis.db.IdAutoType;
import cn.xbatis.db.annotations.LogicDelete;
import cn.xbatis.db.annotations.Table;
import cn.xbatis.db.annotations.TableField;
import cn.xbatis.db.annotations.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统用户
 *
 * @author lizhifu
 * @since 2025/3/3
 */
@Data
@Table("t_user")
public class SysUserEntity {
    @TableId(value = IdAutoType.AUTO)
    private Long userId;

    /**
     * 登录账号
     */
    private String loginName;

    /**
     * 登录密码
     */
    private String loginPwd;

    /**
     * 员工名称
     */
    private String actualName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 部门id
     */
    private Long departmentId;

    /**
     * 职务级别ID
     */
    private Long positionId;

    /**
     * 是否为超级管理员: 0 不是，1是
     */
    private Boolean administratorFlag;

    /**
     * 是否被禁用 0否1是
     */
    private Boolean disabledFlag;

    /**
     * 是否删除0否 1是
     */
    @LogicDelete(beforeValue = "0", afterValue = "1", deleteTimeField = "deleteTime")
    private Boolean deletedFlag;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;

    /**
     * 更新时间
     */
    @TableField(updateDefaultValue = "{NOW}")
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    @TableField(defaultValue = "{NOW}")
    private LocalDateTime createTime;
}
