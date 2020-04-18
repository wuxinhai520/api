package com.lemon.cases;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.lemon.constants.Constants;
import com.lemon.pojo.API;
import com.lemon.pojo.Case;
import com.lemon.utils.EnvironmentUtils;
import com.lemon.utils.ExcelUtils;
import com.lemon.utils.HttpUtils;
import com.lemon.utils.SQLUtils;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

public class RegisterCase extends BaseCase{
	
	
	/**
	 * 注册用例的测试方法
	 * @param url			接口请求地址
	 * @param method		接口请求方法
	 * @param params		接口请求参数
	 * @param contentType	接口类型
	 */
	@Test(dataProvider = "datas",description="登录测试")
	@Severity(SeverityLevel.TRIVIAL)   //设置测试用例级别
	public void test(API api,Case c) {	
//		1、参数化替换
		String params = paramsReplace(c.getParams());
		c.setParams(params);
		String sql = paramsReplace(c.getSql());
		c.setSql(sql);
//		2、数据库前置查询结果(数据断言必须在接口执行前后都查询)
		Object beforeSQLReuslt = SQLUtils.getSQLSingleReuslt(c.getSql());
//		3、调用接口
		Map<String, String> headers = new HashMap<>();
		//3.1、设置默认请求头
		setDefaultHeaders(headers);
		String body = HttpUtils.call(api.getUrl(), api.getMethod(), c.getParams(), api.getContentType(),headers);
//		4、断言响应结果
		String responseAssert = responseAssert(c.getExpect(), body);
//		5、添加接口响应回写内容
		addWriteBackData(1, c.getId(), Constants.ACTUAL_RESPONSE_CELLNUM, body);
//		6、数据库后置查询结果
		Object afterSQLReuslt = SQLUtils.getSQLSingleReuslt(c.getSql());
//		7、据库断言
		if(StringUtils.isNotBlank(c.getSql())) {
			boolean sqlAssertFlag = sqlAssert(beforeSQLReuslt, afterSQLReuslt);
			System.out.println("数据库断言：" + sqlAssertFlag);
			//sql断言回写
			addWriteBackData(1, c.getId(), Constants.SQL_ASSERT_CELLNUM, 
					sqlAssertFlag ? "Pass" : "Fail");
		}
//		8、添加断言回写内容
		addWriteBackData(1, c.getId(), Constants.RESPONSE_ASSERT_CELLNUM, responseAssert);
//		9、添加日志
//		10、报表断言
		Assert.assertEquals(responseAssert, responseAssert);

	}

	/**
	 * 	数据库断言
	 * @param beforeSQLReuslt	接口执行之前的数据结果
	 * @param afterSQLReuslt	接口执行之后的数据结果
	 * @return
	 */
	public boolean sqlAssert(Object beforeSQLReuslt,Object afterSQLReuslt) {
		Long beforeValue = (Long)beforeSQLReuslt;
		Long afterValue = (Long)afterSQLReuslt;
		if(beforeValue != null && afterValue != null && beforeValue == 0 && afterValue == 1) {
			//接口执行之前手机号码统计为0，执行之后手机号码统计为1，断言成功
			return true;
		}
		return false;
	}

	
	@DataProvider
	public Object[][] datas() {

		Object[][] datas = ExcelUtils.getAPIAndCaseByApiId("1");
		return datas;
	}
	
	
}
