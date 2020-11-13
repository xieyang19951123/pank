package com.xy.pank.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.pank.dao.DotDao;
import com.xy.pank.entity.Accessory;
import com.xy.pank.entity.Dot;
import com.xy.pank.service.DotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DotServiceImpl  extends ServiceImpl<DotDao, Dot> implements DotService {

    @Autowired
    private DotDao dotDao;

    @Override
    public Dot getDotByTag(String tag) {
        Dot dot = new Dot();
        dot.setDTag(tag);
        Dot dot1 = baseMapper.selectOne(new QueryWrapper<>(dot));
        if(dot1 !=null){
            if(dot1.getAId()!=null){

                String ids = dot1.getAId().replaceAll("\\[", "(").replaceAll("]", ")");
                List<Accessory> accessorys = dotDao.accessorys(ids);
                dot1.setAccessories(accessorys);
            }
        }
        return dot1;
    }
}
