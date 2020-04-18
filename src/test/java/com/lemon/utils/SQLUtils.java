package com.lemon.utils;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang3.StringUtils;

import com.lemon.pojo.Member;

public class SQLUtils {

	public static void main(String[] args) throws Exception {
		//DBUtils 
		QueryRunner qr = new QueryRunner();
		//conn 数据库
		Connection conn = JDBCUtils.getConnection();
//		Member member = qr.query(conn, "select * from member a where a.id = 10 ;",
//				new BeanHandler<Member>(Member.class));
		
		Object object = qr.query(conn,"select count(*) from member a where a.id = 10 ;",new ScalarHandler<Object>());
		System.out.println(object);
		JDBCUtils.close(conn);
	}
	
	/**
	 * 	根据sql语句执行查询单个结果集
	 * @param sql	sql语句
	 * @return
	 */
	public static Object getSQLSingleReuslt(String sql) {
		//1、如果sql语句为空，不执行sql查询
		if(StringUtils.isBlank(sql)) {
			return null;
		}
		//2、DBUtils操作sql语句核心类
		QueryRunner qr = new QueryRunner();
		//3、获取数据库连接
		Connection conn = JDBCUtils.getConnection();
		//3.1、定义返回值
		Object result = null;
		try {
			//4、执行sql语句
			result = qr.query(conn,sql,new ScalarHandler<Object>());
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCUtils.close(conn);
		}
		return result;
	}
	
	/**
	 * 	获取一个随机的Member对象
	 * @param sql	sql语句
	 * @return
	 */
	public static Member getOneRandomMember() {
		String sql = "select * from member order by rand() LIMIT 1;";
		//2、DBUtils操作sql语句核心类
		QueryRunner qr = new QueryRunner();
		//3、获取数据库连接
		Connection conn = JDBCUtils.getConnection();
		//3.1、定义返回值
		Member result = null;
		try {
			//4、执行sql语句
			result = qr.query(conn,sql,new BeanHandler<Member>(Member.class));
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCUtils.close(conn);
		}
		return result;
	}
	
	
	

}
