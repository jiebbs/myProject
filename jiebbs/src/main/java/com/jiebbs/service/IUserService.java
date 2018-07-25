package com.jiebbs.service;

import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojos.User;

/**
 * 
 * @author weijie_zhu
 *
 */
public interface IUserService {
	
	ServerResponse<User> login(String username,String password);
	
	ServerResponse<String> register(User user);
	
	ServerResponse<String> checkValid(String str,String type);
}
