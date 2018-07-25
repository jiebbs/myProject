package com.jiebbs.common;

public class Const {
	
	public static final String CURRENT_USER = "currentUser";
	
	//前端进行异步校验使用
	public static final String USERNAME = "username";
	
	public static final String EMAIL = "email";
	
	//使用内部类规定用户类型
	public interface Role{
		int ROLE_CUSTORMER = 0;//普通用户
		int ROLE_ADMIN = 1;//管理员
	}
}
