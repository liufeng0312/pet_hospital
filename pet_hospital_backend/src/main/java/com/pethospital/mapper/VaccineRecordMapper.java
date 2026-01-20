package com.pethospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pethospital.entity.VaccineRecord;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface VaccineRecordMapper extends BaseMapper<VaccineRecord> {
    
    @Select("<script>" +
            "SELECT vr.*, " +
            "p.name as pet_name, p.species as pet_species, " +
            "e.name as doctor_name, " +
            "vi.unit_price as vaccine_price " +
            "FROM vaccine_records vr " +
            "LEFT JOIN pets p ON vr.pet_id = p.id " +
            "LEFT JOIN employees e ON vr.doctor_id = e.id " +
            "LEFT JOIN vaccine_inventory vi ON vr.batch_number = vi.batch_number AND vi.deleted_at IS NULL " +
            "WHERE vr.deleted_at IS NULL " +
            "<if test='petId != null'> AND vr.pet_id = #{petId} </if>" +
            "ORDER BY vr.vaccination_date DESC" +
            "</script>")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "petId", column = "pet_id"),
            @Result(property = "vaccineName", column = "vaccine_name"),
            @Result(property = "vaccineType", column = "vaccine_type"),
            @Result(property = "batchNumber", column = "batch_number"),
            @Result(property = "vaccinationDate", column = "vaccination_date"),
            @Result(property = "nextDueDate", column = "next_due_date"),
            @Result(property = "doctorId", column = "doctor_id"),
            @Result(property = "notes", column = "notes"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "pet.name", column = "pet_name"),
            @Result(property = "pet.species", column = "pet_species"),
            @Result(property = "doctor.name", column = "doctor_name"),
            @Result(property = "price", column = "vaccine_price")
    })
    List<VaccineRecord> selectWithDetails(@Param("petId") Long petId);
    
    /**
     * 获取即将到期的疫苗记录
     */
    @Select("SELECT * FROM vaccine_records " +
            "WHERE deleted_at IS NULL " +
            "AND next_due_date IS NOT NULL " +
            "AND next_due_date BETWEEN #{startDate} AND #{endDate}")
    List<VaccineRecord> getUpcomingVaccines(@Param("startDate") LocalDate startDate, 
                                             @Param("endDate") LocalDate endDate);
}
