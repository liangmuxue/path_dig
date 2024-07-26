package com.ruoyi.main.service.impl;

import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import java.io.*;
import java.net.URL;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.framework.config.ServerConfig;
import com.ruoyi.main.dto.SampleDTO;
import org.springframework.stereotype.Service;
import com.ruoyi.main.mapper.SampleMapper;
import com.ruoyi.main.domain.Sample;
import com.ruoyi.main.service.ISampleService;


import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 样本管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-06-25
 */
@Service
public class SampleServiceImpl implements ISampleService 
{
    @Resource
    private SampleMapper sampleMapper;
    @Resource
    private ISysUserService sysUserService;
    @Resource
    private ServerConfig serverConfig;

    /**
     * 查询样本管理
     * 
     * @param id 样本管理主键
     * @return 样本管理
     */
    @Override
    public Sample selectSampleById(Long id)
    {
        return sampleMapper.selectSampleById(id);
    }

    /**
     * 查询样本管理列表
     * 
     * @param sample 样本管理
     * @return 样本管理
     */
    @Override
    public List<Sample> selectSampleList(Sample sample)
    {
        return sampleMapper.selectSampleList(sample);
    }

    /**
     * 新增样本管理
     * 
     * @param sample 样本管理
     * @return 结果
     */
    @Override
    public int insertSample(Sample sample)
    {
        if(sample.getType()==0){//只有样本库上传 直接保存
            sample.setSave(0);
        }else {
            sample.setState(1); //否则 都是默认不保存的
        }
        sample.setState(0);
        sample.setRegistrationTime(System.currentTimeMillis());
        int i = sampleMapper.insertSample(sample);
        sample.setSampleId("SVS"+sample.getId());
        sampleMapper.updateSample(sample);
        return i;
    }

    /**
     * 修改样本管理
     * 
     * @param sample 样本管理
     * @return 结果
     */
    @Override
    public int updateSample(Sample sample)
    {
        return sampleMapper.updateSample(sample);
    }

    /**
     * 批量删除样本管理
     * 
     * @param ids 需要删除的样本管理主键
     * @return 结果
     */
    @Override
    public int deleteSampleByIds(Long[] ids)
    {
        return sampleMapper.deleteSampleByIds(ids);
    }

    /**
     * 删除样本管理信息
     * 
     * @param id 样本管理主键
     * @return 结果
     */
    @Override
    public int deleteSampleById(Long id)
    {
        return sampleMapper.deleteSampleById(id);
    }

    @Override
    public PageInfo<Sample> page(SampleDTO sampleDTO, int num, int size) {
        PageHelper.startPage(num,size);
        PageInfo<Sample> pageInfo = new PageInfo<>(sampleMapper.selectSampleDTOList(sampleDTO));
        return pageInfo;
    }

    @Override
    public List<Sample> selectSampleListByIds(String ids) {
        return sampleMapper.selectSampleListByIds(ids);
    }

    @Override
    public void export(HttpServletResponse response, Sample sample) {
        List<Sample> list = sampleMapper.selectSampleListByIds(sample.getIds());
        list.stream().forEach(a->{
            a.setDoctorName(sysUserService.selectUserById(a.getDoctor()).getNickName());
            if(a.getState()==0){
                a.setStateName("未生成");
            }else {
                a.setStateName("已生成");
            }
            if(a.getSvs()!=null){
                a.setSvs("有");
            }else{
                a.setSvs("无");
            }
            // 转换为 LocalDateTime
            LocalDateTime datetime = LocalDateTime.ofInstant(Instant.ofEpochMilli(a.getRegistrationTime()), ZoneId.systemDefault());
            // 指定输出格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // 格式化日期时间
            String formattedDateTime = datetime.format(formatter);
            a.setRegistrationDate(formattedDateTime);
        });
        ExcelUtil<Sample> util = new ExcelUtil<Sample>(Sample.class);
        util.exportExcel(response, list, "样本数据");
    }


    @Override
    public String svsExport(HttpServletResponse response, Sample sample) {
        List<Sample> list = sampleMapper.selectSampleListByIds(sample.getIds());
        String timestamp = String.valueOf(System.currentTimeMillis());
        String folderPath = "/home/program/path-dig/file/download/" + timestamp + "/";
        // 创建新文件夹
        File folder = new File(folderPath);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                System.err.println("无法创建文件夹：" + folderPath);
                // 如果创建文件夹失败，可以根据实际情况进行处理，这里直接返回
            }
        }
        list.stream().forEach(a->{
            if(a.getSvs()!=null){
                // 获取实际文件的URL
                String fileUrl = a.getSvs();
                // 构建下载文件的本地路径和文件名
                String fileName = folderPath + getFileNameFromUrl(fileUrl);
                try {
                    // 下载文件到指定路径
                    downloadFile(fileUrl, fileName);
                    System.out.println("文件已下载到：" + fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        list.stream().forEach(a->{
            a.setDoctorName(sysUserService.selectUserById(a.getDoctor()).getNickName());
            if(a.getState()==0){
                a.setStateName("未生成");
            }else {
                a.setStateName("已生成");
            }
            if(a.getSvs()!=null){
                a.setSvs("有");
            }else{
                a.setSvs("无");
            }
            // 转换为 LocalDateTime
            LocalDateTime datetime = LocalDateTime.ofInstant(Instant.ofEpochMilli(a.getRegistrationTime()), ZoneId.systemDefault());
            // 指定输出格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // 格式化日期时间
            String formattedDateTime = datetime.format(formatter);
            a.setRegistrationDate(formattedDateTime);
        });
        // 导出Excel文件到新创建的文件夹
        ExcelUtil<Sample> util = new ExcelUtil<>(Sample.class);
        String excelFileName = folderPath + "example.xlsx";
        try {
            File excelFile = new File(excelFileName);
            if (!excelFile.getParentFile().exists()) {
                excelFile.getParentFile().mkdirs();
            }
            // 提取文件名部分作为工作表名
            String sheetName = "样本数据";
            System.out.println("sheetName = " + sheetName);
            util.exportExcel(list, sheetName, sheetName,timestamp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //转压缩包
        String zipFilePath = "/home/program/path-dig/file/download/" + timestamp + ".zip";
        try {
            Path zip = Paths.get(folderPath);
            FileOutputStream fos = new FileOutputStream(zipFilePath);
            ZipOutputStream zos = new ZipOutputStream(fos);

            Files.walk(zip)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(zip.relativize(path).toString());
                        try {
                            zos.putNextEntry(zipEntry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            zos.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverConfig.getUrl() + "/profile/download/" + timestamp + ".zip";
    }

    @Override
    public List<Sample> selectNotSave() {
        return sampleMapper.selectNotSave();
    }

    @Override
    public void delSvs(Long id) {
         sampleMapper.delSvs(id);
    }

    @Override
    public int saveAfterAnalysis(Sample sample) {
        return sampleMapper.saveAfterAnalysis(sample);
    }


    // 从URL中获取文件名
    private String getFileNameFromUrl(String fileUrl) {
        // 例如，假设fileUrl的形式为http://example.com/files/sample.docx，则从URL中截取出文件名sample.docx
        int index = fileUrl.lastIndexOf("/");
        if (index != -1) {
            return fileUrl.substring(index + 1);
        }
        return "unknown_filename";
    }

    // 下载文件方法（示例，具体实现根据你的需求选择合适的下载方法）
    private void downloadFile(String fileUrl, String fileName) throws IOException {
        URL url = new URL(fileUrl);
        try (InputStream in = url.openStream();
             FileOutputStream out = new FileOutputStream(fileName)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }


}
