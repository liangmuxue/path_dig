package com.ruoyi.main.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * ai诊断图对象 report_type
 * 
 * @author ruoyi
 * @date 2024-06-27
 */
@Data
public class ReportType
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 样本报告id */
    @Excel(name = "样本报告id")
    private Long reportId;

    /** 分类 */
    @Excel(name = "分类")
    private String type;

    /** 定位 */
    @Excel(name = "定位")
    private String location;

}
