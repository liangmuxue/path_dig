package com.ruoyi.main.dto;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.main.domain.ReportType;
import lombok.Data;

import java.util.List;

@Data
public class SampleReportDTO {

    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 样本表主键id */
    @Excel(name = "样本表主键id")
    private Long samplePid;

    /** 质控分析 0模糊无效 1有效 */
    @Excel(name = "质控分析 0模糊无效 1有效")
    private Integer quality;

    /** 样本病理号 */
    @Excel(name = "样本病理号")
    private String sampleId;

    /** ai诊断 */
    @Excel(name = "ai诊断")
    private String aiDiagnosis;

    /** 医生诊断 */
    @Excel(name = "医生诊断")
    private String doctorDiagnosis;

    /** 录检医生 */
    @Excel(name = "录检医生")
    private Long inspectDoctor;

    /** 审核医生 */
    @Excel(name = "审核医生")
    private Long verifyDoctor;

    /** ai诊断日期 */
    @Excel(name = "ai诊断日期")
    private Long aiTime;

    /** 报告日期 */
    @Excel(name = "报告日期")
    private Long reportTime;

    /** 修改时间 */
    @Excel(name = "修改时间")
    private Long updateTime;

    /** 审核状态 0未审核 1已审核 */
    @Excel(name = "审核状态 0未审核 1已审核")
    private Long state;

    private String picOne;

    private String picTwo;

    private String picBig;

   private Long startTime;

   private Long endTime;

    private Integer pageNum;

    private Integer pageSize;


}
