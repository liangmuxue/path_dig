package com.ruoyi.main.service.impl;

import com.ruoyi.common.core.domain.entity.Resource;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.main.service.IResourceService;
import com.ruoyi.system.mapper.ResourceMapper;
import com.ruoyi.system.mapper.SysRoleMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * com.ss.manaplat.common.web
 *
 * @author JF
 * @create 2024/6/25
 * @email fujinfeng@ss-cas.com
 **/
@Service
public class ResourceServiceImpl implements IResourceService {

    @Autowired
    private ResourceMapper resourceMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysRoleMapper roleMapper;


    @Override
    public List<Resource> tree() {
        List<Resource> resources = this.resourceMapper.selectResource();
        resources.sort((o1, o2) -> Integer.compare(o1.getResourceId().compareTo(o2.getResourceId()), 0));
        return treeList(resources);
    }


    public static List<Resource> treeList(List<Resource> resources) {
        List<Resource> treeData = new ArrayList<>();
        Resource one = new Resource();
        Resource two = new Resource();
        Resource three = new Resource();
        //生成树
        for (Resource resource : resources) {
            if (resource.getParentId() == null || "".equals(resource.getParentId())) {
                Resource tempOne = new Resource();
                BeanUtils.copyProperties(resource, tempOne);
                one = tempOne;
                treeData.add(tempOne);
            } else if (resource.getParentId().equals(one.getResourceId())) {
                if (one.getChildren() == null) {
                    one.setChildren(new ArrayList<>());
                }
                Resource tempTwo = new Resource();
                BeanUtils.copyProperties(resource, tempTwo);
                two = tempTwo;
                one.getChildren().add(tempTwo);
            } else if (resource.getParentId().equals(two.getResourceId())) {
                if (two.getChildren() == null) {
                    two.setChildren(new ArrayList<>());
                }
                Resource tempThree = new Resource();
                BeanUtils.copyProperties(resource, tempThree);
                three = tempThree;
                two.getChildren().add(tempThree);
            } else if (resource.getParentId().equals(three.getResourceId())) {
                if (three.getChildren() == null) {
                    three.setChildren(new ArrayList<>());
                }
                Resource tempFour = new Resource();
                BeanUtils.copyProperties(resource, tempFour);
                three.getChildren().add(tempFour);
            }
        }
        return treeData;
    }

    @Override
    public SysUser get(Long userId) {

        SysUser check = new SysUser();
        check.setUserId(userId);
        //搜索本用户的基本信息
        SysUser detail = sysUserMapper.detail(check);
        detail.setPassword("");
        if (detail.getRoleId() == 1){
            //超级管理员查询所有菜单
            List<Resource> resources = resourceMapper.selectResourceByCondition(new Resource());
            detail.setResourceList(resources);
            return detail;
        }

//        List<RoleBoxResource> allRoleBoxResourceList = null;
        SysRole role = roleMapper.selectRoleById(detail.getRoleId());
        List<String> resourceIds = roleMapper.selectResourceIdByRoleId(detail.getRoleId());
        String join = String.join(",", resourceIds);
        Resource resource = new Resource();
        resource.setResCodes(join);
        List<Resource> resources = resourceMapper.selectResourceByCondition(resource);
        detail.setResourceList(resources);
        ArrayList<SysRole> roles = new ArrayList<>();
        roles.add(role);
        detail.setRoles(roles);
        return detail;
    }
}
