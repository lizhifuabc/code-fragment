package io.github.lizhifuabc.rbac.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import io.github.lizhifuabc.rbac.system.domain.entity.SysUser;
import cn.xbatis.core.mybatis.mapper.MybatisMapper;

/**
 * <p>
 * 员工表 Mapper 接口
 * </p>
 *
 * @author lizhifu
 * @since 2025-03-06
 */
@Mapper
public interface SysUserMapper extends MybatisMapper<SysUser> {
}
