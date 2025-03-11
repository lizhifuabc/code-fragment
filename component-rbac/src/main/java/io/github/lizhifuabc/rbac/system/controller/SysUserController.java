package io.github.lizhifuabc.rbac.system.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.beans.factory.annotation.Autowired;
import io.github.lizhifuabc.rbac.system.service.SysUserService;
import io.github.lizhifuabc.rbac.system.domain.entity.SysUser;
import cn.xbatis.core.mybatis.mapper.context.Pager;

/**
 * <p>
 * 员工表 控制器
 * </p>
 *
 * @author lizhifu
 * @since 2025-03-06
 */
@RestController
@RequestMapping("/sysUser")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 根据ID查询
     */
    @GetMapping("/get")
    public Object get(Long employeeId){
        // TODO 代码自动生成 未实现（注意）
        return null;
    }

    /**
     * 新增
     */
    @PostMapping("/save")
    public Object save(SysUser sysUser){
        // TODO 代码自动生成 未实现（注意）
        return null;
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    public Object update(SysUser sysUser){
        // TODO 代码自动生成 未实现（注意）
        return null;
    }

    /**
     * 根据ID删除
     */
    @DeleteMapping("/delete")
    public Object delete(Long employeeId){
        // TODO 代码自动生成 未实现（注意）
        return null;
    }

    /**
     * 分页查询
     */
    @GetMapping("/find")
    public Object find(Pager<SysUser> pager){
        // TODO 代码自动生成 未实现（注意）
        return null;
    }

}
