package io.github.lizhifuabc.rbac.system.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import io.github.lizhifuabc.rbac.system.mapper.SysUserMapper;
import io.github.lizhifuabc.rbac.system.domain.entity.SysUser;
import io.github.lizhifuabc.rbac.system.dao.SysUserDao;
import cn.xbatis.core.mvc.impl.DaoImpl;

/**
 * <p>
 * 员工表 Dao 实现类
 * </p>
 *
 * @author lizhifu
 * @since 2025-03-06
 */
@Repository
public class SysUserDaoImpl extends DaoImpl<SysUser, Long> implements SysUserDao {

    @Autowired
    public SysUserDaoImpl (SysUserMapper sysUserMapper){
        super(sysUserMapper);
    }

    @Override
    protected SysUserMapper getMapper(){
        return (SysUserMapper) this.mapper;
    }

}
