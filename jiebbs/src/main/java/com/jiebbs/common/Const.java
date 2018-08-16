package com.jiebbs.common;

import java.util.Set;

import com.google.common.collect.Sets;

public class Const {
	
	public static final String CURRENT_USER = "currentUser";
	
	public interface ProductListOrderBy{

		Set<String> PRICE_DESC_ASC = Sets.newHashSet("price_desc","price_asc");
	}
	

	public interface CartCheckStatus{
		int CHECKED = 1;
		int UNCHECKED = 0;
		
		String LIMIT_NUM_FAIL ="LIMIT_NUM_FAIL";
		String LIMIT_NUM_SUCCESS ="LIMIT_NUM_SUCCESS";
	}
	

	public static final String USERNAME = "username";
	
	public static final String EMAIL = "email";

	public interface Role{
		int ROLE_CUSTORMER = 0;
		int ROLE_ADMIN = 1;
	}

	public enum ProductStatus{
		ON_SALE(1,"在线");
		private int code;
		private String status;
		
		private ProductStatus(int code, String status) {
			this.code = code;
			this.status = status;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}
	}
	
	public enum TradeStatus{
		CANCALED(0,"已取消"),
		NO_PAY(10,"未支付"),
		PAID(20,"已支付"),
		SHIPPING(40,"已发货"),
		ORDER_SUCCESS(50,"订单完成"),
		ORDER_CLOSE(60,"订单关闭");
		
		private String value;
		private int code;
		private TradeStatus( int code,String value) {
			this.value = value;
			this.code = code;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		
		public static TradeStatus getTypeByCode(int code) {
			for(TradeStatus status:values()) {
				if(status.getCode()==code) {
					return status; 
				}
			}
			throw new RuntimeException("没有找到对应的枚举");
		}
		
	}
	
	public interface AlipayCallBack{
		String TRADE_STATUS_WAIT_BUYER_PAY="WAIT_BUYER_PAY";
		String TRADE_STATUS_TRADE_SUCCESS="TRADE_SUCCESS";
		
		String RESPONSE_SUCCESS = "success";
		String RESPONSE_FAILED = "failed";
		
	}
	
	public enum PayPlatform{
		ALIPAY(1,"支付宝")
		
		;
		private String value;
		private int code;
		private PayPlatform( int code,String value) {
			this.value = value;
			this.code = code;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
	}
	
	public enum PaymentType{
		
		PAY_ONLINE(1,"在线支付");
		
		private int code;
		private String value;
		private PaymentType(int code, String value) {
			this.code = code;
			this.value = value;
		}
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		
		public static PaymentType getTypeByCode(int code) {
			for(PaymentType type:values()) {
				if(type.getCode()==code) {
					return type; 
				}
			}
			throw new RuntimeException("没有找到对应的枚举");
		}
	}
	
}
