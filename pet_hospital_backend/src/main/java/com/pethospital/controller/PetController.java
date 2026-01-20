package com.pethospital.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pethospital.common.Result;
import com.pethospital.entity.Pet;
import com.pethospital.service.PetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "宠物管理")
@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @ApiOperation("分页查询宠物列表")
    @GetMapping
    public Result<Page<Pet>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String species,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(petService.searchPets(keyword, customerId, species, page, size));
    }

    @ApiOperation("获取宠物详情")
    @GetMapping("/{id}")
    public Result<Pet> getById(@PathVariable Long id) {
        return Result.success(petService.getDetail(id));
    }

    @ApiOperation("根据客户ID获取宠物列表")
    @GetMapping("/customer/{customerId}")
    public Result<List<Pet>> getByCustomerId(@PathVariable Long customerId) {
        return Result.success(petService.getByCustomerId(customerId));
    }

    @ApiOperation("获取我的宠物列表")
    @GetMapping("/my-pets")
    public Result<List<Pet>> getMyPets(@RequestAttribute("customerId") Long customerId) {
        return Result.success(petService.getByCustomerId(customerId));
    }

    @ApiOperation("新增宠物（小程序）")
    @PostMapping("/create")
    public Result<Pet> create(
            @RequestAttribute("customerId") Long customerId,
            @RequestBody Pet pet) {
        pet.setCustomerId(customerId);
        petService.save(pet);
        return Result.success(pet);
    }

    @ApiOperation("更新宠物（小程序）")
    @PutMapping("/{id}/update")
    public Result<Pet> updatePet(@PathVariable Long id, @RequestBody Pet pet) {
        pet.setId(id);
        petService.updateById(pet);
        return Result.success(pet);
    }

    @ApiOperation("新增宠物")
    @PostMapping
    public Result<Boolean> save(@RequestBody Pet pet) {
        return Result.success(petService.save(pet));
    }

    @ApiOperation("更新宠物")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Pet pet) {
        pet.setId(id);
        return Result.success(petService.updateById(pet));
    }

    @ApiOperation("删除宠物")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(petService.removeById(id));
    }
}

