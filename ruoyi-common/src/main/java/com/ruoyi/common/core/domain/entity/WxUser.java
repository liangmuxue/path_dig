package com.ruoyi.common.core.domain.entity;

import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class WxUser extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 微信唯一标识 */
    private String openId;

    private String nickname;

    public WxUser(String openId, String nickname)
    {
        super();
        this.openId = openId;
        this.nickname = nickname;
    }

    public String getOpenId()
    {
        return openId;
    }

    public void setOpenId(String openId)
    {
        this.openId = openId;
    }

    public String getNickname()
    {
        return nickname;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("openId", getOpenId())
                .append("nickname", getNickname())
                .toString();
    }
}
