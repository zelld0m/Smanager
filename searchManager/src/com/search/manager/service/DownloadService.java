package com.search.manager.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Service;

import com.search.manager.report.model.ReportBean;
import com.search.manager.report.model.ReportModel;
import com.search.manager.utility.DateAndTimeUtils;

/**
 * Service for processing Apache POI-based reports
 *
 */
@Service("downloadService")
public class DownloadService {

	// TODO: code cleanup: create one class that handles all excel related stuff
	public enum downloadType { EXCEL, PDF, CSV, XML }
	
	private static final Logger logger = Logger.getLogger(DownloadService.class);

	private final static short BORDER_TOP 		= (short)0x01;
	private final static short BORDER_BOTTOM 	= (short)0x02;
	private final static short BORDER_LEFT 		= (short)0x04;
	private final static short BORDER_RIGHT 	= (short)0x08;

	private static HSSFRow createRow(HSSFSheet worksheet, int rowIndex, Float height) {
		HSSFRow row = worksheet.createRow(rowIndex);
		if (height != null) {
			row.setHeightInPoints(height);
		}
		return row;
	}
	
	private static HSSFCell createCell(HSSFRow row, int columnNumber, HSSFCellStyle bodyCellStyle, Object value) {
		HSSFCell cell = row.createCell(columnNumber);
		cell.setCellValue(value == null ? "" : String.valueOf(value));
		cell.setCellStyle(bodyCellStyle);;
		return cell;
	}
	
	private static Font createFont(HSSFWorkbook workbook, short fontSize, boolean isBold) {
		return createFont(workbook, null, fontSize, null, isBold, null, null, null);
	}
	
	private static Font createFont(HSSFWorkbook workbook, String fontName, Short fontSize, Short color,
			Boolean isBold, Boolean isItalic, Boolean isUnderlined, Boolean isStrikedout) {
		Font font = workbook.createFont();
		if (StringUtils.isNotEmpty(fontName)) {
			font.setFontName(fontName);
		}
		if (isBold != null) {
			font.setBoldweight(isBold ? Font.BOLDWEIGHT_BOLD : Font.BOLDWEIGHT_NORMAL);
		}
		if (fontSize != null) {
			font.setFontHeightInPoints(fontSize);
		}
		if (color != null) {
			font.setColor(color);
		}
		if (isItalic != null) {
			font.setItalic(isItalic);
		}
		if (isUnderlined != null) {
			font.setUnderline(isUnderlined ? Font.U_NONE: Font.U_SINGLE);
		}
		if (isStrikedout != null) {
			font.setStrikeout(isStrikedout);
		}
		return font;
	}
	
	private static HSSFCellStyle createCellStyle(HSSFWorkbook workbook, Font font, Short alignment,
			Short verticalAlignment, Boolean wrapText) {
		return createCellStyle(workbook, font, null, null, alignment, verticalAlignment, wrapText, null, null);
	}
	
	private static HSSFCellStyle createCellStyle(HSSFWorkbook workbook, Font font, Short borderLocation, Short borderType,
			Short alignment, Short verticalAlignment, Boolean wrapText, Short backgroundColor, Short fillStyle) {
		
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		if (font != null) {
			cellStyle.setFont(font);
		}
		if (alignment != null) {
			cellStyle.setAlignment(alignment);
		}
		if (verticalAlignment != null) {
			cellStyle.setVerticalAlignment(verticalAlignment);
		}
		if (borderLocation != null && borderType != null) {
			if ((borderLocation & BORDER_TOP) > 0) {
				cellStyle.setBorderTop(borderType);
			}
			if ((borderLocation & BORDER_BOTTOM) > 0) {
				cellStyle.setBorderBottom(borderType);
			}
			if ((borderLocation & BORDER_LEFT) > 0) {
				cellStyle.setBorderLeft(borderType);
			}
			if ((borderLocation & BORDER_RIGHT) > 0) {
				cellStyle.setBorderRight(borderType);
			}
		}
		if (wrapText != null) {
			cellStyle.setWrapText(wrapText);
		}
		if (backgroundColor != null) {
			cellStyle.setFillBackgroundColor(backgroundColor);
		}
		if (fillStyle != null) {
			cellStyle.setFillPattern(fillStyle);
		}
		return cellStyle;
	}
	
	private static short getNumberOfLines(String cellValue, int colunmWidth, Font font) {
		// TODO: adjust line width based on font width
		if (StringUtils.isBlank(cellValue)) {
			return 1;
		}
		short lines = 0;
		String[] data = StringUtils.split(cellValue, "\n\r");
		for (String line: data) {
			lines += 1 + (line.length() / ((colunmWidth / 256) - 1));
		}
		return lines;
	}
	
	private static int prepareXls(HSSFWorkbook workbook, HSSFSheet worksheet, int rowIndex, ReportModel<? extends ReportBean<?>> model, boolean mainModel) {
		
		// Set column widths
		for (int i = 0; i < model.getColumnCount(); i++) {
			int colSize = model.getColumn(i).size() * 256;
			if (colSize > worksheet.getColumnWidth(i)) {
				worksheet.setColumnWidth(i, colSize);				
			}
		}

		if (mainModel) {
			// Create cell style for the report title
			HSSFCellStyle cellStyleTitle = createCellStyle(workbook, createFont(workbook, (short)12, true), CellStyle.ALIGN_CENTER,
					CellStyle.VERTICAL_CENTER, true);
			// Create report title
			HSSFRow rowTitle = createRow(worksheet, rowIndex, 25f);
			createCell(rowTitle, 0, cellStyleTitle, model.getReportHeader().getReportName());
			// Create merged region for the report title
			worksheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,0,model.getColumnCount() - 1));

			// Create report subtitle
			Font rowSubTitleFont = createFont(workbook, (short)11, true);
			cellStyleTitle = createCellStyle(workbook, rowSubTitleFont, CellStyle.ALIGN_CENTER,
					CellStyle.VERTICAL_CENTER, true);
			HSSFRow rowSubTitle = createRow(worksheet, ++rowIndex, 25f);
			String subReportName = model.getReportHeader().getSubReportName();
			createCell(rowSubTitle, 0, cellStyleTitle, subReportName);
			// Create merged region for the report subtitle
			worksheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,0,model.getColumnCount() - 1));
			short cellWidth = 0;
			for (int i = 0; i < model.getColumnCount() ; i++) {
				cellWidth += worksheet.getColumnWidth(i);
			}
			short numLines = getNumberOfLines(subReportName, cellWidth,rowSubTitleFont);
			if (numLines > 1) {
				rowSubTitle.setHeightInPoints(rowSubTitle.getHeightInPoints() * numLines);	
			}


			// empty line
			createRow(worksheet, ++rowIndex, 10f);
			worksheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,0,model.getColumnCount() - 1));
			
			// Request details:
			HSSFCellStyle cellStyleHeaderParam = createCellStyle(workbook, createFont(workbook, (short)11, true), null, null,
					CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, false, null, null);
			HSSFCellStyle cellStyleHeaderValue = createCellStyle(workbook, createFont(workbook, (short)11, false), CellStyle.ALIGN_LEFT,
					CellStyle.VERTICAL_CENTER, false);
			
			HSSFRow rowHeader = createRow(worksheet, ++rowIndex, 25f);
			createCell(rowHeader, 0, cellStyleHeaderParam, "Requested by:");
			createCell(rowHeader, 1, cellStyleHeaderValue, UtilityService.getUsername());
			worksheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,1,model.getColumnCount() - 1));
			rowHeader = createRow(worksheet, ++rowIndex, 25f);
			createCell(rowHeader, 0, cellStyleHeaderParam, "Generated on:");
			createCell(rowHeader, 1, cellStyleHeaderValue, DateAndTimeUtils.formatDateTimeUsingConfig(UtilityService.getStoreName(), model.getReportHeader().getDate()));
			worksheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,1,model.getColumnCount() - 1));
		}

		// empty line
		createRow(worksheet, ++rowIndex, 10f);
		worksheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,0,model.getColumnCount() - 1));
		
		/* Column Headers */
		HSSFCellStyle headerCellStyle = createCellStyle(workbook, createFont(workbook, (short)10, true), BORDER_BOTTOM, CellStyle.BORDER_THIN,
				CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, true, HSSFColor.GREY_25_PERCENT.index, CellStyle.FINE_DOTS);
		HSSFRow rowHeader = createRow(worksheet, ++rowIndex, 25f);
		for (int i = 0; i < model.getColumnCount(); i++) {
			createCell(rowHeader, i, headerCellStyle, model.getColumn(i).label());
		}
		
		/* Data */
		Font bodyFont = createFont(workbook, (short)10, false);
		HSSFCellStyle bodyCellStyle = createCellStyle(workbook, bodyFont, null, null,
				CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, true, null, null);
		for (int i = 0; i < model.getNumberOfRecords(); i++) {
			short lines = 1;
			HSSFRow row = createRow(worksheet, ++rowIndex, 15f);
			for (int j = 0; j < model.getColumnCount(); j++) {
				HSSFCell cell = createCell(row, j, bodyCellStyle, model.getCell(i,j));
				short numLines = getNumberOfLines(cell.getStringCellValue(), worksheet.getColumnWidth(j), bodyFont);
				if (lines < numLines) {
					lines = numLines;
				}
			}
			if (lines > 1) {
				row.setHeightInPoints(row.getHeightInPoints() * lines);			
			}
		}
		
		return rowIndex;
	}
	
	/**
	 * Processes the download for Excel format.
	 * It does the following steps:
	 * <pre>1. Create new workbook
	 * 2. Create new worksheet
	 * 3. Define starting indices for rows and columns
	 * 4. Build layout
	 * 5. Fill report
	 * 6. Set the HttpServletResponse properties
	 * 7. Write to the output stream
	 * </pre>
	 */
	public void downloadXLS(HttpServletResponse response, ReportModel<? extends ReportBean<?>> mainModel, 
			List<ReportModel<? extends ReportBean<?>>> subModels) throws ClassNotFoundException {
		logger.debug("Downloading Excel report");
		// 1. Create new workbook
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 2. Create new worksheet
		HSSFSheet worksheet = workbook.createSheet("Data");
		int rowIndex = 0;
		// 3. prepare worksheet
		
		rowIndex = prepareXls(workbook, worksheet, rowIndex, mainModel, true);
		String fileName = mainModel.getReportHeader().getFileName() + ".xls";
		
		if (subModels != null) {
			for (ReportModel<? extends ReportBean<?>> model: subModels) {
				rowIndex++;
				rowIndex = prepareXls(workbook, worksheet, rowIndex, model, false);
			}
		}
		
		response.setHeader("Content-Disposition", "inline; filename=" + fileName);
		// Make sure to set the correct content type
		response.setContentType("application/vnd.ms-excel");
		// Write to the output stream
		
		logger.debug("Writing report to the stream");
		ServletOutputStream outputStream  = null;
		try {
			// Retrieve the output stream
			outputStream = response.getOutputStream();
			// Write to the output stream
			worksheet.getWorkbook().write(outputStream);
			// Flush the stream
			outputStream.flush();

		} catch (Exception e) {
			logger.error("Unable to write report to the output stream");
		} finally {
			try { if (outputStream != null) outputStream.close(); } catch (IOException e) { } 
		}
		
	}
}
