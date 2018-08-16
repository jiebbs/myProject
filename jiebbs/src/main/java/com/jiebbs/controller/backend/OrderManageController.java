package com.jiebbs.controller.backend;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.jiebbs.common.Const;
import com.jiebbs.common.ResponseCode;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojos.User;
import com.jiebbs.service.IOrderService;
import com.jiebbs.service.IUserService;
import com.jiebbs.vo.OrderVO;

@Controller
@RequestMapping(value="/manage/order/")
public class OrderManageController {
	@Autowired
	private IOrderService iOrderService;
	@Autowired
	private IUserService iUserService;	
	
	@RequestMapping(value="list.do",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<PageInfo> list(HttpSession session,@RequestParam(value="pageNum",defaultValue="1")Integer pageNum,@RequestParam(value="pageSize",defaultValue="10")Integer pageSize) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(null==user) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(),ResponseCode.NEED_LOGGING.getDesc());
		}else if(!iUserService.checkAdminRole(user).isSuccess()) {
			return ServerResponse.createByErrorMessage("无权限进行该操作");
		}
		return iOrderService.manageList(pageNum,pageSize);
		
	}
	
	@RequestMapping(value="detail.do",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<OrderVO> orderDetail(HttpSession session,Long orderNum) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(null==user) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(),ResponseCode.NEED_LOGGING.getDesc());
		}else if(!iUserService.checkAdminRole(user).isSuccess()) {
			return ServerResponse.createByErrorMessage("无权限进行该操作");
		}
		return iOrderService.manageDetail(orderNum);
		
	}
	
	@RequestMapping(value="search.do",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<OrderVO> search(HttpSession session,Long orderNum,@RequestParam(value="pageNum",defaultValue="1")Integer pageNum,@RequestParam(value="pageSize",defaultValue="10")Integer pageSize) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(null==user) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(),ResponseCode.NEED_LOGGING.getDesc());
		}else if(!iUserService.checkAdminRole(user).isSuccess()) {
			return ServerResponse.createByErrorMessage("无权限进行该操作");
		}
		return iOrderService.manageSearch(orderNum);
		
	}
}
