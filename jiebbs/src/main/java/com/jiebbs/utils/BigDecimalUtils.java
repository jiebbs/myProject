package com.jiebbs.utils;

import java.math.BigDecimal;

public class BigDecimalUtils {

	private BigDecimalUtils() {}
	
	public static BigDecimal add(double num1,double num2){
		BigDecimal b1 = new BigDecimal(Double.toString(num1));
		BigDecimal b2 = new BigDecimal(Double.toString(num2));
		return b1.add(b2);
	}
	
	public static BigDecimal subtraction(double num1,double num2){
		BigDecimal b1 = new BigDecimal(Double.toString(num1));
		BigDecimal b2 = new BigDecimal(Double.toString(num2));
		return b1.subtract(b2);
	}
	
	public static BigDecimal multiplication(double num1,double num2){
		BigDecimal b1 = new BigDecimal(Double.toString(num1));
		BigDecimal b2 = new BigDecimal(Double.toString(num2));
		return b1.multiply(b2);
	}
	//��Ҫ������ܳ����������()��Ҫ����divide(����������С��λ��ģʽ)��������õ�����������
	public static BigDecimal division(double num1,double num2){
		BigDecimal b1 = new BigDecimal(Double.toString(num1));
		BigDecimal b2 = new BigDecimal(Double.toString(num2));
		return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);//�������룬����2ΪС��
	}
}
