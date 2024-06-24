package com.ruoyi.abuwxapi;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.abuwx.domain.AbucoderWxuser;
import com.ruoyi.abuwx.service.IAbucoderWxuserService;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.WxUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.framework.config.ServerConfig;
import com.ruoyi.framework.web.service.TokenService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/wxapi/")
public class WxLoginController {

    /**
     * 测试接口
     * @return
     */
    @GetMapping("test")
    public AjaxResult test(){
        return AjaxResult.success("小程序api调试成功！~");
    }

    @Autowired
    private IAbucoderWxuserService iAbucoderWxuserService;

    @Autowired
    private ServerConfig serverConfig;
    @Autowired
    private TokenService tokenService;

    /**
     * 你自己的微信小程序APPID
     */

    private final static String AppID = "wx9bae9c1f61c278d5";
    /**
     * 你自己的微信APP密钥
     */
    private final static String AppSecret = "c94f8bef8e568262f65d21dc0edeeee5";

    /**
     * 登录时获取的 code（微信官方提供的临时凭证）
     * @param object
     * @return
     */
    @PostMapping("/wxlogin")
    public AjaxResult wxLogin(@RequestBody JSONObject object){
        //微信官方提供的微信小程序登录授权时使用的URL地址
        String url  = "https://api.weixin.qq.com/sns/jscode2session";
        System.out.println(object);

        /**
         * 拼接需要的参数
         * appid = AppID 你自己的微信小程序APPID
         * js_code = AppSecret 你自己的微信APP密钥
         * grant_type=authorization_code = code 微信官方提供的临时凭证
         */
        String params = StrUtil.format("appid={}&secret={}&js_code={}&grant_type=authorization_code", AppID, AppSecret, object.get("code"));
        //开始发起网络请求,若依管理系统自带网络请求工具，直接使用即可
        String res = HttpUtils.sendGet(url,params);
        JSONObject jsonObject = JSON.parseObject(res);
        String openid = (String) jsonObject.get("openid");
        if (StrUtil.isEmpty(openid)) {
            return AjaxResult.error("未获取到openid");
        }
//-------------------------------------------------------------------------------------------------------------------------------------
        AjaxResult ajax = AjaxResult.success();
        /**先通过openid来查询是否存在*/
        AbucoderWxuser wxUserVo = iAbucoderWxuserService.selectWxuserOpenID(openid);
        if (wxUserVo == null){
            /**如果不存在就插入到我们的数据库里*/
            AbucoderWxuser  wxuser = new AbucoderWxuser();
            wxuser.setOpenid(openid);
                // 第一次登录 给一个默认昵称
            String name="QH_"+ RandomUtil.randomStringUpper(7);
            wxuser.setNickname(name);
                // 积分为0
            wxuser.setScore(0l);
                // 给一个默认头像
            wxuser.setAvatar("https://mmbiz.qpic.cn/mmbiz/icTdbqWNOwNRna42FI242Lcia07jQodd2FJGIYQfG0LAJGFxM4FbnQP6yfMxBgJ0F3YRqJCJ1aPAK2dQagdusBZg/0");
            // wx用户表里添加一条用户信息
            iAbucoderWxuserService.insertAbucoderWxuser(wxuser);
            /**返回结果集到前段*/
            // 生成假token
//            LoginUser loginUser = new LoginUser(new WxUser(openid, wxuser.getNickname()));
//            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(openid, wxuser.getNickname());
//            authenticationToken.setDetails(loginUser);
//            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//            String token = tokenService.createToken(loginUser);
//            ajax.put("msg",200);
//            ajax.put(Constants.TOKEN, token);
//            ajax.put("data",iAbucoderWxuserService.selectAbucoderWxuserOpenID(openid));
            ajax.put("msg",200);
            ajax.put("data",wxuser);
            return ajax;
        }else {
            /**返回结果集到前段*/
            // 生成假token
//            LoginUser loginUser = new LoginUser(new WxUser(openid, wxUserVo.getNickname()));
//            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(openid, wxUserVo.getNickname());
//            authenticationToken.setDetails(loginUser);
//            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//            String token = tokenService.createToken(loginUser);
            ajax.put("mes",200);
//            ajax.put(Constants.TOKEN, token);
            ajax.put("data",wxUserVo);
            return ajax;
        }
    }

    @PostMapping("/upload")
    @ResponseBody
    public AjaxResult uploadFile(MultipartFile file) throws Exception
    {
        System.out.println(file);
        try
        {
            // 上传文件路径
            String filePath = RuoYiConfig.getUploadPath();
            System.out.println(filePath);
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = serverConfig.getUrl() + fileName;
            AjaxResult ajax = AjaxResult.success();
            ajax.put("url", url);
            ajax.put("fileName", fileName);
            ajax.put("newFileName", FileUtils.getName(fileName));
            ajax.put("originalFilename", file.getOriginalFilename());
            return ajax;
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 保存昵称与头像信息到用户信息里
     * @param object
     * @return
     */
//    @PostMapping("/saveUserInfo")
//    @ResponseBody
//    public AjaxResult saveUserInfo(@RequestBody JSONObject object){
//        System.out.println(object);
//        WxUserVo wxUserVo = iAbucoderWxuserService.selectAbucoderWxuserOpenID(String.valueOf(object.get("openid")));
//        AbucoderWxuser abucoderWxuser = new AbucoderWxuser();
//        BeanUtils.copyProperties(wxUserVo,abucoderWxuser);
//        if (StringUtils.hasLength(String.valueOf(object.get("nickName")))){
//            abucoderWxuser.setNickname(String.valueOf(object.get("nickName")));
//            abucoderWxuser.setCreateBy(String.valueOf(object.get("nickName")));
//        }
//        if (StringUtils.hasLength(String.valueOf(object.get("avatarUrl")))){
//            abucoderWxuser.setAvatar(String.valueOf(object.get("avatarUrl")));
//        }
//        abucoderWxuser.setUpdateTime(DateUtils.getNowDate());
//        iAbucoderWxuserService.updateAbucoderWxuser(abucoderWxuser);
//        //返回前段需要的数据
//        return AjaxResult.success(abucoderWxuser);
//    }
}
