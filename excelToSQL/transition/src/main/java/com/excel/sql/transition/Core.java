package com.excel.sql.transition;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.monitorjbl.xlsx.StreamingReader;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Core {
	/*
	 * 读取Excel存入数组
	 */
    public static List<String> readXlsx(String path,int isLocal){
        List<String> list = new ArrayList<>();
        System.out.println("读取...");
        InputStream in = null;
        try {
            in = new FileInputStream(path);
            //XSSFWorkbook xss = new XSSFWorkbook(in);
            Workbook xss = StreamingReader.builder()
            			.rowCacheSize(100)
            			.bufferSize(4096)
            			.open(in);
            System.out.println("共有："+xss.getNumberOfSheets()+"sheet");
            Sheet sheet = xss.getSheetAt(0);	//只读取第一个sheet
            System.out.println("共有："+sheet.getLastRowNum()+"行");
            for (Row row : sheet) {
            	if(row.getRowNum() == 0) continue;
            	String str = "('", lastSymbol = "";
                //遍历所有的列
                for (int i = 0; i < row.getLastCellNum(); i++) {
                	if(i==18) break;
                	if(row.getCell(i) == null) {
                		str += "','";
                		continue;
                	}
                	str += row.getCell(i).getStringCellValue() + "','";
                	//System.out.println(i+": "+str);
                }
                str = str.substring(0,str.length()-1);
                str += "now(),now()";
                lastSymbol = (row.getRowNum() == sheet.getLastRowNum()?";":",");
                str += ")" + lastSymbol;
                list.add(str);
                //if(row.getRowNum() == 1) break;
            }
            System.out.println("存储完成...");
            in.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }
    /*
     * 导出至Excel
     */
    public static void exportXlsx(String tableName, String brandName, String[] title, String url,String filePath) throws SQLException, IOException, InterruptedException{
    	Sql querySql = new Sql(url);
    	try {
        	List<List<String>> data = querySql.queryListString(tableName,brandName);
    		System.out.println("正在导出"+brandName+"系统厂方价比对结果...");
        	ExcelExportUtil.exportExcel(brandName,title,data,filePath);
        	
    	}catch(SQLException e1) {
    		e1.printStackTrace();
    	}catch(IOException e2) {
    		e2.printStackTrace();
    	}
    }
    
    /*
     * 查询数据库
     */
    public static String queryDatabase(String param, String type,String url) throws SQLException{
    	Sql querySql = new Sql(url);
    	String result = "";
    	try {
    		result = querySql.query(param,type);
    	}catch(SQLException e1) {
    		e1.printStackTrace();
    	}
    	return result;
    }
    
    /*
     * 执行写入数据库操作
     */
    public static void operateSQL(List<String> list, String tableName, String fields,String brandName,String province,int version,String date, String uploader, int type, String url) throws SQLException {
       // final List<String> sql = new ArrayList<String>();
        Sql executeSql = new Sql(url);
        final StringBuilder sql = new StringBuilder();
        //执行插入数据库操作
        sql.append("insert into "+tableName+" ("+fields+") values \n");    	
        list.forEach(e->{
        	sql.append(e);
        	sql.append("\n");
        });
        try {
			executeSql.insertData(sql.toString());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

        //执行更新操作
        executeSql = new Sql(url);
        final String sql1 = "{CALL proc_sync_localprice_data(?,?,?,?,?)}";
        executeSql.callProcedure(sql1.toString(),brandName,province,version,date,uploader,type);
    }
    
    /*
     * 预处理本地化数据
     */
    public static void preProcessing(List<String> list, String tableName, String fields, String brandCode, String brand, String province,int type,String url){
    	Sql execute = new Sql(url);
    	//System.out.println(tableName);
    	final StringBuilder sql = new StringBuilder();
    	sql.append("insert into " + tableName + " (" + fields + ") values \n");
    	list.forEach(e->{
    		e = e.substring(1);
    		e = "('" + brandCode + "',"+ e;
    		sql.append(e);
    		sql.append("\n");
    	});
    	try {
			execute.insertData(sql.toString());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	execute = new Sql(url);
    	final String sql1 = "{CALL proc_localprice_preprocessing(?,?)}";
    	execute.callProcedure(sql1.toString(),brand,province,0,"0000-00-00","",type);
    }
    
    /*
     * 执行Linux命令
     */
	public static void executeLinux(String command) throws IOException, InterruptedException{
		try {
			String[] cmd = new String[] {"/bin/sh","-c",command};
			Runtime.getRuntime().exec(cmd).waitFor();
		}catch(IOException e) {
			e.printStackTrace();
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
}