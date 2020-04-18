package com.lemon.constants;

public class Constants {
	//常量=恒定不变
	//final修饰类，类不能被技能，修饰方法方法不能被重写，修饰变量变成常量
	//常量 只能赋值一次,基本数据类型值不能改变，引用数据地址值不能改变，但是可以调用方法
	//常量所有英文大写，单词用下划线分割
	public static final String EXCEL_PATH = "src/test/resources/cases_v5.xlsx";
	//鉴权请求头
	public static final String MEDIA_TYPE = "lemonban.v2";
	//实际响应列号
	public static final int ACTUAL_RESPONSE_CELLNUM = 5;
	//响应断言列号
	public static final int RESPONSE_ASSERT_CELLNUM = 6;	//ctrl + shift + x(大写) y（小写）
	//数据库断言列号
	public static final int SQL_ASSERT_CELLNUM = 8;
	
	//jdbc:oracle:thin:@localhost:1521:orcl 
	public static final String JDBC_URL = "jdbc:mysql://api.lemonban.com:3306/futureloan?useUnicode=true&characterEncoding=utf-8";
	public static final String JDBC_USERNAME = "future";
	public static final String JDBC_PASSWORD = "123456";
	
	//参数化
	public static final String PARAM_REGISTER_MOBILEPHONE = "${register_mobilephone}";
	public static final String PARAM_REGISTER_PASSWORD = "${register_password}";
	public static final String PARAM_LOGIN_MOBILEPHONE = "${login_mobilephone}";
	public static final String PARAM_LOGIN_PASSWORD = "${login_password}";
	public static final String PARAM_TOKEN = "${token}";
	public static final String PARAM_MEMBER_ID = "${member_id}";

	
	
	
}
