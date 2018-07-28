package com.jiebbs.service;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.annotations.Param;

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
	
	ServerResponse<String> selectQuestion(String username);
	
	ServerResponse<String> checkAnswer(String username,String question,String anwser);
	
	ServerResponse<String> resetPassword(String username,String newPassword,String forgetToken);
	
	ServerResponse<String> resetPassword(String newPassword,String oldPassword,User user);
	
	ServerResponse<User> update_Information(User user);
	
	ServerResponse<User> getDetailInformation(Integer userId);
	
	ServerResponse<String> checkAdminRole(User user);
}
