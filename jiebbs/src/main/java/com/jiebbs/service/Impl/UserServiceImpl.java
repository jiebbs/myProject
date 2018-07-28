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
		ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
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
	
	//��������
	public ServerResponse<String> selectQuestion(String username) {
		ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
		if(validResponse.isSuccess()) {
			return ServerResponse.createByErrorMessage("���û�������");
		}
		String question = userMapper.selectQuestionByUsername(username);
		if(org.apache.commons.lang3.StringUtils.isNotBlank(question)) {
			return ServerResponse.createBySuccess(question);
		}
		return ServerResponse.createByErrorMessage("�һ���������ⲻ����");
	}
	
	//��֤�������������Ĵ�
	public ServerResponse<String> checkAnswer(String username,String question,String answer){
		int resultCount = userMapper.checkAnswer(username, question, answer);
		if(resultCount > 0) {
			String forgetToken = UUID.randomUUID().toString();
			TokenCache.setKey(TokenCache.TOKEN_PREFIX+username, forgetToken);
			return ServerResponse.createBySuccess(forgetToken);
		}
		return ServerResponse.createByErrorMessage("����𰸲���ȷ������������");
	}
	
	//�����˺ŵ�����
	public ServerResponse<String> resetPassword(String username,String newPassword,String forgetToken){
		//У��Token�Ƿ�Ϊ��
		if(!org.apache.commons.lang3.StringUtils.isNotBlank(forgetToken)) {
			return ServerResponse.createByErrorMessage("��������Token���봫��");
		}
		//��֤�û����Ƿ����
		ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
		if(validResponse.isSuccess()) {
			return ServerResponse.createByErrorMessage("���û�������");
		}
		
		String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
		//�ӱ��ػ�����ȡ�õ�token����Ϊnull������Ҫ������֤
		if(!org.apache.commons.lang3.StringUtils.isNotBlank(token)) {
			return ServerResponse.createByErrorMessage("token��Ч���߹���");
		}
		if(org.apache.commons.lang3.StringUtils.equals(forgetToken, token)) {
			String md5newPassword = MD5Util.MD5EncodeUtf8(newPassword);
			int rowCount = userMapper.resetPasswordByUsername(username, md5newPassword);
			if(rowCount<=0) {
				return ServerResponse.createBySuccessMessage("�������ʧ��");
			}
		}else {
			return ServerResponse.createByErrorMessage("Token���������»�ȡ���������T");
		}
		return ServerResponse.createByErrorMessage("��������ɹ�");
	}
	
	//��½״̬�¸�������
	public ServerResponse<String> resetPassword(String newPassword,String oldPassword,User user){
		//��ֹ����ԽȨ��һ��ҪУ������û��ľ�����������û��ģ���Ϊ���ǻ��ѯһ��count(1),���ָ��id,��ô�������true,count>0
		int resultCount = userMapper.checkPassword(user.getId(),MD5Util.MD5EncodeUtf8(oldPassword));
		if(resultCount==0) {
			return ServerResponse.createByErrorMessage("���������");
		}
		user.setPassword(MD5Util.MD5EncodeUtf8(newPassword));
		int updateCount = userMapper.updateByPrimaryKeySelective(user);
		if(updateCount > 0) {
			return ServerResponse.createBySuccessMessage("������³ɹ�");
		}
		return ServerResponse.createByErrorMessage("�������ʧ��");
	}
	
	//�����û���Ϣ
	public ServerResponse<User> update_Information(User user){
		//���µ�ʱ��Username�ǲ��ܱ����µ�
		//ͬ��EmailҲҪ����У�飬У���µ�Email�ǲ����Ѿ����ڣ����Ҵ��ڵ�email�����ͬ�Ļ����������ǵ�ǰ�û���
		int resultCount = userMapper.checkEmailByUserId(user.getId(), user.getEmail());
		if(resultCount>0 ) {
			return ServerResponse.createByErrorMessage("�������ѱ�ռ�ã�����������");
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
			return ServerResponse.createBySuccess("�û���Ϣ���³ɹ�",updateUser);
		}
		return ServerResponse.createByErrorMessage("�û���Ϣ����ʧ��");
	}
	
	public ServerResponse<User> getDetailInformation(Integer userId){
		User user = userMapper.selectByPrimaryKey(userId);
		if(null==user) {
			return ServerResponse.createByErrorMessage("�Ҳ�����ǰ�û�");
		}
		//�ÿ�����
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
