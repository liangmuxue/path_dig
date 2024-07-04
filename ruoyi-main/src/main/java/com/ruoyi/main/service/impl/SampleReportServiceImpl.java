package com.ruoyi.main.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.main.domain.Sample;
import com.ruoyi.main.mapper.SampleMapper;
import com.ruoyi.main.vo.AfterUploadVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.main.mapper.SampleReportMapper;
import com.ruoyi.main.domain.SampleReport;
import com.ruoyi.main.service.ISampleReportService;
import javax.annotation.Resource;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * ai诊断分析Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-06-27
 */
@Service
public class SampleReportServiceImpl implements ISampleReportService 
{
    @Resource
    private SampleReportMapper sampleReportMapper;
    @Resource
    private SampleMapper sampleMapper;

    /**
     * 查询ai诊断分析
     * 
     * @param id ai诊断分析主键
     * @return ai诊断分析
     */
    @Override
    public SampleReport selectSampleReportById(Long id)
    {
        return sampleReportMapper.selectSampleReportById(id);
    }

    /**
     * 查询ai诊断分析列表
     * 
     * @param sampleReport ai诊断分析
     * @return ai诊断分析
     */
    @Override
    public List<SampleReport> selectSampleReportList(SampleReport sampleReport)
    {
        return sampleReportMapper.selectSampleReportList(sampleReport);
    }

    /**
     * 新增ai诊断分析
     * 
     * @param sampleReport ai诊断分析
     * @return 结果
     */
    @Override
    public AfterUploadVo insertSampleReport(SampleReport sampleReport)
    {
        AfterUploadVo afterUploadVo = new AfterUploadVo();
        Sample sample = sampleMapper.selectSampleById(sampleReport.getSamplePid());
        if(sample.getSvs()==null){
            afterUploadVo.setError(-1);
            return afterUploadVo;
        }
        // 先上传给算法
        // 构建请求的JSON参数
        String jsonBody = "{\"svs_path\": \"" + sample.getSvsPath() + "\", \"sampleId\": \"" + sample.getSampleId() + "\"}";
        System.out.println("jsonBody = " + jsonBody);
        // 设置请求的URL和Content-Type
        String api = "http://192.168.0.98:8088/upload_svs_file";
        String contentType = "application/json";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 创建HttpPost请求
            HttpPost httpPost = new HttpPost(api);

            // 设置请求的Content-Type
            httpPost.setHeader("Content-Type", contentType);

            // 设置请求的JSON参数
            StringEntity jsonEntity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
            httpPost.setEntity(jsonEntity);

            // 执行请求并获取响应
            HttpResponse response = httpClient.execute(httpPost);

            // 处理响应
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String responseString = EntityUtils.toString(responseEntity);
                System.out.println("Response from server: " + responseString);
                // 在这里处理服务器返回的响应数据
                ObjectMapper objectMapper = new ObjectMapper();
                afterUploadVo = objectMapper.readValue(responseString, AfterUploadVo.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(afterUploadVo.getError()==0){//文件上传成功
            if(sampleReportMapper.checkHaveReport(sampleReport)!=0){//覆盖原来的报告
                sampleReportMapper.deleteSampleReportBySamplePid(sampleReport);
            }
            sampleReport.setSamplePid(sample.getId());
            sampleReport.setSampleId(sample.getSampleId());
            sampleReport.setAiTime(System.currentTimeMillis());
            sampleReport.setUpdateTime(sampleReport.getAiTime());
            sampleReport.setState(0l);
            sampleReportMapper.insertSampleReport(sampleReport);
        }
        return afterUploadVo;
    }

    public void send() {
        try {
            // 指定URL
            URL url = new URL("http://192.168.0.98/stage_send");

            // 创建HttpURLConnection对象
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 设置请求方法为POST
            conn.setRequestMethod("POST");

            // 设置请求头属性
            conn.setRequestProperty("Content-Type", "application/json");

            // 设置允许输出
            conn.setDoOutput(true);

            // 获取输出流并写入请求体（这里假设请求体是一个空的JSON对象）
            try (OutputStream os = conn.getOutputStream()) {
                os.flush(); // 可选的，如果没有实际的请求体内容
            }

            // 获取响应码
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code : " + responseCode);

            // 读取响应内容
            if (responseCode == HttpURLConnection.HTTP_OK) { // 如果响应码是200
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // 打印响应内容
                System.out.println("Response Content : " + response.toString());
            } else {
                System.out.println("POST request not worked");
            }

            // 关闭连接
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 修改ai诊断分析
     * 
     * @param sampleReport ai诊断分析
     * @return 结果
     */
    @Override
    public int updateSampleReport(SampleReport sampleReport)
    {
        sampleReport.setUpdateTime(System.currentTimeMillis());
        return sampleReportMapper.updateSampleReport(sampleReport);
    }

    /**
     * 批量删除ai诊断分析
     * 
     * @param ids 需要删除的ai诊断分析主键
     * @return 结果
     */
    @Override
    public int deleteSampleReportByIds(Long[] ids)
    {
        return sampleReportMapper.deleteSampleReportByIds(ids);
    }

    /**
     * 删除ai诊断分析信息
     * 
     * @param id ai诊断分析主键
     * @return 结果
     */
    @Override
    public int deleteSampleReportById(Long id)
    {
        return sampleReportMapper.deleteSampleReportById(id);
    }

    @Override
    public SampleReport selectSampleReportBySampleId(String sampleId) {
        return sampleReportMapper.selectSampleReportBySampleId(sampleId);
    }


}
