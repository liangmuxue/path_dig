package com.ruoyi.web.controller.system;

import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.framework.web.service.SysPermissionService;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.main.dto.SysRoleDTO;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * com.ss.manaplat.common.web
 *
 * @author JF
 * @create 2024/6/25
 * @email fujinfeng@ss-cas.com
 **/
@RestController
@RequestMapping("/role")
public class RoleController extends BaseController {

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private ISysUserService userService;


    @RequestMapping(value = {"/insert"}, method = {RequestMethod.POST})
    public AjaxResult insert(@RequestBody JSONObject role, BindingResult bindingResult) throws Exception {
        AjaxResult success = null;
        try {

            int i = roleService.insertResourceRole(role);
            success = AjaxResult.success(i);
        } catch (Exception e) {
            logger.error("新增角色失败{}：" + e.toString(), e);
            success = AjaxResult.error(e.getMessage() + ",新增角色失败");
        }
        return success;
    }

    @RequestMapping(value = {"/list"}, method = {RequestMethod.POST})
    public AjaxResult list(@RequestBody SysRoleDTO roleDTO, BindingResult bindingResult) throws Exception {
        AjaxResult success = null;
        try {
            PageHelper.startPage(roleDTO.getPageNum(), roleDTO.getPageSize());
            SysRole role = new SysRole();
            role.setRoleName(roleDTO.getRoleName());
//            BeanUtils.copyProperties(roleDTO,role);
            PageInfo<SysRole> sysRolePageInfo = roleService.listRoleAndResource(role, roleDTO.getPageNum(), roleDTO.getPageSize());
            List<SysRole> list = sysRolePageInfo.getList();
            roleService.selectRoleResourceRef(list);
            success = AjaxResult.success(list);
        } catch (Exception e) {
            logger.error("查询失败{}：" + e.toString(), e);
            success = AjaxResult.error(e.getMessage() + ",查询失败");
        }
        return success;
    }


    @RequestMapping(value = {"/detail"}, method = {RequestMethod.POST})
    public AjaxResult detail(@RequestBody JSONObject role, BindingResult bindingResult) throws Exception {
        AjaxResult success = null;
        try {
            JSONObject jsonObject = roleService.detail(role);
            success = AjaxResult.success(jsonObject);
        } catch (Exception e) {
            logger.error("查询角色失败{}：" + e.toString(), e);
            success = AjaxResult.error("查询角色失败," + e.getMessage());
        }
        return success;
    }

    @RequestMapping(value = {"/update"}, method = {RequestMethod.POST})
    public AjaxResult update(@RequestBody JSONObject role, BindingResult bindingResult) throws Exception {
        AjaxResult success = null;
        try {
            Integer i = roleService.updateResourceRole(role);
            success = AjaxResult.success(i);
        } catch (Exception e) {
            logger.error("修改角色失败{}：" + e.toString(), e);
            success = AjaxResult.error("修改角色失败," + e.getMessage());
        }
        return success;
    }

    @RequestMapping(value = {"/getAll"}, method = {RequestMethod.POST})
    public AjaxResult getAll(@RequestBody  JSONObject jsonObject) throws Exception {
        AjaxResult success = null;
        try {
            //1查超级管理员
            Integer isConditionQuery = jsonObject.getInteger("isConditionQuery");
            List<SysRole> all = roleService.getAll(isConditionQuery);
            success = AjaxResult.success(all);
        } catch (Exception e) {
            logger.error("查询角色失败,{}：" + e.toString(), e);
            success = AjaxResult.error("修改角色失败," + e.getMessage());
        }
        return success;
    }

    @RequestMapping(value = {"/deleteRole"}, method = {RequestMethod.POST})
    public AjaxResult deleteRole(@RequestBody  JSONObject jsonObject) throws Exception {
        AjaxResult success = null;
        try {
            int i = roleService.deleteRoleAndResource(jsonObject);
            success = AjaxResult.success(i);
        } catch (Exception e) {
            logger.error("删除角色失败,{}：" + e.toString(), e);
            success = AjaxResult.error("删除角色失败," + e.getMessage());
        }
        return success;
    }

}
