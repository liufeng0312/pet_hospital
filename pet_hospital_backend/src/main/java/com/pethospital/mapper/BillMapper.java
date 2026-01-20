package com.pethospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pethospital.entity.Bill;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BillMapper extends BaseMapper<Bill> {

    @Select("SELECT b.*, c.name as customer_name, c.level as customer_level " +
            "FROM bills b " +
            "LEFT JOIN customers c ON b.customer_id = c.id " +
            "WHERE b.status = #{status} " +
            "ORDER BY b.created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "customerId", column = "customer_id"),
            @Result(property = "relatedType", column = "related_type"),
            @Result(property = "relatedId", column = "related_id"),
            @Result(property = "totalAmount", column = "total_amount"),
            @Result(property = "discountAmount", column = "discount_amount"),
            @Result(property = "finalAmount", column = "final_amount"),
            @Result(property = "paymentMethod", column = "payment_method"),
            @Result(property = "status", column = "status"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "paidAt", column = "paid_at"),
            @Result(property = "customer.name", column = "customer_name"),
            @Result(property = "customer.level", column = "customer_level")
    })
    List<Bill> selectByStatusWithCustomer(@Param("status") Integer status);
}
