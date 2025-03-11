package io.github.lizhifuabc.rbac.system.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import io.github.lizhifuabc.rbac.system.service.SysUserService;
import io.github.lizhifuabc.rbac.system.domain.entity.SysUser;
import io.github.lizhifuabc.rbac.system.dao.SysUserDao;

/**
 * <p>
 * 员工表 Service 实现类
 * </p>
 *
 * @author lizhifu
 * @since 2025-03-06
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserDao sysUserDao;

    protected SysUserDao getDao() {
        return sysUserDao;
    }
    
}
