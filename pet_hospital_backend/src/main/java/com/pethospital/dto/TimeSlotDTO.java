package com.pethospital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSlotDTO {
    private String time;
    private Boolean available;
    private Integer remaining;
}
