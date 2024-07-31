package com.ruoyi.web.controller.system;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.user.UserPasswordNotMatchException;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.framework.manager.factory.AsyncFactory;
import com.ruoyi.main.dto.SysUserDTO;
import com.ruoyi.system.service.ISysPostService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("/dig/user")
public class UserController extends BaseController {
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysPostService postService;
    @Autowired
    private AuthenticationManager authenticationManager;


    @PostMapping("/list")
    public Object list(@RequestBody SysUserDTO user) {
        PageHelper.startPage(user.getPageNum(), user.getPageSize());
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(user, sysUser);
        PageInfo<SysUser> sysUserPageInfo = userService.selectUserPage(sysUser, user.getPageNum(), user.getPageSize());
        List<SysUser> list = sysUserPageInfo.getList();
        for (SysUser su : list) {
            if (su.getLoginDate() != null) {
                su.setLoginTime(su.getLoginDate().getTime());
            }
        }
        TableDataInfo dataTable = getDataTable(list);
        JSONObject res =  (JSONObject) JSON.toJSON(dataTable);
        res.put("totalPage",(res.getInteger("total") + user.getPageSize() - 1) / user.getPageSize());
        return res;
    }


    @PostMapping("/insert")
    public AjaxResult add(@Validated @RequestBody SysUser user) {
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName()))) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        user.setPassword("Admin123");
        user.setCreateBy(getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return AjaxResult.success(userService.insertUser(user));
    }

    /**
     * 重置密码
     */
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody SysUser user) {
//        userService.checkUserAllowed(user);
//        userService.checkUserDataScope(user.getUserId());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(getUsername());
        int i = userService.resetPwd(user);
        return AjaxResult.success(i);
    }


    /**
     * 修改密码
     */
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/updatePwd")
    public AjaxResult updatePwd(@RequestBody SysUser user) {
        try {
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(user.getUserName(), Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            } else {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(user.getUserName(), Constants.LOGIN_FAIL, e.getMessage()));
                throw new ServiceException(e.getMessage());
            }
        }
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setNewPassword(SecurityUtils.encryptPassword(user.getNewPassword()));
        user.setUpdateBy(getUsername());
        int i = userService.updatePwd(user);

        return AjaxResult.success(i);
    }

    @PostMapping("/checkLoginName")
    public AjaxResult checkLoginName(@RequestBody SysUserDTO user) {
        SysUser sysUser = new SysUser();
        sysUser.setUserName(user.getUserName());
        SysUser detail = userService.detail(sysUser);
        if (detail != null) {
            return AjaxResult.success(1);
        } else {
            return AjaxResult.success(0);
        }

    }

    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    public AjaxResult update(@RequestBody SysUser user) {
        user.setUpdateBy(getUsername());
        int i = userService.updateUser(user);
        return AjaxResult.success(i);
    }

    @PostMapping("/detail")
    public AjaxResult detail(@RequestBody SysUser user) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(user.getUserId());
        SysUser detail = userService.detail(sysUser);
        return AjaxResult.success(detail);
    }

    @PostMapping("/remove")
    public AjaxResult remove(@RequestBody JSONObject jo) {
        List<Long> userIdList = jo.getJSONArray("userIds").toJavaList(Long.class);
        Long[] userIds = userIdList.toArray(new Long[userIdList.size()]);
        if (userIds.length < 1) {
            throw new ServiceException("删除ID不能为空");
        }
        if (ArrayUtils.contains(userIds, getUserId())) {
            return error("当前用户不能删除");
        }
        int i = userService.removeUserByIds(userIds);
        return AjaxResult.success(i);
    }

}
