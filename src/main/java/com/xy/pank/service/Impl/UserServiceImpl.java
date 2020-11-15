package com.xy.pank.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.sym.NameN;
import com.xy.pank.dao.*;
import com.xy.pank.entity.*;
import com.xy.pank.entity.vo.ShowEntity;
import com.xy.pank.idworker.Sid;
import com.xy.pank.service.MonthService;
import com.xy.pank.service.UserService;
import com.xy.pank.untils.HttpGetAndPost;
import com.xy.pank.untils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.OpNE;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.zip.DeflaterOutputStream;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, PkUserEntity> implements UserService {
    @Autowired
    private Sid sid;
    @Autowired
    private UserDao userDao;

    @Autowired
    private HttpGetAndPost httpGetAndPost;


    @Autowired
    private RankingDao rankingDao;

    @Autowired
    private DotDao dotDao;

    @Autowired
    private RankingTeamDao rankingTeamDao;

    @Autowired
    private PunchCardRecordDao punchCardRecordDao;

    long nd = 1000 * 24 * 60 * 60;

    long nh = 1000 * 60 * 60;

    long nm = 1000 * 60;

    long ns = 1000;

    @Autowired
    private MonthDao monthDao;

    @Override
    public R getOpenId(String code) {
        System.out.println(code);
        if (StringUtils.isEmpty(code)) {
            return R.error(10001,"校验code为空");
        }
        //获取openid
        Map<String, String> openId = httpGetAndPost.getUser(code);
        //Map<String, String> openId1 = httpGetAndPost.getOpenId(code);
        if(openId !=null){
            //判断用户是否存在
            String id = openId.get("openid");
            if(StringUtils.isEmpty(id)){
                return R.error(openId.get("errmsg"));
            }
            PkUserEntity user = new PkUserEntity();
            user.setOpenid(id);
            user.setShowStatus(1);
            PkUserEntity pkUserEntity1 = userDao.selectOne(new QueryWrapper<>(user));
            if(pkUserEntity1 !=null ){//用户存在
                if( pkUserEntity1.getInthegame()==1){
                    return R.error(300,"正在游戏中").put("page",pkUserEntity1);
                }
                return R.error(301,"没在游戏").put("page",pkUserEntity1);
            }
            //用户不存在
            PkUserEntity pkUserEntity = new PkUserEntity();
            pkUserEntity.setNickname(openId.get("nickname"));
            pkUserEntity.setOpenid(openId.get("openid"));
            pkUserEntity.setHeadimgurl(openId.get("headimgurl"));
            pkUserEntity.setShowStatus(1);
            pkUserEntity.setInthegame(0);
            pkUserEntity.setCreateTime(new Date());
            //添加统计信息
            Calendar instance = Calendar.getInstance();
            int monthint = instance.get(Calendar.MONTH) + 1;
            int yearint = instance.get(Calendar.YEAR);
            int weekint = instance.get(Calendar.WEEK_OF_MONTH);
            Month month = new Month();
            month.setYears(yearint);
            month.setMonth(monthint);
            month.setWeek(weekint);
            month.setShowStatus(1);
            Month month1 = monthDao.selectOne(new QueryWrapper<>(month));
            if(month1 == null){
                month.setPnumber(1);
                monthDao.insert(month);
            }else{
                month1.setPnumber(month1.getPnumber()+1);
                monthDao.updateById(month1);
            }
            userDao.insert(pkUserEntity);
            return R.ok("正常情况下").put("page",pkUserEntity);
        }

        return R.error("获取用户信息失败");
    }

    @Override
    public R getpunchCard(String openid, String tag) {
        //Map<String, String> params = httpGetAndPost.getOpenId(code);//获取openid


            PkUserEntity user = new PkUserEntity();
            user.setOpenid(openid);
            user.setShowStatus(1);
            PkUserEntity pkUserEntity1 = userDao.selectOne(new QueryWrapper<>(user));
            if(pkUserEntity1 !=null ){//用户存在
                if( pkUserEntity1.getInthegame()==1){
                    //return R.error(300,"正在游戏中");
                    //查询当前的信息
                    Ranking ranking = new Ranking();
                    ranking.setUserid(pkUserEntity1.getId());
                    ranking.setComplete(0);
                    //当前用户参加游戏的信息
                    Ranking ranking1 = rankingDao.selectOne(new QueryWrapper<>(ranking));
                    if(ranking1 == null){
                        //查询打卡情况
                        Integer notcount = dotDao.selectCount(null);
                        ShowEntity showEntity = new ShowEntity();
                        showEntity.setMintue("");
                        showEntity.setMintuegongl(null);
                        showEntity.setNotcount(notcount);
                        showEntity.setYetcount(0);
                        return R.ok().put("page",showEntity);
                    }
                    if(ranking1.getOrigin() ==null){
                        ranking1.setOrigin(tag);
                        //rankingDao.updateById(ranking1);
                    }
                    ShowEntity getshowentity = getshowentity(ranking1, tag);
                    return R.ok().put("page",getshowentity);
                }

            }


        //查询打卡情况
        Integer notcount = dotDao.selectCount(null);
        ShowEntity showEntity = new ShowEntity();
        showEntity.setMintue("");
        showEntity.setMintuegongl(null);
        showEntity.setNotcount(notcount);
        showEntity.setYetcount(0);
        return R.ok().put("page",showEntity);
    }

    @Override
    public R linkCard(String openid, String tag) {

           // String openid = params.get("openid");
            PkUserEntity user = new PkUserEntity();
            user.setOpenid(openid);
            user.setShowStatus(1);
            PkUserEntity pkUserEntity1 = userDao.selectOne(new QueryWrapper<>(user));
            //进行打卡
           pkUserEntity1.setInthegame(1);
           userDao.updateById(pkUserEntity1);//开始游戏
                //查询当前的信息
            Ranking ranking = new Ranking();
            ranking.setUserid(pkUserEntity1.getId());
            ranking.setComplete(0);
            //当前用户参加游戏的信息
            Ranking ranking1 = rankingDao.selectOne(new QueryWrapper<>(ranking));
            if(ranking1 == null){//第一次打卡
                log.info("第一次打卡");
                ranking.setCreateTime(new Date());
                ranking.setOrigin(tag);//设置起点
                ranking.setTeamId(0);
                //ranking.set
                rankingDao.insert(ranking);
                //ranking1.setId(ranking.getId());
                ranking1 = rankingDao.selectById(ranking.getId());
                System.out.println(ranking1);
            }
                //插入打卡记录
            PunchCardRecord punchCardRecord = new PunchCardRecord();
            punchCardRecord.setRankingid(ranking1.getId());
            punchCardRecord.setDotTag(tag);
            PunchCardRecord punchCardRecord1 = punchCardRecordDao.selectOne(new QueryWrapper<>(punchCardRecord));
            if(punchCardRecord1 != null){
                return R.error(305,"该点已经打过卡");
            }
            punchCardRecord.setOpenid(openid);
            punchCardRecord.setCreateTime(new Date());
            punchCardRecordDao.insert(punchCardRecord);
            //查询打卡记录
            PunchCardRecord yetpunchcard = new PunchCardRecord();
            if(ranking1.getStartTime() ==null){
                ranking1.setStartTime(new Date());
                rankingDao.updateById(ranking1);
            }
        if(ranking1.getOrigin() ==null){
            ranking1.setOrigin(tag);
            rankingDao.updateById(ranking1);
        }

            yetpunchcard.setRankingid(ranking1.getId());
            Integer yetcount = punchCardRecordDao.selectCount(new QueryWrapper<>(yetpunchcard));
            Integer allcount = dotDao.selectCount(null);
            if(allcount == yetcount){
                log.info("完成所有的打卡");
                ranking1.setComplete(1);
                ranking1.setEndTime(new Date());
                ranking1.setTerminus(tag);
                ranking1.setUseTime((int)(new Date().getTime()-ranking1.getStartTime().getTime()));
                rankingDao.updateById(ranking1);//完成所有的打卡

                if(ranking1.getTeamId() != 0){//查询是否是对内最后一个完成的
                    Ranking rankingteam = new Ranking();
                    rankingteam.setComplete(0);//未完成
                    rankingteam.setTeamId(ranking1.getTeamId());
                    List<Ranking> count = rankingDao.selectList(new QueryWrapper<>(rankingteam));
                    AtomicLong overall = new AtomicLong();
                    if(count.size() == 0){
                        rankingteam.setComplete(1);
                        count = rankingDao.selectList(new QueryWrapper<>(rankingteam));
                        List<ShowEntity> showEntities = new ArrayList<>();
                        count.stream().forEach(item->{
                            ShowEntity showEntity = getshowentity(item,item.getTerminus());
                            pkUserEntity1.setInthegame(0);
                            userDao.updateById(pkUserEntity1);
                            overall.addAndGet(showEntity.getSecond());
                           // item.setShowEntity(showEntity);
                            showEntities.add(showEntity);
                        });
                        long i =  overall.get()/count.size();
                        RankingTeam rankingTeam = this.rankingTeamDao.selectById(ranking1.getTeamId());
                        rankingTeam.setComplete(3);
                        rankingTeam.setShowEntities(showEntities);
                        rankingTeam.setAverageTime(Integer.valueOf((int) i));
                        rankingTeam.setAverageTimes((i% nd/nh>=10? i% nd/nh : "0"+i% nd/nh)+":"+(i% nd % nh / nm>=10?i% nd % nh / nm:"0"+i% nd % nh / nm)+":"+(i% nd % nh % nm / ns>=10?i% nd % nh % nm / ns:"0"+i% nd % nh % nm / ns));
                        rankingTeamDao.updateById(rankingTeam);
                        List<RankingTeam> rankingteamlist = userDao.rankingteamlist();
                        String mingci = null;
                        for (int j = 0; j < rankingteamlist.size(); j++) {
                            if(rankingteamlist.get(j).getId() ==rankingTeam.getId() ){
                                mingci = (rankingteamlist.size()-(j+1))+"/"+rankingteamlist.size();
                            }
                        }
                        return R.error(203,"队伍完成打卡").put("page",rankingTeam).put("mingci",mingci);
                    }
                    //
                    return R.error(202,"完成了打卡但不是最后一个").put("page",getshowentity(ranking1, tag));
                }
                //个人完成所有的打卡
                ShowEntity getshowentity = getshowentity(ranking1, tag);
                List<Ranking> selectpangpna = userDao.selectpangpna();

                for (int i = 0; i < selectpangpna.size(); i++) {
                    if(selectpangpna.get(i).getUserid() == ranking1.getUserid()){
                        BigDecimal defeat  = new BigDecimal(i+1).divide(new BigDecimal(selectpangpna.size()),2,BigDecimal.ROUND_HALF_UP);
                        getshowentity.setDefeat(defeat);
                        getshowentity.setMingci((i+1)+"");
                        break;
                    }
                }
                pkUserEntity1.setInthegame(0);
                userDao.updateById(pkUserEntity1);
                return R.error(201,"个人完成完成打卡").put("page",getshowentity);
            }
            return R.error(200,"完成打卡").put("page",getshowentity(ranking1, tag));

       // return R.error("获取用户下信息失败");
    }

    @Override
    public R rankinglist(String openid) {
        //Map<String, String> openId = httpGetAndPost.getOpenId(code);

        PkUserEntity pkUserEntity = new PkUserEntity();
        pkUserEntity.setOpenid(openid);
        PkUserEntity pkUserEntity1 = userDao.selectOne(new QueryWrapper<>(pkUserEntity));
        List<Ranking> selectpangpna = userDao.selectpangpna();
        ArrayList<ShowEntity> showEntities = new ArrayList<>();
        String mingci = null;

        //List<Ranking> collect1 = selectpangpna.stream().sorted(Comparator.comparing(Ranking::getUseTime)).collect(Collectors.toList());
        for (int i = 0; i <selectpangpna.size() ; i++) {
            List<Ranking> ranking = rankingDao.selectList(new QueryWrapper<>(selectpangpna.get(i)));
            Optional<Ranking> rs = ranking.stream().filter(item -> item.getUseTime() != null).distinct().min((e1, e2) -> e1.getUseTime().compareTo(e2.getUseTime()));
            if(!rs.isPresent()){
                continue;
            }
            Ranking ranking1 = rs.get();
            ShowEntity getshowentity = getshowentity(ranking1, ranking1.getTerminus());
            showEntities.add(getshowentity);
        }
        List<ShowEntity> collect = showEntities.stream().sorted(Comparator.comparing(ShowEntity::getSecond)).collect(Collectors.toList());
        for (int i = 0; i <collect.size() ; i++) {
            if(collect.get(i).getUid() == pkUserEntity1.getId()){
                mingci = (i+1) +"/"+collect.size();
            }
        }

        return R.ok().put("page",collect).put("mingci",mingci);
    }

    @Override
    public R rankingteamlist() {
        List<RankingTeam> rankingteamlist = userDao.rankingteamlist();
        List<RankingTeam> collect = rankingteamlist.stream().map(item -> {
            Ranking ranking = new Ranking();
            ranking.setTeamId(item.getId());
            List<Ranking> ranking1 = rankingDao.selectList(new QueryWrapper<>(ranking));
            List<ShowEntity> showEntities = new ArrayList<>();
            ranking1.forEach(mytem -> {
                showEntities.add(getshowentity(mytem, mytem.getTerminus()));
            });
            item.setShowEntities(showEntities);
            item.setAverageTimes((item.getAverageTime()% nd/nh>=10? item.getAverageTime()% nd/nh : "0"+item.getAverageTime()% nd/nh)+":"+(item.getAverageTime()% nd % nh / nm>=10?item.getAverageTime()% nd % nh / nm:"0"+item.getAverageTime()% nd % nh / nm)+":"+(item.getAverageTime()% nd % nh % nm / ns>=10?item.getAverageTime()% nd % nh % nm / ns:"0"+item.getAverageTime()% nd % nh % nm / ns));
            return item;
        }).sorted(Comparator.comparing(RankingTeam::getAverageTime)).collect(Collectors.toList());
        return R.ok().put("page",collect);
    }

    @Override
    public R insertTeam(String teamname,String openid) {
        PkUserEntity pkUserEntity = new PkUserEntity();
        pkUserEntity.setOpenid(openid);
        PkUserEntity pkUserEntity1 = userDao.selectOne(new QueryWrapper<>(pkUserEntity));
        RankingTeam rankingTeam = new RankingTeam();
        rankingTeam.setTeamName(teamname);
        RankingTeam rankingTeam1 = rankingTeamDao.selectOne(new QueryWrapper<>(rankingTeam));
        if(rankingTeam1!=null){
                return R.error(300,"队伍已经存在,请重新输入队伍名");
        }
        rankingTeam.setComplete(0);
        rankingTeam.setTeamTag(sid.nextShort());
        Ranking ranking = new Ranking();
        ranking.setComplete(0);
        ranking.setUserid(pkUserEntity1.getId());
        ranking.setCreateTime(new Date());
        rankingTeamDao.insert(rankingTeam);
        ranking.setTeamId(rankingTeam.getId());
        rankingDao.insert(ranking);
        return R.ok().put("page",rankingTeam);
    }

    @Override
    public R addTeam(String openid, String teamtag) {
        RankingTeam rankingTeam = new RankingTeam();
        rankingTeam.setTeamTag(teamtag);
        RankingTeam rankingTeam1 = rankingTeamDao.selectOne(new QueryWrapper<>(rankingTeam));
        if(rankingTeam1==null){
            return R.error(300,"队伍不存在");
        }
        if(rankingTeam1.getComplete()==1){
            return R.error(302,"队伍已经开始游戏");
        }
        PkUserEntity pkUserEntity = new PkUserEntity();
        pkUserEntity.setOpenid(openid);
        PkUserEntity pkUserEntity1 = userDao.selectOne(new QueryWrapper<>(pkUserEntity));
        Ranking ranking = new Ranking();
        ranking.setTeamId(rankingTeam1.getId());
        ranking.setComplete(0);
        ranking.setCreateTime(new Date());
        ranking.setUserid(pkUserEntity1.getId());
        rankingDao.insert(ranking);
        return R.ok("加入成功");
    }

    @Override
    public R startTeam(String teamtag) {

        RankingTeam rankingTeam = new RankingTeam();
        rankingTeam.setTeamTag(teamtag);
        RankingTeam rankingTeam1 = rankingTeamDao.selectOne(new QueryWrapper<>(rankingTeam));
        rankingTeam1.setComplete(1);
        rankingTeamDao.updateById(rankingTeam1);
        Ranking ranking = new Ranking();
        ranking.setTeamId(rankingTeam1.getId());
        List<Ranking> rankings = rankingDao.selectList(new QueryWrapper<>(ranking));
        rankings.forEach(item->{
            item.setComplete(0);
            item.setStartTime(new Date());
            rankingDao.updateById(ranking);
            PkUserEntity pkUserEntity = new PkUserEntity();
            pkUserEntity.setId(item.getUserid());
            pkUserEntity.setInthegame(1);
            userDao.updateById(pkUserEntity);
        });
        return R.ok();
    }

    @Override
    public R getTeammember(String openid) {
        //Map<String, String> openId = httpGetAndPost.getOpenId(code);
        //获取openid
        //PkUserEntity pkUserEntity2 = new PkUserEntity();

        if(openid == null){
            return R.error("参数为空");
        }
        PkUserEntity pkUserEntity1 = new PkUserEntity();

        pkUserEntity1.setOpenid(openid);
        PkUserEntity pkUserEntity2 = userDao.selectOne(new QueryWrapper<>(pkUserEntity1));
        if(pkUserEntity2 == null){
            return R.error(300,"用户不存在");
        }


//        RankingTeam rankingTeam = new RankingTeam();
//        rankingTeam.setTeamTag(code);
//        RankingTeam rankingTeam1 = rankingTeamDao.selectOne(new QueryWrapper<>(rankingTeam));
        Ranking ranking = new Ranking();
        ranking.setUserid(pkUserEntity2.getId());
        ranking.setComplete(0);
        Ranking rankings = rankingDao.selectOne(new QueryWrapper<>(ranking));
        if(rankings == null){
            return R.error(300,"用户不存在队伍");
        }
        if(rankings.getTeamId() == 0){
            return R.error(300,"用户不存在队伍");
        }
        RankingTeam rankingTeam = rankingTeamDao.selectById(rankings.getTeamId());
        if(rankingTeam == null){

        }
        Ranking ranking1 = new Ranking();
        ranking1.setTeamId(rankingTeam.getId());
        List<Ranking> ranking2 = rankingDao.selectList(new QueryWrapper<>(ranking1));
        List<ShowEntity> showEntities = new ArrayList<>();
        ranking2.forEach(item->{
            PkUserEntity pkUserEntity = userDao.selectById(item.getUserid());
            ShowEntity showEntity = new ShowEntity();
            showEntity.setNickName(pkUserEntity.getNickname());
            showEntity.setHeadimg(pkUserEntity.getHeadimgurl());
            showEntities.add(showEntity);
        });
        rankingTeam.setShowEntities(showEntities);

        return R.ok().put("page",rankingTeam);
    }

    @Override
    public R getmyopenid(String code) {
        Map<String, String> openId = httpGetAndPost.getOpenId(code);
        if(openId.get("errcode") !=null){
            return R.error("获取openId使用");
        }
        return R.ok().put("opendid",openId.get("openid"));
    }

    @Override
    public R getMyti(String openid) {
        PkUserEntity pkUserEntity = new PkUserEntity();
        pkUserEntity.setOpenid(openid);
        pkUserEntity.setInthegame(0);
        PkUserEntity pkUserEntity1 = userDao.selectOne(new QueryWrapper<>(pkUserEntity));
        if(pkUserEntity1 == null){
            return R.error("获取用户失败");
        }
        Ranking selectrankingbyid = userDao.selectrankingbyid(pkUserEntity1.getId());
        ShowEntity getshowentity = getshowentity(selectrankingbyid, selectrankingbyid.getTerminus());
        List<Ranking> selectpangpna = userDao.selectpangpna();
        String mingci = null;
        for (int i = 0; i <selectpangpna.size() ; i++) {
            if(pkUserEntity1.getId() == selectpangpna.get(i).getUserid()){
                mingci = (i+1)+"/"+selectpangpna.size();
                getshowentity.setDefeat(new BigDecimal(selectpangpna.size()).subtract(new BigDecimal(i+1)).divide(new BigDecimal(selectpangpna.size()),2,BigDecimal.ROUND_HALF_UP));

            }
        }
        PunchCardRecord punchCardRecord =  new PunchCardRecord();
        punchCardRecord.setRankingid(selectrankingbyid.getId());
        List<PunchCardRecord> punchCardRecords = punchCardRecordDao.selectList(new QueryWrapper<>(punchCardRecord));
        double d = 0;
        for (int i = 0; i <punchCardRecords.size() ; i++) {
            if((punchCardRecords.size()>(i+1))){
              d+=computed(punchCardRecords.get(i).getDotTag(),punchCardRecords.get(i+1).getDotTag());
            }
        }
        getshowentity.setGongli(d);
        return R.ok().put("page",getshowentity).put("mingci",mingci);
    }

    @Override
    public R getUserDot(String openid) {
        List<Dot> userDot = dotDao.getUserDot(openid);
        List<Dot> count = dotDao.selectList(null);
        if(count.size() == userDot.size()){
            return R.ok().put("in",userDot).put("not",0);
        }
        if(userDot.size() == 0){
            //List<Dot> dots = dotDao.selectList(null);
            return R.ok().put("in",0).put("not",count);
        }
        List<Dot> usernot = dotDao.getUsernot(userDot);

        return R.ok().put("in",userDot).put("not",usernot);
    }

    @Override
    public R stopGame(String openid) {
        PkUserEntity pkUserEntity = new PkUserEntity();
        pkUserEntity.setOpenid(openid);
        PkUserEntity pkUserEntity1 = userDao.selectOne(new QueryWrapper<>(pkUserEntity));
        Ranking ranking = new Ranking();
        ranking.setComplete(0);
        ranking.setUserid(pkUserEntity1.getId());
        List<Ranking> rankings = rankingDao.selectList(new QueryWrapper<>(ranking));
        if(rankings.size()==0){
            return R.error(300,"用户不存在");
        }
        if(rankings.size()>1){
            return R.error(301,"用户异常");
        }
        if(rankings.get(0).getTeamId()!=0){
            return R.error(301,"该用户存在队伍");
        }
        rankings.get(0).setComplete(1);
        rankings.get(0).setEndTime(new Date());
        rankingDao.updateById(rankings.get(0));
        pkUserEntity1.setInthegame(0);
        userDao.updateById(pkUserEntity1);
        return R.ok("退出游戏");
    }


    //计算两点的距离
    public  double getDistanceMeter(GlobalCoordinates gpsFrom, GlobalCoordinates gpsTo, Ellipsoid ellipsoid)
    {
        //创建GeodeticCalculator，调用计算方法，传入坐标系、经纬度用于计算距离
        GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(ellipsoid, gpsFrom, gpsTo);

        return geoCurve.getEllipsoidalDistance();
    }


    public ShowEntity getshowentity(Ranking ranking1,String tag){
        Dot current = new Dot();
        current.setDTag(tag);

        //当前点位的信息
        Dot currentDot = dotDao.selectOne(new QueryWrapper<>(current));
        System.out.println(currentDot);
        String[] currentlongitudes = currentDot.getLongitude().split(",");
        double  currentlongitude= Double.parseDouble(currentlongitudes[0]);//经度
        double  currentimensionality= Double.parseDouble(currentlongitudes[1]);//维度
        GlobalCoordinates startglobalCoordinates = new GlobalCoordinates( currentimensionality,currentlongitude);

        //起始点位
        Dot start = new Dot();
        start.setDTag(ranking1.getOrigin());

        Dot startDot = dotDao.selectOne(new QueryWrapper<>(start));

        String[] longitudes = startDot.getLongitude().split(",");
        double  startlongitude= Double.parseDouble(longitudes[0]);//经度
        double  stardimensionality= Double.parseDouble(longitudes[1]);//维度
        GlobalCoordinates currentglobalCoordinates = new GlobalCoordinates(stardimensionality, startlongitude);


        double meter1 = getDistanceMeter(startglobalCoordinates, currentglobalCoordinates, Ellipsoid.Sphere)/1000;//距离
        //当前时间
        Calendar currentTime = Calendar.getInstance();
        currentTime.setTime(new Date());
        //秒钟差
        long sec = 0;
        if(ranking1.getStartTime() == null){
            sec =0;
        }else{
            if(ranking1.getComplete() == 1){
                sec = ranking1.getEndTime().getTime()- ranking1.getStartTime().getTime();
            }else {

                sec = (new Date().getTime() - ranking1.getStartTime().getTime());
            }
        }

        double mun =sec / nm;//分钟

        //计算卡路里//
        Random rand = new Random();
        //double d = rand.nextInt(3)+3.5;//随机生成卡路里的
        //生成消耗的卡路里
        double v = mun * 3.5;
        //查询打卡情况
        PunchCardRecord punchCardRecord = new PunchCardRecord();
        punchCardRecord.setRankingid(ranking1.getId());
        Integer yetcount = punchCardRecordDao.selectCount(new QueryWrapper<>(punchCardRecord));//已打卡的数量
        Integer notcount = dotDao.selectCount(null)-yetcount;
        PkUserEntity pkUserEntity = userDao.selectById(ranking1.getUserid());
        ShowEntity showEntity = new ShowEntity();
        showEntity.setMintue((sec% nd/nh>=10? sec% nd/nh : "0"+sec% nd/nh)+":"+(sec% nd % nh / nm>=10?sec% nd % nh / nm:"0"+sec% nd % nh / nm)+":"+(sec% nd % nh % nm / ns>=10?sec% nd % nh % nm / ns:"0"+sec% nd % nh % nm / ns));

        punchCardRecord.setRankingid(ranking1.getId());
        List<PunchCardRecord> punchCardRecords = punchCardRecordDao.selectList(new QueryWrapper<>(punchCardRecord));
        double d = 0;
        for (int i = 0; i <punchCardRecords.size() ; i++) {
            if((punchCardRecords.size()>(i+1))){
                d+=computed(punchCardRecords.get(i).getDotTag(),punchCardRecords.get(i+1).getDotTag());
            }
        }
        System.out.println(mun);
        System.out.println(meter1);
        int musm =(int) Math.ceil(sec/d);  // 分钟/公里
        String metu = (musm / nm>=10?musm  / nm:"0"+musm/ nm)+"‘"+(musm% nd % nh % nm / ns>=10?musm% nd % nh % nm / ns:"0"+musm% nd % nh % nm / ns)+"“";
        if(!ranking1.getOrigin().equals(tag)){
            showEntity.setMintuegongl(metu);
        }
        showEntity.setKaluli(v);
        showEntity.setNotcount(notcount);
        showEntity.setYetcount(yetcount);
        showEntity.setSecond(sec);
        showEntity.setHeadimg(pkUserEntity.getHeadimgurl());
        showEntity.setNickName(pkUserEntity.getNickname());
        showEntity.setGongli(d);
        showEntity.setUid(ranking1.getUserid());
        return  showEntity;
    }

    /*计算两个点的距离*/
    private double computed(String tag1, String tag2){
        System.out.println(tag1+"========="+tag2);
        Dot getdot = getdot(tag1);
        Dot getdot1 = getdot(tag2);
        String[] currentlongitudes = getdot.getLongitude().split(",");
        double  currentlongitude= Double.parseDouble(currentlongitudes[0]);//经度
        double  currentimensionality= Double.parseDouble(currentlongitudes[1]);//维度
        GlobalCoordinates startglobalCoordinates = new GlobalCoordinates( currentimensionality,currentlongitude);
        String[] longitudes = getdot1.getLongitude().split(",");
        double  startlongitude= Double.parseDouble(longitudes[0]);//经度
        double  stardimensionality= Double.parseDouble(longitudes[1]);//维度
        GlobalCoordinates currentglobalCoordinates = new GlobalCoordinates(stardimensionality, startlongitude);

        double meter1 = getDistanceMeter(startglobalCoordinates, currentglobalCoordinates, Ellipsoid.Sphere)/1000;//距离
        System.out.println(meter1);
        return meter1;

    }




    private Dot getdot(String tag){

        Dot dot = new Dot();
        dot.setDTag(tag);
        Dot dot1 = dotDao.selectOne(new QueryWrapper<>(dot));

        return dot1;
    }

    public static void main(String[] args) throws Exception{

        Calendar instance = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = simpleDateFormat.parse("2011-10-16");
        instance.setTime(parse);
        int i = instance.get(Calendar.MONTH)+1;
        int i1 = instance.get(Calendar.YEAR);
        System.out.println(instance.get(Calendar.WEEK_OF_MONTH));
    }

    public  static  double getDistanceMeter1(GlobalCoordinates gpsFrom, GlobalCoordinates gpsTo, Ellipsoid ellipsoid)
    {
        //创建GeodeticCalculator，调用计算方法，传入坐标系、经纬度用于计算距离
        GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(ellipsoid, gpsFrom, gpsTo);

        return geoCurve.getEllipsoidalDistance();
    }
}
