package com.ruoyi.main.dto;

import com.ruoyi.common.annotation.Excel;
import lombok.Data;

@Data
public class SampleDTO {

    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 病理样本号 */
    @Excel(name = "病理样本号")
    private String sampleId;

    /** 样本注册时间 */
    @Excel(name = "样本注册时间")
    private Long registrationTime;

    /** 录检医生Id */
    @Excel(name = "录检医生")
    private Long doctor;

    /** 状态报告 0未生成 1已生成 */
    @Excel(name = "状态报告 0未生成 1已生成")
    private Integer state;

    /** 样本源文件 */
    @Excel(name = "样本源文件")
    private String svs;

    /** 样本源文件路径 */
    @Excel(name = "样本源文件路径")
    private String svsPath;

    /** 样本图片 */
    @Excel(name = "样本图片")
    private String pic;

    /** 备注 */
    @Excel(name = "备注")
    private String note;

    private Long startTime;

    private Long endTime;

    /** 来源 样本库0 ai本地1 */
    private Integer type;

    /** 保存源文件 1不保存 */
    private Integer save;

    private Integer pageNum;

    private Integer pageSize;
}
