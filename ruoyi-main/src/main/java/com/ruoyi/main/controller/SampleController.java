package com.ruoyi.main.controller;

import java.util.List;
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

    /**
     * 查询样本管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:sample:list')")
    @GetMapping("/list")
    public TableDataInfo list(Sample sample)
    {
        startPage();
        List<Sample> list = sampleService.selectSampleList(sample);
        return getDataTable(list);
    }

    /**
     * 导出样本管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:sample:export')")
    @Log(title = "样本管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Sample sample)
    {
        List<Sample> list = sampleService.selectSampleList(sample);
        ExcelUtil<Sample> util = new ExcelUtil<Sample>(Sample.class);
        util.exportExcel(response, list, "样本管理数据");
    }

    /**
     * 获取样本管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:sample:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(sampleService.selectSampleById(id));
    }

    /**
     * 新增样本管理
     */
    @PreAuthorize("@ss.hasPermi('system:sample:add')")
    @Log(title = "样本管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Sample sample)
    {
        return toAjax(sampleService.insertSample(sample));
    }

    /**
     * 修改样本管理
     */
    @PreAuthorize("@ss.hasPermi('system:sample:edit')")
    @Log(title = "样本管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Sample sample)
    {
        return toAjax(sampleService.updateSample(sample));
    }

    /**
     * 删除样本管理
     */
    @PreAuthorize("@ss.hasPermi('system:sample:remove')")
    @Log(title = "样本管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sampleService.deleteSampleByIds(ids));
    }
}
