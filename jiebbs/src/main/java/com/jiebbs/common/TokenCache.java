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
	 * 这个类使用初始化链，即以下方法调用顺序不影响类创建
	 * initialCapacity(1000) 初始化容量1000
	 * maximumSize(10000) 允许最大的容量，超过后LoadingCache启动LRU算法（最小使用算法），进行类型清除
	 * expireAfterAccess(12, TimeUnit.HOURS) 有效时间，第一个参数为时间长度，第二个参数为时间单位
	 */
	private static LoadingCache<String,String> localCache = 
			CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS)
			.build(new CacheLoader<String,String>() {
				//默认的数据加载实现，当调用get取值的时候，如果key没有对应的值，就调用这个方法进行加载
				@Override
				public String load(String s) throws Exception {
					//若直接返回null,因为方法内部会调用会使用equals，就会导致空指针异常，所以返回String类型的null
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
