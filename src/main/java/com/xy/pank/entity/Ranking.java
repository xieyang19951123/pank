package com.xy.pank.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xy.pank.entity.vo.ShowEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("pk_ranking")
public class Ranking {

    private Integer id;

    private Integer userid;

    private Integer calorie;

    private Date startTime;

    private Date endTime;

    private Date createTime;

    private Integer complete;

    private Integer teamId;

    private String origin;

    @TableField(exist = false)
    private ShowEntity showEntity;

    private String terminus;

    private Integer useTime;

    @TableLogic(value = "1",delval = "0")
    private Integer showStatus;



}
