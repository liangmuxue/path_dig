package com.ruoyi.main.service;

import java.util.List;
import com.ruoyi.main.domain.SampleReport;
import com.ruoyi.main.vo.AfterUploadVo;

/**
 * ai诊断分析Service接口
 * 
 * @author ruoyi
 * @date 2024-06-27
 */
public interface ISampleReportService 
{
    /**
     * 查询ai诊断分析
     * 
     * @param id ai诊断分析主键
     * @return ai诊断分析
     */
    public SampleReport selectSampleReportById(Long id);

    /**
     * 查询ai诊断分析列表
     * 
     * @param sampleReport ai诊断分析
     * @return ai诊断分析集合
     */
    public List<SampleReport> selectSampleReportList(SampleReport sampleReport);

    /**
     * 新增ai诊断分析
     * 
     * @param sampleReport ai诊断分析
     * @return 结果
     */
    public AfterUploadVo insertSampleReport(SampleReport sampleReport);

    /**
     * 修改ai诊断分析
     * 
     * @param sampleReport ai诊断分析
     * @return 结果
     */
    public int updateSampleReport(SampleReport sampleReport);

    /**
     * 批量删除ai诊断分析
     * 
     * @param ids 需要删除的ai诊断分析主键集合
     * @return 结果
     */
    public int deleteSampleReportByIds(Long[] ids);

    /**
     * 删除ai诊断分析信息
     * 
     * @param id ai诊断分析主键
     * @return 结果
     */
    public int deleteSampleReportById(Long id);

    SampleReport selectSampleReportBySampleId(String sampleId);
}
