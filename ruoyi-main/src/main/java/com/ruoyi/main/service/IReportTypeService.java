package com.ruoyi.main.service;

import java.util.List;
import com.ruoyi.main.domain.ReportType;

/**
 * ai诊断图Service接口
 * 
 * @author ruoyi
 * @date 2024-06-27
 */
public interface IReportTypeService 
{
    /**
     * 查询ai诊断图
     * 
     * @param id ai诊断图主键
     * @return ai诊断图
     */
    public ReportType selectReportTypeById(Long id);

    /**
     * 查询ai诊断图列表
     * 
     * @param reportType ai诊断图
     * @return ai诊断图集合
     */
    public List<ReportType> selectReportTypeList(ReportType reportType);

    /**
     * 新增ai诊断图
     * 
     * @param reportType ai诊断图
     * @return 结果
     */
    public int insertReportType(ReportType reportType);

    /**
     * 修改ai诊断图
     * 
     * @param reportType ai诊断图
     * @return 结果
     */
    public int updateReportType(ReportType reportType);

    /**
     * 批量删除ai诊断图
     * 
     * @param ids 需要删除的ai诊断图主键集合
     * @return 结果
     */
    public int deleteReportTypeByIds(Long[] ids);

    /**
     * 删除ai诊断图信息
     * 
     * @param id ai诊断图主键
     * @return 结果
     */
    public int deleteReportTypeById(Long id);

    List<ReportType> selectReportTypeByReportId(Long id);

    void deleteReportTypeByReport(Long id);
}
