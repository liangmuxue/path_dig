package com.ruoyi.main.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;
import java.util.List;

/**
 * 样本管理对象 sample
 * 
 * @author ruoyi
 * @date 2024-06-25
 */
@Data
public class Sample
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 病理样本号 */
    @Excel(name = "病理样本号")
    private String sampleId;

    /** 样本注册时间 */
    private Long registrationTime;

    /** 录检医生Id */
    private Long doctor;

    /** 状态报告 0未生成 1已生成 */
    private Integer state;

    @Excel(name = "状态报告")
    private String stateName;

    /** 样本源文件 */
    @Excel(name = "样本源文件")
    private String svs;

    /** 样本图片 */
    @Excel(name = "样本图片")
    private String pic;

    /** 备注 */
    @Excel(name = "备注")
    private String note;

    @Excel(name = "录检医生")
    private String doctorName;

    private String ids;

    @Excel(name = "样本注册时间")
    private String registrationDate;

    /** 来源 样本库0 ai本地1 */
    private Integer type;

    /** 保存源文件 1不保存 */
    private Integer save;

}
