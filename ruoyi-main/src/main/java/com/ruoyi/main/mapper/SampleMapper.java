package com.ruoyi.main.mapper;

import java.util.List;
import com.ruoyi.main.domain.Sample;
import com.ruoyi.main.dto.SampleDTO;
import org.apache.ibatis.annotations.Param;

/**
 * 样本管理Mapper接口
 * 
 * @author ruoyi
 * @date 2024-06-25
 */
public interface SampleMapper 
{
    /**
     * 查询样本管理
     * 
     * @param id 样本管理主键
     * @return 样本管理
     */
    public Sample selectSampleById(Long id);

    /**
     * 查询样本管理列表
     * 
     * @param sample 样本管理
     * @return 样本管理集合
     */
    public List<Sample> selectSampleList(Sample sample);

    /**
     * 新增样本管理
     * 
     * @param sample 样本管理
     * @return 结果
     */
    public int insertSample(Sample sample);

    /**
     * 修改样本管理
     * 
     * @param sample 样本管理
     * @return 结果
     */
    public int updateSample(Sample sample);

    /**
     * 删除样本管理
     * 
     * @param id 样本管理主键
     * @return 结果
     */
    public int deleteSampleById(Long id);

    /**
     * 批量删除样本管理
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSampleByIds(Long[] ids);

    List<Sample>  selectSampleDTOList(SampleDTO sampleDTO);

    List<Sample> selectSampleListByIds(String ids);

    List<Sample> selectNotSave();

    void delSvs(Long id);

    Sample selectSampleBySampleId(String sampleId);
}
