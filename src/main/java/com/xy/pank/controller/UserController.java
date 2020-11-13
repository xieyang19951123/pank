package com.xy.pank.controller;

import com.xy.pank.service.UserService;
import com.xy.pank.untils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {


    @Autowired
    private UserService userService;


    //获取用户的openid
    @RequestMapping("/getopenId")
    public R getUserOpenId(String code){
        return userService.getOpenId(code);
    }

    //获取用户的打卡信息
    @RequestMapping("/getpunchCard")
    public R getpunchCard(@RequestParam("openid") String openid , @RequestParam("tag") String tag){
        return userService.getpunchCard(openid,tag);
    }


    //进行打卡
    @RequestMapping("linkCard")
    public R linkCard(@RequestParam("openid") String openid , @RequestParam("tag") String tag){
        return userService.linkCard(openid,tag);
    }

    //排行榜
    @RequestMapping("getRanklist")
    public R rankinglist(String openid){
        return userService.rankinglist(openid);
    }


    //队伍排行榜
    @RequestMapping("rankingteamlist")
    public R rankingteamlist(){
        return userService.rankingteamlist();
    }


    //创建队伍
    @RequestMapping("insertTeam")
    public R insertTeam(@RequestParam("teamname") String teamname,@RequestParam("openid") String openid){
        return userService.insertTeam(teamname,openid);
    }


    //加入队伍
    @RequestMapping("addTeam")
    public R addTeam(String openid,String teamtag){
       return userService.addTeam(openid,teamtag);
    }

    //队伍开始游戏
    @RequestMapping("startTeam")
    public R startTeam(@RequestParam("teamname")String teamname){
        return userService.startTeam(teamname);
    }

    @RequestMapping("getTeammember")
    public R getTeammember(@RequestParam("openid")String openid){
        return  userService.getTeammember(openid);
    }

    @RequestMapping("getmyopenid")
    public R getmyopenid(@RequestParam("code") String code ){
        return userService.getmyopenid(code);
    }

    @RequestMapping("getMyti")
    public R getMyti(@RequestParam("openid")String openid){
        return userService.getMyti(openid);
    }

    //用户查询自己得打卡点

    @RequestMapping(value = "getUserDot",method = RequestMethod.GET)
    public R getUserDot(@RequestParam("openid")String openid){
        return userService.getUserDot(openid);
    }

    //停止游戏
    @RequestMapping(value = "stopGame")
    public R stopGame(@RequestParam("openid")String openid){
        return userService.stopGame(openid);
    }
}
