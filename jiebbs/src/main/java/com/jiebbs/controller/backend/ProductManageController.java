package com.jiebbs.controller.backend;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jiebbs.common.Const;
import com.jiebbs.common.ResponseCode;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojos.Product;
import com.jiebbs.pojos.User;
import com.jiebbs.service.IProductService;
import com.jiebbs.service.IUserService;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {
	@Autowired
	private IUserService iUserService;
	@Autowired
	private IProductService iProductService;
	
	@RequestMapping(value="saveProduct.do",method=RequestMethod.GET)
	@ResponseBody()
	public ServerResponse productSave(HttpSession session,Product product) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), "用户未登录，请登录");
		}
		if(iUserService.checkAdminRole(user).isSuccess()) {
			return iProductService.saveOrUpdateProduct(product);
		}
		return ServerResponse.createByErrorMessage("你不是管理员，无权限进行该操作！");
	} 
	
	@RequestMapping(value="set_sale_status.do",method=RequestMethod.GET)
	@ResponseBody()
	public ServerResponse setSaleStatus(HttpSession session,Integer productId,Integer status) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), "用户未登录，请登录");
		}
		if(iUserService.checkAdminRole(user).isSuccess()) {
			return iProductService.setSaleStatus(productId,status);
		}
		return ServerResponse.createByErrorMessage("你不是管理员，无权限进行该操作！");
	} 
	
	@RequestMapping(value="product_detail.do",method=RequestMethod.GET)
	@ResponseBody()
	public ServerResponse getProductDetail(HttpSession session,Integer productId) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), "用户未登录，请登录");
		}
		if(iUserService.checkAdminRole(user).isSuccess()) {
			return iProductService.manageProductDetails(productId);
		}
		return ServerResponse.createByErrorMessage("你不是管理员，无权限进行该操作！");
	} 
}
