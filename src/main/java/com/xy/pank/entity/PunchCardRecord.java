package com.xy.pank.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("pk_punch_card_record")
public class PunchCardRecord  {

    private Integer id;

    private String dotTag;

    private String openid;

    private Date createTime;

    private Integer rankingid;

    private Integer type;
}
