package com.pethospital.controller;

import com.pethospital.common.Result;
import com.pethospital.dto.BindPhoneDTO;
import com.pethospital.dto.WechatLoginDTO;
import com.pethospital.dto.WechatLoginVO;
import com.pethospital.service.WechatAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 微信认证Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class WechatAuthController {
    
    private final WechatAuthService wechatAuthService;
    
    /**
     * 微信登录
     */
    @PostMapping("/wechat-login")
    public Result<WechatLoginVO> wechatLogin(@RequestBody WechatLoginDTO dto) {
        log.info("收到微信登录请求，code: {}", dto.getCode());
        try {
            WechatLoginVO vo = wechatAuthService.wechatLogin(dto);
            return Result.success(vo);
        } catch (Exception e) {
            log.error("微信登录失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 绑定手机号
     */
    @PostMapping("/bind-phone")
    public Result<WechatLoginVO> bindPhone(@RequestBody BindPhoneDTO dto) {
        log.info("收到绑定手机号请求，phone: {}", dto.getPhone());
        try {
            WechatLoginVO vo = wechatAuthService.bindPhone(dto);
            return Result.success(vo);
        } catch (Exception e) {
            log.error("绑定手机号失败", e);
            return Result.error(e.getMessage());
        }
    }
}
