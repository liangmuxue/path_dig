package com.ruoyi.abuwx.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.abuwx.domain.AbucoderWxuser;
import com.ruoyi.abuwx.service.IAbucoderWxuserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 微信用户Controller
 * 
 * @author ruoyi
 * @date 2023-06-29
 */
@RestController
@RequestMapping("/wx/wxuser")
public class AbucoderWxuserController extends BaseController
{
    @Autowired
    private IAbucoderWxuserService abucoderWxuserService;

    /**
     * 查询微信用户列表
     */
    @GetMapping("/list")
    public TableDataInfo list(AbucoderWxuser abucoderWxuser)
    {
        startPage();
        List<AbucoderWxuser> list = abucoderWxuserService.selectAbucoderWxuserList(abucoderWxuser);
        return getDataTable(list);
    }

    /**
     * 获取微信用户详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(abucoderWxuserService.selectAbucoderWxuserById(id));
    }

    /**
     * 新增微信用户
     */
    @Log(title = "微信用户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AbucoderWxuser abucoderWxuser)
    {
        return toAjax(abucoderWxuserService.insertAbucoderWxuser(abucoderWxuser));
    }

    /**
     * 修改微信用户
     */
    @Log(title = "微信用户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AbucoderWxuser abucoderWxuser)
    {
        return toAjax(abucoderWxuserService.updateAbucoderWxuser(abucoderWxuser));
    }

    /**
     * 删除微信用户
     */
    @Log(title = "微信用户", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(abucoderWxuserService.deleteAbucoderWxuserByIds(ids));
    }
}
