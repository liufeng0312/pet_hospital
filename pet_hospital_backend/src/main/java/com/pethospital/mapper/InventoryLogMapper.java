package com.pethospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pethospital.entity.InventoryLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface InventoryLogMapper extends BaseMapper<InventoryLog> {

    /**
     * 分页查询库存流水，带药品和操作人信息
     */
    @Select("<script>" +
            "SELECT il.*, " +
            "d.name as drug_name, d.specification as drug_specification, d.unit as drug_unit, " +
            "e.name as operator_name " +
            "FROM inventory_logs il " +
            "LEFT JOIN drugs d ON il.drug_id = d.id " +
            "LEFT JOIN employees e ON il.operator_id = e.id " +
            "WHERE 1=1 " +
            "<if test='drugId != null'> AND il.drug_id = #{drugId} </if>" +
            "<if test='type != null'> AND il.type = #{type} </if>" +
            "ORDER BY il.created_at DESC" +
            "</script>")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "drugId", column = "drug_id"),
            @Result(property = "type", column = "type"),
            @Result(property = "quantity", column = "quantity"),
            @Result(property = "batchNumber", column = "batch_number"),
            @Result(property = "expiryDate", column = "expiry_date"),
            @Result(property = "operatorId", column = "operator_id"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "drug.name", column = "drug_name"),
            @Result(property = "drug.specification", column = "drug_specification"),
            @Result(property = "drug.unit", column = "drug_unit"),
            @Result(property = "operator.name", column = "operator_name")
    })
    List<InventoryLog> selectPageWithDetails(Page<InventoryLog> page,
                                              @Param("drugId") Long drugId,
                                              @Param("type") Integer type);
}
