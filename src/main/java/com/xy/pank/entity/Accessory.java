package com.xy.pank.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("pk_accessory")
public class Accessory {

    private Integer id;

    private String name;

    private String url;

    private String urlMin;

    @TableLogic(value = "1",delval = "0")
    private Integer showStatus;
}
