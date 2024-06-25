package com.ruoyi.main.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.main.mapper.SampleMapper;
import com.ruoyi.main.domain.Sample;
import com.ruoyi.main.service.ISampleService;

/**
 * 样本管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-06-25
 */
@Service
public class SampleServiceImpl implements ISampleService 
{
    @Autowired
    private SampleMapper sampleMapper;

    /**
     * 查询样本管理
     * 
     * @param id 样本管理主键
     * @return 样本管理
     */
    @Override
    public Sample selectSampleById(Long id)
    {
        return sampleMapper.selectSampleById(id);
    }

    /**
     * 查询样本管理列表
     * 
     * @param sample 样本管理
     * @return 样本管理
     */
    @Override
    public List<Sample> selectSampleList(Sample sample)
    {
        return sampleMapper.selectSampleList(sample);
    }

    /**
     * 新增样本管理
     * 
     * @param sample 样本管理
     * @return 结果
     */
    @Override
    public int insertSample(Sample sample)
    {
        return sampleMapper.insertSample(sample);
    }

    /**
     * 修改样本管理
     * 
     * @param sample 样本管理
     * @return 结果
     */
    @Override
    public int updateSample(Sample sample)
    {
        return sampleMapper.updateSample(sample);
    }

    /**
     * 批量删除样本管理
     * 
     * @param ids 需要删除的样本管理主键
     * @return 结果
     */
    @Override
    public int deleteSampleByIds(Long[] ids)
    {
        return sampleMapper.deleteSampleByIds(ids);
    }

    /**
     * 删除样本管理信息
     * 
     * @param id 样本管理主键
     * @return 结果
     */
    @Override
    public int deleteSampleById(Long id)
    {
        return sampleMapper.deleteSampleById(id);
    }
}
