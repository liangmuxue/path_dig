package com.ruoyi.main.controller;

import java.util.List;
import java.util.Map;
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
    @PostMapping("/getInfo")
    public AjaxResult getInfo(@RequestBody Map<String, Long> requestBody)
    {
        Long id = requestBody.get("id");
        SampleJob sampleJob = sampleJobService.selectSampleJobBySamplePid(id);
        if(sampleJob.getState()==0){
            sampleJob.setStateMsg("文件正在分析");
        }
        if(sampleJob.getState()==1){
            sampleJob.setStateMsg("ais模型推理完成");
        }
        if(sampleJob.getState()==2){
            sampleJob.setStateMsg("hsil模型推理完成");
        }
        if(sampleJob.getState()==3){
            sampleJob.setStateMsg("lsil模型推理完成");
        }
        if(sampleJob.getState()==4){
            sampleJob.setStateMsg("推理结果处理完成，样本有效 ");
        }
        if(sampleJob.getState()==5){
            sampleJob.setStateMsg("推理结果处理完成，样本模糊无效");
        }
        return AjaxResult.success(sampleJob);
    }

    //py->java阶段更新分析任务的状态
    @PostMapping("/stageSend")
    public AjaxResult stageSend(@RequestBody SampleJob sampleJob)
    {
        return toAjax(sampleJobService.updateAfterStageSend(sampleJob));
    }

    //给前端当前用户当前执行中任务的样本id
    @PostMapping("/getInProgressJob")
    public AjaxResult getInProgressJob()
    {
        SampleJob sampleJob = new SampleJob();
        sampleJob.setDoctor(getUserId());
        return AjaxResult.success(sampleJobService.getInProgressJob(sampleJob));
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
