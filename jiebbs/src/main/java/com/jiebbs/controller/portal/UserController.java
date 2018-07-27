package com.jiebbs.controller.portal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jiebbs.common.Const;
import com.jiebbs.common.ResponseCode;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojos.User;
import com.jiebbs.service.IUserService;

/**
 *�û������� 
 */
@Controller
@RequestMapping("/user/")
public class UserController {
	
	@Autowired
	private IUserService iUserService;
	
	/**
	 * 
	 * @param username 
	 * @param password
	 * @param session
	 * @return
	 */
	@RequestMapping(value="login.do",method=RequestMethod.POST)
	@ResponseBody//�ñ�ǩ����������ֵ�Զ����л�ΪJson
	public ServerResponse<User> login(String username,String password,HttpSession session) {
		//service
		ServerResponse<User> response = iUserService.login(username, password);
		if(response.isSuccess()) {
			session.setAttribute(Const.CURRENT_USER, response.getData());
		}
		return response;
	}
	/**
	 * �ǳ��ӿ�
	 * @param session
	 * @return
	 */
	@RequestMapping(value="logout.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> logout(HttpSession session){
		session.removeAttribute(Const.CURRENT_USER);
		return ServerResponse.createBySuccess();
	} 
	
	@RequestMapping(value="register.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> register(User user){
		return iUserService.register(user);
	}
	
	
	@RequestMapping(value="valid.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> checkValid(String str,String type){
		return iUserService.checkValid(str, type);
	}
	 
	@RequestMapping(value="get_user_info.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> getUserInfo(HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(null == user) {
			return ServerResponse.createByErrorMessage("�û�δ��¼���޷���ȡ�û���Ϣ");
		}else {
			return ServerResponse.createBySuccess(user);
		}
	}
	
	@RequestMapping(value="get_forget_question.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> getForgetQuestion(String username){
		return iUserService.selectQuestion(username);
	}
	@RequestMapping(value="forget_check_anwser.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
		return iUserService.checkAnswer(username, question, answer);
	}
	@RequestMapping(value="forget_reset_Password.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetResetPassword(String username,String newPassword,String forgetToken){
		return iUserService.resetPassword(username, newPassword, forgetToken);
	}
	@RequestMapping(value="reset_Password.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> resetPassword(HttpSession session,String oldPassword,String newPassword){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorMessage("�û�δ��¼");
		}
		return iUserService.resetPassword(newPassword, oldPassword, user);
	}
	
	@RequestMapping(value="update_Information.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> update_Information(HttpSession session,User user){
		User current_user = (User) session.getAttribute(Const.CURRENT_USER);
		if(current_user == null) {
			return ServerResponse.createByErrorMessage("�û�δ��¼");
		}
		user.setId(current_user.getId());
		user.setUsername(current_user.getUsername());
		ServerResponse<User> response = iUserService.update_Information(user);
		if(response.isSuccess()) {
			session.setAttribute(Const.CURRENT_USER, response.getData());
		}
		return response;
	}
	
	@RequestMapping(value="get_detail_Information.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> get_detail_Information(HttpSession session){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), "�û���Ҫ����ǿ�Ƶ�¼��status=10");
		}
		
		return iUserService.getDetailInformation(user.getId());
	}
}
