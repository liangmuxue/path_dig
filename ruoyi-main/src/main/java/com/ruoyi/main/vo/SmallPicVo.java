package com.ruoyi.main.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SmallPicVo {

    private int[] size;

    private Map<String, List<int[]>> boxes;

    private Map<String, Integer> level;

    private Map<String, List<List<List<Double>>>> box_point_vertices;
}
