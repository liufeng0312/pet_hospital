package com.pethospital.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pethospital.entity.Registration;

import java.time.LocalDate;
import java.util.List;

public interface RegistrationService extends IService<Registration> {

    /**
     * 创建挂号，自动生成排队号
     */
    Registration createRegistration(Registration registration);

    /**
     * 分页查询挂号列表
     */
    Page<Registration> listRegistrations(String status, LocalDate date, Integer page, Integer size);

    /**
     * 获取排队看板数据
     */
    List<Registration> getQueueBoard();

    /**
     * 叫号 - 将状态改为就诊中
     */
    boolean callNext(Long id);

    /**
     * 完成就诊
     */
    boolean complete(Long id);

    /**
     * 取消挂号
     */
    boolean cancelRegistration(Long id);

    /**
     * 根据客户ID获取预约列表（小程序）
     */
    List<Registration> getByCustomerId(Long customerId);

    /**
     * 获取医生可预约时间段
     */
    List<com.pethospital.dto.TimeSlotDTO> getAvailableSlots(Long doctorId, String date);
}
