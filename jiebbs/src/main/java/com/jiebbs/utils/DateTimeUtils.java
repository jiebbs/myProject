package com.jiebbs.utils;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTimeUtils {
	
	private static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss"; 
	
	//标准转换
	public static Date str2Date(String strDate) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
		DateTime dateTime = dateTimeFormatter.parseDateTime(strDate);
		return dateTime.toDate();
	}
	
	//标准转换
	public static String date2Str(Date date) {
		if(date == null) {
			return StringUtils.EMPTY;
		}
		
		DateTime dateTime = new DateTime(date);
		return dateTime.toString(STANDARD_FORMAT);
	}
	//使用joda-time
	//字符串转时间
	public static Date str2Date(String strDate,String format) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(format);
		DateTime dateTime = dateTimeFormatter.parseDateTime(strDate);
		return dateTime.toDate();
	}
	
	//时间转字符串
	public static String date2Str(Date date,String format) {
		if(date == null) {
			return StringUtils.EMPTY;
		}
		
		DateTime dateTime = new DateTime(date);
		return dateTime.toString(format);
	}
}
