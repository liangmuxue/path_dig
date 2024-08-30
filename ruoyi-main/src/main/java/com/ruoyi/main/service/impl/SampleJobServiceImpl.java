package com.ruoyi.main.service.impl;

import java.util.List;

import com.ruoyi.main.domain.SampleReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.main.mapper.SampleJobMapper;
import com.ruoyi.main.domain.SampleJob;
import com.ruoyi.main.service.ISampleJobService;

import javax.annotation.Resource;

/**
 * 样本分析jobService业务层处理
 * 
 * @author ruoyi
 * @date 2024-07-03
 */
@Service
public class SampleJobServiceImpl implements ISampleJobService 
{
    @Resource
    private SampleJobMapper sampleJobMapper;

    /**
     * 查询样本分析job
     * 
     * @param id 样本分析job主键
     * @return 样本分析job
     */
    @Override
    public SampleJob selectSampleJobById(Long id)
    {
        return sampleJobMapper.selectSampleJobById(id);
    }

    /**
     * 查询样本分析job列表
     * 
     * @param sampleJob 样本分析job
     * @return 样本分析job
     */
    @Override
    public List<SampleJob> selectSampleJobList(SampleJob sampleJob)
    {
        return sampleJobMapper.selectSampleJobList(sampleJob);
    }

    /**
     * 新增样本分析job
     * 
     * @param sampleJob 样本分析job
     * @return 结果
     */
    @Override
    public int insertSampleJob(SampleJob sampleJob)
    {
        return sampleJobMapper.insertSampleJob(sampleJob);
    }

    /**
     * 修改样本分析job
     * 
     * @param sampleJob 样本分析job
     * @return 结果
     */
    @Override
    public int updateSampleJob(SampleJob sampleJob)
    {
        return sampleJobMapper.updateSampleJob(sampleJob);
    }

    /**
     * 批量删除样本分析job
     * 
     * @param ids 需要删除的样本分析job主键
     * @return 结果
     */
    @Override
    public int deleteSampleJobByIds(Long[] ids)
    {
        return sampleJobMapper.deleteSampleJobByIds(ids);
    }

    /**
     * 删除样本分析job信息
     * 
     * @param id 样本分析job主键
     * @return 结果
     */
    @Override
    public int deleteSampleJobById(Long id)
    {
        return sampleJobMapper.deleteSampleJobById(id);
    }

    @Override
    public int checkBeforeAnalysis() {
        return sampleJobMapper.checkBeforeAnalysis();
    }

    @Override
    public int updateAfterStageSend(SampleJob sampleJob) {
        return sampleJobMapper.updateAfterStageSend(sampleJob);
    }

    @Override
    public void deleteSampleJobBySampleId(String sampleId) {
        sampleJobMapper.deleteSampleJobBySampleId(sampleId);
    }

    @Override
    public SampleJob selectSampleJobBySamplePid(Long id) {
        return sampleJobMapper.selectSampleJobBySamplePid(id);
    }

    @Override
    public SampleJob getInProgressJob(SampleJob sampleJob) {
        return sampleJobMapper.getInProgressJob(sampleJob);
    }

    @Override
    public void updateAllJobing() {
        sampleJobMapper.updateAllJobing();
    }

    @Override
    public List<SampleJob> selectSampleJobing() {
        return sampleJobMapper.selectSampleJobing();
    }

    @Override
    public SampleJob getInProgressJobDone() {
        return sampleJobMapper.getInProgressJobDone();
    }


}
