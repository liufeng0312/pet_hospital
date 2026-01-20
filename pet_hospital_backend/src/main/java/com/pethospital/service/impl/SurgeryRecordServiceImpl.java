package com.pethospital.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethospital.entity.SurgeryRecord;
import com.pethospital.mapper.SurgeryRecordMapper;
import com.pethospital.service.SurgeryRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

import com.pethospital.entity.Bill;
import com.pethospital.mapper.BillMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pethospital.entity.ServiceItem;
import com.pethospital.mapper.ServiceItemMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class SurgeryRecordServiceImpl extends ServiceImpl<SurgeryRecordMapper, SurgeryRecord> 
        implements SurgeryRecordService {
    
    private final ServiceItemMapper serviceItemMapper;
    private final BillMapper billMapper;

    @Override
    public boolean save(SurgeryRecord entity) {
        if (entity.getAmount() == null && entity.getServiceItemId() != null) {
            ServiceItem serviceItem = serviceItemMapper.selectById(entity.getServiceItemId());
            if (serviceItem != null) {
                entity.setAmount(serviceItem.getPrice());
            }
        }
        return super.save(entity);
    }

    @Override
    public IPage<SurgeryRecord> listWithDetails(Integer page, Integer size, Integer status,
                                                 String petName, String startDate, String endDate) {
        Page<SurgeryRecord> pageParam = new Page<>(page, size);
        return baseMapper.selectPageWithDetails(pageParam, status, petName, startDate, endDate);
    }

    @Override
    public SurgeryRecord getByIdWithDetails(Long id) {
        return baseMapper.selectByIdWithDetails(id);
    }

    @Override
    public List<SurgeryRecord> listByPetId(Long petId) {
        return baseMapper.selectByPetId(petId);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        SurgeryRecord record = new SurgeryRecord();
        record.setId(id);
        record.setStatus(status);
        return updateById(record);
    }

    @Override
    public void syncPaymentStatus() {
        log.info("Starting syncPaymentStatus...");
        // 1. 查询所有已支付的手术账单
        List<Bill> paidSurgeryBills = billMapper.selectList(new LambdaQueryWrapper<Bill>()
                .eq(Bill::getRelatedType, "SURGERY")
                .eq(Bill::getStatus, 1)); // 1: PAID

        log.info("Found {} paid surgery bills", paidSurgeryBills.size());

        if (paidSurgeryBills.isEmpty()) {
            return;
        }

        // 2. 遍历账单，更新对应的手术记录状态
        for (Bill bill : paidSurgeryBills) {
            Long surgeryId = bill.getRelatedId();
            SurgeryRecord surgery = getById(surgeryId);
            
            if (surgery == null) {
                log.warn("Surgery record not found for bill id: {}, surgery id: {}", bill.getId(), surgeryId);
                continue;
            }

            log.info("Checking surgery id: {}, current status: {}", surgeryId, surgery.getStatus());

            // 如果存在且状态为 0 (待支付) 或 null，则更新为 1 (待手术)
            // 注意：处理 status 为 null 的情况
            if (surgery.getStatus() == null || surgery.getStatus() == 0) {
                log.info("Updating surgery id: {} to status 1", surgeryId);
                surgery.setStatus(1);
                updateById(surgery);
            }
        }
        log.info("syncPaymentStatus completed.");
    }
}
