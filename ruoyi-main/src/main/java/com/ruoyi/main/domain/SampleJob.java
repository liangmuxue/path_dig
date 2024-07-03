package com.ruoyi.main.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 样本分析job对象 sample_job
 * 
 * @author ruoyi
 * @date 2024-07-03
 */
@Data
public class SampleJob
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 样本表主键id */
    @Excel(name = "样本表主键id")
    private Long samplePid;

    /** 样本号 */
    @Excel(name = "样本号")
    private Long sampleId;

    /** 任务状态0进行中 -1失败 1完成  */
    @Excel(name = "任务状态0进行中 -1失败 1完成 ")
    private Long state;

    /** 任务时间 */
    @Excel(name = "任务时间")
    private String time;

}
