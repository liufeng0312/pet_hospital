package com.pethospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethospital.entity.Customer;
import com.pethospital.entity.Pet;
import com.pethospital.mapper.PetMapper;
import com.pethospital.service.CustomerService;
import com.pethospital.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetServiceImpl extends ServiceImpl<PetMapper, Pet> implements PetService {
    
    private final PetMapper petMapper;
    private final CustomerService customerService;

    @Override
    public Page<Pet> searchPets(String keyword, Long customerId, String species, int page, int size) {
        LambdaQueryWrapper<Pet> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Pet::getName, keyword);
        }
        
        if (customerId != null) {
            wrapper.eq(Pet::getCustomerId, customerId);
        }
        
        if (StringUtils.hasText(species)) {
            wrapper.eq(Pet::getSpecies, species);
        }
        
        wrapper.orderByDesc(Pet::getCreatedAt);
        
        Page<Pet> result = this.page(new Page<>(page, size), wrapper);
        
        // 填充客户信息
        result.getRecords().forEach(pet -> {
            Customer customer = customerService.getById(pet.getCustomerId());
            pet.setCustomer(customer);
        });
        
        return result;
    }

    @Override
    public List<Pet> getByCustomerId(Long customerId) {
        return this.list(new LambdaQueryWrapper<Pet>().eq(Pet::getCustomerId, customerId));
    }

    @Override
    public Pet getDetail(Long id) {
        return petMapper.selectByIdWithCustomer(id);
    }
}
