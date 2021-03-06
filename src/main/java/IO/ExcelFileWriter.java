package main.java.IO;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import main.java.EPP.EPP;
import main.java.EPP.Evaluated;
import main.java.EPP.Team;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;

/**
 * Utility class to write an EPP to a XLSX file
 * @author Vincent Lacasse
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
			int firstCol = Team.S_NOTE_EQUIPE;
			int lastCol = Team.S_NOTE_EQUIPE;
			cra = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
			sheet.addMergedRegion(cra);

			for (Evaluated e : team) {
				row = sheet.createRow(rowCount);

				cell = row.createCell(Team.S_GROUPE);
				cell.setCellValue(team.getName());

				cell = row.createCell(Team.S_NOM);
				cell.setCellValue(e.getLastName());

				cell = row.createCell(Team.S_PRENOM);
				cell.setCellValue(e.getFirstName());

				cell = row.createCell(Team.S_COURRIEL);
				cell.setCellValue(e.getEMail());

				cell = row.createCell(Team.S_NOTE_EPP);
				cell.setCellValue(e.getNote());
				cell.setCellStyle(style);

				cell = row.createCell(Team.S_MNG);
				cell.setCellValue(team.getMean());
				cell.setCellStyle(style);

				cell = row.createCell(Team.S_FACTEUR);
				cell.setCellValue(e.getFactor());
				cell.setCellStyle(style);

				cell = row.createCell(Team.S_NOTE_ETUDIANT);
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
			JOptionPane.showMessageDialog(null, "Le fichier " + XLSXFileName + " n'a pas ??t?? trouv??.", "Erreur", JOptionPane.ERROR_MESSAGE);
			return false;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Erreur ?? l'??criture du fichier " + XLSXFileName, "Erreur", JOptionPane.ERROR_MESSAGE);
			System.err.println("Error writing to file " + XLSXFileName);
			return false;
		}
		return true;
	}

	private static String getFormula(int firstRow, int rowCount) {
		return "G" + (rowCount+1) + "*H" + (firstRow+1);
	}
}
