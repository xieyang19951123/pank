package com.xy.pank.controller;

import com.xy.pank.dao.BulletinTextDao;
import com.xy.pank.dao.BulletinimgDao;
import com.xy.pank.dao.DotDao;
import com.xy.pank.entity.Accessory;
import com.xy.pank.entity.Bulletinimg;
import com.xy.pank.entity.Dot;
import com.xy.pank.service.DotService;
import com.xy.pank.untils.R;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("pank/dot/")
public class Dotcontroller {

    @Autowired
    private DotService dotService;


    @Autowired
    private BulletinimgDao bulletinimgDao;

    @Autowired
    private BulletinTextDao bulletinTextDao;

    @Autowired
    private DotDao dotDao;


    //根据标识获取地点信息
        @RequestMapping("/getDot")
    public R getDotByTag(@RequestParam("tag")String tag){
        if(StringUtils.isEmpty(tag)){
            return R.error(400,"tag为空");
        }
        Dot dot  = dotService.getDotByTag(tag);
        return R.ok().put("page",dot);
    }


    @RequestMapping("getBulletimg")
    public  R getBulletimg(){
        Bulletinimg bulletinimgs = bulletinimgDao.selectById(1);
        String s = bulletinimgs.getBulletinimg().replaceAll("\\[", "(").replaceAll("]", ")");
        List<Accessory> accessorys = dotDao.accessorys(s);
        return R.ok().put("page",accessorys);
    }


    @RequestMapping("getBulleText")
    public R getBulleText(){
        return R.ok().put("page",bulletinTextDao.selectList(null));
    }


}
