package com.ruoyi.common.core.domain.entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * com.ss.manaplat.common.web
 *
 * @author JF
 * @create 2024/6/25
 * @email fujinfeng@ss-cas.com
 **/
@Data
@ToString
public class Resource {

    private String resourceId;
    private String appId;
    private String resCode;
    private String resCanme;
    private String resEname;
    private Integer status;
    private String remark;
    private Integer resType;
    private Integer resOrder;
    private String url;
    private String resIco;
    private String parentId;
    private Long createdTime;
    private String createrdUserId;
    private Long updatedTime;
    private String updaterdUserId;
    private Long deletedTime;
    private String deleterdUserId;
    private Integer initFlag;
    private Integer authority = 0;
    private List<String> resourceIds;
    private Integer userType;
    private String resCodes;
    private List<Resource> children;


}
