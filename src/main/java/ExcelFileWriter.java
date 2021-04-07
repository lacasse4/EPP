package main.java;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
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
		CellRangeAddress cra;

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Sommaire de l'EPP");
		DataFormat format = workbook.createDataFormat();
		CellStyle style = workbook.createCellStyle();
		style.setDataFormat(format.getFormat("0.00"));

		String[] header = epp.getHeader();
		row = sheet.createRow(0);
		for (int i = 0; i < header.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(header[i]);
		}

		int rowCount = 1;
		for (Team team : epp) {

			int firstRow = rowCount;
			int lastRow = rowCount + team.size() - 1;
			int firstCol = Team.NOTE_EQUIPE;
			int lastCol = Team.NOTE_EQUIPE;
			cra = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
			sheet.addMergedRegion(cra);

			for (Evaluated e : team) {
				row = sheet.createRow(rowCount);

				cell = row.createCell(Team.GROUPE);
				cell.setCellValue(team.getName());

				cell = row.createCell(Team.NOM);
				cell.setCellValue(e.getLastName());

				cell = row.createCell(Team.PRENOM);
				cell.setCellValue(e.getFirstName());

				cell = row.createCell(Team.NOTE_EPP);
				cell.setCellValue(e.getNote());
				cell.setCellStyle(style);

				cell = row.createCell(Team.MNG);
				cell.setCellValue(team.getMean());
				cell.setCellStyle(style);

				cell = row.createCell(Team.FACTEUR);
				cell.setCellValue(e.getFactor());
				cell.setCellStyle(style);

				cell = row.createCell(Team.NOTE_ETUDIANT);
				cell.setCellFormula(getFormula(firstRow, rowCount));
				cell.setCellStyle(style);

				rowCount ++;
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

	private static String getFormula(int firstRow, int rowCount) {
		return "F" + (rowCount+1) + "*G" + (firstRow+1);
	}
}
