package com.ruoyi.main.task;

import com.ruoyi.main.domain.Sample;
import com.ruoyi.main.domain.SampleJob;
import com.ruoyi.main.service.ISampleJobService;
import com.ruoyi.main.service.ISampleService;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.io.File;
import javax.annotation.Resource;

@Component("testTask")
public class TimedTasks {

    @Resource
    private ISampleService sampleService;
    @Resource
    private ISampleJobService sampleJobService;

    //每天晚上0点删除ai模块本地上传的svs源文件
    public void delSvs(){
        List<Sample> list =sampleService.selectNotSave();
        list.stream().forEach(a->{
            if(a.getSvs()!=null){
                // 找到profile后面的字符串
                String searchString = "/profile";
                int index = a.getSvs().indexOf(searchString);

                // 找到了profile后的字符串起始位置
                String profileString = a.getSvs().substring(index + searchString.length());
                System.out.println("提取的profile后的字符串为: " + profileString);

                String path = "/home/program/path-dig/file" + profileString;
                File file = new File(path);
                System.out.println("path= " + path);
                if (file.exists()) {
                    if (file.delete()) {
                        System.out.println(a.getSvs()+ "文件删除成功");
                    } else {
                        System.out.println(a.getSvs() + "文件删除失败");
                    }
                } else {
                    System.out.println(a.getSvs()+ "文件不存在");
                }
            }
            sampleService.delSvs(a.getId());
        });
    }

    //检测算法模型是否崩溃
    public void detectionModel() {
        try {
            // 指定URL
            URL url = new URL("http://192.168.0.98:8088/sendHeath");
            // 创建HttpURLConnection对象
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置请求方法为POST
            conn.setRequestMethod("POST");
            // 设置请求头属性
            conn.setRequestProperty("Content-Type", "application/json");
            // 设置允许输出
            conn.setDoOutput(true);
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
                // 设置 AjaxResult 的返回值
            } else { //解析失败 心跳检测
                System.out.println("POST request not worked");
                //改样本工作状态和报告生成状态
                List<SampleJob> list = sampleJobService.selectSampleJobing();
                list.stream().forEach(a->{
                    Sample sample = sampleService.selectSampleById(a.getSamplePid());
                    sample.setWorking(0);
                    sample.setState(0);
                    sampleService.updateSample(sample);
                });
                sampleJobService.updateAllJobing();
            }
            // 关闭连接
            conn.disconnect();
        } catch (Exception e) {
            //改样本工作状态和报告生成状态
            List<SampleJob> list = sampleJobService.selectSampleJobing();
            list.stream().forEach(a->{
                Sample sample = sampleService.selectSampleById(a.getSamplePid());
                sample.setWorking(0);
                sample.setState(0);
                sampleService.updateSample(sample);
            });
            sampleJobService.updateAllJobing();
            System.out.println("检查算法模型崩溃: " + e);
        }
    }


}
