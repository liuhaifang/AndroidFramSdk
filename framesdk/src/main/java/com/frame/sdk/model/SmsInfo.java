package com.frame.sdk.model;

/**
 * 短信实体类
 */
public class SmsInfo {
	public String localName;// 本机名称
	public String localPhone;// 本机手机号
	public long time;// 收到或接收的时间
	public String content;// 内容
	public String name;// 发送人/接收者名称
	public String phone;// 发送人/接收者号码
	public int type;//短信类型1是接收到的，2是已发出 
}
