package com.jiebbs.service;

import org.springframework.stereotype.Service;

import com.github.pagehelper.PageInfo;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojos.Product;
import com.jiebbs.vo.ProductDetailVO;

public interface IProductService {
	
	ServerResponse saveOrUpdateProduct(Product product);
	
	ServerResponse<String> setSaleStatus(Integer productId,Integer status);
	
	ServerResponse<ProductDetailVO> manageProductDetails(Integer productId);
	
	ServerResponse<PageInfo> getProductList(Integer pageNum,Integer pageSize);
	
	ServerResponse<PageInfo> productSearch(Integer productId,String productName,Integer pageNum,Integer pageSize);
	
	ServerResponse<ProductDetailVO> userProductDetails(Integer productId);
	
	ServerResponse<PageInfo> getProductByKeyWordAndCategory(String keyWord,Integer categoryId,Integer pageNum,Integer pageSize,String orderBy);
}
