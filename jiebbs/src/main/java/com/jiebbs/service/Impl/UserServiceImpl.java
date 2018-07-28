package com.jiebbs.service.Impl;

import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiebbs.common.Const;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.common.TokenCache;
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
		ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
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
	
	//忘记密码
	public ServerResponse<String> selectQuestion(String username) {
		ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
		if(validResponse.isSuccess()) {
			return ServerResponse.createByErrorMessage("该用户不存在");
		}
		String question = userMapper.selectQuestionByUsername(username);
		if(org.apache.commons.lang3.StringUtils.isNotBlank(question)) {
			return ServerResponse.createBySuccess(question);
		}
		return ServerResponse.createByErrorMessage("找回密码的问题不存在");
	}
	
	//验证忘记密码的问题的答案
	public ServerResponse<String> checkAnswer(String username,String question,String answer){
		int resultCount = userMapper.checkAnswer(username, question, answer);
		if(resultCount > 0) {
			String forgetToken = UUID.randomUUID().toString();
			TokenCache.setKey(TokenCache.TOKEN_PREFIX+username, forgetToken);
			return ServerResponse.createBySuccess(forgetToken);
		}
		return ServerResponse.createByErrorMessage("问题答案不正确，请重新输入");
	}
	
	//重置账号的密码
	public ServerResponse<String> resetPassword(String username,String newPassword,String forgetToken){
		//校验Token是否为空
		if(!org.apache.commons.lang3.StringUtils.isNotBlank(forgetToken)) {
			return ServerResponse.createByErrorMessage("参数错误，Token必须传入");
		}
		//验证用户名是否存在
		ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
		if(validResponse.isSuccess()) {
			return ServerResponse.createByErrorMessage("该用户不存在");
		}
		
		String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
		//从本地缓存中取得的token可能为null，所以要进行验证
		if(!org.apache.commons.lang3.StringUtils.isNotBlank(token)) {
			return ServerResponse.createByErrorMessage("token无效或者过期");
		}
		if(org.apache.commons.lang3.StringUtils.equals(forgetToken, token)) {
			String md5newPassword = MD5Util.MD5EncodeUtf8(newPassword);
			int rowCount = userMapper.resetPasswordByUsername(username, md5newPassword);
			if(rowCount<=0) {
				return ServerResponse.createBySuccessMessage("密码更新失败");
			}
		}else {
			return ServerResponse.createByErrorMessage("Token错误，请重新获取重置密码的T");
		}
		return ServerResponse.createByErrorMessage("更新密码成功");
	}
	
	//登陆状态下更新密码
	public ServerResponse<String> resetPassword(String newPassword,String oldPassword,User user){
		//防止横向越权，一定要校验这个用户的旧密码是这个用户的，因为我们会查询一个count(1),如果指定id,那么结果就是true,count>0
		int resultCount = userMapper.checkPassword(user.getId(),MD5Util.MD5EncodeUtf8(oldPassword));
		if(resultCount==0) {
			return ServerResponse.createByErrorMessage("旧密码错误");
		}
		user.setPassword(MD5Util.MD5EncodeUtf8(newPassword));
		int updateCount = userMapper.updateByPrimaryKeySelective(user);
		if(updateCount > 0) {
			return ServerResponse.createBySuccessMessage("密码更新成功");
		}
		return ServerResponse.createByErrorMessage("密码更新失败");
	}
	
	//更新用户信息
	public ServerResponse<User> update_Information(User user){
		//更新的时候Username是不能被更新的
		//同事Email也要进行校验，校验新的Email是不是已经存在，并且存在的email如果相同的话不能是我们当前用户的
		int resultCount = userMapper.checkEmailByUserId(user.getId(), user.getEmail());
		if(resultCount>0 ) {
			return ServerResponse.createByErrorMessage("此邮箱已被占用，请重新输入");
		}
		User updateUser = new User();
		updateUser.setId(user.getId());
		updateUser.setEmail(user.getEmail());
		updateUser.setAnswer(user.getAnswer());
		updateUser.setPhone(user.getPhone());
		updateUser.setQuestion(user.getQuestion());
		
		int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
		if(updateCount > 0) {
			//updateUser = userMapper.selectByPrimaryKey(updateUser.getId());
			//updateUser.setPassword(org.apache.commons.lang.StringUtils.EMPTY);
			return ServerResponse.createBySuccess("用户信息更新成功",updateUser);
		}
		return ServerResponse.createByErrorMessage("用户信息更新失败");
	}
	
	public ServerResponse<User> getDetailInformation(Integer userId){
		User user = userMapper.selectByPrimaryKey(userId);
		if(null==user) {
			return ServerResponse.createByErrorMessage("找不到当前用户");
		}
		//置空密码
		user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
		return ServerResponse.createBySuccess(user);
	}
	
	public ServerResponse<String> checkAdminRole(User user){
		if(user!=null&&user.getRole().intValue()==Const.Role.ROLE_ADMIN) {
			return ServerResponse.createBySuccess();
		}
		return ServerResponse.createByError();
	}
}
