package com.jiebbs.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiebbs.common.Const;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.daos.UserMapper;
import com.jiebbs.pojos.User;
import com.jiebbs.service.IUserService;
import com.jiebbs.utils.MD5Util;

@Service("iUserService")
public class UserServiceImpl implements IUserService{
	
	@Autowired
	private UserMapper userMapper;
	
	//登陆方法
	public ServerResponse<User> login(String username, String password) {
		
		int resultCount = userMapper.checkUsername(username);
		if(resultCount == 0) {
			return ServerResponse.createByErrorMessage("用户名不存在");
		}
		//TODO 密码用MD5加密
		//需要将传入的密码进行MD5加密以保证可以和数据库进行正确的校验
		String md5Password = MD5Util.MD5EncodeUtf8(password);
		User user = userMapper.selectLogin(username, md5Password);
		if(null == user) {
			return ServerResponse.createByErrorMessage("密码错误");
		}
		
		user.setPassword(org.apache.commons.lang.StringUtils.EMPTY);
		
		return ServerResponse.createBySuccess("登录成功", user);
	}
	
	//注册方法 	
	public ServerResponse<String> register(User user) {
		ServerResponse validResponse = this.checkValid(user.getUsername(),Const.CURRENT_USER);
		if(!validResponse.isSuccess()) {
			return ServerResponse.createByErrorMessage("用户名已存在");
		}
		validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
		if(!validResponse.isSuccess()) {
			return ServerResponse.createByErrorMessage("邮箱已存在");
		}
		
		user.setRole(Const.Role.ROLE_CUSTORMER);
		
		//密码进行MD5加密
		user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
		
		int resultCount = userMapper.insert(user);
		if(resultCount == 0) {
			return ServerResponse.createByErrorMessage("注册失败");
		}
		return ServerResponse.createBySuccessMessage("注册成功");
	}
	
	//数据校验方法
	public ServerResponse<String> checkValid(String str, String type) {
		int resultCount = 0;
		if(org.apache.commons.lang3.StringUtils.isNotBlank(type)){
			//开始校验
			if(Const.USERNAME.equals(type)){
				resultCount  = userMapper.checkUsername(str);
				if(resultCount > 0) {
					return ServerResponse.createByErrorMessage("用户名已存在");
				}
			}
			if(Const.EMAIL.equals(type)) {
				resultCount = userMapper.checkEmail(str);
				if(resultCount > 0) {
					return ServerResponse.createByErrorMessage("邮箱已存在");
				}
			}
		}else {
			return ServerResponse.createByErrorMessage("参数错误");
		}
		
		return ServerResponse.createBySuccessMessage("校验成功");
	}
	
}
