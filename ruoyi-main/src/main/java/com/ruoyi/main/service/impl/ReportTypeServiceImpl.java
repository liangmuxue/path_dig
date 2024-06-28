package com.ruoyi.main.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.main.mapper.ReportTypeMapper;
import com.ruoyi.main.domain.ReportType;
import com.ruoyi.main.service.IReportTypeService;

import javax.annotation.Resource;

/**
 * ai诊断图Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-06-27
 */
@Service
public class ReportTypeServiceImpl implements IReportTypeService 
{
    @Resource
    private ReportTypeMapper reportTypeMapper;

    /**
     * 查询ai诊断图
     * 
     * @param id ai诊断图主键
     * @return ai诊断图
     */
    @Override
    public ReportType selectReportTypeById(Long id)
    {
        return reportTypeMapper.selectReportTypeById(id);
    }

    /**
     * 查询ai诊断图列表
     * 
     * @param reportType ai诊断图
     * @return ai诊断图
     */
    @Override
    public List<ReportType> selectReportTypeList(ReportType reportType)
    {
        return reportTypeMapper.selectReportTypeList(reportType);
    }

    /**
     * 新增ai诊断图
     * 
     * @param reportType ai诊断图
     * @return 结果
     */
    @Override
    public int insertReportType(ReportType reportType)
    {
        return reportTypeMapper.insertReportType(reportType);
    }

    /**
     * 修改ai诊断图
     * 
     * @param reportType ai诊断图
     * @return 结果
     */
    @Override
    public int updateReportType(ReportType reportType)
    {
        return reportTypeMapper.updateReportType(reportType);
    }

    /**
     * 批量删除ai诊断图
     * 
     * @param ids 需要删除的ai诊断图主键
     * @return 结果
     */
    @Override
    public int deleteReportTypeByIds(Long[] ids)
    {
        return reportTypeMapper.deleteReportTypeByIds(ids);
    }

    /**
     * 删除ai诊断图信息
     * 
     * @param id ai诊断图主键
     * @return 结果
     */
    @Override
    public int deleteReportTypeById(Long id)
    {
        return reportTypeMapper.deleteReportTypeById(id);
    }
}
