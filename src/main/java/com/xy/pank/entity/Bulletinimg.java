package com.xy.pank.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@TableName("pk_bulletinimg")
@Data
public class Bulletinimg {

    private Integer id;

    private String bulletinimg;

    @TableField(exist = false)
    private List<Accessory> accessories;
}
