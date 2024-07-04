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
    private String sampleId;

    /** 任务状态 0文件正在分析
     1ais模型推理完成
     2hsil模型推理完成
     3lsil模型推理完成
     4推理结果处理完成，样本有效
     5.推理结果处理完成，样本模糊无效 */
    private Long state;

    /** 任务时间 */
    @Excel(name = "任务时间")
    private Long time;

}
