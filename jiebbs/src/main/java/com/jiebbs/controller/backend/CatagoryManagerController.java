package com.jiebbs.controller.backend;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jiebbs.common.Const;
import com.jiebbs.common.ResponseCode;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojos.Classification;
import com.jiebbs.pojos.User;
import com.jiebbs.service.ICatagoryService;
import com.jiebbs.service.IUserService;

@Controller
@RequestMapping(value="/manage/catagory")
public class CatagoryManagerController {
	
	@Autowired
	private IUserService iUserService;
	@Autowired
	private ICatagoryService iCatagoryService;
	
	
	@RequestMapping(value="addCatagory.do",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<String> addCatagory(HttpSession session,String catagoryName,@RequestParam(value="parentId",defaultValue="0")Integer parentId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user==null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(),"用户为登陆，请强制用户登陆");
		}
		ServerResponse<String> response = iUserService.checkAdminRole(user);
		if(response.isSuccess()) {
			
			return iCatagoryService.addCatagory(catagoryName,parentId);
		}
		return ServerResponse.createByErrorMessage("此账号不是管理员账号，无权进行该操作");
	}
}

