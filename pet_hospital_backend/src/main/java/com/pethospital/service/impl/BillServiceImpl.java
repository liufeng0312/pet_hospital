package com.pethospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethospital.entity.Bill;
import com.pethospital.entity.Customer;
import com.pethospital.entity.LabTest;
import com.pethospital.entity.Prescription;
import com.pethospital.entity.SurgeryRecord;
import com.pethospital.mapper.BillMapper;
import com.pethospital.service.BillService;
import com.pethospital.service.CustomerService;
import com.pethospital.service.LabTestService;
import com.pethospital.service.PrescriptionService;
import com.pethospital.service.SurgeryRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class BillServiceImpl extends ServiceImpl<BillMapper, Bill> implements BillService {

    private final BillMapper billMapper;
    private final PrescriptionService prescriptionService;
    private final CustomerService customerService;
    private final LabTestService labTestService;
    private final SurgeryRecordService surgeryRecordService;

    @Override
    @Transactional
    public void createFromPrescription(Long prescriptionId) {
        Prescription prescription = prescriptionService.getById(prescriptionId);
        if (prescription == null) {
            throw new RuntimeException("处方不存在");
        }
        
        // 检查是否已存在账单
        Long count = lambdaQuery()
                .eq(Bill::getRelatedType, "PRESCRIPTION")
                .eq(Bill::getRelatedId, prescriptionId)
                .count();
        if (count > 0) {
            return; // 已创建，忽略
        }

        // 获取客户ID (需要通过病历 -> 宠物 -> 客户)
        // 这里简化，假设 Prescription -> MedicalRecord -> Pet -> Customer
        // 实际上我们需要查询关联信息。
        // 为了方便，直接再次查询一次详细信息，或者假设 prescriptionService 能够提供
        // 修正：Prescription 实体中关联的 MedicalRecord 可能没加载出来。
        // 这里需要更严谨的获取。
        // 简化：如果 Prescription 只是 ID，我们需要查询 MedicalRecord。
        // 但是 PrescriptionService.getDetail(id) 可以返回详细信息。
        
        Prescription detail = prescriptionService.getDetail(prescriptionId);
        if (detail == null || detail.getMedicalRecord() == null || detail.getMedicalRecord().getPet() == null) {
             throw new RuntimeException("无法获取客户信息");
        }
        Long customerId = detail.getMedicalRecord().getPet().getCustomer().getId();
        
        Bill bill = new Bill();
        bill.setCustomerId(customerId);
        bill.setRelatedType("PRESCRIPTION");
        bill.setRelatedId(prescriptionId);
        bill.setTotalAmount(detail.getTotalAmount());
        bill.setDiscountAmount(BigDecimal.ZERO);
        bill.setFinalAmount(detail.getTotalAmount());
        bill.setStatus(0); // UNPAID
        
        save(bill);
    }

    @Override
    @Transactional
    public void createFromLabTest(Long labTestId) {
        // 检查是否已存在账单
        Long count = lambdaQuery()
                .eq(Bill::getRelatedType, "LAB_TEST")
                .eq(Bill::getRelatedId, labTestId)
                .count();
        if (count > 0) {
            return; // 已创建，忽略
        }

        // 获取检查单详情
        LabTest labTest = labTestService.getByIdWithDetails(labTestId);
        if (labTest == null || labTest.getMedicalRecord() == null || 
            labTest.getMedicalRecord().getPet() == null) {
            throw new RuntimeException("无法获取检查单信息");
        }
        Long customerId = labTest.getMedicalRecord().getPet().getCustomer().getId();
        
        // 获取服务项目价格
        BigDecimal amount = labTest.getServiceItem() != null && labTest.getServiceItem().getPrice() != null
                ? labTest.getServiceItem().getPrice()
                : BigDecimal.ZERO;
        
        Bill bill = new Bill();
        bill.setCustomerId(customerId);
        bill.setRelatedType("LAB_TEST");
        bill.setRelatedId(labTestId);
        bill.setTotalAmount(amount);
        bill.setDiscountAmount(BigDecimal.ZERO);
        bill.setFinalAmount(amount);
        bill.setStatus(0); // UNPAID
        
        save(bill);
    }

    @Override
    @Transactional
    public void createFromSurgery(Long surgeryId) {
        // 检查是否已存在账单
        Long count = lambdaQuery()
                .eq(Bill::getRelatedType, "SURGERY")
                .eq(Bill::getRelatedId, surgeryId)
                .count();
        if (count > 0) {
            return; // 已创建，忽略
        }

        // 获取手术记录详情
        SurgeryRecord surgery = surgeryRecordService.getByIdWithDetails(surgeryId);
        if (surgery == null || surgery.getPet() == null || 
            surgery.getPet().getCustomer() == null) {
            throw new RuntimeException("无法获取手术信息：找不到关联的宠物或客户");
        }
        Long customerId = surgery.getPet().getCustomer().getId();
        
        // 获取手术金额
        BigDecimal amount = surgery.getAmount() != null ? surgery.getAmount() : BigDecimal.ZERO;
        
        Bill bill = new Bill();
        bill.setCustomerId(customerId);
        bill.setRelatedType("SURGERY");
        bill.setRelatedId(surgeryId);
        bill.setTotalAmount(amount);
        bill.setDiscountAmount(BigDecimal.ZERO);
        bill.setFinalAmount(amount);
        bill.setStatus(0); // UNPAID
        
        save(bill);
    }

    @Override
    @Transactional
    public void pay(Long billId, String paymentMethod) {
        Bill bill = getById(billId);
        if (bill == null) {
            throw new RuntimeException("账单不存在");
        }
        if (bill.getStatus() == 1) {
            throw new RuntimeException("账单已支付");
        }
        
        // 检查会员折扣
        Customer customer = customerService.getById(bill.getCustomerId());
        if (customer != null && customer.getLevel() != null && customer.getLevel() == 1) {
            // VIP 9折
            BigDecimal total = bill.getTotalAmount();
            BigDecimal discount = total.multiply(new BigDecimal("0.1"));
            BigDecimal finalAmount = total.subtract(discount);
            
            bill.setDiscountAmount(discount);
            bill.setFinalAmount(finalAmount);
        } else {
            bill.setFinalAmount(bill.getTotalAmount());
        }
        
        bill.setPaymentMethod(paymentMethod);
        bill.setStatus(1); // PAID
        bill.setPaidAt(LocalDateTime.now());
        updateById(bill);
        
        // 更新关联单据状态
        log.info("Bill paid. RelatedType: {}, RelatedId: {}", bill.getRelatedType(), bill.getRelatedId());
        
        if ("PRESCRIPTION".equals(bill.getRelatedType())) {
            prescriptionService.updateStatus(bill.getRelatedId(), 1); // 1: PAID (已支付)
        } else if ("SURGERY".equals(bill.getRelatedType())) {
            log.info("Updating surgery status for id: {}", bill.getRelatedId());
            boolean updated = surgeryRecordService.updateStatus(bill.getRelatedId(), 1); // 1: 已支付待手术
            log.info("Surgery status update result: {}", updated);
        } else {
            log.info("No related document update for type: {}", bill.getRelatedType());
        }
    }

    @Override
    public List<Bill> listUnpaid() {
        return billMapper.selectByStatusWithCustomer(0);
    }

    @Override
    public List<Bill> listPaid() {
        return billMapper.selectByStatusWithCustomer(1);
    }

    @Override
    public Page<Bill> getMyBills(Long customerId, Integer page, Integer size) {
        return lambdaQuery()
                .eq(Bill::getCustomerId, customerId)
                .orderByDesc(Bill::getCreatedAt)
                .page(new Page<>(page, size));
    }
}
