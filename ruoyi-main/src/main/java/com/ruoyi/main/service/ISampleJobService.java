package com.ruoyi.main.service;

import java.util.List;
import com.ruoyi.main.domain.SampleJob;
import com.ruoyi.main.domain.SampleReport;

/**
 * 样本分析jobService接口
 * 
 * @author ruoyi
 * @date 2024-07-03
 */
public interface ISampleJobService 
{
    /**
     * 查询样本分析job
     * 
     * @param id 样本分析job主键
     * @return 样本分析job
     */
    public SampleJob selectSampleJobById(Long id);

    /**
     * 查询样本分析job列表
     * 
     * @param sampleJob 样本分析job
     * @return 样本分析job集合
     */
    public List<SampleJob> selectSampleJobList(SampleJob sampleJob);

    /**
     * 新增样本分析job
     * 
     * @param sampleJob 样本分析job
     * @return 结果
     */
    public int insertSampleJob(SampleJob sampleJob);

    /**
     * 修改样本分析job
     * 
     * @param sampleJob 样本分析job
     * @return 结果
     */
    public int updateSampleJob(SampleJob sampleJob);

    /**
     * 批量删除样本分析job
     * 
     * @param ids 需要删除的样本分析job主键集合
     * @return 结果
     */
    public int deleteSampleJobByIds(Long[] ids);

    /**
     * 删除样本分析job信息
     * 
     * @param id 样本分析job主键
     * @return 结果
     */
    public int deleteSampleJobById(Long id);

    int checkBeforeAnalysis(SampleReport sampleReport);
}
