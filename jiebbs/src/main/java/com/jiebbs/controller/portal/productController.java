package com.jiebbs.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.service.IProductService;
import com.jiebbs.vo.ProductDetailVO;

@Controller
@RequestMapping(value="/product/")
public class productController {
	
	@Autowired
	private IProductService iProductService;
	
	@RequestMapping(value="user_get_product_detail.do",method=RequestMethod.GET)
	@ResponseBody()
	public ServerResponse<ProductDetailVO> userGetProductDetail(Integer productId){
		return iProductService.userProductDetails(productId);
	}
	
	@RequestMapping(value="user_product_list.do",method=RequestMethod.GET)
	@ResponseBody()
	public ServerResponse<PageInfo> userProductList(@RequestParam(value="keyWord",required=false)String keyWord,
													@RequestParam(value="categoryId",required=false)Integer categoryId,
													@RequestParam(value="pageNum",defaultValue="1")Integer pageNum,
													@RequestParam(value="pageSize",defaultValue="10")Integer pageSize,
													@RequestParam(value="orderBy",defaultValue="")String orderBy){
		
		return iProductService.getProductByKeyWordAndCategory(keyWord, categoryId, pageNum, pageSize, orderBy);
	}
}
