package com.search.manager.report.model;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.search.manager.model.ElevateProduct;
import com.search.manager.report.annotation.ReportField;

/**
 * ReportModel represents the records in the report being generated.
 * Only content should be defined here and not presentation.
 * Presentation should be managed/handled by whatever service is using this model.
 */
public class ReportModel<T extends ReportBean<?>> {

	private static final Logger logger = Logger.getLogger(ReportModel.class);

	private List<T> records;
    private Class<T> beanClass;
    
    private ReportHeader reportHeader;
    private SubReportHeader subHeader;
    
    private List<ReportField> reportFields = new ArrayList<ReportField>();
    private Object[][] data;
    
    private Comparator<ReportField> reportFieldComparator = new Comparator<ReportField>() {
		@Override
		public int compare(ReportField field1, ReportField field2) {
			return field1.sortOrder() - field2.sortOrder();
		}
    };
		
	public ReportModel(ReportHeader reportHeader, Class<T> type, List<T> records) {
		this.reportHeader = reportHeader;
		this.records = records;
		this.beanClass = type;
		process();
	}
	
	private void process() {
		Map<ReportField, Method> methodMap = new HashMap<ReportField, Method>();
		for (Method method: beanClass.getMethods()) {
			ReportField field = method.getAnnotation(ReportField.class);
			if (field != null) {
				methodMap.put(field, method);
			}
		}
		int recordCount = records == null ? 0 : records.size();
		int columnCount = methodMap.size();
		
		reportFields.addAll(methodMap.keySet());

		Collections.sort(reportFields, reportFieldComparator);
		
		List<ReportField> columns = getColumns();
		data = new Object[recordCount][columnCount];
		for (int i = 0; i < recordCount; i++) {
			for (int j = 0; j < columnCount; j++) {
				try {
					data[i][j] = methodMap.get(columns.get(j)).invoke(records.get(i));
				} catch (Exception e) {
					logger.error("Failed to get value for field", e);
				}
			}
		}
	}

	public List<ReportField> getColumns() {
		return new ArrayList<ReportField>(reportFields);
	}
	
	public Object[] getRecord(int i) {
		if (data != null && i < data.length) {
			return data[i];
		}
		return null;
	}
	
	public Object getCell(int i, int j) {
		if (data != null && i < data.length && j < reportFields.size()) {
			return data[i][j];
		}
		return null;
	}
	
	public ReportField getColumn(int i) {
		return reportFields.get(i);
	}
	
	public int getColumnCount() {
		return (reportFields == null) ? 0 : reportFields.size();
	}
	
	public int getNumberOfRecords() {
		return (data == null) ? 0 : data.length;
	}
	
	/* Testing */
	public final static void main(String[] args) {
		
		List<ElevateReportBean> list = new ArrayList<ElevateReportBean>();

		ElevateProduct elevate = new ElevateProduct("DP1", "EDP1", "Apple", "Apple-1",
				"iPod", "Music Player", null, new Date(), new Date(),
				new Date(), "1st item", "admin", "admin", 1);
		ElevateReportBean bean = new ElevateReportBean(elevate);
		list.add(bean);

		elevate = new ElevateProduct("DP2", "EDP2", "Apple", "Apple-2",
				"Macbook Pro", "Portable Laptop", null, new Date(), new Date(),
				new Date(), "2nd item", "user", "user", 1);
		bean = new ElevateReportBean(elevate);
		list.add(bean);

//		ReportModel<ElevateReportBean> model = new ReportModel<ElevateReportBean>(ElevateReportBean.class, list);
		ReportHeader reportHeader = new ReportHeader("Search GUI (%%STORE%%)", "List of Elevated Items", "elevate", new Date());
		ElevateReportModel model = new ElevateReportModel(reportHeader, list);

		for (int i = 0; i < model.getColumnCount(); i++) {
			ReportField field = model.getColumn(i);
			System.out.print(String.format("%" + 20/*field.width()*/ + "s", field.label()));
		}
		System.out.println();
		for (int i = 0; i < model.getNumberOfRecords(); i++) {
			for (Object object: model.getRecord(i)) {
				System.out.print(String.format("%" + 20/*columns.get(j).width()*/ + "s", object));
			}
			System.out.println();
		}
	}

	public ReportHeader getReportHeader() {
		return reportHeader;
	}

}
