package com.ruoyi.main.dto;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.entity.Resource;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * com.ss.manaplat.common.web
 *
 * @author JF
 * @create 2024/6/27
 * @email fujinfeng@ss-cas.com
 **/
@Data
@ToString
public class SysRoleDTO {
    @Excel(name = "角色序号", cellType = Excel.ColumnType.NUMERIC)
    private Long roleId;

    /**
     * 角色名称
     */
    @Excel(name = "角色名称")
    private String roleName;
    private String roleKey;


    /**
     * 角色状态（0正常 1停用）
     */
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    private List<Resource> resourceList;
    Integer pageNum;
    Integer pageSize;

}
