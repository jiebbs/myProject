package com.jiebbs.service;

import org.springframework.stereotype.Service;

import com.jiebbs.common.ServerResponse;
import com.jiebbs.pojos.Product;
import com.jiebbs.vo.ProductDetailVO;

public interface IProductService {
	
	ServerResponse saveOrUpdateProduct(Product product);
	
	ServerResponse<String> setSaleStatus(Integer productId,Integer status);
	
	ServerResponse<ProductDetailVO> manageProductDetails(Integer productId);
}
