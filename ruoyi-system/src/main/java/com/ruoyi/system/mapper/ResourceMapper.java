package com.ruoyi.system.mapper;

import com.ruoyi.common.core.domain.entity.Resource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 样本管理Mapper接口
 * 
 * @author ruoyi
 * @date 2024-06-25
 */
public interface ResourceMapper
{
    List<Resource> selectResource();

    List<Resource> selectResourceByCondition(Resource resource);

    int insertResourceRef(@Param("roleId") Long roleId, @Param("resourceId") String resourceId );

    List<String> selectResourceIdByRoleId(Long roleId);

    Integer deleteResourceRefByRoleId(Long roleId);

}
