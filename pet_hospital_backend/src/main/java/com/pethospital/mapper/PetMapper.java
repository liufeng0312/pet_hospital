package com.pethospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pethospital.entity.Pet;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PetMapper extends BaseMapper<Pet> {

    @Select("SELECT p.*, COALESCE(wc.nickname, c.name) as customer_name, COALESCE(wc.phone, c.phone) as customer_phone " +
            "FROM pets p " +
            "LEFT JOIN wechat_users wc ON p.customer_id = wc.id " +
            "LEFT JOIN customers c ON p.customer_id = c.id " +
            "WHERE p.id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "customerId", column = "customer_id"),
            @Result(property = "name", column = "name"),
            @Result(property = "species", column = "species"),
            @Result(property = "breed", column = "breed"),
            @Result(property = "gender", column = "gender"),
            @Result(property = "birthDate", column = "birth_date"),
            @Result(property = "weight", column = "weight"),
            @Result(property = "photoUrl", column = "photo_url"),
            @Result(property = "notes", column = "notes"),
            @Result(property = "customer.name", column = "customer_name"),
            @Result(property = "customer.phone", column = "customer_phone")
    })
    Pet selectByIdWithCustomer(@Param("id") Long id);
}
