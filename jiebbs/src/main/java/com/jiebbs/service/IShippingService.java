package com.jiebbs.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojos.Shipping;

public interface IShippingService {

	ServerResponse add(Integer userId,Shipping shipping);
	
	ServerResponse<String> del(Integer userId,Integer shippingId);
	
	ServerResponse<String> upd(Integer userId,Shipping shipping);
	
	ServerResponse<Shipping> select(Integer userId,Integer shippingId);
	
	ServerResponse<PageInfo> listShipping(Integer userId,Integer pageNum,Integer pageSize);
}
