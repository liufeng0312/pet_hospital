package com.pethospital.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pethospital.entity.Notification;

public interface NotificationService extends IService<Notification> {
    
    /**
     * 获取通知列表
     */
    Page<Notification> getMyNotifications(Long customerId, Integer page, Integer size);
    
    /**
     * 标记已读
     */
    boolean markAsRead(Long id);
    
    /**
     * 全部标记已读
     */
    boolean markAllAsRead(Long customerId);
    
    /**
     * 获取未读数量
     */
    int getUnreadCount(Long customerId);
    
    /**
     * 创建通知（系统内部调用）
     */
    Notification createNotification(Notification notification);
}
