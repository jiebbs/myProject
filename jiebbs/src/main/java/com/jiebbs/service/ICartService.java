package com.jiebbs.service;

import com.jiebbs.common.ServerResponse;
import com.jiebbs.vo.CartVO;

public interface ICartService {

	ServerResponse<CartVO> addProduct2Cart(Integer userId,Integer count,Integer productId);
}
