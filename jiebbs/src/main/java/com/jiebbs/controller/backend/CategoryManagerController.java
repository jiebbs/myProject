package com.jiebbs.controller.backend;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jiebbs.common.Const;
import com.jiebbs.common.ResponseCode;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojos.Classification;
import com.jiebbs.pojos.User;
import com.jiebbs.service.ICategoryService;
import com.jiebbs.service.IUserService;

@Controller
@RequestMapping(value="/manage/Category")
public class CategoryManagerController {
	
	@Autowired
	private IUserService iUserService;
	@Autowired
	private ICategoryService iCategoryService;
	
	
	@RequestMapping(value="addCategory.do",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<String> addCategory(HttpSession session,String CategoryName,@RequestParam(value="parentId",defaultValue="0")Integer parentId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user==null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(),"用户未登陆，请强制用户登陆");
		}
		ServerResponse<String> response = iUserService.checkAdminRole(user);
		if(response.isSuccess()) {
			
			return iCategoryService.addCategory(CategoryName,parentId);
		}
		return ServerResponse.createByErrorMessage("此账号不是管理员账号，无权进行该操作");
	}
	
	@RequestMapping(value="set_CategoryName.do",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<String> setCategoryName(HttpSession session,String newCategoryName,Integer CategoryId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user==null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(),"用户为登陆，请强制用户登陆");
		}
		ServerResponse<String> response = iUserService.checkAdminRole(user);
		if(response.isSuccess()) {
			return iCategoryService.updataCategoryName(newCategoryName,CategoryId);
		}
		return ServerResponse.createByErrorMessage("此账号不是管理员账号，无权进行该操作");
	}
	@RequestMapping(value="get_children_parallel_category.do",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<List<Classification>> getChildrenParallelCategory(HttpSession session,@RequestParam(value="categoryId",defaultValue="0")Integer categoryId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user==null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(),"用户为登陆，请强制用户登陆");
		}
		ServerResponse<String> response = iUserService.checkAdminRole(user);
		if(response.isSuccess()) {
			return iCategoryService.getChildrenParallelCategory(categoryId);
		}
	
		return ServerResponse.createByErrorMessage("此账号不是管理员账号，无权进行该操作");
	}
	@RequestMapping(value="get_deep_category.do",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<List<Integer>> getCategoryAndDeepChildrenCategory(HttpSession session,@RequestParam(value="categoryId",defaultValue="0")Integer categoryId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user==null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(),"用户为登陆，请强制用户登陆");
		}
		ServerResponse<String> response = iUserService.checkAdminRole(user);
		if(response.isSuccess()) {
			
			return iCategoryService.selectCategoryAndChildrenById(categoryId);
		}
		
		return ServerResponse.createByErrorMessage("此账号不是管理员账号，无权进行该操作");
	}
}

