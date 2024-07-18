package com.ruoyi.main.util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Data;

@Data
@Component
public class ExtractConfiguration {

    @Value("${linux.saveUrl}")
    private String saveUrl;

    @Value("${ruoyi.profile}")
    private String profile;


}
