package com.jiebbs.service.Impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiebbs.common.ResponseCode;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.daos.ClassificationMapper;
import com.jiebbs.daos.ProductMapper;
import com.jiebbs.pojos.Classification;
import com.jiebbs.pojos.Product;
import com.jiebbs.service.IProductService;
import com.jiebbs.utils.DateTimeUtils;
import com.jiebbs.utils.PropertiesUtil;
import com.jiebbs.vo.ProductDetailVO;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {
	
	@Autowired
	private ProductMapper productMapper;
	@Autowired
	private ClassificationMapper classificationMapper;
	
	public ServerResponse saveOrUpdateProduct(Product product) {
		if(null != product) {
			if(StringUtils.isNotBlank(product.getSubImage())) {
				String[] subImageArray = product.getSubImage().split(",");
				if(subImageArray.length>0) {
					product.setMainImage(subImageArray[0]);
				}
			}
			if(product.getId() != null) {
				int resultCount = productMapper.updateByPrimaryKeySelective(product);
				if(resultCount>0) {
					return ServerResponse.createBySuccessMessage("更新产品信息成功！");
				}
				return ServerResponse.createByErrorMessage("产品信息更失败");
			}else {
				int resultCount = productMapper.insert(product);
				if(resultCount>0) {
					return ServerResponse.createBySuccessMessage("新增产品成功");
				}
				return ServerResponse.createByErrorMessage("新增产品失败");
			}
			
		}
		return ServerResponse.createByErrorMessage("新增或更新产品参数不正确");
	}
	
	
	public ServerResponse<String> setSaleStatus(Integer productId,Integer status){
		if(productId==null||status==null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Product product = new Product();
		product.setId(productId);
		product.setStatus(status);
		int resultCount = productMapper.updateByPrimaryKeySelective(product);
		if(resultCount>0) {
			return ServerResponse.createBySuccessMessage("更新产品状态成功");
		}
		return ServerResponse.createByErrorMessage("更新产品状态失败");
	} 
	
	
	public ServerResponse<ProductDetailVO> manageProductDetails(Integer productId){
		if(null==productId) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Product product = productMapper.selectByPrimaryKey(productId);
		if(null==product) {
			return ServerResponse.createByErrorMessage("该产品不存在");
		}
		ProductDetailVO productDetailVO =  assembleProductDetailVO(product);
		
		return ServerResponse.createBySuccess(productDetailVO);
	}
	
	private ProductDetailVO assembleProductDetailVO(Product product) {
		ProductDetailVO productDetailVO = new ProductDetailVO();
		productDetailVO.setId(product.getId());
		productDetailVO.setName(product.getName());
		productDetailVO.setSubtitle(product.getSubtitle());
		productDetailVO.setPrice(product.getPrice());
		productDetailVO.setMainImage(product.getMainImage());
		productDetailVO.setSubImages(product.getSubImage());
		productDetailVO.setCategoryId(product.getCategoryId());
		productDetailVO.setDetail(product.getDetail());
		productDetailVO.setStatus(product.getStatus());
		productDetailVO.setStock(product.getStock());
		
		productDetailVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.jiebbs.com/"));
		Classification classification = classificationMapper.selectByPrimaryKey(product.getCategoryId());
		if(classification==null) {
			//分类对象为空默认，为处于根节点
			productDetailVO.setParentCategoryId(0);
		}else {
			productDetailVO.setParentCategoryId(classification.getParentId());
		}
		
		productDetailVO.setCreateTime(DateTimeUtils.date2Str(product.getCreateTime()));
		productDetailVO.setCreateTime(DateTimeUtils.date2Str(product.getUpdateTime()));
		return productDetailVO;
	}
}
