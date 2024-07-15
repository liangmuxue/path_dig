package com.ruoyi.main.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.framework.config.ServerConfig;
import com.ruoyi.main.dto.SampleDTO;
import com.ruoyi.system.service.ISysUserService;
import org.apache.http.util.EntityUtils;
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
import org.springframework.web.multipart.MultipartFile;

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
    @Resource
    private ServerConfig serverConfig;
    private static final String FILE_DELIMETER = ",";

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

    //样本管理--批量导出
    @PostMapping("/svsExport")
    public AjaxResult svsExport(HttpServletResponse response,@RequestBody Sample sample)
    {
        String result = sampleService.svsExport(response,sample);
        return AjaxResult.success(result);
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


    /**
     * 通用上传请求（单个）
     */
    @PostMapping("/upload")
    public AjaxResult uploadFile(MultipartFile file) throws Exception
    {
        try
        {
            // 上传文件路径
            String filePath = RuoYiConfig.getUploadPath();
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = serverConfig.getUrl() + fileName;

            // 找到profile后面的字符串
            String searchString = "profile/";
            int index = fileName.indexOf(searchString);
            // 找到了profile后的字符串起始位置
            String profileString = fileName.substring(index + searchString.length());
            System.out.println("提取的profile后的字符串为: " + profileString);
            String path = "/home/program/path-dig/file/" + profileString;

            AjaxResult ajax = AjaxResult.success();
            ajax.put("url", url);
            ajax.put("fileName", fileName);
            ajax.put("newFileName", FileUtils.getName(fileName));
            ajax.put("originalFilename", file.getOriginalFilename());
            ajax.put("path", path);
            return ajax;
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/uploads")
    public AjaxResult uploadFiles(List<MultipartFile> files) throws Exception
    {
        try
        {
            // 上传文件路径
            String filePath = RuoYiConfig.getUploadPath();
            List<String> urls = new ArrayList<String>();
            List<String> fileNames = new ArrayList<String>();
            List<String> newFileNames = new ArrayList<String>();
            List<String> originalFilenames = new ArrayList<String>();
            for (MultipartFile file : files)
            {
                // 上传并返回新文件名称
                String fileName = FileUploadUtils.upload(filePath, file);
                String url = serverConfig.getUrl() + fileName;
                urls.add(url);
                fileNames.add(fileName);
                newFileNames.add(FileUtils.getName(fileName));
                originalFilenames.add(file.getOriginalFilename());
            }
            AjaxResult ajax = AjaxResult.success();
            ajax.put("urls", StringUtils.join(urls, FILE_DELIMETER));
            ajax.put("fileNames", StringUtils.join(fileNames, FILE_DELIMETER));
            ajax.put("newFileNames", StringUtils.join(newFileNames, FILE_DELIMETER));
            ajax.put("originalFilenames", StringUtils.join(originalFilenames, FILE_DELIMETER));
            return ajax;
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }
}
