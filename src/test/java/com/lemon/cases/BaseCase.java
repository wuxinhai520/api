package com.lemon.cases;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.alibaba.fastjson.JSONPath;
import com.lemon.constants.Constants;
import com.lemon.pojo.API;
import com.lemon.pojo.Case;
import com.lemon.pojo.Member;
import com.lemon.pojo.WriteBackData;
import com.lemon.utils.EnvironmentUtils;
import com.lemon.utils.ExcelUtils;
import com.lemon.utils.SQLUtils;

import io.qameta.allure.Step;

public class BaseCase {

	private Logger log = Logger.getLogger(BaseCase.class);
	
	/**
	 * 	参数化替换方法
	 * @param params	需要替换的字符串
	 * @return			替换之后的字符串
	 */
	@Step("参数化 @params")
	public String paramsReplace(String params) {
		//如果参数为null，返回参数本身
		if(StringUtils.isBlank(params)) {
			return params;
		}
		//1、从环境变量中获取所有的占位符
		Set<String> keySet = EnvironmentUtils.env.keySet();
		//2、遍历环境变量env
		for (String key : keySet) {
			//3、key就是参数化的占位符${xxxx} value参数化具体要替换的值。
			//4、把需要替换的字符串执行replace(key,value);
			String value = EnvironmentUtils.env.get(key);
			//5、替换并且重新接受
			params = params.replace(key, value);
		}
		return params;
	}
	
	/**
	 * 	响应断言
	 * @param expect	期望值
	 * @param body		响应体
	 * @return
	 */
	public String responseAssert(String expect, String body) {
		//1、根据特殊分割符，切割期望值 "code":0@@"msg":"OK"
		String[] expectArray = expect.split("@@");
		//2、定义返回值
		String responseAssertResult = "断言成功";
		//3、循环期望值切割之后的数组
		for (String expectValue : expectArray) {
			//4、如果响应体包含期望值，认为当前断言式成功。
			boolean flag = body.contains(expectValue);
			//5、如果不包含期望值，直接判断断言失败。
			if(flag == false) {
				responseAssertResult = "期望值：" + expectValue + "不在响应体内。";
				System.out.println(responseAssertResult);
				break;
			}
		}
		System.out.println("断言响应结果:" + responseAssertResult);
		return responseAssertResult;
	}
	
	/**
	 * 	设置默认请求头
	 * 	"X-Lemonban-Media-Type", Constants.MEDIA_TYPE
	 *  "Content-Type", "application/json"
	 * @param headers
	 */
	public void setDefaultHeaders(Map<String, String> headers) {
		headers.put("X-Lemonban-Media-Type", Constants.MEDIA_TYPE);
		headers.put("Content-Type", "application/json");
	}

	/**
	 * 从body里面获取jsonpath对应值，存储到env环境变量中
	 * @param body		响应体
	 * @param jsonPath	jsonpath表达式
	 * @param envKey	env环境变量的可以
	 */
	public void setVariableInEnv(String body,String jsonPath,String envKey) {
		Object value = JSONPath.read(body, jsonPath);
		if(value != null) {
			EnvironmentUtils.env.put(envKey, value.toString());
		}
	}
	
	/**
	 * 从环境变量中获取token设置到header中
	 * @param headers	请求头map
	 */
	public void getToken2Header(Map<String, String> headers) {
		String token = EnvironmentUtils.env.get("${token}");
		if(StringUtils.isNotBlank(token)) {
			headers.put("Authorization", "Bearer " + token);
		}
	}
	
	/**
	 * 添加wbd回写对象到wbdList集合中。
	 * @param sheetIndex		回写sheetIndex
	 * @param rowNum			回写行号
	 * @param cellNum			回写列号
	 * @param content			回写内容
	 */
	public void addWriteBackData(int sheetIndex,int rowNum,int cellNum, String content) {
		WriteBackData wbd = new WriteBackData(sheetIndex, rowNum, cellNum, content);
		ExcelUtils.wbdList.add(wbd);
	}
	
	@BeforeSuite
	public void init() throws Exception {
		log.info("=======================项目初始化============================");
		ExcelUtils.apiList = ExcelUtils.read(0, 1, API.class);
		ExcelUtils.caseList = ExcelUtils.read(1, 1, Case.class);
		log.info("=======================参数化开始============================");
//		EnvironmentUtils.env.put(Constants.PARAM_REGISTER_MOBILEPHONE, EnvironmentUtils.getRegisterPhone());
//		EnvironmentUtils.env.put(Constants.PARAM_REGISTER_PASSWORD, "12345678");
//		Member member = SQLUtils.getOneRandomMember();
//		System.out.println(member);
//		EnvironmentUtils.env.put(Constants.PARAM_LOGIN_MOBILEPHONE, member.getMobile_phone());
//		EnvironmentUtils.env.put(Constants.PARAM_LOGIN_PASSWORD, member.getPwd());
		Properties prop = new Properties();
		//1、加载paras.properties文件
		FileInputStream fis = new FileInputStream("src/test/resources/params.properties");
		prop.load(fis);
		//2、获取prop所有的key
		Set<Object> keySet = prop.keySet();
		for (Object key : keySet) {
			String value = prop.get(key).toString();
			//3、把prop的key和value存入 env
			EnvironmentUtils.env.put(key.toString(),value);
		}
		//关流
		fis.close();
	}
	
	@AfterSuite
	public void finish() {		
		log.info("=======================项目结束============================");
		ExcelUtils.batchWrite();
	}
}
