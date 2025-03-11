package io.github.lizhifuabc.rbac.system.domain.entity;

import lombok.experimental.FieldNameConstants;
import lombok.Data;
import java.time.LocalDateTime;
import cn.xbatis.db.annotations.TableId;
import cn.xbatis.db.annotations.TableField;
import cn.xbatis.db.annotations.Table;
import cn.xbatis.db.annotations.LogicDelete;
import cn.xbatis.db.IdAutoType;

/**
 * <p>
 * 员工表
 * </p>
 *
 * @author lizhifu
 * @since 2025-03-06
 */
@Data
@FieldNameConstants
@Table(value ="t_sys_user")
public class SysUser {

    /**
     * 主键
     */
    @TableId(IdAutoType.AUTO)
    private Long employeeId;

    /**
     * 登录帐号
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

    private String avatar;

    /**
     * 性别
     */
    @TableField(defaultValue = "0")
    private Boolean gender;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 部门id
     */
    private Integer departmentId;

    /**
     * 职务ID
     */
    private Long positionId;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 是否被禁用 0否1是
     */
    private Byte disabledFlag;

    /**
     * 是否删除0否 1是
     */
    @LogicDelete(beforeValue="0",afterValue="1",deleteTimeField="deleted_time")
    private Byte deletedFlag;

    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;

    /**
     * 是否为超级管理员: 0 不是，1是
     */
    @TableField(defaultValue = "0")
    private Byte administratorFlag;

    /**
     * 备注
     */
    private String remark;

    /**
     * 更新时间
     */
    @TableField(defaultValue = "{NOW}",updateDefaultValue = "{NOW}")
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    @TableField(defaultValue = "{NOW}")
    private LocalDateTime createTime;

}
