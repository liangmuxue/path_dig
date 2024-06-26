package com.ruoyi.main.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.main.dto.SampleDTO;
import com.ruoyi.system.service.ISysUserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.main.domain.Sample;
import com.ruoyi.main.service.ISampleService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 样本管理Controller
 * 
 * @author ruoyi
 * @date 2024-06-25
 */
@RestController
@RequestMapping("/system/sample")
public class SampleController extends BaseController
{
    @Autowired
    private ISampleService sampleService;
    @Resource
    private ISysUserService sysUserService;

    /**
     * 查询样本管理列表
     */
    @GetMapping("/list")
    public TableDataInfo list(Sample sample)
    {
        startPage();
        List<Sample> list = sampleService.selectSampleList(sample);
        return getDataTable(list);
    }

    //分页查询
    @GetMapping("/pageList")
    public TableDataInfo pageList(SampleDTO sampleDTO, Integer pageNum, Integer pageSize)
    {
        PageHelper.startPage(pageNum, pageSize);
        // 调用你的 page 方法获取分页数据
        PageInfo<Sample> pageInfo = sampleService.page(sampleDTO, pageNum, pageSize);
        List<Sample> sampleList = pageInfo.getList();
        sampleList.stream().forEach(a->{
            a.setDoctorName(sysUserService.selectUserById(a.getDoctor()).getNickName());
        });
        return getDataTable(sampleList);
    }

    /**
     * 导出样本管理列表
     */
    @Log(title = "样本管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Sample sample)
    {
        List<Sample> list = sampleService.selectSampleList(sample);
        ExcelUtil<Sample> util = new ExcelUtil<Sample>(Sample.class);
        util.exportExcel(response, list, "样本数据");
    }

    //导出execl
    @PostMapping("/batchExport")
    public void batchExport(HttpServletResponse response,@RequestBody Sample sample)
    {
        sampleService.export(response,sample);
    }

    //导出svs
    @PostMapping("/svsExport")
    public String svsExport(HttpServletResponse response,@RequestBody Sample sample)
    {
//        sampleService.svsExport(response,sample);
        return sampleService.svsExport(response,sample);
    }

    /**
     * 获取样本管理详细信息
     */
    @GetMapping("/getInfo")
    public AjaxResult getInfo(Long id)
    {
        Sample sample = sampleService.selectSampleById(id);
        sample.setDoctorName(sysUserService.selectUserById(sample.getDoctor()).getNickName());
        return AjaxResult.success(sample);
    }

    /**
     * 新增样本管理
     */
    @Log(title = "样本管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Sample sample)
    {
        sample.setDoctor(getUserId());
        return toAjax(sampleService.insertSample(sample));
    }

    /**
     * 新增样本管理--多个
     */
    @Log(title = "样本管理", businessType = BusinessType.INSERT)
    @PostMapping("/addMore")
    public AjaxResult addMore(@RequestBody Sample sample)
    {
        sample.setDoctor(getUserId());
        String[] urls = sample.getSvs().split(",");
        for (int i = 0; i < urls.length; i++) {
            sample.setSvs(urls[i]);
            sampleService.insertSample(sample);
        }
        return AjaxResult.success();
    }

    /**
     * 修改样本管理
     */
    @Log(title = "样本管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Sample sample)
    {
        return toAjax(sampleService.updateSample(sample));
    }

    /**
     * 删除样本管理
     */
    @Log(title = "样本管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sampleService.deleteSampleByIds(ids));
    }
}
