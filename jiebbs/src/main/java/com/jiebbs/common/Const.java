package com.jiebbs.common;

import java.util.Set;

import com.google.common.collect.Sets;

public class Const {
	
	public static final String CURRENT_USER = "currentUser";
	
	public interface ProductListOrderBy{
		//ʹ��Set������contains������ʱ�临�Ӷ�ΪO(1),��List��ʱ�临�Ӷ�ΪO(N)
		Set<String> PRICE_DESC_ASC = Sets.newHashSet("price_desc","price_asc");
	}
	//ǰ�˽����첽У��ʹ��
	public static final String USERNAME = "username";
	
	public static final String EMAIL = "email";
	
	//ʹ���ڲ���涨�û�����
	public interface Role{
		int ROLE_CUSTORMER = 0;//��ͨ�û�
		int ROLE_ADMIN = 1;//����Ա
	}
	//�涨��Ʒ״̬
	public enum ProductStatus{
		ON_SALE(1,"����");
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
