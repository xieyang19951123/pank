package com.xy.pank.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("pk_bulletintext")
public class BulletinText  {

    private Integer id;

    private String mytext;
}
