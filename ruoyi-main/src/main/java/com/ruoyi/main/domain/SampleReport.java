package com.ruoyi.main.domain;

import lombok.Data;
import com.ruoyi.common.annotation.Excel;

import java.util.List;


/**
 * ai诊断分析对象 sample_report
 * 
 * @author ruoyi
 * @date 2024-06-27
 */
@Data
public class SampleReport
{
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

    private String inspectDoctorName;

    /** 审核医生 */
    @Excel(name = "审核医生")
    private Long verifyDoctor;

    private String verifyDoctorName;

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

    private String stateName;

    private String picOne;

    private String picTwo;

    private String picBig;

    private String size;

    private String disease;

    private Integer done;

    private String zoom;

    private List<ReportType> lsilList;

    private List<ReportType> hsilList;

    private List<ReportType> aisList;

}
