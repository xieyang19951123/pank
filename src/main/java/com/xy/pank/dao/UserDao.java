package com.xy.pank.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.pank.entity.PkUserEntity;
import com.xy.pank.entity.Ranking;
import com.xy.pank.entity.RankingTeam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao extends BaseMapper<PkUserEntity> {

    List<Ranking> selectpangpna();


    List<RankingTeam> rankingteamlist();


    Ranking selectrankingbyid(Integer userid);
}
