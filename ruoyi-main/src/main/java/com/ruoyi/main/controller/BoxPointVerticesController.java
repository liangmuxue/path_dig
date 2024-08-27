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
import com.ruoyi.main.domain.BoxPointVertices;
import com.ruoyi.main.service.IBoxPointVerticesService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * box_point_vertices多边形边框点Controller
 * 
 * @author ruoyi
 * @date 2024-08-27
 */
@RestController
@RequestMapping("/system/boxPointVertices")
public class BoxPointVerticesController extends BaseController
{
    @Autowired
    private IBoxPointVerticesService boxPointVerticesService;

    /**
     * 查询box_point_vertices多边形边框点列表
     */
    @PreAuthorize("@ss.hasPermi('system:boxPointVertices:list')")
    @GetMapping("/list")
    public TableDataInfo list(BoxPointVertices boxPointVertices)
    {
        startPage();
        List<BoxPointVertices> list = boxPointVerticesService.selectBoxPointVerticesList(boxPointVertices);
        return getDataTable(list);
    }

    /**
     * 导出box_point_vertices多边形边框点列表
     */
    @PreAuthorize("@ss.hasPermi('system:boxPointVertices:export')")
    @Log(title = "box_point_vertices多边形边框点", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BoxPointVertices boxPointVertices)
    {
        List<BoxPointVertices> list = boxPointVerticesService.selectBoxPointVerticesList(boxPointVertices);
        ExcelUtil<BoxPointVertices> util = new ExcelUtil<BoxPointVertices>(BoxPointVertices.class);
        util.exportExcel(response, list, "box_point_vertices多边形边框点数据");
    }

    /**
     * 获取box_point_vertices多边形边框点详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:boxPointVertices:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(boxPointVerticesService.selectBoxPointVerticesById(id));
    }

    /**
     * 新增box_point_vertices多边形边框点
     */
    @PreAuthorize("@ss.hasPermi('system:boxPointVertices:add')")
    @Log(title = "box_point_vertices多边形边框点", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BoxPointVertices boxPointVertices)
    {
        return toAjax(boxPointVerticesService.insertBoxPointVertices(boxPointVertices));
    }

    /**
     * 修改box_point_vertices多边形边框点
     */
    @PreAuthorize("@ss.hasPermi('system:boxPointVertices:edit')")
    @Log(title = "box_point_vertices多边形边框点", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BoxPointVertices boxPointVertices)
    {
        return toAjax(boxPointVerticesService.updateBoxPointVertices(boxPointVertices));
    }

    /**
     * 删除box_point_vertices多边形边框点
     */
    @PreAuthorize("@ss.hasPermi('system:boxPointVertices:remove')")
    @Log(title = "box_point_vertices多边形边框点", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(boxPointVerticesService.deleteBoxPointVerticesByIds(ids));
    }
}
