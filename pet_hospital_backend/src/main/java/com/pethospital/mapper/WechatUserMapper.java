package com.pethospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pethospital.entity.WechatUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 微信用户Mapper接口
 */
@Mapper
public interface WechatUserMapper extends BaseMapper<WechatUser> {
}
