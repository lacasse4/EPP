package main.java;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Utility class to write an EPP to a XLSX file
 * @author Vincent Lacasse
 * Only String, Double and Integer objects are supported
 */
public class ExcelFileWriter {
	
	public static boolean write(String XLSXFileName, EPP epp) {
		Row row;
		Cell cell;
		
		List<Object[]> outputXLSX = epp.getResults();
		outputXLSX.add(0, epp.getHeader());
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Sommaire de l'EPP");

		DataFormat format = workbook.createDataFormat();
		CellStyle style = workbook.createCellStyle();
		style.setDataFormat(format.getFormat("0.00"));
				
		int rowCount = 0;

		for (Object[] s : outputXLSX) {
			row = sheet.createRow(rowCount++);

			for (int i = 0; i < s.length; i++) {
				cell = row.createCell(i);
				if (s[i] instanceof Double) {
					cell.setCellValue((Double)s[i]);
					cell.setCellStyle(style);
				}
				else if (s[i] instanceof Integer) {
					cell.setCellValue((Integer)s[i]);
				}
				else if (s[i] instanceof String) {
					cell.setCellValue((String)s[i]);           		
				}
				else throw new IllegalArgumentException("Array items must be String, Double or Integer.");
			}   
		}
		
		try {
			FileOutputStream outputStream = new FileOutputStream(XLSXFileName);
			workbook.write(outputStream);
			workbook.close();
		} catch (FileNotFoundException e) {
			System.err.println("File " + XLSXFileName + " was not found.");
			return false;
		} catch (IOException e) {
			System.err.println("Error writing to file " + XLSXFileName);
			return false;
		}
		return true;
	}
}
