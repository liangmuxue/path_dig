package com.ruoyi.main.controller;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.main.util.ExtractConfiguration;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


import java.util.Map;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.framework.config.ServerConfig;
import com.ruoyi.main.dto.SampleDTO;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
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
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
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
    @Resource
    private ExtractConfiguration extractConfiguration;
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
    @PostMapping("/pageList")
    public TableDataInfo pageList(@RequestBody SampleDTO sampleDTO)
    {
        PageHelper.startPage(sampleDTO.getPageNum(), sampleDTO.getPageSize());
        // 调用你的 page 方法获取分页数据
        PageInfo<Sample> pageInfo = sampleService.page(sampleDTO, sampleDTO.getPageNum(), sampleDTO.getPageSize());
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
    @PostMapping("/getInfo")
    public AjaxResult getInfo(@RequestBody Sample sample)
    {
        sample = sampleService.selectSampleById(sample.getId());
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
        sampleService.insertSample(sample);
        return AjaxResult.success(sample.getId());
    }

    //本地上传 解析后 保存源文件
    @PostMapping("/saveAfterAnalysis")
    public AjaxResult saveAfterAnalysis(@RequestBody Sample sample){
        sample.setSave(0);//本地分析完保存才使用
        return AjaxResult.success(sampleService.updateSample(sample));
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
    @PostMapping("/update")
    public AjaxResult edit(@RequestBody Sample sample)
    {
        return toAjax(sampleService.updateSample(sample));
    }

    /**
     * 删除样本管理
     */
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody Map<String, Long[]> requestBody) {
        Long[] ids = requestBody.get("ids");
        if (ids == null || ids.length == 0) {
            // 如果 ids 为空或者长度为0，可以根据具体情况返回错误信息或者处理逻辑
            return AjaxResult.error("未提供要删除的样本数据的ID");
        }
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
//            String path = "/home/program/path-dig/file/" + profileString;
            String path = extractConfiguration.getProfile() + profileString;

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
    public AjaxResult uploadFiles(MultipartFile  file) throws Exception
    {
        if (file.isEmpty()) {
            return AjaxResult.error("Please select a file to upload");
        }
        String uploadDir = "C:\\Users\\DELL\\Desktop\\111\\";
        String targetDir = "C:\\Users\\DELL\\Desktop\\111\\";

        try {
            // Save uploaded file to uploadDir
            String originalFilename = file.getOriginalFilename();
            String decodedFilename = new String(originalFilename.getBytes("ISO8859-1"), "UTF-8");
            String filePath = uploadDir + decodedFilename;
            File dest = new File(filePath);
            file.transferTo(dest);

            // Step 1: Extract files from the zip archive
            unzip(filePath, targetDir);

            // Step 2: Upload SVS files to another directory
            String destinationDirectory = "C:\\Users\\DELL\\Desktop\\destination\\";
            File sourceDir = new File(targetDir);
            File destDir = new File(destinationDirectory);
            org.apache.commons.io.FileUtils.copyDirectory(sourceDir, destDir);
            return AjaxResult.success("Files uploaded and extracted successfully");
        } catch (IOException e) {
            e.printStackTrace();
            return AjaxResult.error(e.getMessage());
        }

    }

    public void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath), Charset.forName("UTF-8"))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                String entryName = zipEntry.getName();
                File newFile = new File(destDir, entryName);
                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    // Ensure parent directory exists
                    newFile.getParentFile().mkdirs();
                    // Write file content
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
                zipEntry = zis.getNextEntry();
            }
        }

    }

    public File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target directory: " + zipEntry.getName());
        }
        return destFile;
    }


}
