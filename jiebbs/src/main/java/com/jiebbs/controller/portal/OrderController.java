package com.jiebbs.controller.portal;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.jiebbs.common.Const;
import com.jiebbs.common.ResponseCode;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojos.User;
import com.jiebbs.service.IOrderService;

@Controller
@RequestMapping("/order/")
public class OrderController {
	
	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
	@Autowired
	private IOrderService iOrderService;
	
	@RequestMapping(value="create.do",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse createOrder(HttpSession session,Integer shippingId) {
		User user =(User)session.getAttribute(Const.CURRENT_USER);
		if(null == user) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), ResponseCode.NEED_LOGGING.getDesc());
		}
		return iOrderService.createOrder(user.getId(), shippingId);
	}
	
	@RequestMapping(value="cancal.do",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse cancalOrder(HttpSession session,Long orderNum) {
		User user =(User)session.getAttribute(Const.CURRENT_USER);
		if(null == user) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), ResponseCode.NEED_LOGGING.getDesc());
		}
		return iOrderService.cancalOrder(user.getId(), orderNum);
	}
	
	@RequestMapping(value="get_order_cart_product.do",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse getOrderCartProduct(HttpSession session) {
		User user =(User)session.getAttribute(Const.CURRENT_USER);
		if(null == user) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), ResponseCode.NEED_LOGGING.getDesc());
		}
		return iOrderService.getOrderCartProduct(user.getId());
	}
	
	@RequestMapping(value="detail.do",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse getOrderDetail(HttpSession session,Long orderNum) {
		User user =(User)session.getAttribute(Const.CURRENT_USER);
		if(null == user) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), ResponseCode.NEED_LOGGING.getDesc());
		}
		return iOrderService.getOrderDetail(user.getId(), orderNum);
	}
	
	@RequestMapping(value="list.do",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse orderList(HttpSession session,@RequestParam(value="pageNum",defaultValue="1")Integer pageNum,@RequestParam(value="pageSize",defaultValue="10")Integer pageSize) {
		User user =(User)session.getAttribute(Const.CURRENT_USER);
		if(null == user) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), ResponseCode.NEED_LOGGING.getDesc());
		}
		return iOrderService.getOrderlist(user.getId(), pageNum, pageSize);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@RequestMapping(value="pay.do",method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse pay(HttpSession session,Long orderNum,HttpServletRequest request) {
		User user =(User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), ResponseCode.NEED_LOGGING.getDesc());
		}
		String path = request.getSession().getServletContext().getRealPath("upload");

		return ServerResponse.createBySuccess(iOrderService.pay(user.getId(), orderNum, path));
	}
	
	@RequestMapping(value="alipay_callback.do")
	@ResponseBody()
	public Object alipayCallBack(HttpServletRequest request) {
		Map<String,String> params = Maps.newHashMap();
		
		Map requestParams = request.getParameterMap();
		Iterator iter = requestParams.keySet().iterator();
		while(iter.hasNext()) {
			String name = (String)iter.next();
			String[] values = (String[])requestParams.get(name);
			String valueStr = "";
			for(int i = 0;i<values.length;i++) {
				
				valueStr = (i==values.length-1)?valueStr+values[i]:valueStr+values[i]+",";
			}
			params.put(name, valueStr);
		}
		logger.info("支付宝回调，sign:{},trade_status:{},参数：{}",params.get("sign"),params.get("trade_status"),params);
		
		//验证支付宝回调正确性
		//必须除去sign和sign_type 2个参数
		
		params.remove("sign_type");
		try {
			boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8", Configs.getSignType());
			if(!alipayRSACheckedV2) {
				//业务逻辑
				return ServerResponse.createByErrorMessage("非法请求");
			}
			
			
		} catch (AlipayApiException e) {
			logger.error("验签异常",e);
		} 
		
		//TODO 验证支付宝参数的正确性
		
		ServerResponse serverResponse = iOrderService.aliCallBack(params);
		
		if(serverResponse.isSuccess()) {
			return Const.AlipayCallBack.RESPONSE_SUCCESS;
		}
		return Const.AlipayCallBack.RESPONSE_FAILED;
	}
	
	@RequestMapping(value="qurey_order_pay_status.do",method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<Boolean> qurreyOrderPayStatus(HttpSession session,Long orderNum) {
		User user =(User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), ResponseCode.NEED_LOGGING.getDesc());
		}
		
		ServerResponse serverResponse =  iOrderService.qureyOrderPayStatus(user.getId(), orderNum);
		if(serverResponse.isSuccess()) {
			return ServerResponse.createBySuccess(true);
		}else {
			return ServerResponse.createBySuccess(false);
		}
	}
	
	
	
	
}
