package com.pethospital.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethospital.entity.ServiceItem;
import com.pethospital.mapper.ServiceItemMapper;
import com.pethospital.service.ServiceItemService;
import org.springframework.stereotype.Service;

@Service
public class ServiceItemServiceImpl extends ServiceImpl<ServiceItemMapper, ServiceItem> implements ServiceItemService {
}
