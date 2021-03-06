package com.search.manager.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.jodatime.JodaPatternType;
import com.search.manager.report.model.ReportBean;
import com.search.manager.report.model.ReportModel;
import com.search.manager.report.model.SubReportHeader;

/**
 * Service for processing Apache POI-based reports
 *
 */
@Service("downloadService")
public class DownloadService {

	@Autowired
	private UtilityService utilityService;
	@Autowired
	private JodaDateTimeUtil jodaDateTimeUtil;
	
	// TODO: code cleanup: create one class that handles all excel related stuff
	public enum downloadType {
		EXCEL, PDF, CSV, XML
	}
	private static final Logger logger =
			LoggerFactory.getLogger(DownloadService.class);
	private final static short BORDER_TOP = (short) 0x01;
	private final static short BORDER_BOTTOM = (short) 0x02;
	private final static short BORDER_LEFT = (short) 0x04;
	private final static short BORDER_RIGHT = (short) 0x08;

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
		cell.setCellStyle(bodyCellStyle);
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
			font.setUnderline(isUnderlined ? Font.U_NONE : Font.U_SINGLE);
		}
		if (isStrikedout != null) {
			font.setStrikeout(isStrikedout);
		}
		return font;
	}

	private static HSSFCellStyle getCellStyle(HSSFWorkbook workbook, Map<String, HSSFCellStyle> styleMap, String styleName, Font font, Short alignment,
			Short verticalAlignment, Boolean wrapText) {
		return getCellStyle(workbook, styleMap, styleName, font, null, null, alignment, verticalAlignment, wrapText, null, null);
	}

	private static HSSFCellStyle getCellStyle(HSSFWorkbook workbook, Map<String, HSSFCellStyle> styleMap, String styleName, Font font, Short borderLocation, Short borderType,
			Short alignment, Short verticalAlignment, Boolean wrapText, Short backgroundColor, Short fillStyle) {

		HSSFCellStyle cellStyle = styleMap.get(styleName);

		if (cellStyle == null) {
			cellStyle = workbook.createCellStyle();
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
				cellStyle.setFillForegroundColor(backgroundColor);
			}
			if (fillStyle != null) {
				cellStyle.setFillPattern(fillStyle);
			}
			styleMap.put(styleName, cellStyle);
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
		for (String line : data) {
			lines += 1 + (line.length() / ((colunmWidth / 256) - 1));
		}
		return lines;
	}

	private int prepareXls(HSSFWorkbook workbook, HSSFSheet worksheet, Map<String, HSSFCellStyle> styleMap,
			int rowIndex, ReportModel<? extends ReportBean<?>> model, boolean mainModel, boolean writeSubHeader) {
		// Set column widths
		for (int i = 0; i < model.getColumnCount(); i++) {
			int colSize = model.getColumn(i).size() * 256;
			if (colSize > worksheet.getColumnWidth(i)) {
				worksheet.setColumnWidth(i, colSize);
			}
		}

		if (mainModel) {
			rowIndex = writeHeader(workbook, worksheet, styleMap, rowIndex, model);
		}

		if (writeSubHeader) {
			rowIndex = writeSubHeader(workbook, worksheet, styleMap, rowIndex, model);
		}

		if (model.getNumberOfRecords() > 0) {
			rowIndex = writeDetails(workbook, worksheet, styleMap, rowIndex, model);
		}

		return rowIndex;
	}

	private int writeHeader(HSSFWorkbook workbook, HSSFSheet worksheet, Map<String, HSSFCellStyle> styleMap,
			int rowIndex, ReportModel<? extends ReportBean<?>> model) {

		// Create cell style for the report title
		HSSFCellStyle cellStyleTitle = getCellStyle(workbook, styleMap, "TITLE", createFont(workbook, (short) 12, true), CellStyle.ALIGN_CENTER,
				CellStyle.VERTICAL_CENTER, true);
		// Create report title
		HSSFRow rowTitle = createRow(worksheet, rowIndex, 25f);
		createCell(rowTitle, 0, cellStyleTitle, replaceValues(model.getReportHeader().getReportName(), model.getReportHeader().getDate()));
		// Create merged region for the report title
		worksheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, model.getColumnCount() - 1));

		// Create report subtitle
		Font rowSubTitleFont = createFont(workbook, (short) 11, true);
		HSSFCellStyle cellStyleSubTitle = getCellStyle(workbook, styleMap, "SUBTITLE", rowSubTitleFont, CellStyle.ALIGN_CENTER,
				CellStyle.VERTICAL_CENTER, true);
		HSSFRow rowSubTitle = createRow(worksheet, ++rowIndex, 25f);
		String subReportName = replaceValues(model.getReportHeader().getSubReportName(), model.getReportHeader().getDate());
		createCell(rowSubTitle, 0, cellStyleSubTitle, subReportName);
		// Create merged region for the report subtitle
		worksheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, model.getColumnCount() - 1));
		short cellWidth = 0;
		for (int i = 0; i < model.getColumnCount(); i++) {
			cellWidth += worksheet.getColumnWidth(i);
		}
		short numLines = getNumberOfLines(subReportName, cellWidth, rowSubTitleFont);
		if (numLines > 1) {
			rowSubTitle.setHeightInPoints(rowSubTitle.getHeightInPoints() * numLines);
		}

		// empty line
		createRow(worksheet, ++rowIndex, 10f);
		worksheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, model.getColumnCount() - 1));

		// Request details:
		HSSFCellStyle cellStyleHeaderParam = getCellStyle(workbook, styleMap, "HEADER_NAME", createFont(workbook, (short) 11, true), null, null,
				CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, false, null, null);
		HSSFCellStyle cellStyleHeaderValue = getCellStyle(workbook, styleMap, "HEADER_VALUE", createFont(workbook, (short) 11, false), CellStyle.ALIGN_LEFT,
				CellStyle.VERTICAL_CENTER, false);
		HSSFRow rowHeader = createRow(worksheet, ++rowIndex, 25f);
		createCell(rowHeader, 0, cellStyleHeaderParam, "Requested by:");
		createCell(rowHeader, 1, cellStyleHeaderValue, utilityService.getUsername());
		worksheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 1, model.getColumnCount() - 1));
		rowHeader = createRow(worksheet, ++rowIndex, 25f);
		createCell(rowHeader, 0, cellStyleHeaderParam, "Generated on:");
//		createCell(rowHeader, 1, cellStyleHeaderValue, dateAndTimeUtils.formatDateTimeUsingConfig(utilityService.getStoreId(), model.getReportHeader().getDate()));
		createCell(rowHeader, 1, cellStyleHeaderValue, jodaDateTimeUtil.formatFromStorePattern(utilityService.getStoreId(), model.getReportHeader().getDate(), JodaPatternType.DATE));
		worksheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 1, model.getColumnCount() - 1));
		return rowIndex;
	}

	private static int writeSubHeader(HSSFWorkbook workbook, HSSFSheet worksheet, Map<String, HSSFCellStyle> styleMap,
			int rowIndex, ReportModel<? extends ReportBean<?>> model) {
		// empty line
		createRow(worksheet, ++rowIndex, 10f);
		worksheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, model.getColumnCount() - 1));

		HSSFCellStyle cellStyleHeaderParam = getCellStyle(workbook, styleMap, "SUBREPORT_HEADER_NAME", createFont(workbook, (short) 11, true),
				null, null, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, false, null, null);
		HSSFCellStyle cellStyleHeaderValue = getCellStyle(workbook, styleMap, "SUBREPORT_HEADER_VALUE", createFont(workbook, (short) 11, false),
				CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, false);
		
		/* Record Headers */
		SubReportHeader subReportHeader = model.getSubReportHeader();
		if (subReportHeader != null && subReportHeader.getRows() != null) {
			Map<String, String> items = subReportHeader.getRows();
		
			for (Map.Entry<String, String> entry : items.entrySet()) {
				HSSFRow rowHeader = createRow(worksheet, ++rowIndex, 25f);
				createCell(rowHeader, 0, cellStyleHeaderParam, entry.getKey());
				createCell(rowHeader, 1, cellStyleHeaderValue, entry.getValue());
				worksheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 1, model.getColumnCount() - 1));
			}
		}
		
		/* Column Headers */
		HSSFCellStyle headerCellStyle = getCellStyle(workbook, styleMap, "COLUMN_NAME", createFont(workbook, (short) 10, true), BORDER_BOTTOM, CellStyle.BORDER_THIN,
				CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, true, HSSFColor.GREY_40_PERCENT.index, HSSFCellStyle.SOLID_FOREGROUND);
		HSSFRow rowHeader = createRow(worksheet, ++rowIndex, 25f);
		for (int i = 0; i < model.getColumnCount(); i++) {
			createCell(rowHeader, i, headerCellStyle, model.getColumn(i).label());
		}

		return rowIndex;
	}

	private static int writeDetails(HSSFWorkbook workbook, HSSFSheet worksheet, Map<String, HSSFCellStyle> styleMap,
			int rowIndex, ReportModel<? extends ReportBean<?>> model) {
		/* Data */
		Font bodyFont = createFont(workbook, (short) 10, false);
		HSSFCellStyle bodyCellStyle = getCellStyle(workbook, styleMap, "COLUMN_VALUE", bodyFont, null, null,
				CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, true, null, null);
		for (int i = 0; i < model.getNumberOfRecords(); i++) {
			short lines = 1;
			HSSFRow row = createRow(worksheet, ++rowIndex, 15f);
			for (int j = 0; j < model.getColumnCount(); j++) {
				HSSFCell cell = createCell(row, j, bodyCellStyle, model.getCell(i, j));
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
	 * Processes the download for Excel format. It does the following steps:
	 * <ol>
	 * <li>Create new workbook</li>
	 * <li>Create new worksheet</li>
	 * <li>Define starting indices for rows and columns</li>
	 * <li>Build layout</li>
	 * <li>Fill report</li>
	 * <li>Set the HttpServletResponse properties</li>
	 * <li>Write to the output stream</li>
	 * </ol>
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
		Map<String, HSSFCellStyle> styleMap = new HashMap<String, HSSFCellStyle>();
		rowIndex = prepareXls(workbook, worksheet, styleMap, rowIndex, mainModel, true,
				CollectionUtils.isEmpty(subModels) || mainModel.isShowSubReportHeader());
		String fileName = mainModel.getReportHeader().getFileName() + ".xls";

		if (subModels != null) {
			for (ReportModel<? extends ReportBean<?>> model : subModels) {
				rowIndex++;
				rowIndex = prepareXls(workbook, worksheet, styleMap, rowIndex, model, false, true);
			}
		}

		download(response, workbook, fileName);
	}

	/**
	 * Processes the download for Excel format. It does the following steps:
	 * <pre>1. Create new workbook
	 * 2. Create new worksheet
	 * 3. Define starting indices for rows and columns
	 * 4. Build layout
	 * 5. Fill report
	 * 6. Set the HttpServletResponse properties
	 * 7. Write to the output stream
	 * </pre>
	 */
	public void downloadMultiSheetXLS(HttpServletResponse response, ReportModel<? extends ReportBean<?>> mainModel,
			List<ReportModel<? extends ReportBean<?>>> subModels) throws ClassNotFoundException {
		logger.debug("Downloading Excel report");
		// 1. Create new workbook
		HSSFWorkbook workbook = new HSSFWorkbook();
		String fileName = mainModel.getReportHeader().getFileName() + ".xls";
		Map<String, HSSFCellStyle> styleMap = new HashMap<String, HSSFCellStyle>();

		if (subModels != null) {
			for (ReportModel<? extends ReportBean<?>> model : subModels) {
				model.getSubReportHeader().getRows().get("Version No:");
				// 2. Create new worksheet
				HSSFSheet worksheet = workbook.createSheet("Version " + model.getSubReportHeader().getVersion());
				prepareXls(workbook, worksheet, styleMap, 0, model, true, true);
			}
		}

		download(response, workbook, fileName);
	}

    public void downloadMultiSheets(HttpServletResponse response, ReportModel<? extends ReportBean<?>> mainModel,
            List<ReportModel<? extends ReportBean<?>>> subModels) throws ClassNotFoundException {
        logger.debug("Downloading Excel report");
        // 1. Create new workbook
        HSSFWorkbook workbook = new HSSFWorkbook();
        String fileName = mainModel.getReportHeader().getFileName() + ".xls";
        Map<String, HSSFCellStyle> styleMap = new HashMap<String, HSSFCellStyle>();

        if (subModels != null) {
            for (ReportModel<? extends ReportBean<?>> model : subModels) {
                // 2. Create new worksheet
                HSSFSheet worksheet = workbook.createSheet("" + model.getSubReportHeader().getFileName());
                prepareXls(workbook, worksheet, styleMap, 0, model, true, true);
            }
        }

        download(response, workbook, fileName);
    }
	
	public void download(HttpServletResponse response, HSSFWorkbook workbook, String fileName) throws ClassNotFoundException {
		logger.debug("Downloading Excel report");
		// Make sure to set the correct content type
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		// Write to the output stream

		logger.debug("Writing report to the stream");
		ServletOutputStream outputStream  = null;
		try {
			// Retrieve the output stream
			outputStream = response.getOutputStream();
			// Write to the output stream
			workbook.write(outputStream);
			// Flush the stream
			outputStream.flush();

		} catch (Exception e) {
			logger.error("Unable to write report to the output stream");
		} finally {
			try { if (outputStream != null) outputStream.close(); } catch (IOException e) { } 
		}
	}
	
	private String replaceValues(String string, DateTime dateTime) {
		return StringUtils.replace(StringUtils.replace(
				StringUtils.replace(string, "%%StoreName%%",
						utilityService.getStoreName()), "%%User%%",
				utilityService.getUsername()), "%%Date%%",
				jodaDateTimeUtil.formatFromStorePattern(utilityService.getStoreId(), dateTime,
						JodaPatternType.DATE));
	}
	
}