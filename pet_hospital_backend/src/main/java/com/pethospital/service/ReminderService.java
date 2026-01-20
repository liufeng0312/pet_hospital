package com.pethospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pethospital.entity.Reminder;
import java.util.List;

public interface ReminderService extends IService<Reminder> {
    
    /**
     * 自动生成提醒记录 (模拟: 扫描近期生日和疫苗)
     */
    void generateReminders();
    
    /**
     * 发送提醒
     */
    void send(Long id);
    
    /**
     * 查询所有提醒
     */
    List<Reminder> listAll();
    
    /**
     * 获取客户的提醒列表（小程序）
     */
    List<Reminder> getMyReminders(Long customerId);
}
