package com.jiebbs.controller.backend;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jiebbs.common.Const;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojos.User;
import com.jiebbs.service.IUserService;

@Controller
@RequestMapping("/manage/user")
public class UserManagerController {
	
	@Autowired
	private IUserService iUserService;
	
	@RequestMapping(value="login.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> login(String username,String password,HttpSession session){
		ServerResponse<User> response = iUserService.login(username, password);
		if(response.isSuccess()) {
			User user = response.getData();
			if(user.getRole().equals(Const.Role.ROLE_ADMIN )) {
				//说明登陆的是管理员
				session.setAttribute(Const.CURRENT_USER, user);
			}else {
				return ServerResponse.createByErrorMessage("不是管理员，无法登陆");
			}
		}
		return response;
	}
}
