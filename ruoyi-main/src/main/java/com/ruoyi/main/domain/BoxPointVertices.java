package com.ruoyi.main.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * box_point_vertices多边形边框点对象 box_point_vertices
 * 
 * @author ruoyi
 * @date 2024-08-27
 */
@Data
public class BoxPointVertices
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 类型 */
    @Excel(name = "类型")
    private String type;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String point;

    /** 样本报告id */
    @Excel(name = "样本报告id")
    private Long reportId;


}
