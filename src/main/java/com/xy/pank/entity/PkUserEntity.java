package com.xy.pank.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户管理
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2020-08-26 22:42:52
 */
@Data
@TableName("pk_user")
public class PkUserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;



	//昵称
	private String nickname;


	private String openid;


	//头像
	private String headimgurl;

	@TableLogic(value = "1" ,delval = "0")
	private Integer showStatus;

	/**
	 * 是否在游戏
	 */
	private Integer inthegame;

	private Date createTime;

}
