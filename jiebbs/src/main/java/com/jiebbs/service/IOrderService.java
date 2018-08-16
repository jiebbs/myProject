package com.jiebbs.service;

import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.vo.OrderVO;

public interface IOrderService {

	ServerResponse pay(Integer userId,Long orderNum,String path);
	
	ServerResponse aliCallBack(Map<String,String> params);
	
	ServerResponse qureyOrderPayStatus(Integer userId,Long orderNum);
	
	ServerResponse createOrder(Integer userId,Integer shippingId);
	
	ServerResponse cancalOrder(Integer userId,Long orderNum);
	
	ServerResponse getOrderCartProduct(Integer userId);
	
	ServerResponse<OrderVO> getOrderDetail(Integer userId,Long orderNum);
	
	ServerResponse<PageInfo> getOrderlist(Integer userId,Integer pageNum,Integer pageSize);
	
	ServerResponse<PageInfo> manageList(Integer pageNum,Integer pageSize);
	
	ServerResponse<OrderVO> manageDetail(Long orderNo);
}
