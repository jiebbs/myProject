package com.jiebbs.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;import com.jiebbs.common.ResponseCode;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.daos.ShippingMapper;
import com.jiebbs.pojos.Shipping;
import com.jiebbs.service.IShippingService;

@Service
public class ShippingServiceImpl implements IShippingService{
	
	@Autowired
	private ShippingMapper shippingMapper;
	
	public ServerResponse add(Integer userId,Shipping shipping) {
		if(shipping==null) {
			return ServerResponse.createByErrorMessage("���������봫����ȷ����");
		}
		shipping.setUserId(userId);
		int result = shippingMapper.insert(shipping);
		if(result>0) {
			return ServerResponse.createBySuccessMessage("������ַ�ɹ�");
		}
		return ServerResponse.createByErrorMessage("������ַʧ��");
	}
	
	
}
