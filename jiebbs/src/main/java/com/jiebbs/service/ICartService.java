package com.jiebbs.service;

import com.jiebbs.common.ServerResponse;
import com.jiebbs.vo.CartVO;

public interface ICartService {

	ServerResponse<CartVO> addProduct2Cart(Integer userId,Integer count,Integer productId);
	
	ServerResponse<CartVO> updateProduct2Cart(Integer userId,Integer count,Integer productId);
	
	ServerResponse<CartVO> deleteProductFromCart(Integer userId,String productIds);
	
	ServerResponse<CartVO> selectProductFromCart(Integer userId);
	
	ServerResponse<CartVO> selectOrUnselectProductFromCart(Integer userId,Integer checked,Integer productId);
	
	ServerResponse<Integer> getCartProductCount(Integer userId);
}
