package com.jiebbs.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiebbs.common.ServerResponse;
import com.jiebbs.daos.UserMapper;
import com.jiebbs.pojos.User;
import com.jiebbs.service.IUserService;

@Service("iUserService")
public class UserServiceImpl implements IUserService{
	
	@Autowired
	private UserMapper userMapper;
	
	public ServerResponse<User> login(String username, String password) {
		
		int resultCount = userMapper.checkUsername(username);
		if(resultCount == 0) {
			return ServerResponse.createByErrorMessage("用户名不存在");
		}
		//TODO 密码用MD5加密
		
		User user = userMapper.selectLogin(username, password);
		if(null == user) {
			return ServerResponse.createByErrorMessage("密码错误");
		}
		
		user.setPassword(org.apache.commons.lang.StringUtils.EMPTY);
		
		return ServerResponse.createBySuccess("登录成功", user);
	}
	
}
