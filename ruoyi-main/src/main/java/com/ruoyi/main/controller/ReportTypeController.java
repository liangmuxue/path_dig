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
import com.ruoyi.main.domain.ReportType;
import com.ruoyi.main.service.IReportTypeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * ai诊断图Controller
 * 
 * @author ruoyi
 * @date 2024-06-27
 */
@RestController
@RequestMapping("/system/report/type")
public class ReportTypeController extends BaseController
{
    @Autowired
    private IReportTypeService reportTypeService;

    /**
     * 查询ai诊断图列表
     */
    @GetMapping("/list")
    public TableDataInfo list(ReportType reportType)
    {
        startPage();
        List<ReportType> list = reportTypeService.selectReportTypeList(reportType);
        return getDataTable(list);
    }

    /**
     * 导出ai诊断图列表
     */
    @Log(title = "ai诊断图", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ReportType reportType)
    {
        List<ReportType> list = reportTypeService.selectReportTypeList(reportType);
        ExcelUtil<ReportType> util = new ExcelUtil<ReportType>(ReportType.class);
        util.exportExcel(response, list, "ai诊断图数据");
    }

    /**
     * 获取ai诊断图详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(reportTypeService.selectReportTypeById(id));
    }

    /**
     * 新增ai诊断图
     */
    @Log(title = "ai诊断图", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ReportType reportType)
    {
        return toAjax(reportTypeService.insertReportType(reportType));
    }

    /**
     * 修改ai诊断图
     */
    @Log(title = "ai诊断图", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ReportType reportType)
    {
        return toAjax(reportTypeService.updateReportType(reportType));
    }

    /**
     * 删除ai诊断图
     */
    @Log(title = "ai诊断图", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(reportTypeService.deleteReportTypeByIds(ids));
    }
}
