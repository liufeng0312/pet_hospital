package com.pethospital.service;

import com.pethospital.dto.BindPhoneDTO;
import com.pethospital.dto.WechatLoginDTO;
import com.pethospital.dto.WechatLoginVO;

/**
 * 微信认证服务接口
 */
public interface WechatAuthService {
    
    /**
     * 微信登录
     * @param dto 登录请求
     * @return 登录结果（包含token或需要绑定手机号的标识）
     */
    WechatLoginVO wechatLogin(WechatLoginDTO dto);
    
    /**
     * 绑定手机号
     * @param dto 绑定请求
     * @return 登录结果（包含token和用户信息）
     */
    WechatLoginVO bindPhone(BindPhoneDTO dto);
}
