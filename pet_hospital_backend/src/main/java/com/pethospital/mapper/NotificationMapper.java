package com.pethospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pethospital.entity.Notification;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

    /**
     * 分页查询客户的通知列表
     */
    @Select("SELECT * FROM notifications " +
            "WHERE customer_id = #{customerId} " +
            "ORDER BY created_at DESC")
    List<Notification> selectPageByCustomer(Page<Notification> page, @Param("customerId") Long customerId);

    /**
     * 统计未读通知数量
     */
    @Select("SELECT COUNT(*) FROM notifications " +
            "WHERE customer_id = #{customerId} AND is_read = 0")
    int countUnread(@Param("customerId") Long customerId);
}
