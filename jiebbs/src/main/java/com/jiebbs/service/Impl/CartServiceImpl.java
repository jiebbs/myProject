package com.jiebbs.service.Impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Splitter;
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

import net.sf.jsqlparser.schema.Server;

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
		if(productMapper.selectByPrimaryKey(productId)==null) {
			return ServerResponse.createByErrorMessage("������Ʒid�������Ʒ������");
		}
		Shopping_cart cart = shopping_cartMapper.selectByUserIdAndProductId(userId, productId);
		if(cart == null) {
			Shopping_cart cartItem = new Shopping_cart();
			cartItem.setQuantity(count);
			cartItem.setProductId(productId);
			cartItem.setUserId(userId);
			//�¼��빺�ﳵ����ƷĬ�϶��ǹ�ѡ״̬
			cartItem.setChecked(Const.CartCheckStatus.CHECKED);
			shopping_cartMapper.insert(cartItem);
		}else {
			//��Ϊfalse��˵�������Ʒ�Ѿ��ڹ��ﳵ��
			//����Ʒ�������
			count = cart.getQuantity()+count;
			cart.setQuantity(count);
			shopping_cartMapper.updateByPrimaryKeySelective(cart);
		}
		return ServerResponse.createBySuccess(this.getCartVoIimit(userId));
	}
	
	public ServerResponse<CartVO> updateProduct2Cart(Integer userId,Integer count,Integer productId) {
		if(count==null||productId==null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Shopping_cart cart = shopping_cartMapper.selectByUserIdAndProductId(userId, productId);
		if(cart != null) {
			cart.setQuantity(count);
		}
		shopping_cartMapper.updateByPrimaryKeySelective(cart);
		return ServerResponse.createBySuccess(this.getCartVoIimit(userId));
	}
	
	public ServerResponse<CartVO> deleteProductFromCart(Integer userId,String productIds){
		if(StringUtils.isBlank(productIds)) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		List<String> productList = Splitter.on(",").splitToList(productIds);
		if(CollectionUtils.isEmpty(productList)){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		shopping_cartMapper.delectByUserIdAndProductIds(userId, productList);
		return ServerResponse.createBySuccess(this.getCartVoIimit(userId));
	}
	
	public ServerResponse<CartVO> selectProductFromCart(Integer userId){
		if(null==userId){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		return ServerResponse.createBySuccess(this.getCartVoIimit(userId));
	}
	
	public ServerResponse<CartVO> selectOrUnselectProductFromCart(Integer userId,Integer productId,Integer checked){
		shopping_cartMapper.checkOrUncheckProduct(userId,productId,checked);
		return this.selectProductFromCart(userId);
	}
	
	public ServerResponse<Integer> getCartProductCount(Integer userId){
		if(userId == null) {
			return ServerResponse.createBySuccess(0);
		}
		return ServerResponse.createBySuccess(shopping_cartMapper.selectCartProductCount(userId));
	}
	
	
	private CartVO getCartVoIimit(Integer userId) {
		CartVO cartVO = new CartVO();
		List<Shopping_cart> cartList = shopping_cartMapper.selectCartByUserId(userId);
		List<CartProductVO> cartProductVOList = Lists.newArrayList();
		//��ҵ�����б���ʹ��Bigcimal �� Sting ������
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
					
					//�жϿ��
					int buyCountLimt = 0;
					if(product.getStock()>=temp.getQuantity()) {
						buyCountLimt = temp.getQuantity();
						cartProductVO.setLimitQuantity(Const.CartCheckStatus.LIMIT_NUM_SUCCESS);
					}else {
						buyCountLimt = product.getStock();
						cartProductVO.setLimitQuantity(Const.CartCheckStatus.LIMIT_NUM_FAIL);
						//���ﳵ�и�����Ч���
						Shopping_cart cartForQuantity = new Shopping_cart();
						cartForQuantity.setId(temp.getId());
						cartForQuantity.setQuantity(buyCountLimt);
						shopping_cartMapper.updateByPrimaryKeySelective(cartForQuantity);
					}
					cartProductVO.setQuantity(buyCountLimt);
					
					//������Ʒ�ܼۣ���ǰ���ﳵ����Ʒ���ܼ�(��Ʒ����*��Ʒ����)
					cartProductVO.setProductTotalPrice(BigDecimalUtils.multiplication(product.getPrice().doubleValue(),
																						cartProductVO.getQuantity().doubleValue()));
					//���ù�ѡ״̬
					cartProductVO.setProductChecked(temp.getChecked());
					
					
				}
				//�����Ʒ�Ѿ�����ѡ,���ӵ������Ĺ��ﳵ������
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
