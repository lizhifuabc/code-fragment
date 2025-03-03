package io.github.lizhifuabc.rbac.system.mapper;

import cn.xbatis.core.mybatis.mapper.MybatisMapper;
import io.github.lizhifuabc.rbac.system.domain.entity.SysUserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户
 *
 * @author lizhifu
 * @since 2025/3/3
 */
@Mapper
public interface SysUserMapper extends MybatisMapper<SysUserEntity> {

}
