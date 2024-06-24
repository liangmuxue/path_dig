package com.ruoyi.abuwx.domain;

import com.ruoyi.common.annotation.Excel;
import lombok.Data;

/**
 * 微信用户对象 abucoder_wxuser
 * 
 * @author ruoyi
 * @date 2023-06-29
 */
@Data
public class AbucoderWxuser
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 微信唯一标识符 */
    @Excel(name = "微信唯一标识符")
    private String openid;

    /** 头像 */
    @Excel(name = "头像")
    private String avatar;

    /** 昵称 */
    @Excel(name = "昵称")
    private String nickname;

    /** 性别0：女，1男 */
    @Excel(name = "性别0：女，1男")
    private Integer gender;

    /** 年龄 */
    @Excel(name = "年龄")
    private Long age;

    /** 身高 */
    @Excel(name = "身高")
    private Long height;

    /** 体重 */
    @Excel(name = "体重")
    private Long weight;

    /** 目标 */
    @Excel(name = "目标")
    private String target;

    /** 信誉分 */
    @Excel(name = "信誉分")
    private Long score;
}
