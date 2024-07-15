package com.ruoyi.main.task;

import com.ruoyi.main.domain.Sample;
import com.ruoyi.main.service.ISampleService;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

import javax.annotation.Resource;
import java.util.List;
import java.net.HttpURLConnection;
import java.net.URL;
@Component("testTask")
public class TimedTasks {

    @Resource
    private ISampleService sampleService;


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


}
