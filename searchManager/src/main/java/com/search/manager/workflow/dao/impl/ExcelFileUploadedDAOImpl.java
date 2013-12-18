package com.search.manager.workflow.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.stereotype.Repository;

import com.search.manager.aop.Audit;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.sp.CUDStoredProcedure;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.dao.sp.GetStoredProcedure;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.constants.AuditTrailConstants.Entity;
import com.search.manager.model.constants.AuditTrailConstants.Operation;
import com.search.manager.workflow.dao.ExcelFileUploadedDAO;
import com.search.reports.manager.model.ExcelFileReport;
import com.search.reports.manager.model.ExcelFileUploaded;

@Repository(value = "excelFileUploadedDAO")
public class ExcelFileUploadedDAOImpl implements ExcelFileUploadedDAO{

	// needed by spring AOP
	public ExcelFileUploadedDAOImpl() {
	}
	@Autowired
	JodaDateTimeUtil jodaDateTimeUtil;
	
	@Autowired
	public ExcelFileUploadedDAOImpl(JdbcTemplate jdbcTemplate) {
		getExcelFileUploadedSP = new GetExcelFileUploadedSP(jdbcTemplate);
		getExcelFileReportSP = new GetExcelFileReportSP(jdbcTemplate);
		addExcelFileUploadedSP = new AddExcelFileUploadedSP(jdbcTemplate);
		addExcelFileReportSP = new AddExcelFileReportSP(jdbcTemplate);
		deleteExcelFileUploadedSP = new DeleteExcelFileUploadedSP(jdbcTemplate);
		updateExcelFileUploadedSP = new UpdateExcelFileUploadedSP(jdbcTemplate);
	}

	private GetExcelFileUploadedSP getExcelFileUploadedSP;
	private GetExcelFileReportSP getExcelFileReportSP;
	private AddExcelFileUploadedSP addExcelFileUploadedSP;
	private AddExcelFileReportSP addExcelFileReportSP;
	private DeleteExcelFileUploadedSP deleteExcelFileUploadedSP;
	private UpdateExcelFileUploadedSP updateExcelFileUploadedSP;

	static class CommonExcelFileUploaded{
		public static ExcelFileUploaded getExcelFileUploaded(ResultSet rs,JodaDateTimeUtil jodaDateTimeUtil) throws SQLException{
			return new ExcelFileUploaded(
					rs.getString(DAOConstants.COLUMN_EXCEL_FILE_UPLOADED_ID),
					rs.getString(DAOConstants.COLUMN_STORE_ID),		
					rs.getInt(DAOConstants.COLUMN_RULE_TYPE_ID),
					rs.getString(DAOConstants.COLUMN_FILE_NAME),
					jodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_ADDED_ON_RULE_STAMP)),
					rs.getString(DAOConstants.COLUMN_ADDED_ON_RULE_BY),
					rs.getString(DAOConstants.COLUMN_CREATED_BY),
					jodaDateTimeUtil.toDateTime(rs
							.getTimestamp(DAOConstants.COLUMN_CREATED_STAMP)),
					jodaDateTimeUtil.toDateTime(rs
							.getTimestamp(DAOConstants.COLUMN_CREATED_TX_STAMP)),
					rs.getString(DAOConstants.COLUMN_LAST_UPDATED_BY),
					jodaDateTimeUtil.toDateTime(rs
							.getTimestamp(DAOConstants.COLUMN_LAST_UPDATED_STAMP)),
					jodaDateTimeUtil.toDateTime(rs
							.getTimestamp(DAOConstants.COLUMN_LAST_UPDATED_TX_STAMP)));
		}
	}
	static class CommonExcelFileReport{
		public static ExcelFileReport getExcelFileReport(ResultSet rs,JodaDateTimeUtil jodaDateTimeUtil) throws SQLException{
			return new ExcelFileReport(
					rs.getString(DAOConstants.COLUMN_EXCEL_FILE_UPLOADED_ID),
					rs.getString(DAOConstants.COLUMN_STORE_ID),
					rs.getInt(DAOConstants.COLUMN_RULE_TYPE_ID),
					rs.getString(DAOConstants.COLUMN_KEYWORD),
					rs.getString(DAOConstants.COLUMN_RANK),
					rs.getString(DAOConstants.COLUMN_SKU),
					rs.getString(DAOConstants.COLUMN_NAME),
					jodaDateTimeUtil.toDateTime(rs
							.getTimestamp(DAOConstants.COLUMN_EXPIRATION)),
					rs.getString(DAOConstants.COLUMN_CREATED_BY),
					jodaDateTimeUtil.toDateTime(rs
							.getTimestamp(DAOConstants.COLUMN_CREATED_STAMP)),
					jodaDateTimeUtil.toDateTime(rs
							.getTimestamp(DAOConstants.COLUMN_CREATED_TX_STAMP)),
					rs.getString(DAOConstants.COLUMN_LAST_UPDATED_BY),
					jodaDateTimeUtil.toDateTime(rs
							.getTimestamp(DAOConstants.COLUMN_LAST_UPDATED_STAMP)),
					jodaDateTimeUtil.toDateTime(rs
							.getTimestamp(DAOConstants.COLUMN_LAST_UPDATED_TX_STAMP)));
		}
	}
	
	private class AddExcelFileUploadedSP extends GetStoredProcedure {
		public AddExcelFileUploadedSP(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_ADD_EXCEL_FILE_UPLOADED);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(
					DAOConstants.PARAM_EXCEL_FILE_UPLOADED_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_TYPE_ID,
					Types.INTEGER));			
			declareParameter(new SqlParameter(DAOConstants.PARAM_FILE_NAME,
					Types.VARCHAR));			
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_STAMP,
					Types.DATE));			
		}
		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1,
					new RowMapper<ExcelFileUploaded>() {
						public ExcelFileUploaded mapRow(ResultSet rs, int rowNum)
								throws SQLException {
								return CommonExcelFileUploaded.getExcelFileUploaded(rs,jodaDateTimeUtil);
						}
					}));
		}		
	}

	private class AddExcelFileReportSP extends GetStoredProcedure {
		public AddExcelFileReportSP(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_ADD_EXCEL_FILE_REPORT);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(
					DAOConstants.PARAM_EXCEL_FILE_UPLOADED_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_TYPE_ID,
					Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RANK,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_SKU,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_NAME,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_EXPIRATION,
					Types.DATE));
			declareParameter(new SqlParameter(
					DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));			
		}
		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1,
					new RowMapper<ExcelFileReport>() {
						public ExcelFileReport mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							return CommonExcelFileReport.getExcelFileReport(rs,jodaDateTimeUtil);
						}
					}));
		}		
	}

	private class DeleteExcelFileUploadedSP extends CUDStoredProcedure {
		public DeleteExcelFileUploadedSP(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_DELETE_EXCEL_FILE_UPLOADED);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(
					DAOConstants.PARAM_EXCEL_FILE_UPLOADED_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_FILE_NAME,
					Types.VARCHAR));
		}
	}
	
	private class UpdateExcelFileUploadedSP extends GetStoredProcedure {
		public UpdateExcelFileUploadedSP(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_UPDATE_EXCEL_FILE_UPLOADED);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(
					DAOConstants.PARAM_EXCEL_FILE_UPLOADED_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_ADDED_ON_RULE_BY,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_ADDED_ON_RULE_DATE,
					Types.DATE));			
		}
		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1,
					new RowMapper<ExcelFileUploaded>() {
						public ExcelFileUploaded mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							return CommonExcelFileUploaded.getExcelFileUploaded(rs,jodaDateTimeUtil);
						}
					}));
		}		
	}	
		
	private class GetExcelFileUploadedSP extends GetStoredProcedure {
		public GetExcelFileUploadedSP(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_GET_EXCEL_FILE_UPLOADED);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(
					DAOConstants.PARAM_EXCEL_FILE_UPLOADED_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_TYPE_ID,
					Types.INTEGER));			
			declareParameter(new SqlParameter(DAOConstants.PARAM_FILE_NAME,
					Types.VARCHAR));			
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW,
					Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW,
					Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1,
					new RowMapper<ExcelFileUploaded>() {
						public ExcelFileUploaded mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							return CommonExcelFileUploaded.getExcelFileUploaded(rs,jodaDateTimeUtil);
						}
					}));
		}
	}

	private class GetExcelFileReportSP extends GetStoredProcedure {
		public GetExcelFileReportSP(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_GET_EXCEL_FILE_REPORT);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(
					DAOConstants.PARAM_EXCEL_FILE_UPLOADED_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID,
					Types.VARCHAR));			
			declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_TYPE_ID,
					Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_KEYWORD,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_RANK,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_SKU,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_NAME,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_EXPIRATION,
					Types.DATE));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW,
					Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW,
					Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1,
					new RowMapper<ExcelFileReport>() {
						public ExcelFileReport mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							return new ExcelFileReport(
									rs.getString(DAOConstants.COLUMN_EXCEL_FILE_UPLOADED_ID),
									rs.getString(DAOConstants.COLUMN_STORE_ID),
									rs.getInt(DAOConstants.COLUMN_RULE_TYPE_ID),
									rs.getString(DAOConstants.COLUMN_KEYWORD),
									rs.getString(DAOConstants.COLUMN_RANK),
									rs.getString(DAOConstants.COLUMN_SKU),
									rs.getString(DAOConstants.COLUMN_NAME),
									jodaDateTimeUtil.toDateTime(rs
											.getTimestamp(DAOConstants.COLUMN_EXPIRATION)),
									rs.getString(DAOConstants.COLUMN_CREATED_BY),
									jodaDateTimeUtil.toDateTime(rs
											.getTimestamp(DAOConstants.COLUMN_CREATED_STAMP)),
									jodaDateTimeUtil.toDateTime(rs
											.getTimestamp(DAOConstants.COLUMN_CREATED_TX_STAMP)),
									rs.getString(DAOConstants.COLUMN_LAST_UPDATED_BY),
									jodaDateTimeUtil.toDateTime(rs
											.getTimestamp(DAOConstants.COLUMN_LAST_UPDATED_STAMP)),
									jodaDateTimeUtil.toDateTime(rs
											.getTimestamp(DAOConstants.COLUMN_LAST_UPDATED_TX_STAMP)));
						}
					}));
		}
	}	

	public RecordSet<ExcelFileUploaded> getExcelFileUploadeds(
			SearchCriteria<ExcelFileUploaded> criteria) throws DaoException {
		try {
			ExcelFileUploaded excelFileUploaded = criteria.getModel();

			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_EXCEL_FILE_UPLOADED_ID,
					excelFileUploaded.getExcelFileUploadedId());
			inputs.put(DAOConstants.PARAM_STORE_ID,
					excelFileUploaded.getStoreId());
			inputs.put(DAOConstants.PARAM_FILE_NAME,
					excelFileUploaded.getFileName());
			inputs.put(DAOConstants.PARAM_RULE_TYPE_ID,
					excelFileUploaded.getRuleTypeId());			
			inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
			inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());
			return DAOUtils.getRecordSet(getExcelFileUploadedSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during getExcelFileUploadeds()",
					e);
		}
	}

	public ExcelFileUploaded getExcelFileUploaded(
			ExcelFileUploaded excelFileUploaded) throws DaoException {
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_EXCEL_FILE_UPLOADED_ID,
					excelFileUploaded.getExcelFileUploadedId());
			inputs.put(DAOConstants.PARAM_STORE_ID,
					excelFileUploaded.getStoreId());
			inputs.put(DAOConstants.PARAM_FILE_NAME,
					excelFileUploaded.getFileName());
			inputs.put(DAOConstants.PARAM_RULE_TYPE_ID,
					excelFileUploaded.getRuleTypeId());			
			inputs.put(DAOConstants.PARAM_START_ROW, null);
			inputs.put(DAOConstants.PARAM_END_ROW, null);
			return DAOUtils.getItem(getExcelFileUploadedSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during getExcelFileUploaded()",
					e);
		}
	}
	
	public RecordSet<ExcelFileReport> getExcelFileReports(
			SearchCriteria<ExcelFileReport> criteria) throws DaoException {
		try {
			ExcelFileReport excelFileReport = criteria.getModel();
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_EXCEL_FILE_UPLOADED_ID,
					excelFileReport.getExcelFileUploadedId());
			inputs.put(DAOConstants.PARAM_STORE_ID,
					excelFileReport.getStoreId());
			inputs.put(DAOConstants.PARAM_RULE_TYPE_ID,
					excelFileReport.getRuleTypeId());
			inputs.put(DAOConstants.PARAM_RANK, excelFileReport.getRank());
			inputs.put(DAOConstants.PARAM_KEYWORD,
					excelFileReport.getKeyword());
			inputs.put(DAOConstants.PARAM_SKU, excelFileReport.getSku());
			inputs.put(DAOConstants.PARAM_NAME, excelFileReport.getName());
			inputs.put(DAOConstants.PARAM_EXPIRATION,
					excelFileReport.getExpiration());	
			inputs.put(DAOConstants.PARAM_START_ROW, criteria.getStartRow());
			inputs.put(DAOConstants.PARAM_END_ROW, criteria.getEndRow());			
			return DAOUtils
					.getRecordSet(getExcelFileReportSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during getExcelFileReport()", e);
		}
	}

	public ExcelFileReport getExcelFileReport(
			ExcelFileReport excelFileReport) throws DaoException {
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_EXCEL_FILE_REPORT_ID,
					excelFileReport.getExcelFileUploadedId());
			inputs.put(DAOConstants.PARAM_EXCEL_FILE_UPLOADED_ID,
					excelFileReport.getExcelFileUploadedId());
			inputs.put(DAOConstants.PARAM_STORE_ID,
					excelFileReport.getStoreId());
			inputs.put(DAOConstants.PARAM_RULE_TYPE_ID,
					excelFileReport.getRuleTypeId());
			inputs.put(DAOConstants.PARAM_RANK, excelFileReport.getRank());
			inputs.put(DAOConstants.PARAM_KEYWORD,
					excelFileReport.getKeyword());
			inputs.put(DAOConstants.PARAM_SKU, excelFileReport.getSku());
			inputs.put(DAOConstants.PARAM_EXPIRATION,
					excelFileReport.getExpiration());
			inputs.put(DAOConstants.PARAM_START_ROW, null);
			inputs.put(DAOConstants.PARAM_END_ROW, null);
			return DAOUtils.getItem(getExcelFileReportSP.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during getExcelFileReport()", e);
		}
	}

	@Audit(entity = Entity.excelFileUploaded, operation = Operation.add)
	public ExcelFileUploaded addExcelFileUploaded(ExcelFileUploaded excelFileUploaded)
			throws DaoException {
		try {	
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_EXCEL_FILE_UPLOADED_ID,
					excelFileUploaded.getExcelFileUploadedId());
			inputs.put(DAOConstants.PARAM_STORE_ID,
					excelFileUploaded.getStoreId());		
			inputs.put(DAOConstants.PARAM_FILE_NAME,
					excelFileUploaded.getFileName());
			inputs.put(DAOConstants.PARAM_RULE_TYPE_ID,excelFileUploaded.getRuleTypeId());				
			inputs.put(DAOConstants.PARAM_CREATED_BY,
					excelFileUploaded.getCreatedBy());
			inputs.put(DAOConstants.PARAM_CREATED_STAMP,
					excelFileUploaded.getCreatedStamp());			
			return DAOUtils.getItem(addExcelFileUploadedSP
					.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during addExcelFileUploadedSP()",
					e);
		}
	}

	@Audit(entity = Entity.excelFileReport, operation = Operation.add)
	public ExcelFileReport addExcelFileReport(ExcelFileReport excelFileReport)
			throws DaoException {
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_EXCEL_FILE_UPLOADED_ID,
					excelFileReport.getExcelFileUploadedId());
			inputs.put(DAOConstants.PARAM_STORE_ID,
					excelFileReport.getStoreId());
			inputs.put(DAOConstants.PARAM_RULE_TYPE_ID,
					excelFileReport.getRuleTypeId());
			inputs.put(DAOConstants.PARAM_KEYWORD,
					excelFileReport.getKeyword());			
			inputs.put(DAOConstants.PARAM_RANK, excelFileReport.getRank());
			inputs.put(DAOConstants.PARAM_SKU, excelFileReport.getSku());
			inputs.put(DAOConstants.PARAM_NAME,
					excelFileReport.getName());				
			inputs.put(DAOConstants.PARAM_EXPIRATION,
					jodaDateTimeUtil.toSqlDate(excelFileReport.getExpiration()));		
			inputs.put(DAOConstants.PARAM_CREATED_BY,
					excelFileReport.getCreatedBy());	
			return DAOUtils.getItem(addExcelFileReportSP
					.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during addExcelFileReportSP()",
					e);
		}
	}
	@Audit(entity = Entity.excelFileUploaded, operation = Operation.delete)
	public int deleteExcelFileUploaded(ExcelFileUploaded excelFileUploaded)
			throws DaoException {
		try {
			int count = -1;			
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_EXCEL_FILE_UPLOADED_ID,
					excelFileUploaded.getExcelFileUploadedId());
			inputs.put(DAOConstants.PARAM_STORE_ID,
					excelFileUploaded.getStoreId());
			inputs.put(DAOConstants.PARAM_FILE_NAME,
					excelFileUploaded.getFileName());		
			count = DAOUtils.getUpdateCount(deleteExcelFileUploadedSP
					.execute(inputs));
			return count;
		} catch (Exception e) {
			throw new DaoException("Failed during deleteExcelFileUploadedSP()",
					e);
		}
	}
	@Audit(entity = Entity.excelFileReport, operation = Operation.update)
	public ExcelFileUploaded updateExcelFileUploaded(ExcelFileUploaded excelFileUploaded)
			throws DaoException {
		try {
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(DAOConstants.PARAM_EXCEL_FILE_UPLOADED_ID,
					excelFileUploaded.getExcelFileUploadedId());
			inputs.put(DAOConstants.PARAM_ADDED_ON_RULE_BY,
					excelFileUploaded.getAddedOnRuleBy());
			inputs.put(DAOConstants.PARAM_ADDED_ON_RULE_DATE,
					jodaDateTimeUtil.toSqlDate(excelFileUploaded.getAddedOnRuleDate()));			
			return DAOUtils.getItem(updateExcelFileUploadedSP
					.execute(inputs));
		} catch (Exception e) {
			throw new DaoException("Failed during updateExcelFileUploaded()",
					e);
		}
	}	
}
