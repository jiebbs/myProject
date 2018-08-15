package com.jiebbs.service.Impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.daos.ShippingMapper;
import com.jiebbs.pojos.Shipping;
import com.jiebbs.service.IShippingService;

@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService{
	
	@Autowired
	private ShippingMapper shippingMapper;
	
	public ServerResponse<Map> add(Integer userId,Shipping shipping) {
		if(shipping==null) {
			return ServerResponse.createByErrorMessage("参数错误，请传入正确参数");
		}
		shipping.setUserId(userId);
		int row = shippingMapper.insert(shipping);
		if(row>0) {
			Map result = Maps.newHashMap();
			result.put("shippingId", shipping.getId());
			return ServerResponse.createBySuccess("新建地址成功",result);
		}
		return ServerResponse.createByErrorMessage("新增地址失败");
	}
	
	public ServerResponse<String> del(Integer userId,Integer shippingId) {
		if(shippingId==null) {
			return ServerResponse.createByErrorMessage("参数错误，请传入正确参数");
		}
		int row = shippingMapper.deleteByUserIdAndShippingId(shippingId, userId);
		if(row>0) {
			return ServerResponse.createBySuccess("删除地址成功");
		}
		return ServerResponse.createByErrorMessage("删除地址失败");
	}
	
	public ServerResponse<String> upd(Integer userId,Shipping shipping) {
		if(shipping==null) {
			return ServerResponse.createByErrorMessage("参数错误，请传入正确参数");
		}
		shipping.setUserId(userId);
		int row = shippingMapper.updateByShipping(shipping);
		if(row>0) {
			return ServerResponse.createBySuccess("更新地址成功");
		}
		return ServerResponse.createByErrorMessage("更新地址失败");
	}
	
	public ServerResponse<Shipping> select(Integer userId,Integer shippingId) {
		if(shippingId==null) {
			return ServerResponse.createByErrorMessage("参数错误，请传入正确参数");
		}
		Shipping shipping= shippingMapper.selectByUserIdAndShippingId(userId, shippingId);
		if(shipping!=null) {
			return ServerResponse.createBySuccess(shipping);
		}
		return ServerResponse.createByErrorMessage("没有该地址记录");
	}
	
	public ServerResponse<PageInfo> listShipping(Integer userId,Integer pageNum,Integer pageSize){
		PageHelper.startPage(pageNum,pageSize);
		List<Shipping> shippingList = shippingMapper.selectAllShippingByUserId(userId);
		if(null==shippingList) {
			shippingList = Lists.newArrayList();
		}
		PageInfo result = new PageInfo(shippingList);
		
		return ServerResponse.createBySuccess(result);
	}
	
	
}
