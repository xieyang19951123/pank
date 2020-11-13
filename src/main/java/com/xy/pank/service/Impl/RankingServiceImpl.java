package com.xy.pank.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.pank.dao.RankingDao;
import com.xy.pank.entity.Ranking;
import com.xy.pank.service.RankingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RankingServiceImpl extends ServiceImpl<RankingDao, Ranking> implements RankingService {


    @Autowired
    private RankingDao rankingDao;
}
