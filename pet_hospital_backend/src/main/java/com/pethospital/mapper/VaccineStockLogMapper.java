package com.pethospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pethospital.entity.VaccineStockLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface VaccineStockLogMapper extends BaseMapper<VaccineStockLog> {
    
    @Select("SELECT vsl.*, " +
            "vi.vaccine_name, vi.batch_number, " +
            "e.name as operator_name " +
            "FROM vaccine_stock_logs vsl " +
            "LEFT JOIN vaccine_inventory vi ON vsl.inventory_id = vi.id " +
            "LEFT JOIN employees e ON vsl.operator_id = e.id " +
            "WHERE vsl.inventory_id = #{inventoryId} " +
            "ORDER BY vsl.created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "inventoryId", column = "inventory_id"),
            @Result(property = "changeType", column = "change_type"),
            @Result(property = "quantity", column = "quantity"),
            @Result(property = "relatedRecordId", column = "related_record_id"),
            @Result(property = "operatorId", column = "operator_id"),
            @Result(property = "reason", column = "reason"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "inventory.vaccineName", column = "vaccine_name"),
            @Result(property = "inventory.batchNumber", column = "batch_number"),
            @Result(property = "operator.name", column = "operator_name")
    })
    List<VaccineStockLog> selectByInventoryId(@Param("inventoryId") Long inventoryId);
}
