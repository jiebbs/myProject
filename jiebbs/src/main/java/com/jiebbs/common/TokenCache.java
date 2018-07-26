package com.jiebbs.common;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class TokenCache {
	public static final String TOKEN_PREFIX = "token_";
	
	private static Logger logger = LoggerFactory.getLogger(TokenCache.class);
	/*
	 * �����ʹ�ó�ʼ�����������·�������˳��Ӱ���ഴ��
	 * initialCapacity(1000) ��ʼ������1000
	 * maximumSize(10000) ��������������������LoadingCache����LRU�㷨����Сʹ���㷨���������������
	 * expireAfterAccess(12, TimeUnit.HOURS) ��Чʱ�䣬��һ������Ϊʱ�䳤�ȣ��ڶ�������Ϊʱ�䵥λ
	 */
	private static LoadingCache<String,String> localCache = 
			CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS)
			.build(new CacheLoader<String,String>() {
				//Ĭ�ϵ����ݼ���ʵ�֣�������getȡֵ��ʱ�����keyû�ж�Ӧ��ֵ���͵�������������м���
				@Override
				public String load(String s) throws Exception {
					//��ֱ�ӷ���null,��Ϊ�����ڲ�����û�ʹ��equals���ͻᵼ�¿�ָ���쳣�����Է���String���͵�null
					return "null";
				}
			});
	
	public static void setKey(String key,String value) {
		localCache.put(key, value);
	}
	
	public static String getKey(String key) {
		String value = null;
		try {
			value = localCache.get(key);
			if("null".equals(value)) {
				return null;
			}
			return value;
		} catch (ExecutionException e) {
			logger.error("loadingCache get error!",e);
			e.printStackTrace();
		}
		return null;
	}
}
