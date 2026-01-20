package com.pethospital.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pethospital.entity.Bill;
import java.util.List;

public interface BillService extends IService<Bill> {

    /**
     * 根据处方创建账单
     */
    void createFromPrescription(Long prescriptionId);

    /**
     * 根据检查单创建账单
     */
    void createFromLabTest(Long labTestId);

    /**
     * 根据手术记录创建账单
     */
    void createFromSurgery(Long surgeryId);

    /**
     * 支付账单
     */
    void pay(Long billId, String paymentMethod);

    /**
     * 获取未支付账单
     */
    List<Bill> listUnpaid();

    /**
     * 获取已支付账单
     */
    List<Bill> listPaid();

    /**
     * 获取客户的账单列表（小程序）
     */
    Page<Bill> getMyBills(Long customerId, Integer page, Integer size);
}
