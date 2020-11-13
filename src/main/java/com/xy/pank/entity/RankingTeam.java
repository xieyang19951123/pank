package com.xy.pank.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xy.pank.entity.vo.ShowEntity;
import lombok.Data;

import java.util.List;

@Data
@TableName("pk_ranking_team")
public class RankingTeam {

    private Integer id;

    private String teamName;

    private Integer averageTime;

    private String teamTag;

    private Integer complete;

    @TableField(exist = false)
    private List<ShowEntity> showEntities;


    @TableField(exist = false)
    private String averageTimes;
}
