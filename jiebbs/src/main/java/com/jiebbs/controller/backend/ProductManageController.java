package com.jiebbs.controller.backend;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Maps;
import com.jiebbs.common.Const;
import com.jiebbs.common.ResponseCode;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojos.Product;
import com.jiebbs.pojos.User;
import com.jiebbs.service.IFileService;
import com.jiebbs.service.IProductService;
import com.jiebbs.service.IUserService;
import com.jiebbs.utils.PropertiesUtil;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {
	@Autowired
	private IUserService iUserService;
	@Autowired
	private IProductService iProductService;
	@Autowired
	private IFileService iFileService;
	
	@RequestMapping(value="saveProduct.do",method=RequestMethod.POST)
	@ResponseBody()
	public ServerResponse productSave(HttpSession session,Product product) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), "用户未登录，请登录");
		}
		if(iUserService.checkAdminRole(user).isSuccess()) {
			return iProductService.saveOrUpdateProduct(product);
		}
		return ServerResponse.createByErrorMessage("你不是管理员，无权限进行该操作！");
	} 
	
	@RequestMapping(value="set_sale_status.do",method=RequestMethod.POST)
	@ResponseBody()
	public ServerResponse setSaleStatus(HttpSession session,Integer productId,Integer status) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), "用户未登录，请登录");
		}
		if(iUserService.checkAdminRole(user).isSuccess()) {
			return iProductService.setSaleStatus(productId,status);
		}
		return ServerResponse.createByErrorMessage("你不是管理员，无权限进行该操作！");
	} 
	
	@RequestMapping(value="product_detail.do",method=RequestMethod.POST)
	@ResponseBody()
	public ServerResponse getProductDetail(HttpSession session,Integer productId) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), "用户未登录，请登录");
		}
		if(iUserService.checkAdminRole(user).isSuccess()) {
			return iProductService.manageProductDetails(productId);
		}
		return ServerResponse.createByErrorMessage("你不是管理员，无权限进行该操作！");
	} 
	
	@RequestMapping(value="product_list.do",method=RequestMethod.POST)
	@ResponseBody()
	public ServerResponse getProductList(HttpSession session,@RequestParam(value="pageNum",defaultValue="1")Integer pageNum,@RequestParam(value="pageSize",defaultValue="10")Integer pageSize) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), "用户未登录，请登录");
		}
		if(iUserService.checkAdminRole(user).isSuccess()) {
			return iProductService.getProductList(pageNum, pageSize);
		}
		return ServerResponse.createByErrorMessage("你不是管理员，无权限进行该操作！");
	} 
	
	@RequestMapping(value="product_search.do",method=RequestMethod.POST)
	@ResponseBody()
	public ServerResponse ProductSearch(HttpSession session,Integer productId,String productName,@RequestParam(value="pageNum",defaultValue="1")Integer pageNum,@RequestParam(value="pageSize",defaultValue="10")Integer pageSize) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), "用户未登录，请登录");
		}
		if(iUserService.checkAdminRole(user).isSuccess()) {
			return iProductService.productSearch(productId, productName, pageNum, pageSize);
		}
		return ServerResponse.createByErrorMessage("你不是管理员，无权限进行该操作！");
	} 
	
	@RequestMapping(value="file_upload.do",method=RequestMethod.POST)
	@ResponseBody()
	public ServerResponse fileUpload(HttpSession session,@RequestParam(value="upload_file",required=false)MultipartFile file,HttpServletRequest request) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGGING.getCode(), "用户未登录，请登录");
		}
		if(iUserService.checkAdminRole(user).isSuccess()) {
			String path = request.getServletContext().getRealPath("upload");
			String targetFileName = iFileService.upload(file, path);
			String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
			Map fileMap = Maps.newHashMap();
			fileMap.put("uri",targetFileName);
			fileMap.put("url",url);
			return ServerResponse.createBySuccess(fileMap);
		}
		return ServerResponse.createByErrorMessage("你不是管理员，无权限进行该操作！");
	} 
	
	@RequestMapping(value="richtext_img_upload.do",method=RequestMethod.POST)
	@ResponseBody()
	//富文本上传需要修改一下响应头
	public Map richtextImgUpload(HttpSession session,@RequestParam(value="upload_file",required=false)MultipartFile file,HttpServletRequest request,HttpServletResponse response) {
		Map resultMap = Maps.newHashMap();
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			resultMap.put("success", false);
			resultMap.put("msg", "请登录管理员账号");
			return resultMap;
		}
		//富文本中对于返回值有自己的要求，我们使用的是simditor所以按照simditor的要求进行返回
		if(iUserService.checkAdminRole(user).isSuccess()) {
			String path = request.getServletContext().getRealPath("upload");
			String targetFileName = iFileService.upload(file, path);
			if(StringUtils.isNotBlank(targetFileName)) {
				resultMap.put("success", false);
				resultMap.put("msg", "上传文件失败");
				return resultMap;
			}
			String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
			resultMap.put("success", true);
			resultMap.put("msg", "上传成功");
			resultMap.put("file_path",url);
			//与前端的约定，某些前端插件对后端返回都有要求
			response.addHeader("Access-Control-Allow-Headers","X-File-Name");
			return resultMap;
		}
		resultMap.put("success", false);
		resultMap.put("msg", "无权限操作");
		return resultMap;
	} 
}
