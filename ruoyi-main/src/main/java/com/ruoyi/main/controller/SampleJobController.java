package com.ruoyi.main.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.ruoyi.main.domain.ReportType;
import com.ruoyi.main.domain.Sample;
import com.ruoyi.main.domain.SampleReport;
import com.ruoyi.main.vo.ResultRecipientVo;
import com.ruoyi.main.vo.StageSendVo;
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
import com.ruoyi.main.domain.SampleJob;
import com.ruoyi.main.service.ISampleJobService;
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

    //py->java阶段更新分析任务的状态
    @PostMapping("/stageSend")
    public AjaxResult stageSend(@RequestBody SampleJob sampleJob)
    {
        System.out.println("**************************** sampleJob state ****************************** = " + sampleJob.getState());
        return toAjax(sampleJobService.updateAfterStageSend(sampleJob));
    }

    //给前端当前用户当前执行中任务的样本id
    @PostMapping("/getInProgressJob")
    public AjaxResult getInProgressJob()
    {
        SampleJob sampleJob = new SampleJob();
        sampleJob.setDoctor(getUserId());
        return AjaxResult.success(sampleJobService.getInProgressJob(sampleJob));
    }

    //取消正在分析中的任务
    @PostMapping("/cancelDiagnosis")
    public AjaxResult cancelDiagnosis(@RequestBody SampleJob sampleJob){
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
                }else {
                    return AjaxResult.error("取消分析任务失败");
                }
                // 设置 AjaxResult 的返回值
                ajaxResult.put("code",200);
                ajaxResult.put("msg",stageSendVo);

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
