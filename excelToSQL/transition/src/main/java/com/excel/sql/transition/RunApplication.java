package com.excel.sql.transition;

import java.io.BufferedReader;
import java.io.File;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;


public class RunApplication {

    public static void main(String[] args) throws IOException, SQLException, InterruptedException {
    	//List<String> tables = Core.readXlsx(args[0],1);
    	File file = new File(args[0]);
    	if(!file.isDirectory()) {
    		System.out.println("不是文件夹，请选择正确的文件夹");
    		return;
    	}else {
        	System.out.println("导入文件路径："+args[0]+"/" +"\n");
        	String[] filelist = file.list();
        	System.out.println("请选择当前操作环境：1.测试 2.生产...");
        	BufferedReader br_0 = new BufferedReader(new InputStreamReader(System.in));
        	int system = br_0.read() - 48;
        	//设置数据库URL
        	String db_url = system==1?"jdbc:mysql://rm-uf6hnc20q03xba0l0ao.mysql.rds.aliyuncs.com:3306/piccclaimdb":"jdbc:mysql://rm-j5e4ss080mucdc9u0.mysql.rds.aliyuncs.com:3306/piccclaimdb";
        	//设置导出路径
        	String filePath = system==1?"/Users/alanwang/Desktop/导出/":"./导出/";
        	//获取操作方式
			System.out.println("请选择处理方式：1.预处理 2.本地化价格导入...");
        	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));				
			int choice = br.read() - 48;
			int version = 0;
			BufferedReader br_2 = new BufferedReader(new InputStreamReader(System.in));
			//获取系统版本
			if(choice == 2) {
				System.out.println("请选择版本类型：1.精友2代"+"\n"+"2.精友2.5代"+"\n"+"3.多定损");
				version = br_2.read() - 48;
			}
        	
        	for(int i = 0; i < filelist.length; i++) {
        		if(!filelist[i].endsWith(".xlsx")) {
        			continue;	//如果文件夹内文件不是xlsx结尾，则跳过不执行
        		}
        		else {	        			
        			System.out.println("正在处理'"+filelist[i]+"'...");
        			String fileName = filelist[i].substring(0,filelist[i].length() - 5);	//获取Excel文件名称
        			String province = args[1];	//导入省份     
        			String tableName = "", fields = "";
        			if(choice == 1) {	//如果选择方式为预处理
        				List<String> tables = Core.readXlsx(args[0]+"/"+filelist[i],1);
        				tableName = "picc_mostrecent_localprice_" + Core.queryDatabase(province,"PROVINCE",db_url);
        				String brandCode = Core.queryDatabase(fileName,"BRANDCODE",db_url);
        				fields = "brand_code,part_name,oe,fit_model,part_remark,sys_manufacture_price,sys_market_price,"+
	        	        		"4s_price,4s_local_remark,market_oem_price,market_oem_remark,market_afm_price,market_afm_remark,"+
	        	        		"jap_price,jap_remark,market_reman_price,market_reman_remark,id_immutable,is_double_hundred,create_time,update_time";
	        			Core.preProcessing(tables,tableName,fields,brandCode,fileName,province,choice,db_url);
	        			String[] title = {"零件名称","OE","系统厂方价","上次系统厂方价"};
	        			Core.exportXlsx(tableName,fileName,title,db_url,filePath);
	        			System.out.println();
        			}
        			else if(choice == 2) {
            			String date = args[2];	//更新时间批次
            			String uploader	= args[3];	//上传人
	        			List<String> tables = Core.readXlsx(args[0]+"/"+filelist[i],1);	//导入Excel，并存储至二维数组
	        			if(version == 1) {	//精友2代导入信息
	        				tableName = "jy_local_price_insert";	//导入表名
	        				fields = "part_name,oe,fit_model,part_remark,sys_manufacture_price,sys_market_price,"+
	        	        		"4s_price,4s_local_remark,market_oem_price,market_oem_remark,market_afm_price,market_afm_remark,"+
	        	        		"jap_price,jap_remark,market_reman_price,market_reman_remark,id_immutable,is_double_hundred,create_time,update_time";
	        			}
	        			else if(version == 2) {	//精友2.5代导入信息
	        				tableName = "local_price_insert_2n5";
	        				fields = "part_name,oe,part_remark,sys_manufacture_price,sys_market_price,4s_price,"+
	        						"local_oem_price,local_afm_price,local_fit_price,local_reman_price,oem_price_supplier,afm_price_brandname,"+
	        						"afm_price_supplier,fit_price_supplier,reman_price_supplier,id_immutable,create_time,update_time";
	        			}
	        			Core.operateSQL(tables,tableName,fields,fileName,province,version,date,uploader,choice,db_url);	        			
	        			System.out.println();
        			}
        			else {System.out.println("未选择操作方式");}

        		} 
        	}
        	br.close();	    
        	br_2.close();
        	Core.executeLinux("rm -rf ./导入/*.xlsx");
        	System.out.println("任务结束");
        	
    	}
    }
}
