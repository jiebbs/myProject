package com.jiebbs.controller.portal;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
	
	@RequestMapping(value="add_adress.do",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse addAdress(HttpSession session,Shipping shipping){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), ResponseCode.NEED_LOGGING.getDesc());
		}
		return iShippingService.add(user.getId(), shipping);
	}
	
	@RequestMapping(value="del_adress.do",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<String> delAdress(HttpSession session,Integer shippingId){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), ResponseCode.NEED_LOGGING.getDesc());
		}
		return iShippingService.del(user.getId(),shippingId);
	}
	
	@RequestMapping(value="upd_adress.do",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<String> updAdress(HttpSession session,Shipping shipping){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), ResponseCode.NEED_LOGGING.getDesc());
		}
		return iShippingService.upd(user.getId(), shipping);
	}
	
	@RequestMapping(value="select_adress.do",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<Shipping> selectAdress(HttpSession session,Integer shippingId){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), ResponseCode.NEED_LOGGING.getDesc());
		}
		return iShippingService.select(user.getId(), shippingId);
	}
	
	@RequestMapping(value="list_adress.do",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<PageInfo> listAdress(HttpSession session,@RequestParam(value="pageNum",defaultValue="1")Integer pageNum,@RequestParam(value="pageSize",defaultValue="10")Integer pageSize){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), ResponseCode.NEED_LOGGING.getDesc());
		}
		
		return iShippingService.listShipping(user.getId(), pageNum, pageSize);
	}
	
}
