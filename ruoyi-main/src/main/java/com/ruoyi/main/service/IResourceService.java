package com.ruoyi.main.service;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.entity.Resource;

import java.util.List;

/**
 * com.ss.manaplat.common.web
 *
 * @author ASUS
 * @create 2024/6/25
 * @email fujinfeng@ss-cas.com
 **/
public interface IResourceService {
     List<Resource> tree();
      SysUser get(Long userId);
}
