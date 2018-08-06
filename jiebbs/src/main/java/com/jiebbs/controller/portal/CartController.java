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
	
	@RequestMapping(value="update_product.do",method=RequestMethod.GET)
	@ResponseBody()
	public ServerResponse<CartVO> update(HttpSession session,Integer count,Integer productId) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user==null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), ResponseCode.NEED_LOGGING.getDesc());
		}	
		return iCartService.updateProduct2Cart(user.getId(), count, productId);
	}
	
	@RequestMapping(value="delete_product.do",method=RequestMethod.GET)
	@ResponseBody()
	public ServerResponse<CartVO> delete(HttpSession session,String productIds) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user==null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), ResponseCode.NEED_LOGGING.getDesc());
		}	
		return iCartService.deleteProductFromCart(user.getId(),productIds);
	}
	
	@RequestMapping(value="list_product.do",method=RequestMethod.GET)
	@ResponseBody()
	public ServerResponse<CartVO> list(HttpSession session) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user==null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), ResponseCode.NEED_LOGGING.getDesc());
		}	
		return iCartService.selectProductFromCart(user.getId());
	}
	
	//全选
	
	@RequestMapping(value="selectAll_product.do",method=RequestMethod.GET)
	@ResponseBody()
	public ServerResponse<CartVO> selectAll(HttpSession session) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user==null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), ResponseCode.NEED_LOGGING.getDesc());
		}	
		return iCartService.selectOrUnselectProductFromCart(user.getId(),null,Const.CartCheckStatus.CHECKED);
	}
	
	//全反选
	@RequestMapping(value="unSelectAll_product.do",method=RequestMethod.GET)
	@ResponseBody()
	public ServerResponse<CartVO> unSelectAll(HttpSession session) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user==null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), ResponseCode.NEED_LOGGING.getDesc());
		}	
		return iCartService.selectOrUnselectProductFromCart(user.getId(),null, Const.CartCheckStatus.UNCHECKED);
	}
	
	
	//单选
	
	@RequestMapping(value="selectOne_product.do",method=RequestMethod.GET)
	@ResponseBody()
	public ServerResponse<CartVO> selectOne(HttpSession session,Integer productId) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user==null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), ResponseCode.NEED_LOGGING.getDesc());
		}	
		return iCartService.selectOrUnselectProductFromCart(user.getId(),productId,Const.CartCheckStatus.CHECKED);
	}
	
	//单独反选
	@RequestMapping(value="unSelectOne_product.do",method=RequestMethod.GET)
	@ResponseBody()
	public ServerResponse<CartVO> unSelectOne(HttpSession session,Integer productId) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user==null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), ResponseCode.NEED_LOGGING.getDesc());
		}	
		return iCartService.selectOrUnselectProductFromCart(user.getId(),productId,Const.CartCheckStatus.UNCHECKED);
	}
	//查询用户购物车当前的产品数量（如果一个商品显示10，我们就显示10个）
	
	@RequestMapping(value="get_cart_product_count.do",method=RequestMethod.GET)
	@ResponseBody()
	public ServerResponse<Integer> getCartProductCount(HttpSession session) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user==null) {
			//没有登录的用户收到的数据为0
			return ServerResponse.createBySuccess(0);
		}	
		return iCartService.getCartProductCount(user.getId());
	}
	
}
