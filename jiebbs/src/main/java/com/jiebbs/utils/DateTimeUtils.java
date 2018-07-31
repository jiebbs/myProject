package com.jiebbs.utils;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTimeUtils {
	
	private static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss"; 
	
	//��׼ת��
	public static Date str2Date(String strDate) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
		DateTime dateTime = dateTimeFormatter.parseDateTime(strDate);
		return dateTime.toDate();
	}
	
	//��׼ת��
	public static String date2Str(Date date) {
		if(date == null) {
			return StringUtils.EMPTY;
		}
		
		DateTime dateTime = new DateTime(date);
		return dateTime.toString(STANDARD_FORMAT);
	}
	//ʹ��joda-time
	//�ַ���תʱ��
	public static Date str2Date(String strDate,String format) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(format);
		DateTime dateTime = dateTimeFormatter.parseDateTime(strDate);
		return dateTime.toDate();
	}
	
	//ʱ��ת�ַ���
	public static String date2Str(Date date,String format) {
		if(date == null) {
			return StringUtils.EMPTY;
		}
		
		DateTime dateTime = new DateTime(date);
		return dateTime.toString(format);
	}
}
