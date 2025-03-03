package io.github.lizhifuabc.rbac.system.dao.impl;

import cn.xbatis.core.mvc.impl.DaoImpl;
import io.github.lizhifuabc.rbac.system.dao.SysUserDao;
import io.github.lizhifuabc.rbac.system.domain.entity.SysUserEntity;
import io.github.lizhifuabc.rbac.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * dao
 *
 * @author lizhifu
 * @since 2025/3/3
 */
@Repository
public class SysUserDaoImpl extends DaoImpl<SysUserEntity, Long> implements SysUserDao {
    @Autowired
    public SysUserDaoImpl(SysUserMapper sysUserMapper) {
        super(sysUserMapper);
    }
}
