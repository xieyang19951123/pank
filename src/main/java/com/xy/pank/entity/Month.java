package com.xy.pank.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("pk_month")
public class Month {

    private Integer id;

    private Integer month;

    private Integer week;

    private Integer years;

    private Integer pnumber;

    private Integer showStatus;


}
