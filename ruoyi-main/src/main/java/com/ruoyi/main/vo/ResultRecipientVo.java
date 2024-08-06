package com.ruoyi.main.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ResultRecipientVo {

    private String category;

    private int[] size;

    private Map<String, List<int[]>> boxes;

    private String dizFileUrl;
}
