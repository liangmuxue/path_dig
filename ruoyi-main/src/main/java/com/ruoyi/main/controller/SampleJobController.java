package com.ruoyi.main.controller;

import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.main.domain.SampleJob;
import com.ruoyi.main.service.ISampleJobService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 样本分析jobController
 * 
 * @author ruoyi
 * @date 2024-07-03
 */
@RestController
@RequestMapping("/system/job")
public class SampleJobController extends BaseController
{
    @Resource
    private ISampleJobService sampleJobService;

    /**
     * 查询样本分析job列表
     */
    @GetMapping("/list")
    public TableDataInfo list(SampleJob sampleJob)
    {
        startPage();
        List<SampleJob> list = sampleJobService.selectSampleJobList(sampleJob);
        return getDataTable(list);
    }

    /**
     * 导出样本分析job列表
     */
    @Log(title = "样本分析job", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SampleJob sampleJob)
    {
        List<SampleJob> list = sampleJobService.selectSampleJobList(sampleJob);
        ExcelUtil<SampleJob> util = new ExcelUtil<SampleJob>(SampleJob.class);
        util.exportExcel(response, list, "样本分析job数据");
    }

    /**
     * 获取样本分析job详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(sampleJobService.selectSampleJobById(id));
    }


    /**
     * 新增样本分析job
     */
    @Log(title = "样本分析job", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SampleJob sampleJob)
    {
        return toAjax(sampleJobService.insertSampleJob(sampleJob));
    }

    /**
     * 修改样本分析job
     */
    @Log(title = "样本分析job", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SampleJob sampleJob)
    {
        return toAjax(sampleJobService.updateSampleJob(sampleJob));
    }

    /**
     * 删除样本分析job
     */
    @Log(title = "样本分析job", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sampleJobService.deleteSampleJobByIds(ids));
    }
}
