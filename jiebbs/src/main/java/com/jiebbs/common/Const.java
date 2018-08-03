package com.jiebbs.common;

import java.util.Set;

import com.google.common.collect.Sets;

public class Const {
	
	public static final String CURRENT_USER = "currentUser";
	
	public interface ProductListOrderBy{
		//使用Set集合是contains方法的时间复杂度为O(1),而List的时间复杂度为O(N)
		Set<String> PRICE_DESC_ASC = Sets.newHashSet("price_desc","price_asc");
	}
	//前端进行异步校验使用
	public static final String USERNAME = "username";
	
	public static final String EMAIL = "email";
	
	//使用内部类规定用户类型
	public interface Role{
		int ROLE_CUSTORMER = 0;//普通用户
		int ROLE_ADMIN = 1;//管理员
	}
	//规定产品状态
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
}
