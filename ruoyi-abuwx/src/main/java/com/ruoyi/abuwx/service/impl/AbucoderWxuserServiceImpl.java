package com.ruoyi.abuwx.service.impl;

import com.ruoyi.abuwx.domain.AbucoderWxuser;
import com.ruoyi.abuwx.mapper.AbucoderWxuserMapper;
import com.ruoyi.abuwx.service.IAbucoderWxuserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 微信用户Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-06-29
 */
@Service
public class AbucoderWxuserServiceImpl implements IAbucoderWxuserService 
{
    @Resource
    private AbucoderWxuserMapper abucoderWxuserMapper;

    /**
     * 查询微信用户
     * 
     * @param id 微信用户主键
     * @return 微信用户
     */
    @Override
    public AbucoderWxuser selectAbucoderWxuserById(Long id)
    {
        return abucoderWxuserMapper.selectAbucoderWxuserById(id);
    }

    /**
     * 查询微信用户列表
     * 
     * @param abucoderWxuser 微信用户
     * @return 微信用户
     */
    @Override
    public List<AbucoderWxuser> selectAbucoderWxuserList(AbucoderWxuser abucoderWxuser)
    {
        return abucoderWxuserMapper.selectAbucoderWxuserList(abucoderWxuser);
    }

    /**
     * 新增微信用户
     * 
     * @param abucoderWxuser 微信用户
     * @return 结果
     */
    @Override
    public int insertAbucoderWxuser(AbucoderWxuser abucoderWxuser)
    {
        return abucoderWxuserMapper.insertAbucoderWxuser(abucoderWxuser);
    }

    /**
     * 修改微信用户
     * 
     * @param abucoderWxuser 微信用户
     * @return 结果
     */
    @Override
    public int updateAbucoderWxuser(AbucoderWxuser abucoderWxuser)
    {
        return abucoderWxuserMapper.updateAbucoderWxuser(abucoderWxuser);
    }

    /**
     * 批量删除微信用户
     * 
     * @param ids 需要删除的微信用户主键
     * @return 结果
     */
    @Override
    public int deleteAbucoderWxuserByIds(Long[] ids)
    {
        return abucoderWxuserMapper.deleteAbucoderWxuserByIds(ids);
    }

    /**
     * 删除微信用户信息
     * 
     * @param id 微信用户主键
     * @return 结果
     */
    @Override
    public int deleteAbucoderWxuserById(Long id)
    {
        return abucoderWxuserMapper.deleteAbucoderWxuserById(id);
    }

    @Override
    public AbucoderWxuser selectWxuserOpenID(String openid) {
        return abucoderWxuserMapper.selectWxuserOpenID(openid);
    }
}
