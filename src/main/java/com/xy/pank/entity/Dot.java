package com.xy.pank.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@Data
@TableName("ps_dot")
public class Dot {

    private Integer id;

    private String dName;

        private  String dDescribe;

    @TableLogic(value = "1",delval = "0")
    private Integer show_status;

    private String aId;

    private String qcodeUrl;

    private  String dTag;

    private String longitude;
    @TableField(exist = false)
    private Integer[] aids;
    @TableField(exist = false)
    private List<Accessory> accessories;
}
