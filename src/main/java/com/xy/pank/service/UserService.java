package com.xy.pank.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.pank.entity.PkUserEntity;
import com.xy.pank.untils.R;

public interface UserService extends IService<PkUserEntity> {
    R getOpenId(String code);

    R getpunchCard(String openid, String tag);

    R linkCard(String openid, String tag);

    R rankinglist(String openid);

    R rankingteamlist();

    R insertTeam(String teamname,String openid);

    R addTeam(String openid, String teamtag);

    R startTeam(String teamname);

    R getTeammember(String openid);

    R getmyopenid(String code);

    R getMyti(String openid);

    R getUserDot(String openid);

    R stopGame(String openid);
}
