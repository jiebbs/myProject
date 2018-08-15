package com.jiebbs.service;

import java.util.Map;

import com.jiebbs.common.ServerResponse;

public interface IOrderService {

	ServerResponse pay(Integer userId,Long orderNum,String path);
	
	ServerResponse aliCallBack(Map<String,String> params);
	
	ServerResponse qureyOrderPayStatus(Integer userId,Long orderNum);
}
