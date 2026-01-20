package com.pethospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethospital.entity.Notification;
import com.pethospital.mapper.NotificationMapper;
import com.pethospital.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> 
        implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Override
    public Page<Notification> getMyNotifications(Long customerId, Integer page, Integer size) {
        Page<Notification> pageParam = new Page<>(page, size);
        List<Notification> records = notificationMapper.selectPageByCustomer(pageParam, customerId);
        pageParam.setRecords(records);
        return pageParam;
    }

    @Override
    @Transactional
    public boolean markAsRead(Long id) {
        Notification notification = getById(id);
        if (notification == null) {
            return false;
        }
        notification.setIsRead(1);
        notification.setReadAt(LocalDateTime.now());
        return updateById(notification);
    }

    @Override
    @Transactional
    public boolean markAllAsRead(Long customerId) {
        LambdaUpdateWrapper<Notification> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Notification::getCustomerId, customerId)
               .eq(Notification::getIsRead, 0)
               .set(Notification::getIsRead, 1)
               .set(Notification::getReadAt, LocalDateTime.now());
        return update(wrapper);
    }

    @Override
    public int getUnreadCount(Long customerId) {
        return notificationMapper.countUnread(customerId);
    }

    @Override
    @Transactional
    public Notification createNotification(Notification notification) {
        save(notification);
        return notification;
    }
}
