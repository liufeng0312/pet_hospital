package com.pethospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethospital.entity.Hospitalization;
import com.pethospital.mapper.HospitalizationMapper;
import com.pethospital.service.HospitalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HospitalizationServiceImpl extends ServiceImpl<HospitalizationMapper, Hospitalization> 
        implements HospitalizationService {

    private final HospitalizationMapper hospitalizationMapper;

    // 定义床位号 101-120
    private static final List<String> BED_NUMBERS = new ArrayList<>();
    static {
        for (int i = 1; i <= 20; i++) {
            BED_NUMBERS.add("1" + String.format("%02d", i));
        }
    }

    @Override
    @Transactional
    public void admit(Hospitalization hospitalization) {
        // 检查床位是否被占用
        LambdaQueryWrapper<Hospitalization> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Hospitalization::getBedNumber, hospitalization.getBedNumber())
               .eq(Hospitalization::getStatus, "ACTIVE");
        
        if (count(wrapper) > 0) {
            throw new RuntimeException("该床位已被占用");
        }
        
        // 检查宠物是否已住院
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Hospitalization::getPetId, hospitalization.getPetId())
               .eq(Hospitalization::getStatus, "ACTIVE");
        
        if (count(wrapper) > 0) {
            throw new RuntimeException("该宠物已在住院中");
        }
        
        hospitalization.setStatus("ACTIVE");
        hospitalization.setStartTime(LocalDateTime.now());
        save(hospitalization);
    }

    @Override
    @Transactional
    public void discharge(Long id) {
        Hospitalization hospitalization = getById(id);
        if (hospitalization == null) {
            throw new RuntimeException("住院记录不存在");
        }
        
        hospitalization.setStatus("DISCHARGED");
        hospitalization.setEndTime(LocalDateTime.now());
        updateById(hospitalization);
    }

    @Override
    public List<Map<String, Object>> getBedStatus() {
        // 获取所有活跃住院记录
        List<Hospitalization> activeHospitalizations = hospitalizationMapper.selectActiveWithPet();
        Map<String, Hospitalization> occupiedMap = activeHospitalizations.stream()
                .collect(Collectors.toMap(Hospitalization::getBedNumber, h -> h));
        
        List<Map<String, Object>> bedStatusList = new ArrayList<>();
        
        for (String bedNo : BED_NUMBERS) {
            Map<String, Object> status = new HashMap<>();
            status.put("bedNumber", bedNo);
            
            if (occupiedMap.containsKey(bedNo)) {
                status.put("isOccupied", true);
                status.put("hospitalization", occupiedMap.get(bedNo));
            } else {
                status.put("isOccupied", false);
            }
            bedStatusList.add(status);
        }
        
        return bedStatusList;
    }

    @Override
    public com.baomidou.mybatisplus.core.metadata.IPage<Hospitalization> list(Integer page, Integer size, Long petId, String petName, String status, String startDate, String endDate) {
        return hospitalizationMapper.selectPageWithDetail(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size), petId, petName, status, startDate, endDate);
    }
}
