package com.ruoyi.main.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.main.service.IResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * com.ss.manaplat.common.web
 *
 * @author ASUS
 * @create 2024/6/26
 * @email fujinfeng@ss-cas.com
 **/
@RestController
@RequestMapping("/resource")
public class ResourceController extends BaseController {
    @Autowired
    private IResourceService resourceService;


    @RequestMapping(value = {"/get"}, method = {RequestMethod.POST})
    public AjaxResult get(@RequestBody SysUser user, BindingResult bindingResult) throws Exception {
        AjaxResult success = null;
        try {
            SysUser sysUser = resourceService.get(user.getUserId());
            success = AjaxResult.success(sysUser);
        } catch (Exception e) {
            logger.error("查询用户权限失败：" + e.toString(), e);
            success = AjaxResult.error("查询用户权限失败");
        }
        return success;
    }


}
