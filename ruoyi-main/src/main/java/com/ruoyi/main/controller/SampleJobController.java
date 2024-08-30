package com.ruoyi.main.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.ruoyi.main.domain.*;
import com.ruoyi.main.mapper.SampleMapper;
import com.ruoyi.main.service.*;
import com.ruoyi.main.util.ExtractConfiguration;
import com.ruoyi.main.vo.ResultRecipientVo;
import com.ruoyi.main.vo.SmallPicVo;
import com.ruoyi.main.vo.StageSendVo;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.data.redis.core.RedisTemplate;
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
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 样本分析jobController
 * 
 * @author ruoyi
 * @date 2024-07-03
 */
@RestController
@RequestMapping("/system/job")
public class SampleJobController extends BaseController
{
    @Resource
    private ISampleJobService sampleJobService;
    @Resource
    private ISampleService sampleService;
    @Autowired
    private ISampleReportService sampleReportService;
    @Resource
    private SampleMapper sampleMapper;
    @Resource
    private IReportTypeService reportTypeService;
    @Resource
    private IBoxPointVerticesService boxPointVerticesService;
    @Resource
    private RedisTemplate redisTemplate;


    /**
     * 查询样本分析job列表
     */
    @GetMapping("/list")
    public TableDataInfo list(SampleJob sampleJob)
    {
        startPage();
        List<SampleJob> list = sampleJobService.selectSampleJobList(sampleJob);
        return getDataTable(list);
    }

    /**
     * 导出样本分析job列表
     */
    @Log(title = "样本分析job", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SampleJob sampleJob)
    {
        List<SampleJob> list = sampleJobService.selectSampleJobList(sampleJob);
        ExcelUtil<SampleJob> util = new ExcelUtil<SampleJob>(SampleJob.class);
        util.exportExcel(response, list, "样本分析job数据");
    }

    /**
     * 获取样本分析job详细信息
     */
    @PostMapping("/getInfo")
    public AjaxResult getInfo(@RequestBody Map<String, Long> requestBody)
    {
        Long id = requestBody.get("id");
        SampleJob sampleJob = sampleJobService.selectSampleJobBySamplePid(id);
        if(sampleJob==null){
            return AjaxResult.error("此样本分析任务已被删除");
        }
        if(sampleJob.getState()==0){
            sampleJob.setStateMsg("文件正在分析");
        }
        if(sampleJob.getState()==1){
            sampleJob.setStateMsg("ais模型推理完成");
        }
        if(sampleJob.getState()==2){
            sampleJob.setStateMsg("hsil模型推理完成");
        }
        if(sampleJob.getState()==3){
            sampleJob.setStateMsg("lsil模型推理完成");
        }
        if(sampleJob.getState()==4){
            sampleJob.setStateMsg("推理结果处理完成，样本有效 ");
        }
        if(sampleJob.getState()==5){
            sampleJob.setStateMsg("推理结果处理完成，样本模糊无效");
        }
        return AjaxResult.success(sampleJob);
    }

    //py->java阶段更新分析任务的状态 NEW
    @PostMapping("/stageSend")
    public AjaxResult stageSend(@RequestBody SampleJob sampleJob)
    {
        System.out.println("**************************** sampleJob state ****************************** = " + sampleJob.getState());
        if(sampleJob.getState()==4){
            SampleReport sampleReport = sampleReportService.selectSampleReportBySampleId(sampleJob.getSampleId());
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
                    System.out.println("responseCode = " + "获取结果成功200");
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
                    //svs大图JPG
                    String fullJpgUrl = "http://192.168.0.98:8092/"+sampleReport.getSampleId()+"/full_"+sampleReport.getSampleId()+".jpg";
                    //打印一下瓦片图的size
                    System.out.println("resultRecipientVo.size = " + resultRecipientVo.getSize());
                    //zoom
                    int[] zoom = resultRecipientVo.getZoom();
                    //拿到对象接收的结果
                    SampleReport report = sampleReportService.selectSampleReportBySampleId(sampleReport.getSampleId());
                    Map<String, List<int[]>> boxes = resultRecipientVo.getBoxes();
                    Map<String, int[]> size = resultRecipientVo.getSize();
                    int[] lsilSize = size.get("lsil");
                    int[] hsilSize = size.get("hsil");
                    int[] aisSize= size.get("ais");
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
                            reportType.setSize(gson.toJson(lsilSize));
                            reportType.setLevel(resultRecipientVo.getLevel().get("lsil"));
                            reportType.setSource(0);
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
                            reportType.setSize(gson.toJson(hsilSize));
                            reportType.setLevel(resultRecipientVo.getLevel().get("hsil"));
                            reportType.setSource(0);
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
                            reportType.setSize(gson.toJson(aisSize));
                            reportType.setLevel(resultRecipientVo.getLevel().get("ais"));
                            reportType.setSource(0);
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
                        System.out.println("list = " + list.size());
                        if(list.size()>1){
                            report.setPicOne(list.get(0).getPic());
                            report.setPicTwo(list.get(1).getPic());
                        }else {
                            report.setPicOne(list.get(0).getPic());
                        }
                    }
                    report.setPicBig(dziUrl);
                    report.setSize(gson.toJson(lsilSize));//最大层级lsil
                    report.setDone(1);
                    report.setZoom(gson.toJson(zoom));
                    sampleReportService.updateSampleReport(report);//更新报告
                    //报告生成后 样本的报告已生成标识改变
                    Sample sample = sampleMapper.selectSampleById(report.getSamplePid());
                    sample.setState(1);
                    sample.setPic(fullJpgUrl);
                    sample.setWorking(0);//恢复成未分析的状态
                    sampleMapper.updateSample(sample);

                    // 设置 AjaxResult 的返回值
                    ajaxResult.put("code",200);
                    ajaxResult.put("resultRecipientVo",resultRecipientVo);
                } else {
                    System.out.println("拿到分析结果读取响应内容响应错误");
                    ajaxResult.put("code",responseCode);
                    ajaxResult.put("msg", "拿到分析结果读取响应内容的响应码 " + responseCode);
                }
                // 关闭连接
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                ajaxResult.put("code", 500); // Internal server error
                ajaxResult.put("msg", "拿到分析结果错误" + e.getMessage());
            }
//            sampleReport=sampleReportService.selectSampleReportBySamplePId(sampleReport.getSamplePid());
            //开始保存全部小图
            try {
                // 构建请求体JSON
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode requestBody = mapper.createObjectNode();
                // 添加sampleId参数
                requestBody.put("sampleId", sampleReport.getSampleId());
                // 指定URL
                URL url = new URL("http://192.168.0.98:8088/saveAllSamll");
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
                    System.out.println("Response Content : " + response);
                    SmallPicVo smallPicVo = mapper.readValue(response.toString(), SmallPicVo.class);
                    int  aisLevel = smallPicVo.getLevel().get("ais");
                    int  lsilLevel = smallPicVo.getLevel().get("lsil");
                    int  hsilLevel = smallPicVo.getLevel().get("hsil");
                    //拿到对象接收的结果
                    SampleReport report = sampleReportService.selectSampleReportBySampleId(sampleReport.getSampleId());
                    Map<String, List<int[]>> boxes = smallPicVo.getBoxes();
                    Map<String, List<List<List<Double>>>> verticesMap = smallPicVo.getBox_point_vertices();
                    Gson gson = new Gson();
                    // 打印 ais 内容
                    System.out.println("ais:");
                    List<List<List<Double>>> ais = verticesMap.get("ais");
                    if (ais != null) {
                        for (List<List<Double>> shape : ais) {
                            System.out.println("ais--------------"+shape);
                            BoxPointVertices boxPointVertices = new BoxPointVertices();
                            boxPointVertices.setPoint(gson.toJson(shape));
                            boxPointVertices.setType("ais");
                            boxPointVertices.setReportId(report.getId());
                            boxPointVerticesService.insertBoxPointVertices(boxPointVertices);
                        }
                    }

                    // 打印 hsil 内容
                    System.out.println("hsil:");
                    List<List<List<Double>>> hsil = verticesMap.get("hsil");
                    if (hsil != null) {
                        for (List<List<Double>> shape : hsil) {
                            System.out.println("hsil--------------"+shape);
                            BoxPointVertices boxPointVertices = new BoxPointVertices();
                            boxPointVertices.setPoint(gson.toJson(shape));
                            boxPointVertices.setType("hsil");
                            boxPointVertices.setReportId(report.getId());
                            boxPointVerticesService.insertBoxPointVertices(boxPointVertices);
                        }
                    }

                    // 打印 lsil 内容
                    System.out.println("lsil:");
                    List<List<List<Double>>> lsil = verticesMap.get("lsil");
                    if (lsil != null) {
                        for (List<List<Double>> shape : lsil) {
                            System.out.println("lsil--------------"+shape);
                            BoxPointVertices boxPointVertices = new BoxPointVertices();
                            boxPointVertices.setPoint(gson.toJson(shape));
                            boxPointVertices.setType("lsil");
                            boxPointVertices.setReportId(report.getId());
                            boxPointVerticesService.insertBoxPointVertices(boxPointVertices);
                        }
                    }
                    List<int[]> lsilBoxes = boxes.get("lsil");
                    if (lsilBoxes != null) {
                        ReportType reportType = new ReportType();
                        reportType.setType("lsil");
                        reportType.setReportId(report.getId());
                        for (int[] box : lsilBoxes) {
                            String json = gson.toJson(box);
                            reportType.setLocation(json);
                            reportType.setPic(allPicUrl(json,sampleReport.getSampleId(),"lsil"));
                            reportType.setSize(gson.toJson(smallPicVo.getSize()));
                            reportType.setLevel(lsilLevel);
                            reportType.setSource(1);
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
                            reportType.setPic(allPicUrl(json,sampleReport.getSampleId(),"hsil"));
                            reportType.setSize(gson.toJson(smallPicVo.getSize()));
                            reportType.setLevel(hsilLevel);
                            reportType.setSource(1);
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
                            reportType.setPic(allPicUrl(json,sampleReport.getSampleId(),"ais"));
                            reportType.setSize(gson.toJson(smallPicVo.getSize()));
                            reportType.setLevel(aisLevel);
                            reportType.setSource(1);
                            reportTypeService.insertReportType(reportType);
                        }
                    }
                    // 设置 AjaxResult 的返回值
                    ajaxResult.put("code",200);
                    ajaxResult.put("smallPicVo",smallPicVo);

                } else {
                    System.out.println("拿到全部小图读取响应内容响应错误");
                    ajaxResult.put("code",responseCode);
                    ajaxResult.put("msg", "拿到全部小图读取响应内容的响应码 " + responseCode);
                }
                // 关闭连接
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                ajaxResult.put("code", 500); // Internal server error
                ajaxResult.put("msg", "拿到全部小图错误" + e.getMessage());
            }
            sampleJobService.updateAfterStageSend(sampleJob);
            redisTemplate.opsForValue().set("canUpload", 0);
            return ajaxResult;
        }else {
            sampleJobService.updateAfterStageSend(sampleJob);
            return AjaxResult.success();
        }

    }

    public String picUrl(String json,String sampleId,String model){
        String save = "http://192.168.0.98:8092/"+sampleId+"/"+model+"/smallPic15/"+json+".jpg";
        return save;
    }

    public String allPicUrl(String json,String sampleId,String model){
        String save = "http://192.168.0.98:8092/"+sampleId+"/"+model+"/smallPicAll/"+json+".jpg";
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

    //查能不能上传to解析的
    @PostMapping("/canUpload")
    public AjaxResult canUpload(@RequestBody Map<Object, Object> map) {
        AjaxResult ajaxResult = new AjaxResult();
        String code = map.get("code") != null ? map.get("code").toString() : null;
        // 检查 Redis 中是否存在指定的 key
        Boolean hasKey = redisTemplate.hasKey("canUpload");
        if (hasKey != null && !hasKey) {
            redisTemplate.opsForValue().set("canUpload", "0");
        }
        if (code != null) {
            // 修改 Redis 中 "canUpload" 键的值
            redisTemplate.opsForValue().set("canUpload", code);
            String value = redisTemplate.opsForValue().get("canUpload").toString();
            ajaxResult.put("code",200);
            ajaxResult.put("msg",value);
            return ajaxResult;
        } else {//目前都走这个
//            String number = redisTemplate.opsForValue().get("canUpload").toString();//1
            SampleJob sampleJob = sampleJobService.getInProgressJob(null);//0123
//            if(!number.equals("1")){//0
//                if(sampleJob==null){
//                    redisTemplate.opsForValue().set("canUpload", 0);
//                }else {
//                    redisTemplate.opsForValue().set("canUpload", 1);
//                }
//            }
            // 如果存在，则获取当前 key 的值
            String value = redisTemplate.opsForValue().get("canUpload").toString();
            // 返回当前值
            ajaxResult.put("code",200);
            ajaxResult.put("msg",value);
            ajaxResult.put("data",sampleJob);
            return ajaxResult;
        }
    }

    //给前端当前用户当前执行中任务的样本id
    @PostMapping("/getInProgressJob")
    public AjaxResult getInProgressJob()
    {
        SampleJob sampleJob = new SampleJob();
        return AjaxResult.success(sampleJobService.getInProgressJob(sampleJob));
    }

    //取消正在分析中的任务
    @PostMapping("/cancelDiagnosis")
    public AjaxResult cancelDiagnosis(@RequestBody SampleJob sampleJob){
        Sample sample = sampleService.selectSampleBySampleId(sampleJob.getSampleId());
        AjaxResult ajaxResult = new AjaxResult();
        try {
            // 构建请求体JSON
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode requestBody = mapper.createObjectNode();
            // 添加sampleId参数
            requestBody.put("sampleId", sampleJob.getSampleId());
            // 指定URL
            URL url = new URL("http://192.168.0.98:8088/cancelDiagnosis");
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
                System.out.println("Response Content : " + response);
                StageSendVo stageSendVo = mapper.readValue(response.toString(), StageSendVo.class);
                System.out.println("stageSendVo = " + stageSendVo.getContent());//200成功 500失败
                if(stageSendVo.getContent().equals("200")){
                    sampleJobService.deleteSampleJobBySampleId(sampleJob.getSampleId());
                    sample.setWorking(0);//恢复成未分析的状态
                    sample.setState(0);
                    sampleService.updateSample(sample);
                }else {
                    return AjaxResult.error("取消分析任务失败");
                }
                // 设置 AjaxResult 的返回值
                ajaxResult.put("code",200);
                ajaxResult.put("msg",stageSendVo);

            } else {
                System.out.println("POST request not worked");
                ajaxResult.put("code",responseCode);
                ajaxResult.put("msg", "算法响应码取消分析任务失败" + responseCode);
            }
            // 关闭连接
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            ajaxResult.put("code", 500); // Internal server error
            ajaxResult.put("msg", "服务异常取消分析任务失败" + e.getMessage());
        }
        return ajaxResult;
    }

    /**
     * 新增样本分析job
     */
    @Log(title = "样本分析job", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SampleJob sampleJob)
    {
        return toAjax(sampleJobService.insertSampleJob(sampleJob));
    }

    /**
     * 修改样本分析job
     */
    @Log(title = "样本分析job", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SampleJob sampleJob)
    {
        return toAjax(sampleJobService.updateSampleJob(sampleJob));
    }

    /**
     * 删除样本分析job
     */
    @Log(title = "样本分析job", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sampleJobService.deleteSampleJobByIds(ids));
    }
}
