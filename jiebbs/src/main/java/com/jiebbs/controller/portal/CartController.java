package com.jiebbs.controller.portal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jiebbs.common.Const;
import com.jiebbs.common.ResponseCode;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojos.User;
import com.jiebbs.service.ICartService;
import com.jiebbs.vo.CartVO;

@Controller
@RequestMapping(value="/cart/")
public class CartController {
	@Autowired
	private ICartService iCartService;
	
	@RequestMapping(value="add_product.do",method=RequestMethod.GET)
	@ResponseBody()
	public ServerResponse<CartVO> add(HttpSession session,Integer count,Integer productId) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user==null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), ResponseCode.NEED_LOGGING.getDesc());
		}	
		return iCartService.addProduct2Cart(user.getId(), count, productId);
	}
}
