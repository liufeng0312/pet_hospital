package com.pethospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pethospital.entity.VaccineInventory;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface VaccineInventoryMapper extends BaseMapper<VaccineInventory> {
    
    /**
     * 获取低库存项目
     */
    @Select("SELECT * FROM vaccine_inventory " +
            "WHERE deleted_at IS NULL " +
            "AND quantity < min_quantity " +
            "ORDER BY quantity ASC")
    List<VaccineInventory> getLowStockItems();
    
    /**
     * 获取即将过期的项目
     */
    @Select("SELECT * FROM vaccine_inventory " +
            "WHERE deleted_at IS NULL " +
            "AND expiry_date IS NOT NULL " +
            "AND expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL #{days} DAY) " +
            "ORDER BY expiry_date ASC")
    List<VaccineInventory> getExpiringItems(@Param("days") int days);
    
    /**
     * 根据批号查询
     */
    @Select("SELECT * FROM vaccine_inventory " +
            "WHERE deleted_at IS NULL " +
            "AND batch_number = #{batchNumber}")
    VaccineInventory findByBatchNumber(@Param("batchNumber") String batchNumber);
}
