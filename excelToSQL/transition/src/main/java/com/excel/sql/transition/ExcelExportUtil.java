package com.excel.sql.transition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook; 

public class ExcelExportUtil {
	private static XSSFWorkbook workbook;
	private static XSSFSheet sheet;
	
	public ExcelExportUtil() {
	}
	
	@SuppressWarnings("resource")
	public static void exportExcel(String brandName, String[] title, List<List<String>> dataList,String filePath) throws IOException {
		workbook = new XSSFWorkbook();	//创建一个新的xlsx
		sheet = workbook.createSheet();	//创建一个新的sheet
		workbook.setSheetName(0,brandName);	//设置sheet名称
		
		String fileName = brandName + ".xlsx";
		
		XSSFRow titleRow = sheet.createRow(0);
		//设置标题列
		for(int i=0; i<title.length; ++i) {
			titleRow.createCell(i).setCellValue(title[i]);
		}
		//写入数据
		for(int i=0;i<dataList.size();i++) {
			XSSFRow row = sheet.createRow(i+1);
			for(int j=0; j<dataList.get(i).size();j++) {
				row.createCell(j).setCellValue(dataList.get(i).get(j));
			}
		}
		//导出Excel文件
		File file = new File(filePath+fileName);
		FileOutputStream outStream = new FileOutputStream(file);
		workbook.write(outStream);
		outStream.flush();
		outStream.close();
		System.out.println("导出"+filePath+fileName+"成功！");
	}
	
}
