package com.jiebbs.service.Impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jiebbs.common.Const;
import com.jiebbs.common.ServerResponse;
import com.jiebbs.daos.OrderMapper;
import com.jiebbs.daos.Order_itemMapper;
import com.jiebbs.daos.Pay_infoMapper;
import com.jiebbs.daos.ProductMapper;
import com.jiebbs.daos.ShippingMapper;
import com.jiebbs.daos.Shopping_cartMapper;
import com.jiebbs.pojos.Order;
import com.jiebbs.pojos.Order_item;
import com.jiebbs.pojos.Pay_info;
import com.jiebbs.pojos.Product;
import com.jiebbs.pojos.Shipping;
import com.jiebbs.pojos.Shopping_cart;
import com.jiebbs.service.IOrderService;
import com.jiebbs.utils.BigDecimalUtils;
import com.jiebbs.utils.DateTimeUtils;
import com.jiebbs.utils.FTPUtils;
import com.jiebbs.utils.PropertiesUtil;
import com.jiebbs.vo.OrderItemVO;
import com.jiebbs.vo.OrderProductVO;
import com.jiebbs.vo.OrderVO;
import com.jiebbs.vo.ShippingVO;

@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {
	private static Log log = LogFactory.getLog(OrderServiceImpl.class);

	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private Order_itemMapper orderItemMapper;
	@Autowired
	private Pay_infoMapper pay_infoMapper;
	@Autowired
	private Shopping_cartMapper shopping_cartMapper;
	@Autowired
	private ProductMapper productMapper;
	@Autowired
	private ShippingMapper shippingMapper;
	
	// 支付宝当面付2.0服务
	private static AlipayTradeService tradeService;

	public ServerResponse pay(Integer userId,Long orderNum,String path) {
		Map<String,String> resultMap = Maps.newHashMap();
		Order order = orderMapper.selectByUserIdAndOrderNum(userId, orderNum);
		if(null==order) {
			return ServerResponse.createByErrorMessage("该订单不存在");
		}
		resultMap.put("orderNum",String.valueOf(order.getOrderNo()));



		// (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
		// 需保证商户系统端不能重复，建议通过数据库sequence生成，
		String outTradeNo = String.valueOf(order.getOrderNo());

		// (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
		String subject = new StringBuilder().append("jiebbs商城支付,订单号为：").append(outTradeNo).toString();

		// (必填) 订单总金额，单位为元，不能超过1亿元
		// 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
		String totalAmount = order.getPayment().toString();

		// (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
		// 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
		String undiscountableAmount = "0";

		// 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
		// 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
		String sellerId = "";

		// 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
		String body = new StringBuilder().append("订单:").append(outTradeNo).append("购买商品共").append(totalAmount).append("元").toString();

		// 商户操作员编号，添加此参数可以为商户操作员做销售统计
		String operatorId = "test_operator_id";

		// (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
		String storeId = "test_store_id";

		// 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
		ExtendParams extendParams = new ExtendParams();
		extendParams.setSysServiceProviderId("2088100200300400500");

		// 支付超时，定义为120分钟
		String timeoutExpress = "120m";

		// 商品明细列表，需填写购买商品详细信息，
		List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
		List<Order_item> orderItemList = orderItemMapper.selectByOrderNumAndUserId(userId, orderNum);

		for(Order_item item:orderItemList) {
			// 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
			GoodsDetail goods = GoodsDetail.newInstance(item.getId().toString(), item.getProductName(), 
					BigDecimalUtils.multiplication(item.getCurrentUnitPrice().doubleValue(), new Double(100).doubleValue()).longValue(),
					item.getQuatity());
			// 创建好一个商品后添加至商品明细列表  
			goodsDetailList.add(goods);
		}



		// 创建扫码支付请求builder，设置请求参数
		AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
				.setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
				.setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
				.setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
				.setTimeoutExpress(timeoutExpress)
				.setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
				.setGoodsDetailList(goodsDetailList);

		/** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
		 *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
		 */
		Configs.init("zfbinfo.properties");

		/** 使用Configs提供的默认参数
		 *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
		 */

		tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();



		AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
		switch (result.getTradeStatus()) {
		case SUCCESS:
			log.info("支付宝预下单成功: )");

			AlipayTradePrecreateResponse response = result.getResponse();
			dumpResponse(response);

			// 需要修改为运行机器上的路径
			File folder = new File(path);
			if(!folder.exists()) {
				folder.setWritable(true);
				folder.mkdirs();
			}
			
			
			String qrPath = String.format(path+"/qr-%s.png",
					response.getOutTradeNo());
			String qrFileName = String.format("qr-%s.png",response.getOutTradeNo());
			ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
			
			File targetFile = new File(path,qrFileName);
			try {
				FTPUtils.uploadFile(Lists.newArrayList(targetFile));
			} catch (IOException e) {
				log.error("上传文件失败！",e);
			}
			
			log.info("qrPath:" + qrPath);
			
			String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFile.getName();
			resultMap.put("qrUrl", qrUrl);
			return ServerResponse.createBySuccess(resultMap);

		case FAILED:
			log.error("支付宝预下单失败!!!");
			return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");

		case UNKNOWN:
			log.error("系统异常，预下单状态未知!!!");
			return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

		default:
			log.error("不支持的交易状态，交易返回异常!!!");
			return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
		}
	}
	
	// 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                    response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }
    
    public ServerResponse aliCallBack(Map<String,String> params) {
    	Long orderNo = Long.parseLong(params.get("out_trade_no"));
    	String tradeNo = params.get("trade_no");
    	String tradeStatus = params.get("trade_status");
    	Order order = orderMapper.selectByOrderNum(orderNo);
    	if(null==order) {
    		return ServerResponse.createByErrorMessage("不是本商城订单，回调忽略");
    	}
    	if(order.getStatus()>=Const.TradeStatus.PAID.getCode()) {
    		return ServerResponse.createBySuccess("支付宝重复调用");
    	}
    	if(tradeStatus.equals(Const.AlipayCallBack.TRADE_STATUS_TRADE_SUCCESS)){
    		order.setPaymentTime(DateTimeUtils.str2Date(params.get("gmt_payment")));
    		order.setStatus(Const.TradeStatus.PAID.getCode());
    		orderMapper.updateByPrimaryKeySelective(order);
    	}
    	Pay_info payInfo = new Pay_info();
    	payInfo.setUserId(order.getUserId());
    	payInfo.setOrderNo(order.getOrderNo());
    	payInfo.setPayPlatform(Const.PayPlatform.ALIPAY.getCode());
    	payInfo.setPlatformNumber(tradeNo);
    	payInfo.setPlatformStatus(tradeStatus);
    
    	pay_infoMapper.insert(payInfo);
    	return ServerResponse.createBySuccess();
    }
    
    
    public ServerResponse qureyOrderPayStatus(Integer userId,Long orderNum) {
    	Order order = orderMapper.selectByOrderNum(orderNum);
    	if(null==order) {
    		return ServerResponse.createByErrorMessage("不是本商城订单，回调忽略");
    	}
    	if(order.getStatus()>=Const.TradeStatus.PAID.getCode()) {
    		return ServerResponse.createBySuccess();
    	}
    	
    	return ServerResponse.createByError();
    }
    
    public ServerResponse createOrder(Integer userId,Integer shippingId){
    	//从购物车中获取数据
    	List<Shopping_cart> checkedCartList = shopping_cartMapper.selectCheckedCartByUserId(userId);    	
    	
    	ServerResponse serverResponse = this.getCartOrderItem(userId, checkedCartList);
    	if(!serverResponse.isSuccess()) {
    		return serverResponse;
    	}
    	
    	//计算订单总价
    	List<Order_item> orderItemList =(List<Order_item>)serverResponse.getData();
    	BigDecimal payment = this.totalPriceOfOrderItem(orderItemList);
    	
    	//生成订单
    	Order order = this.assembleOrder(userId, shippingId, payment);
    	if(null==order) {
    		return ServerResponse.createByErrorMessage("订单生成异常");
    	}
    	if(CollectionUtils.isEmpty(orderItemList)) {
    		return ServerResponse.createByErrorMessage("购物车为空");
    	}
    	for(Order_item item:orderItemList) {
    		item.setOrderNo(order.getOrderNo());
    	}
    	//mybatis批量插入
    	orderItemMapper.benchInsert(orderItemList);
    	
    	//生成成功，我们要减少我们的库存
    	this.reduceProductStock(orderItemList);
    	
    	//清空我们的购物车
    	this.cleanCart(checkedCartList);
    	
    	//返回前端数据
    	OrderVO orderVO = this.assembleOrderVO(order, orderItemList);
    	
    	return ServerResponse.createBySuccess(orderVO);
    }
    
    public ServerResponse cancalOrder(Integer userId,Long orderNum) {
    	Order order = orderMapper.selectByUserIdAndOrderNum(userId,orderNum);
    	if(null==order) {
    		return ServerResponse.createByErrorMessage("该用户此订单不存在");
    	}
    	if(order.getStatus()!=Const.TradeStatus.NO_PAY.getCode()){
    		return ServerResponse.createByErrorMessage("该订单已付款，无法取消");
    	}
    	Order newOrder = new Order();
    	newOrder.setUserId(order.getId());
    	newOrder.setStatus(Const.TradeStatus.CANCALED.getCode());
    	int resultCount = orderMapper.updateByPrimaryKeySelective(newOrder);
    	if(resultCount>0) {
    		return ServerResponse.createBySuccessMessage("订单取消成功");
    	}else {
    		return ServerResponse.createByErrorMessage("订单取消异常");
    	}
    }
    
    
    public ServerResponse getOrderCartProduct(Integer userId) {
    	OrderProductVO orderProductVO = new OrderProductVO();
    	
    	List<Shopping_cart> cartList = shopping_cartMapper.selectCheckedCartByUserId(userId);
    	ServerResponse serverResponse = this.getCartOrderItem(userId, cartList);
    	if(!serverResponse.isSuccess()) {
    		return serverResponse;
    	}
    	List<Order_item> orderItemList = (List<Order_item>) serverResponse.getData();
    	List<OrderItemVO> orderItemVOList = Lists.newArrayList();
    	BigDecimal payment = new BigDecimal("0");
    	for(Order_item item:orderItemList) {
    		payment = BigDecimalUtils.add(payment.doubleValue(),item.getTotalPrice().doubleValue());
    		orderItemVOList.add(this.assembleOrderItemVO(item));
    	}
    	orderProductVO.setOrderItemVOList(orderItemVOList);
    	orderProductVO.setProductTotalPrice(payment);
    	orderProductVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
    	
    	return ServerResponse.createBySuccess(orderProductVO);
    	
    }
    
    public ServerResponse<OrderVO> getOrderDetail(Integer userId,Long orderNum){
    	Order order = orderMapper.selectByUserIdAndOrderNum(userId, orderNum);
    	if(null!= order) {
    		List<Order_item> orderItemList = orderItemMapper.selectByOrderNumAndUserId(userId, orderNum);
    		OrderVO orderVO=this.assembleOrderVO(order, orderItemList);
    		return ServerResponse.createBySuccess(orderVO);
    	}
    	
    	return ServerResponse.createByErrorMessage("该用户没有该订单");
    	
    }
    
    
    public ServerResponse<PageInfo> getOrderlist(Integer userId,Integer pageNum,Integer pageSize) {
    	PageHelper.startPage(pageNum,pageSize);
    	List<Order> orderList = orderMapper.selectOrderByUserId(userId);
    	PageInfo pageInfo = new PageInfo(orderList);
    	pageInfo.setList(assembleOrderVOList(userId, orderList));
    	return ServerResponse.createBySuccess(pageInfo);
    }
    
    private List<OrderVO> assembleOrderVOList(Integer userId,List<Order> orderList){
    	List<OrderVO> orderVOList = Lists.newArrayList();
    	
    	for(Order order:orderList) {
    		Long orderNum = order.getOrderNo();
    		List<Order_item> orderItemList = Lists.newArrayList();
    		if(userId==null) {
    			orderItemList = orderItemMapper.selectAllByOrderNum(orderNum);
    			
    		}else {
    			
    			orderItemList = orderItemMapper.selectByOrderNumAndUserId(userId, orderNum);
    		}
    		
    		orderVOList.add(this.assembleOrderVO(order, orderItemList));
    	}
    	return orderVOList;
    }
    
    
    
    
    //backend
    public ServerResponse<PageInfo> manageList(Integer pageNum,Integer pageSize){
    	PageHelper.startPage(pageNum,pageSize);
    	List<Order> orderList = orderMapper.selectAll();
    	List<OrderVO> orderVOList = this.assembleOrderVOList(null, orderList);
    	PageInfo pageResult = new PageInfo(orderList);
    	pageResult.setList(orderVOList);
    	return ServerResponse.createBySuccess(pageResult);
    }
    
    
    public ServerResponse<OrderVO> manageDetail(Long orderNo) {
    	
    	Order order = orderMapper.selectByOrderNum(orderNo);
    	List<Order_item> orderItemList = orderItemMapper.selectAllByOrderNum(orderNo); 
    	if(null==order) {
    		return ServerResponse.createByErrorMessage("无该订单");
    	}
    	OrderVO orderVO = this.assembleOrderVO(order, orderItemList);
    	return ServerResponse.createBySuccess(orderVO);
    }
    
    public ServerResponse<PageInfo> manageSearch(Long orderNo,Integer pageNum,Integer pageSize){
    	PageHelper.startPage(pageNum,pageSize);
    	Order order = orderMapper.selectByOrderNum(orderNo);
    	List<Order_item> orderItemList = orderItemMapper.selectAllByOrderNum(orderNo); 
    	if(null==order) {
    		return ServerResponse.createByErrorMessage("无该订单");
    	}
    	OrderVO orderVO = this.assembleOrderVO(order, orderItemList);
    	PageInfo pageResult = new PageInfo(orderVO);
    	return ServerResponse.createBySuccess(pageResult);
    }
    
    
    
    
    
    private OrderVO assembleOrderVO(Order order,List<Order_item> orderItemList) {
    	
    	OrderVO orderVO = new OrderVO();
    	orderVO.setOrderNum(order.getOrderNo());
    	orderVO.setPayment(order.getPayment());
    	orderVO.setPaymentType(order.getPaymentType());
    	orderVO.setPaymentTypeDesc(Const.PaymentType.getTypeByCode(order.getPaymentType()).getValue());
    	
    	orderVO.setPostage(order.getPostage());
    	orderVO.setStatus(order.getStatus());
    	orderVO.setStatusDesc(Const.TradeStatus.getTypeByCode(order.getStatus()).getValue());
    	
    	orderVO.setShippingId(order.getShippingId());
    	Shipping shipping = shippingMapper.selectByPrimaryKey(order.getId());
    	if(null!=shipping) {
    		orderVO.setReciverName(shipping.getReceiverName());
    		orderVO.setShippingVO(this.assembleShippingVO(shipping));
    		
    	}
    	orderVO.setPaymentTime(DateTimeUtils.date2Str(order.getPaymentTime()));
    	orderVO.setCloseTime(DateTimeUtils.date2Str(order.getCloseTime()));
    	orderVO.setEndTime(DateTimeUtils.date2Str(order.getEndTime()));
    	orderVO.setSendTime(DateTimeUtils.date2Str(order.getSendTime()));
    	orderVO.setCreateTime(DateTimeUtils.date2Str(order.getCreateTime()));
    	
    	orderVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
    	List<OrderItemVO> orderItemVOList = Lists.newArrayList();
    	for(Order_item item:orderItemList) {
    		OrderItemVO orderItemVO = this.assembleOrderItemVO(item);
    		orderItemVOList.add(orderItemVO);
    	}
    	orderVO.setOrderItemVOList(orderItemVOList);
    	return orderVO;
    }
    
    private OrderItemVO assembleOrderItemVO(Order_item orderItem) {
    	OrderItemVO orderItemVO = new OrderItemVO();
    	orderItemVO.setProductName(orderItem.getProductName());
    	orderItemVO.setOrderNo(orderItem.getOrderNo());
    	orderItemVO.setProductId(orderItem.getProductId());
    	orderItemVO.setProductImage(orderItem.getProductImage());
    	orderItemVO.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
    	orderItemVO.setQuantity(orderItem.getQuatity());
    	orderItemVO.setTotalPrice(orderItem.getTotalPrice());
    	
    	orderItemVO.setCreateTime(DateTimeUtils.date2Str(orderItem.getCreateTime()));
    	
    	return orderItemVO;
    }
    
    private ShippingVO assembleShippingVO(Shipping shipping) {
    	ShippingVO shippingVO = new ShippingVO();
    	shippingVO.setReceiverAddress(shipping.getReceiverAddress());
    	shippingVO.setReceiverCity(shipping.getReceiverCity());
    	shippingVO.setReceiverDistrict(shipping.getReceiverDistrict());
    	shippingVO.setReceiverMobile(shipping.getReceiverMobile());
    	shippingVO.setReceiverName(shipping.getReceiverName());
    	shippingVO.setReceiverPhone(shipping.getReceiverPhone());
    	shippingVO.setReceiverProvince(shipping.getReceiverProvince());
    	shippingVO.setReceiverZip(shipping.getReceiverZip());
    	return shippingVO;
    }
    
    private void cleanCart(List<Shopping_cart> cartList) {
    	for(Shopping_cart cart:cartList) {
    		shopping_cartMapper.deleteByPrimaryKey(cart.getId());
    	}
    }
    
    private void reduceProductStock(List<Order_item> orderItemList) {
    	for(Order_item item:orderItemList) {
    		Product product = productMapper.selectByPrimaryKey(item.getProductId());
    		product.setStock(product.getStock()-item.getQuatity());
    		int resultCount = productMapper.updateByPrimaryKeySelective(product);
    	}
    }
    
    
    private Order assembleOrder(Integer userId,Integer shippingId,BigDecimal payment) {
    	Order order = new Order();
    	long orderNo = this.generateOrderNo();
    	order.setOrderNo(orderNo);
    	order.setPayment(payment);
    	order.setStatus(Const.TradeStatus.NO_PAY.getCode());
    	order.setPostage(0);
    	order.setPaymentType(Const.PaymentType.PAY_ONLINE.getCode());
    	order.setPayment(payment);
    	order.setUserId(userId);
    	order.setShippingId(shippingId);
    	int resultCount = orderMapper.insert(order);
    	if(resultCount>0) {
    		return order;
    	}else {
    		return null;
    	}
    	
    }
    
    private long generateOrderNo() {
    	long currentTime = System.currentTimeMillis();
    	return currentTime+new Random().nextInt(100);
    }
    
    private ServerResponse<List<Order_item>> getCartOrderItem(Integer userId,List<Shopping_cart> cartList){
    	List<Order_item> orderItemList = Lists.newArrayList();
    	if(CollectionUtils.isEmpty(cartList)) {
    		return ServerResponse.createByErrorMessage("当前购物车未勾选商品或为空，无法生成订单");
    	}
    	//校验购物车的数据，包括产品状态和数量
    	
    	for(Shopping_cart cart:cartList) {
    		
    		Order_item orderItem = new Order_item();
    		
    		Product product = productMapper.selectByPrimaryKey(cart.getProductId());
    		
    		if(product.getStatus() != Const.ProductStatus.ON_SALE.getCode()) {
    			return ServerResponse.createByErrorMessage("产品"+product.getName()+"不是在售状态");
    		}
    		if(product.getStock()<cart.getQuantity()) {
    			return ServerResponse.createByErrorMessage("产品"+product.getName()+"库存不足");
    		}
    		//组装orderItem
    		orderItem.setUserId(userId);
    		orderItem.setProductId(product.getId());
    		orderItem.setProductName(product.getName());
    		orderItem.setProductImage(product.getMainImage());
    		orderItem.setCurrentUnitPrice(product.getPrice());
    		orderItem.setQuatity(cart.getQuantity());
    		orderItem.setTotalPrice(BigDecimalUtils.multiplication(orderItem.getCurrentUnitPrice().doubleValue(), orderItem.getQuatity().doubleValue()));
    		orderItemList.add(orderItem);
    	}
    	return ServerResponse.createBySuccess(orderItemList);
    }
    
    private BigDecimal totalPriceOfOrderItem(List<Order_item> orderItemList) {
    	BigDecimal total = new BigDecimal("0.00");
    	if(CollectionUtils.isEmpty(orderItemList)) {
    		return total;
    	}
    	for(Order_item item:orderItemList) {
    		total = BigDecimalUtils.add(total.doubleValue(), item.getTotalPrice().doubleValue());
    	}
    	return total;
    }
    
    
}