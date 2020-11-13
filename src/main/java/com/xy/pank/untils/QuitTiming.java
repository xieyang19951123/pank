package com.xy.pank.untils;

import com.alibaba.druid.support.spring.stat.SpringStatUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xy.pank.dao.DotDao;
import com.xy.pank.dao.PunchCardRecordDao;
import com.xy.pank.dao.RankingDao;
import com.xy.pank.dao.UserDao;
import com.xy.pank.entity.Dot;
import com.xy.pank.entity.PkUserEntity;
import com.xy.pank.entity.PunchCardRecord;
import com.xy.pank.entity.Ranking;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Component
public class QuitTiming {

    long nd = 1000 * 24 * 60 * 60;
    @Autowired
    private RankingDao rankingDao;

    @Autowired
    private UserDao userDao;


    @Scheduled( cron = "* 0/5 * * * ? ")
    public void test(){
        System.out.println(1111111);
        Ranking ranking = new Ranking();
        ranking.setComplete(0);
        List<Ranking> rankings = rankingDao.selectList(new QueryWrapper<>(ranking));
        rankings.forEach(item->{
            if(item.getStartTime() != null){
                long dat =  new Date().getTime() - item.getStartTime().getTime();
                long l = dat / nd;
                System.out.println(l);
                if(l >= 1){
                    PkUserEntity pkUserEntity = new PkUserEntity();
                    pkUserEntity.setId(item.getUserid());
                    pkUserEntity.setInthegame(0);
                    item.setComplete(1);
                    rankingDao.updateById(item);
                    userDao.updateById(pkUserEntity);
                }
            }

        });
        //System.out.println("1111111");
    }


    @Autowired
    private DotDao dotDao;

    //long nd = 1000 * 24 * 60 * 60;

    long nh = 1000 * 60 * 60;

    long nm = 1000 * 60;

    long ns = 1000;


    @Autowired
    private PunchCardRecordDao punchCardRecordDao;
   // @Scheduled( cron = "0/1 * * * * ?")
    public void  test1(){
        PunchCardRecord punchCardRecord = new PunchCardRecord();
        punchCardRecord.setRankingid(96);
        List<PunchCardRecord> punchCardRecords = punchCardRecordDao.selectList(new QueryWrapper<>(punchCardRecord));


        Ranking ranking1 = rankingDao.selectById(96);
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

        //计算卡路里//
        double d = 0;
        for (int i = 0; i <punchCardRecords.size() ; i++) {
            if((punchCardRecords.size()>(i+1))){
                d+=computed(punchCardRecords.get(i).getDotTag(),punchCardRecords.get(i+1).getDotTag());
            }
        }
        System.out.println(d);
        double mun =sec / nm;//分钟
        System.out.println(mun);
        System.out.println(d);
        int musm =(int) Math.ceil(mun/d*nm);  // 分钟/公里
        String metu = (musm / nm>=10?musm  / nm:"0"+musm/ nm)+"‘"+(musm% nd % nh % nm / ns>=10?musm% nd % nh % nm / ns:"0"+musm% nd % nh % nm / ns)+"“";
        //计算卡路里//
        System.out.println(metu);
    }

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

    public  double getDistanceMeter(GlobalCoordinates gpsFrom, GlobalCoordinates gpsTo, Ellipsoid ellipsoid)
    {
        //创建GeodeticCalculator，调用计算方法，传入坐标系、经纬度用于计算距离
        GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(ellipsoid, gpsFrom, gpsTo);

        return geoCurve.getEllipsoidalDistance();
    }

    private Dot getdot(String tag){

        Dot dot = new Dot();
        dot.setDTag(tag);
        Dot dot1 = dotDao.selectOne(new QueryWrapper<>(dot));

        return dot1;
    }
}
