package com.xy.pank.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.pank.dao.MonthDao;
import com.xy.pank.entity.Month;
import com.xy.pank.service.MonthService;
import org.springframework.stereotype.Service;

@Service
public class MonthServiceImpl extends ServiceImpl<MonthDao, Month> implements MonthService {
}
