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
	
	//��½����
	public ServerResponse<User> login(String username, String password) {
		
		int resultCount = userMapper.checkUsername(username);
		if(resultCount == 0) {
			return ServerResponse.createByErrorMessage("�û���������");
		}
		//TODO ������MD5����
		//��Ҫ��������������MD5�����Ա�֤���Ժ����ݿ������ȷ��У��
		String md5Password = MD5Util.MD5EncodeUtf8(password);
		User user = userMapper.selectLogin(username, md5Password);
		if(null == user) {
			return ServerResponse.createByErrorMessage("�������");
		}
		
		user.setPassword(org.apache.commons.lang.StringUtils.EMPTY);
		
		return ServerResponse.createBySuccess("��¼�ɹ�", user);
	}
	
	//ע�᷽�� 	
	public ServerResponse<String> register(User user) {
		ServerResponse validResponse = this.checkValid(user.getUsername(),Const.CURRENT_USER);
		if(!validResponse.isSuccess()) {
			return ServerResponse.createByErrorMessage("�û����Ѵ���");
		}
		validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
		if(!validResponse.isSuccess()) {
			return ServerResponse.createByErrorMessage("�����Ѵ���");
		}
		
		user.setRole(Const.Role.ROLE_CUSTORMER);
		
		//�������MD5����
		user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
		
		int resultCount = userMapper.insert(user);
		if(resultCount == 0) {
			return ServerResponse.createByErrorMessage("ע��ʧ��");
		}
		return ServerResponse.createBySuccessMessage("ע��ɹ�");
	}
	
	//����У�鷽��
	public ServerResponse<String> checkValid(String str, String type) {
		int resultCount = 0;
		if(org.apache.commons.lang3.StringUtils.isNotBlank(type)){
			//��ʼУ��
			if(Const.USERNAME.equals(type)){
				resultCount  = userMapper.checkUsername(str);
				if(resultCount > 0) {
					return ServerResponse.createByErrorMessage("�û����Ѵ���");
				}
			}
			if(Const.EMAIL.equals(type)) {
				resultCount = userMapper.checkEmail(str);
				if(resultCount > 0) {
					return ServerResponse.createByErrorMessage("�����Ѵ���");
				}
			}
		}else {
			return ServerResponse.createByErrorMessage("��������");
		}
		
		return ServerResponse.createBySuccessMessage("У��ɹ�");
	}
	
}
