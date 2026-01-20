package com.pethospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pethospital.entity.DoctorSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

@Mapper
public interface DoctorScheduleMapper extends BaseMapper<DoctorSchedule> {
    @Select("SELECT * FROM doctor_schedules WHERE doctor_id = #{doctorId} AND work_date = #{date}")
    DoctorSchedule findByDoctorAndDate(Long doctorId, LocalDate date);
}
