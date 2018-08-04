package com.jiebbs.service.Impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.jiebbs.common.Const;
import com.jiebbs.common.ResponseCode;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.daos.ProductMapper;
import com.jiebbs.daos.Shopping_cartMapper;
import com.jiebbs.pojos.Product;
import com.jiebbs.pojos.Shopping_cart;
import com.jiebbs.service.ICartService;
import com.jiebbs.utils.BigDecimalUtils;
import com.jiebbs.utils.PropertiesUtil;
import com.jiebbs.vo.CartProductVO;
import com.jiebbs.vo.CartVO;

@Service("iCartService")
public class CartServiceImpl implements ICartService {
	
	@Autowired
	private Shopping_cartMapper shopping_cartMapper;
	@Autowired
	private ProductMapper productMapper;
	
	public ServerResponse<CartVO> addProduct2Cart(Integer userId,Integer count,Integer productId) {
		if(count==null||productId==null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Shopping_cart cart = shopping_cartMapper.selectByUserIdAndProductId(userId, productId);
		if(cart == null) {
			Shopping_cart cartItem = new Shopping_cart();
			cartItem.setQuantity(count);
			cartItem.setProductId(productId);
			cartItem.setUserId(userId);
			//新加入购物车的商品默认都是勾选状态
			cartItem.setChecked(Const.CartCheckStatus.CHECKED);
			shopping_cartMapper.insert(cartItem);
		}else {
			//若为false则说明这个产品已经在购物车里
			//则商品数量相加
			count = cart.getQuantity()+count;
			cart.setQuantity(count);
			shopping_cartMapper.updateByPrimaryKeySelective(cart);
		}
		CartVO cartVO = this.getCartVoIimit(userId);
		return ServerResponse.createBySuccess(cartVO);
	}
	
	private CartVO getCartVoIimit(Integer userId) {
		CartVO cartVO = new CartVO();
		List<Shopping_cart> cartList = shopping_cartMapper.selectCartByUserId(userId);
		List<CartProductVO> cartProductVOList = Lists.newArrayList();
		//商业计算中必须使用Bigcimal 的 Sting 构造器
		BigDecimal cartProductTotal  = new BigDecimal("0");
		
		if(CollectionUtils.isNotEmpty(cartList)){
			for(Shopping_cart temp:cartList) {
				CartProductVO cartProductVO = new CartProductVO();
				cartProductVO.setId(temp.getId());
				cartProductVO.setUserId(userId);
				cartProductVO.setProductId(temp.getProductId());
				
				Product product = productMapper.selectByPrimaryKey(temp.getProductId());
				if(null!=product) {
					cartProductVO.setProductMainImage(product.getMainImage());
					cartProductVO.setProductName(product.getName());
					cartProductVO.setProductPrice(product.getPrice());
					cartProductVO.setProductSubtitle(product.getSubtitle());
					cartProductVO.setProductStatus(product.getStatus());
					cartProductVO.setProductStock(product.getStock());
					
					//判断库存
					int buyCountLimt = 0;
					if(product.getStock()>=temp.getQuantity()) {
						buyCountLimt = temp.getQuantity();
						cartProductVO.setLimitQuantity(Const.CartCheckStatus.LIMIT_NUM_SUCCESS);
					}else {
						buyCountLimt = product.getStock();
						cartProductVO.setLimitQuantity(Const.CartCheckStatus.LIMIT_NUM_FAIL);
						//购物车中更新有效库存
						Shopping_cart cartForQuantity = new Shopping_cart();
						cartForQuantity.setId(temp.getId());
						cartForQuantity.setQuantity(buyCountLimt);
						shopping_cartMapper.updateByPrimaryKeySelective(cartForQuantity);
					}
					cartProductVO.setQuantity(buyCountLimt);
					
					//计算商品总价，当前购物车该商品的总价(产品单价*产品数量)
					cartProductVO.setProductTotalPrice(BigDecimalUtils.multiplication(product.getPrice().doubleValue(),
																						cartProductVO.getQuantity().doubleValue()));
					//设置勾选状态
					cartProductVO.setProductChecked(temp.getChecked());
					
					
				}
				//如果产品已经被勾选,增加到整个的购物车单价中
				if(temp.getChecked()==Const.CartCheckStatus.CHECKED){
					cartProductTotal = BigDecimalUtils.add(cartProductTotal.doubleValue(), 
															cartProductVO.getProductTotalPrice().doubleValue());
				}
				cartProductVOList.add(cartProductVO);
			}
		}
		cartVO.setCartTotalPrice(cartProductTotal);
		cartVO.setCartProductVOList(cartProductVOList);
		cartVO.setAllChecked(this.getAllCheckedStatus(userId));
		cartVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
		return cartVO;
	}
	
	private boolean getAllCheckedStatus(Integer userId) {
		if(userId==null) {
			return false;
		}
		return shopping_cartMapper.selectProductCheckedStatusByUserId(userId) == 0;
	}
}
