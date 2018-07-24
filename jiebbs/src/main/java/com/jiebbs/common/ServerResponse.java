package com.jiebbs.common;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * 通用的对象
 * @author weijie_zhu
 *
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
//保证对象序列化Json对象的时候，如果是null的对象，key也会消失
public class ServerResponse<T> implements Serializable{
	
	private int status;
	private String msg;
	private T data;
	
	//成功
	public static <T> ServerResponse<T> createBySuccess(){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
	}
	public static <T> ServerResponse<T> createBySuccessMessage(String msg){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
	}
	public static <T> ServerResponse<T> createBySuccess(T data){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
	}
	public static <T> ServerResponse<T> createBySuccess(String msg,T data){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data); 
	}
	
	//失败
	public static <T> ServerResponse<T> createByError(){
		return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
	}
	public static <T> ServerResponse<T> createByErrorMessage(String errorMessage){
		return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
	}
	
	public static <T> ServerResponse<T> createByErrorCodeMessage(int ErrorCode,String errorMessage){
		return new ServerResponse<T>(ErrorCode,errorMessage);
	}
	
	
	
	private ServerResponse(int status) {
		this.status=status;
	}
	
	private ServerResponse(int status,T data) {
		this(status);
		this.data=data;
	}
	
	private ServerResponse(int status,String msg,T data) {
		this(status,data);
		this.msg=msg;
	}
	
	private ServerResponse(int status,String msg) {
		this(status);
		this.msg=msg;
	}
	@JsonIgnore
	//使这个不会出现在Json序列化的节点中
	public boolean isSuccess(){
		return this.status==ResponseCode.SUCCESS.getCode();
	}

	public int getStatus() {
		return status;
	}

	public String getMsg() {
		return msg;
	}

	public T getData() {
		return data;
	}
	
	
}
