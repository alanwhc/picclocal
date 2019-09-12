package com.excel.sql.transition;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class Sql {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static String DB_URL;;
	static final String user = "bigdata";
	static final String pswd = "Bigdata1234";
	static final String dbname = "piccclaimdb";
	//生产
	/*static final String DB_URL = "jdbc:mysql://rm-j5e4ss080mucdc9u0.mysql.rds.aliyuncs.com:3306/piccclaimdb";
	
	static final String user = "bigdata";
	static final String pswd = "Bigdata1234";
	static final String dbname = "piccclaimdb";
*/
	
	Connection conn = null;
	Statement stmt = null;
	CallableStatement cstmt = null;
	ResultSet rst = null;
	public Sql(String url) {
		try {
			DB_URL = url;
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,user,pswd);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/*
	 * 执行单一SQL结果查询
	 */
	public String query(String param, String type) throws SQLException{
		String result = "",sql = "";
		try {
			switch(type) {
			case "PROVINCE":
				sql = "SELECT province FROM code_province_mapping WHERE province_code = '" + param + "';";
				break;
			case "BRANDCODE":
				sql = "SELECT brand_code FROM jy_brand_manufacturer_mapping WHERE brand_name = '" + param +"';";
				break;
			default:
				break;
			}
			stmt = conn.prepareStatement(sql);
			rst = stmt.executeQuery(sql);
			while(rst.next()) {
				result = rst.getString(1);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/*
	 * 执行表查询
	 */
	public List<List<String>> queryListString(String tableName,String brandName) throws SQLException{
		List<List<String>> result = new ArrayList<List<String>>();
		String sql = "";
		try {
			System.out.println("正在查询"+brandName+"系统厂方价对比结果...");
			String brandCode = query(brandName,"BRANDCODE");
			sql = "SELECT part_name,oe,sys_manufacture_price,previous_sys_price FROM "+tableName+" WHERE brand_code = '"+brandCode+"' AND is_price_changed = '1';";
			stmt = conn.prepareStatement(sql);
			rst = stmt.executeQuery(sql);
			while(rst.next()) {
				List<String> lst = new ArrayList<String>();
				lst.add(rst.getString(1));
				lst.add(rst.getString(2));
				lst.add(rst.getString(3));
				lst.add(rst.getString(4));
				result.add(lst);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(stmt != null) 
					stmt.close();
			}catch(SQLException se2) {
				se2.printStackTrace();
			}
			try {
				if(conn != null) conn.close();
			}
				catch(SQLException se) {
					se.printStackTrace();
				}
		}
		return result;
	}
	
	/*
	 * 数据写入数据库
	 */
	public void insertData(String sql) throws SQLException {
		try {
			//执行SQL语句
			System.out.println("实例化Statement对象...");
			stmt = conn.prepareStatement(sql);
			stmt.executeUpdate(sql);
			System.out.println("导入完成...");
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(stmt != null) 
					stmt.close();
			}catch(SQLException se2) {}
				
			try {
				if(conn != null) conn.close();
			}
				catch(SQLException se) {
					se.printStackTrace();
				}
		}
	}
	
	/*
	 * 调用存储过程
	 */
	public void callProcedure(String sql, String brandName, String province,int version, String date, String uploader, int type) {
		try {
			//执行SQL语句
			cstmt = conn.prepareCall(sql);
			cstmt.setString(1,brandName);	//设置品牌
			cstmt.setString(2,province);	//设置省份
			if(type == 1) {
				System.out.println("调用参数为："+brandName+","+province);
			}
			else if(type == 2) {
				cstmt.setString(3,version+"");	//设置版本
				cstmt.setString(4,date);	//设置更新日期
				cstmt.setString(5,uploader);	//设置更新人			
				System.out.println("调用参数为："+brandName+","+province+","+date+","+uploader);

			}
			else {}
			cstmt.execute();
			System.out.println("更新数据完成...");
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(cstmt != null) 
					cstmt.close();
			}catch(SQLException se2) {}
				
			try {
				if(conn != null) conn.close();
			}
				catch(SQLException se) {
					se.printStackTrace();
				}
		}
	}

}
