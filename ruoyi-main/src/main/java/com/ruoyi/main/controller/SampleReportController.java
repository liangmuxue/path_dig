package com.ruoyi.main.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    public AjaxResult stageSend(@RequestBody SampleReport sampleReport)
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

            // 获取响应码
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            ObjectMapper mapper = new ObjectMapper();
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
     * 新增ai诊断分析
     */
    @Log(title = "ai诊断分析", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SampleReport sampleReport)
    {
        sampleReport.setInspectDoctor(getUserId());
        return toAjax(sampleReportService.insertSampleReport(sampleReport));
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
