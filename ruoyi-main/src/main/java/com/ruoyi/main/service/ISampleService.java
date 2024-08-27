package com.ruoyi.main.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.ruoyi.main.domain.Sample;
import com.ruoyi.main.dto.SampleDTO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 样本管理Service接口
 * 
 * @author ruoyi
 * @date 2024-06-25
 */
public interface ISampleService 
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
     * 批量删除样本管理
     * 
     * @param ids 需要删除的样本管理主键集合
     * @return 结果
     */
    public int deleteSampleByIds(Long[] ids);

    /**
     * 删除样本管理信息
     * 
     * @param id 样本管理主键
     * @return 结果
     */
    public int deleteSampleById(Long id);

    PageInfo<Sample> page(SampleDTO sampleDTO, int num, int size);

    List<Sample> selectSampleListByIds(String ids);

    void export(HttpServletResponse response, Sample sample);

    String svsExport(HttpServletResponse response, Sample sample);

    List<Sample> selectNotSave();

    void delSvs(Long id);

    int saveAfterAnalysis(Sample sample);

    Sample selectSampleBySampleId(String sampleId);
}
