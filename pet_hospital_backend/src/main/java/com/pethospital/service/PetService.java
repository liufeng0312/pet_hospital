package com.pethospital.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pethospital.entity.Pet;

import java.util.List;

public interface PetService extends IService<Pet> {
    Page<Pet> searchPets(String keyword, Long customerId, String species, int page, int size);
    List<Pet> getByCustomerId(Long customerId);

    Pet getDetail(Long id);
}
