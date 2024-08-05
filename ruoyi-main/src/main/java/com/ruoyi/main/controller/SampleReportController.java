package com.ruoyi.main.controller;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.ruoyi.main.domain.ReportType;
import com.ruoyi.main.domain.Sample;
import com.ruoyi.main.domain.SampleJob;
import com.ruoyi.main.dto.SampleReportDTO;
import com.ruoyi.main.mapper.SampleMapper;
import com.ruoyi.main.service.IReportTypeService;
import com.ruoyi.main.service.ISampleJobService;
import com.ruoyi.main.util.ExtractConfiguration;
import com.ruoyi.main.vo.*;
import com.ruoyi.system.service.ISysUserService;
import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold;
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
    @Resource
    private SampleMapper sampleMapper;
    @Resource
    private IReportTypeService reportTypeService;
    @Resource
    private ISysUserService sysUserService;
    @Resource
    private ExtractConfiguration extractConfiguration;
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
     * 查询ai诊断分析列表--多条件分页查询
     */
    @PostMapping("/pageList")
    public TableDataInfo pageList(@RequestBody SampleReportDTO sampleReportDTO)
    {
        PageHelper.startPage(sampleReportDTO.getPageNum(), sampleReportDTO.getPageSize());
        PageInfo<SampleReport> pageInfo = sampleReportService.selectSampleReportPageList(sampleReportDTO, sampleReportDTO.getPageNum(), sampleReportDTO.getPageSize());
        List<SampleReport> list = pageInfo.getList();
        list.stream().forEach(a->{
            if(a.getVerifyDoctor()!=null){
                a.setVerifyDoctorName(sysUserService.selectUserById(a.getVerifyDoctor()).getNickName());
            }
        });
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
    @PostMapping("/getInfo")
    public AjaxResult getInfo(@RequestBody SampleReport sampleReport)
    {
        sampleReport = sampleReportService.selectSampleReportById(sampleReport.getId());
        sampleReport.setInspectDoctorName(sysUserService.selectUserById(sampleReport.getInspectDoctor()).getNickName());
        if(sampleReport.getVerifyDoctor()!=null){
            sampleReport.setVerifyDoctorName(sysUserService.selectUserById(sampleReport.getVerifyDoctor()).getNickName());
        }
        if(sampleReport.getState()==0){
            sampleReport.setStateName("未审核");
        }else {
            sampleReport.setStateName("已审核");
        }
        return AjaxResult.success(sampleReport);
    }

    //send到算法识别查状态--弃用
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
        String url = extractConfiguration.getSaveUrl();
        System.out.println("url = " + url);
        Sample sample = sampleMapper.selectSampleById(sampleReport.getSamplePid());
        //分析前检查当前是否可以进行分析
        int count = sampleJobService.checkBeforeAnalysis();
        if(count>0){
            return AjaxResult.error("已有样本在进行分析中,请稍后再试");
        }
        sampleReport.setSamplePid(sample.getId());
        sampleReport.setInspectDoctor(getUserId());
        SampleJob sampleJob = new SampleJob();
        AfterUploadVo afterUploadVo = sampleReportService.insertSampleReport(sampleReport);
        if(afterUploadVo.getError()==-1){
            return AjaxResult.error("该样本没有源文件,无法分析");
        }else if(afterUploadVo.getError()==-2){
            return AjaxResult.error("算法模型没有开启,请稍后再试");
        } else if(afterUploadVo.getError()==0){//------成功
            sampleJob.setTime(System.currentTimeMillis());
            sampleJob.setState(0l);//初始状态
            sampleJob.setSampleId(sample.getSampleId());
            sampleJob.setSamplePid(sample.getId());
            sampleJob.setDoctor(getUserId());
            sampleJobService.deleteSampleJobBySampleId(sampleJob.getSampleId());
            sampleJobService.insertSampleJob(sampleJob);
            return AjaxResult.success("文件上传成功");
        }else if(afterUploadVo.getError()==1){
            return AjaxResult.error("文件不存在");
        }else if(afterUploadVo.getError()==2){
            return AjaxResult.error("当前算法模型已有文件正在处理中,请稍后再试");
        }else if(afterUploadVo.getError()==3){
            return AjaxResult.error("当前状态异常");
        }
        return AjaxResult.success();
    }


    /**
     * send回信息有结果了调用result拿分析结果
     */
    @Log(title = "拿分析结果", businessType = BusinessType.INSERT)
    @PostMapping("/getResult")
    public AjaxResult getResult(@RequestBody SampleReport sampleReport)
    {
        sampleReport=sampleReportService.selectSampleReportBySamplePId(sampleReport.getSamplePid());
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
                ResultRecipientVo resultRecipientVo = mapper.readValue(response.toString(), ResultRecipientVo.class);
                //svs文件转瓦片图
                String dziUrl = "http://192.168.0.98:8092/"+sampleReport.getSampleId()+"/"+sampleReport.getSampleId()+"_files";
                resultRecipientVo.setDizFileUrl(dziUrl);
                System.out.println("resultRecipientVo.size = " + resultRecipientVo.getSize());
                //拿到对象接收的结果
                SampleReport report = sampleReportService.selectSampleReportBySampleId(sampleReport.getSampleId());
                Map<String, List<int[]>> boxes = resultRecipientVo.getBoxes();
                Gson gson = new Gson();
                List<int[]> lsilBoxes = boxes.get("lsil");
                if (lsilBoxes != null) {
                    ReportType reportType = new ReportType();
                    reportType.setType("lsil");
                    reportType.setReportId(report.getId());
                    for (int[] box : lsilBoxes) {
                        String json = gson.toJson(box);
                        reportType.setLocation(json);
                        reportType.setPic(picUrl(json,sampleReport.getSampleId(),"lsil"));
                        reportTypeService.insertReportType(reportType);
                    }
                }
                List<int[]> hsilBoxes = boxes.get("hsil");
                if (hsilBoxes != null) {
                    ReportType reportType = new ReportType();
                    reportType.setType("hsil");
                    reportType.setReportId(report.getId());
                    for (int[] box : hsilBoxes) {
                        String json = gson.toJson(box);
                        reportType.setLocation(json);
                        reportType.setPic(picUrl(json,sampleReport.getSampleId(),"hsil"));
                        reportTypeService.insertReportType(reportType);
                    }
                }
                List<int[]> aisBoxes = boxes.get("ais");
                if (aisBoxes != null) {
                    ReportType reportType = new ReportType();
                    reportType.setType("ais");
                    reportType.setReportId(report.getId());
                    for (int[] box : aisBoxes) {
                        String json = gson.toJson(box);
                        reportType.setLocation(json);
                        reportType.setPic(picUrl(json,sampleReport.getSampleId(),"ais"));
                        reportTypeService.insertReportType(reportType);
                    }
                }
                report.setAiDiagnosis(aiDiagnosisCode(resultRecipientVo.getCategory()));
                report.setAiTime(System.currentTimeMillis());
                report.setUpdateTime(report.getAiTime());
                report.setQuality(1);//有效样本
                //还有两张图
                if(resultRecipientVo.getCategory()!=null){
                    ReportType reportType = new ReportType();
                    reportType.setReportId(report.getId());
                    reportType.setType(resultRecipientVo.getCategory());
                    List<ReportType> list = reportTypeService.selectReportTypeList(reportType);
                    report.setPicOne(list.get(0).getPic());
                    report.setPicTwo(list.get(1).getPic());
                }
                report.setPicBig(dziUrl);
                report.setSize(gson.toJson(resultRecipientVo.getSize()));
                report.setDone(1);
                sampleReportService.updateSampleReport(report);//更新报告
                //报告生成后 样本的报告已生成标识改变
                Sample sample = sampleMapper.selectSampleById(report.getSamplePid());
                sample.setState(1);
                sampleMapper.updateSample(sample);

                // 设置 AjaxResult 的返回值
                ajaxResult.put("code",200);
                ajaxResult.put("msg",resultRecipientVo);

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

    public String picUrl(String json,String sampleId,String model){
        String save = "http://192.168.0.98:8092/"+sampleId+"/"+model+"/smallPic/"+json+".jpg";
        return save;
    }

    public String aiDiagnosisCode(String aiDiagnosis){
        String result = "";
        if(aiDiagnosis!=null){
            if(aiDiagnosis.equals("normal")){
                return result = "0";
            }
            if(aiDiagnosis.equals("hsil")){
                return result = "1";
            }
            if(aiDiagnosis.equals("lsil")){
                return result = "2";
            }
            if(aiDiagnosis.equals("ais")){
                return result = "3";
            }
        }
        return null;
    }

    /**
     * 根据svs文件转jpg
     */
    @PostMapping("/svsTurnJpg")
    public AjaxResult svsTurnJpg(@RequestBody SampleReport sampleReport)
    {
        AjaxResult ajaxResult = new AjaxResult();
        sampleReport = sampleReportService.selectSampleReportBySampleId(sampleReport.getSampleId());
        Sample sample = sampleMapper.selectSampleBySampleId(sampleReport.getSampleId());
        if(sample.getSvs()==null){
            return AjaxResult.error("源文件解析失败");
        }
        String saveUrl = "/home/program/path-dig/image/";
        try {
            // 构建请求体JSON
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode requestBody = mapper.createObjectNode();
            // 添加sampleId参数
            requestBody.put("saveUrl", saveUrl);
            requestBody.put("sampleId", sampleReport.getSampleId());

            // 指定URL
            URL url = new URL("http://192.168.0.98:8088/turnJpg");
            // 创建HttpURLConnection对象
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置请求方法为POST
            conn.setRequestMethod("POST");
            // 设置请求头属性
            conn.setRequestProperty("Content-Type", "application/json");
            // 设置允许输出
            conn.setDoOutput(true);

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
                SvsTurnJpgVo svsTurnJpgVo = mapper.readValue(response.toString(), SvsTurnJpgVo.class);
                if(svsTurnJpgVo.getCode()==0){//有大图
                    String save = "http://192.168.0.98:8092/"+sampleReport.getSampleId()+"/"+sampleReport.getSampleId()+".jpg";
                    sampleReport.setPicBig(save);
                    svsTurnJpgVo.setSave_big_path(save);
                    //把大图放进报告
                    sampleReportService.updateSampleReport(sampleReport);
                    // 设置 AjaxResult 的返回值
                    ajaxResult.put("code",200);
                    ajaxResult.put("msg",svsTurnJpgVo);
                }
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


    /**
     * 获取ai诊断分析详细信息
     */
    @PostMapping("/getReport")
    public AjaxResult getReport(@RequestBody Map<String, Long> requestBody)
    {
        Long samplePid = requestBody.get("samplePid");
        SampleReport sampleReport = sampleReportService.selectSampleReportBySamplePId(samplePid);
        List<ReportType> list = reportTypeService.selectReportTypeByReportId(sampleReport.getId());
        List<ReportType> lsil =new ArrayList<>();
        List<ReportType> hsil =new ArrayList<>();
        List<ReportType> ais =new ArrayList<>();
        list.stream().forEach(a->{
            if(a.getType().equals("lsil")){
                lsil.add(a);
            }else if(a.getType().equals("hsil")){
                hsil.add(a);
            }else if(a.getType().equals("ais")){
                ais.add(a);
            }
        });
        sampleReport.setLsilList(lsil);
        sampleReport.setHsilList(hsil);
        sampleReport.setAisList(ais);
        sampleReport.setInspectDoctorName(sysUserService.selectUserById(sampleReport.getInspectDoctor()).getNickName());
        if(sampleReport.getVerifyDoctor()!=null){
            sampleReport.setVerifyDoctorName(sysUserService.selectUserById(sampleReport.getVerifyDoctor()).getNickName());
        }
        if(sampleReport.getState()==0){
            sampleReport.setStateName("未审核");
        }else {
            sampleReport.setStateName("已审核");
        }
        return AjaxResult.success(sampleReport);
    }

    /**
     * 修改ai诊断分析
     */
    @Log(title = "ai诊断分析", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody SampleReport sampleReport)
    {
        sampleReport.setInspectDoctor(getUserId());
        sampleReport.setUpdateTime(System.currentTimeMillis());
        sampleReport.setReportTime(sampleReport.getUpdateTime());
        return toAjax(sampleReportService.updateSampleReport(sampleReport));
    }

    /**
     * 审核报告
     */
    @Log(title = "ai诊断分析", businessType = BusinessType.UPDATE)
    @PostMapping("/verify")
    public AjaxResult verify(@RequestBody SampleReport sampleReport)
    {
        sampleReport.setVerifyDoctor(getUserId());
        sampleReport.setState(1l);
        return toAjax(sampleReportService.updateSampleReport(sampleReport));
    }

    /**
     * 删除ai诊断分析
     */
    @Log(title = "ai诊断分析", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody Map<String, Long[]> requestBody)
    {
        Long[] ids = requestBody.get("ids");
        if (ids == null || ids.length == 0) {
            // 如果 ids 为空或者长度为0，可以根据具体情况返回错误信息或者处理逻辑
            return AjaxResult.error("未提供要删除的报告数据的ID");
        }
        //把样本的报告恢复成未生成报告
        for (int i = 0; i < ids.length; i++) {
            Sample sample = sampleMapper.selectSampleById(sampleReportService.selectSampleReportById(ids[i]).getSamplePid());
            sample.setState(0);
            sampleMapper.updateSample(sample);
        }
        sampleReportService.deleteSampleReportByIds(ids);
        for (int i = 0; i < ids.length; i++) {//同时删除小图
            reportTypeService.deleteReportTypeByReport(ids[i]);
        }
        return AjaxResult.success();
    }
}
