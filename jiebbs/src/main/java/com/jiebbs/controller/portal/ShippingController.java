package com.jiebbs.controller.portal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jiebbs.common.Const;
import com.jiebbs.common.ResponseCode;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojos.Shipping;
import com.jiebbs.pojos.User;
import com.jiebbs.service.IShippingService;

@Controller
@RequestMapping(value="/shipping/")
public class ShippingController {
	
	@Autowired
	private IShippingService iShippingService;
	
	@RequestMapping("")
	@ResponseBody
	public ServerResponse addAdress(HttpSession session,Shipping shipping){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		return iShippingService
		
		return null;
	}
}
