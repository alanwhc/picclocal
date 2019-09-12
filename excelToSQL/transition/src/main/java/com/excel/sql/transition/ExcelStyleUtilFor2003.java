package com.excel.sql.transition;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExcelStyleUtilFor2003 {
	/*
	 * 设置水平垂直位置
	 */
	public static void centerLeft(CellStyle cellStyle) {
		cellStyle.setAlignment(HorizontalAlignment.LEFT);	//水平靠左
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);	//垂直居中
	}
	
	/*
	 * 合并单元格
	 */
	public static void mergeCell(Sheet wbSheet,int firstRow,int lastRow,int firstCol,int lastCol) {
		wbSheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
	}
	
	/*
	 * 设置字体格式
	 */
	public static CellStyle getFontStyle(Workbook wb,int fontSize,short color) {
		CellStyle cellStyle = wb.createCellStyle();
		Font font = setFont(wb, fontSize, color);
		centerLeft(cellStyle);
		cellStyle.setFont(font);
		return cellStyle;
	}
	
	private static Font setFont(Workbook wb, int fontSize, short color) {
		//设置字体
		Font font = wb.createFont();
		font.setColor(color);
		font.setFontHeightInPoints((short) fontSize);
		return font;
	}
	
	private static Font setFont(Workbook wb, int fontSize) {
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) fontSize);
		return font;
	}
}
