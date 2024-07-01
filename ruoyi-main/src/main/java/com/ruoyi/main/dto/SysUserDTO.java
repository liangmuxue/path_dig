package com.ruoyi.main.dto;

import com.ruoyi.common.core.domain.entity.Resource;
import com.ruoyi.common.core.domain.entity.SysRole;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * com.ss.manaplat.common.web
 *
 * @author ASUS
 * @create 2024/6/28
 * @email fujinfeng@ss-cas.com
 **/
@Data
@ToString
public class SysUserDTO {
    /**
     * 用户ID
     */
    private Long userId;

    Integer pageNum;
    Integer pageSize;
    /**
     * 用户账号
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phonenumber;

    /**
     * 用户性别
     */
    private String sex;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 密码
     */
    private String password;

    /**
     * 帐号状态（0正常 1停用）
     */
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    /**
     * 最后登录IP
     */
    private String loginIp;

    /**
     * 最后登录时间
     */
    private Date loginDate;


    /**
     * 角色对象
     */
    private List<SysRole> roles;

    /**
     * 角色组
     */
    private Long[] roleIds;

    /**
     * 岗位组
     */
    private Long[] postIds;

    /**
     * 角色ID
     */
    private Long roleId;
    private String newPassword;
    private List<Resource> resourceList;

}
