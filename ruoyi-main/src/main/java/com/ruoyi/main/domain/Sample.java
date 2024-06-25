package com.ruoyi.main.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 样本管理对象 sample
 * 
 * @author ruoyi
 * @date 2024-06-25
 */
@Data
public class Sample
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 病理样本号 */
    @Excel(name = "病理样本号")
    private Long sampleId;

    /** 样本注册时间 */
    @Excel(name = "样本注册时间")
    private String registrationTime;

    /** 录检医生 */
    @Excel(name = "录检医生")
    private String doctor;

    /** 状态报告 0未生成 1已生成 */
    @Excel(name = "状态报告 0未生成 1已生成")
    private Integer state;

    /** 样本图片 */
    @Excel(name = "样本图片")
    private String pic;

    /** 备注 */
    @Excel(name = "备注")
    private String note;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setSampleId(Long sampleId) 
    {
        this.sampleId = sampleId;
    }

    public Long getSampleId() 
    {
        return sampleId;
    }
    public void setRegistrationTime(String registrationTime) 
    {
        this.registrationTime = registrationTime;
    }

    public String getRegistrationTime() 
    {
        return registrationTime;
    }
    public void setDoctor(String doctor) 
    {
        this.doctor = doctor;
    }

    public String getDoctor() 
    {
        return doctor;
    }
    public void setState(Integer state) 
    {
        this.state = state;
    }

    public Integer getState() 
    {
        return state;
    }
    public void setPic(String pic) 
    {
        this.pic = pic;
    }

    public String getPic() 
    {
        return pic;
    }
    public void setNote(String note) 
    {
        this.note = note;
    }

    public String getNote() 
    {
        return note;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("sampleId", getSampleId())
            .append("registrationTime", getRegistrationTime())
            .append("doctor", getDoctor())
            .append("state", getState())
            .append("pic", getPic())
            .append("note", getNote())
            .toString();
    }
}
