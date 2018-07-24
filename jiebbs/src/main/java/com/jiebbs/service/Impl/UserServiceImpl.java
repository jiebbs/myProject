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
			return ServerResponse.createByErrorMessage("�û���������");
		}
		//TODO ������MD5����
		
		User user = userMapper.selectLogin(username, password);
		if(null == user) {
			return ServerResponse.createByErrorMessage("�������");
		}
		
		user.setPassword(org.apache.commons.lang.StringUtils.EMPTY);
		
		return ServerResponse.createBySuccess("��¼�ɹ�", user);
	}
	
}
