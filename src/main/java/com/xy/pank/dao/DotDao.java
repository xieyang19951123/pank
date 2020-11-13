package com.xy.pank.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.pank.entity.Accessory;
import com.xy.pank.entity.Dot;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DotDao extends BaseMapper<Dot> {
    List<Accessory> accessorys(String ids);

    List<Dot> getUserDot(String openid);

    List<Dot> getUsernot(List<Dot>  list);
}
