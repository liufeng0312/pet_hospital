package com.pethospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage; // 导入 IPage
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethospital.entity.Drug;
import com.pethospital.entity.Prescription;
import com.pethospital.entity.PrescriptionItem;
import com.pethospital.mapper.PrescriptionItemMapper;
import com.pethospital.mapper.PrescriptionMapper;
import com.pethospital.service.DrugService;
import com.pethospital.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl extends ServiceImpl<PrescriptionMapper, Prescription> 
        implements PrescriptionService {

    private final PrescriptionMapper prescriptionMapper;
    private final PrescriptionItemMapper prescriptionItemMapper;
    private final DrugService drugService;

    @Override
    @Transactional
    public Prescription createPrescription(Prescription prescription) {
        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        if (prescription.getItems() != null && !prescription.getItems().isEmpty()) {
            for (PrescriptionItem item : prescription.getItems()) {
                // 获取药品当前价格
                Drug drug = drugService.getById(item.getDrugId());
                if (drug == null) {
                    throw new RuntimeException("药品不存在: " + item.getDrugId());
                }
                
                // 设置价格快照
                item.setPrice(drug.getPrice());
                
                // 计算小计
                BigDecimal itemTotal = drug.getPrice().multiply(new BigDecimal(item.getQuantity()));
                totalAmount = totalAmount.add(itemTotal);
            }
        }
        
        // 设置总金额和初始状态
        prescription.setTotalAmount(totalAmount);
        if (prescription.getStatus() == null) {
            prescription.setStatus(0); // 未支付
        }
        
        // 保存处方
        save(prescription);
        
        // 保存处方明细
        if (prescription.getItems() != null) {
            for (PrescriptionItem item : prescription.getItems()) {
                item.setPrescriptionId(prescription.getId());
                prescriptionItemMapper.insert(item);
            }
        }
        
        return prescription;
    }

    @Override
    public IPage<Prescription> listPrescriptions(Long medicalRecordId, Integer status, String startDate, String endDate, String searchText, Integer page, Integer size) {
        if (medicalRecordId != null) {
            // 如果是查询特定病历的处方，使用普通查询（通常病历详情页不需要关联太多信息，或者病历本身已知）
            LambdaQueryWrapper<Prescription> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Prescription::getMedicalRecordId, medicalRecordId)
                   .eq(status != null, Prescription::getStatus, status)
                   .orderByDesc(Prescription::getCreatedAt);
            return page(new Page<>(page, size), wrapper);
        } else {
            // 列表页，需要关联宠物和医生信息
            return prescriptionMapper.selectPageWithDetail(new Page<>(page, size), status, startDate, endDate, searchText);
        }
    }

    @Override
    public Prescription getDetail(Long id) {
        return prescriptionMapper.selectDetailById(id);
    }

    @Override
    @Transactional
    public boolean updateStatus(Long id, Integer status) {
        Prescription prescription = getById(id);
        if (prescription == null) {
            throw new RuntimeException("处方不存在");
        }
        prescription.setStatus(status);
        return updateById(prescription);
    }
}
