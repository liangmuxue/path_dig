package com.ruoyi.main.controller;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ruoyi.main.service.ISampleJobService;
import com.ruoyi.main.vo.Image2;
import com.ruoyi.main.vo.ReportResultVo;
import com.ruoyi.main.vo.StageSendVo;
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
import com.ruoyi.main.domain.SampleReport;
import com.ruoyi.main.service.ISampleReportService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * ai诊断分析Controller
 * 
 * @author ruoyi
 * @date 2024-06-27
 */
@RestController
@RequestMapping("/system/report")
public class SampleReportController extends BaseController
{
    @Autowired
    private ISampleReportService sampleReportService;
    @Resource
    private ISampleJobService sampleJobService;
    /**
     * 查询ai诊断分析列表
     */
    @GetMapping("/list")
    public TableDataInfo list(SampleReport sampleReport)
    {
        startPage();
        List<SampleReport> list = sampleReportService.selectSampleReportList(sampleReport);
        return getDataTable(list);
    }

    /**
     * 导出ai诊断分析列表
     */
    @Log(title = "ai诊断分析", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SampleReport sampleReport)
    {
        List<SampleReport> list = sampleReportService.selectSampleReportList(sampleReport);
        ExcelUtil<SampleReport> util = new ExcelUtil<SampleReport>(SampleReport.class);
        util.exportExcel(response, list, "ai诊断分析数据");
    }

    /**
     * 获取ai诊断分析详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(sampleReportService.selectSampleReportById(id));
    }

    //send到算法识别查状态
    @PostMapping("/stageSend")
    public AjaxResult stageSend()
    {
        AjaxResult ajaxResult = new AjaxResult();
        try {
            // 指定URL

            URL url = new URL("http://192.168.0.98:8088/stage_send");

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

            ObjectMapper mapper = new ObjectMapper();
//            //加参数
//            ObjectNode requestBody = mapper.createObjectNode();
//            requestBody.put("id", sampleReport.getSamplePid());
//            requestBody.putPOJO("sampleReport", sampleReport);
//            // 获取输出流并写入请求体
//            try (OutputStream os = conn.getOutputStream()) {
//                byte[] input = requestBody.toString().getBytes("utf-8");
//                os.write(input, 0, input.length);
//            }

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
                // 将 JSON 字符串转换为对象
                StageSendVo stageSendVo = mapper.readValue(response.toString(), StageSendVo.class);

                // 设置 AjaxResult 的返回值
                ajaxResult.put("code",200);
                ajaxResult.put("msg",stageSendVo);
            } else {
                System.out.println("POST request not worked");
            }
            // 关闭连接
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            return ajaxResult;
        }

        return ajaxResult;
    }

    /**
     * 新增ai诊断分析--进行分析
     */
    @Log(title = "ai诊断分析", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SampleReport sampleReport)
    {
        //分析前检查当前是否可以进行分析
        int count = sampleJobService.checkBeforeAnalysis(sampleReport);
        if(count>0){
            return AjaxResult.error("已有样本在进行分析中,请稍后再试");
        }
        sampleReport.setInspectDoctor(getUserId());
        if(sampleReportService.insertSampleReport(sampleReport)==-1){
            return AjaxResult.error("该样本没有源文件,无法分析");
        }
        return AjaxResult.success();
    }

//    public static void main(String[] args) {
//        File inputFile = new File("path/to/your/svs/file.svs");
//        OpenSlide openSlide = new OpenSlide(inputFile);
//
//        // 获取瓦片的尺寸
//        int tileSize = openSlide.getTileSize();
//
//        // 获取瓦片的总数
//        int[] levelDimensions = openSlide.getLevelDimensions(0);
//        int numXTiles = (int) Math.ceil((double) levelDimensions[0] / tileSize);
//        int numYTiles = (int) Math.ceil((double) levelDimensions[1] / tileSize);
//
//        // 逐个获取瓦片并保存
//        for (int y = 0; y < numYTiles; y++) {
//            for (int x = 0; x < numXTiles; x++) {
//                BufferedImage tile = openSlide.getTile(0, x * tileSize, y * tileSize);
//                File outputFile = new File("output/directory/tile_" + x + "_" + y + ".png");
//                ImageIO.write(tile, "png", outputFile);
//            }
//        }
//
//        openSlide.close();
//    }

    /**
     * send回信息有结果了调用result拿分析结果
     */
    @Log(title = "拿分析结果", businessType = BusinessType.INSERT)
    @PostMapping("/getResult")
    public AjaxResult getResult(@RequestBody SampleReport sampleReport)
    {
        AjaxResult ajaxResult = new AjaxResult();
        try {
            // 构建请求体JSON
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode requestBody = mapper.createObjectNode();
            // 添加sampleId参数
            requestBody.put("sampleId", sampleReport.getSampleId());

            // 指定URL
            URL url = new URL("http://192.168.0.98:8088/receive_svs_results");
            // 创建HttpURLConnection对象
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置请求方法为POST
            conn.setRequestMethod("POST");
            // 设置请求头属性
            conn.setRequestProperty("Content-Type", "application/json");
            // 设置允许输出
            conn.setDoOutput(true);
            // 无参
//            try (OutputStream os = conn.getOutputStream()) {
//                os.flush(); // 可选的，如果没有实际的请求体内容
//            }
            //有参数
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
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
                Image2 image2 = mapper.readValue(response.toString(), Image2.class);

                // 将 JSON 字符串转换为对象
//                ReportResultVo reportResultVo = mapper.readValue(response.toString(), ReportResultVo.class);
//                reportResultVo.getImage().stream().forEach(s->{
//                    s.getAis().stream().forEach(a->{
//                        image(a.getImage());
//                    });
//                    s.getLsil().stream().forEach(a->{
//                        image(a.getImage());
//                    });
//                    s.getHsil().stream().forEach(a->{
//                        image(a.getImage());
//                    });
//                });
                // 设置 AjaxResult 的返回值
                ajaxResult.put("code",200);
                ajaxResult.put("msg",image2);
                // 图片保存路径
                String savePath = "C:\\Users\\DELL\\Desktop\\ruoyi\\"+System.currentTimeMillis()+".jpg";
                saveImage(image2.getImages(), savePath);
                System.out.println("image2 = " +  image2.getBox().toString());
            } else {
                System.out.println("POST request not worked");
                ajaxResult.put("code",responseCode);
                ajaxResult.put("msg", "POST request failed with response code: " + responseCode);
            }
            // 关闭连接
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            ajaxResult.put("code", 500); // Internal server error
            ajaxResult.put("msg", "Internal server error: " + e.getMessage());
        }
        return ajaxResult;
    }

    public static void saveImage(String base64Image, String path) {
        try {
            // 解码Base64数据
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            // 写入文件
            try (FileOutputStream fos = new FileOutputStream(path)) {
                fos.write(imageBytes);
                System.out.println("图片保存成功：" + path);
            } catch (IOException e) {
                System.out.println("保存图片时发生错误：" + e.getMessage());
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Base64 编码的图片数据无效：" + e.getMessage());
        }
    }



    /**
     * 修改ai诊断分析
     */
    @Log(title = "ai诊断分析", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SampleReport sampleReport)
    {
        return toAjax(sampleReportService.updateSampleReport(sampleReport));
    }

    /**
     * 删除ai诊断分析
     */
    @Log(title = "ai诊断分析", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sampleReportService.deleteSampleReportByIds(ids));
    }
}
