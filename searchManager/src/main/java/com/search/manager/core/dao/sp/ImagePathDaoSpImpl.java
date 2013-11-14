package com.search.manager.core.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Repository;

import com.search.manager.core.annotation.Auditable;
import com.search.manager.core.dao.ImagePathDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.model.ImagePath;
import com.search.manager.core.model.ImagePathType;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.dao.sp.CUDStoredProcedure;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.dao.sp.GetStoredProcedure;
import com.search.manager.model.constants.AuditTrailConstants.Entity;

@Auditable(entity = Entity.imagePath)
@Repository("imagePathDaoSp")
public class ImagePathDaoSpImpl extends GenericDaoSpImpl<ImagePath> implements
		ImagePathDao {

	private AddStoredProcedure addSp;
	private UpdateStoredProcedure updateSp;
	@SuppressWarnings("unused")
	private DeleteStoredProcedure deleteSp;
	private SearchStoredProcedure searchSp;

	@SuppressWarnings("unused")
	private ImagePathDaoSpImpl() {
		// do nothing...
	}

	@Autowired(required = true)
	public ImagePathDaoSpImpl(JdbcTemplate jdbcTemplate) {
		addSp = new AddStoredProcedure(jdbcTemplate);
		updateSp = new UpdateStoredProcedure(jdbcTemplate);
		deleteSp = new DeleteStoredProcedure(jdbcTemplate);
		searchSp = new SearchStoredProcedure(jdbcTemplate);
	}

	private class AddStoredProcedure extends CUDStoredProcedure {

		public AddStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_ADD_BANNER_IMAGE_PATH);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_SIZE,
					Types.VARCHAR));
			declareParameter(new SqlParameter(
					DAOConstants.PARAM_IMAGE_PATH_TYPE, Types.VARCHAR));
			declareParameter(new SqlParameter(
					DAOConstants.PARAM_IMAGE_PATH_ALIAS, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY,
					Types.VARCHAR));
		}
	}

	private class UpdateStoredProcedure extends CUDStoredProcedure {

		public UpdateStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_UPDATE_BANNER_IMAGE_PATH);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(
					DAOConstants.PARAM_IMAGE_PATH_ALIAS, Types.VARCHAR));
			declareParameter(new SqlParameter(
					DAOConstants.PARAM_LAST_UPDATED_BY, Types.VARCHAR));
		}
	}

	private class DeleteStoredProcedure extends CUDStoredProcedure {

		public DeleteStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, "");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void declareParameters() {
			// TODO Auto-generated method stub
		}
	}

	private class SearchStoredProcedure extends GetStoredProcedure {

		public SearchStoredProcedure(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, DAOConstants.SP_GET_BANNER_IMAGE_PATH);
		}

		@Override
		protected void declareParameters() {
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH,
					Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_SIZE,
					Types.VARCHAR));
			declareParameter(new SqlParameter(
					DAOConstants.PARAM_IMAGE_PATH_TYPE, Types.VARCHAR));
			declareParameter(new SqlParameter(
					DAOConstants.PARAM_IMAGE_PATH_ALIAS, Types.VARCHAR));
			declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW,
					Types.INTEGER));
			declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW,
					Types.INTEGER));
		}

		@Override
		protected void declareSqlReturnResultSetParameters() {
			declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1,
					new RowMapper<ImagePath>() {
						public ImagePath mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							ImagePath imagePath = new ImagePath(
									rs.getString(DAOConstants.COLUMN_STORE_ID),
									rs.getString(DAOConstants.COLUMN_IMAGE_PATH_ID),
									rs.getString(DAOConstants.COLUMN_IMAGE_PATH),
									rs.getString(DAOConstants.COLUMN_IMAGE_SIZE),
									ImagePathType.get(rs
											.getString(DAOConstants.COLUMN_IMAGE_PATH_TYPE)),
									rs.getString(DAOConstants.COLUMN_IMAGE_PATH_ALIAS),
									rs.getString(DAOConstants.COLUMN_CREATED_BY),
									rs.getString(DAOConstants.COLUMN_LAST_UPDATED_BY));

							return imagePath;
						}
					}));
		}
	}

	@Override
	protected StoredProcedure getAddStoredProcedure() throws CoreDaoException {
		return addSp;
	}

	@Override
	protected StoredProcedure getUpdateStoredProcedure()
			throws CoreDaoException {
		return updateSp;
	}

	@Override
	protected StoredProcedure getDeleteStoredProcedure()
			throws CoreDaoException {
		throw new CoreDaoException("Unimplemented method...");
	}

	@Override
	protected StoredProcedure getSearchStoredProcedure()
			throws CoreDaoException {
		return searchSp;
	}

	@Override
	protected Map<String, Object> generateAddInput(ImagePath model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;

		if (model != null) {
			inputs = new HashMap<String, Object>();

			String imagePathId = model.getId();

			if (StringUtils.isBlank(imagePathId)) {
				imagePathId = DAOUtils.generateUniqueId();
			}

			inputs.put(DAOConstants.MODEL_ID, imagePathId);
			inputs.put(DAOConstants.PARAM_IMAGE_PATH_ID, imagePathId);
			inputs.put(DAOConstants.PARAM_STORE_ID, model.getStoreId());
			inputs.put(DAOConstants.PARAM_IMAGE_PATH, model.getPath());
			inputs.put(DAOConstants.PARAM_IMAGE_SIZE, model.getSize());
			inputs.put(DAOConstants.PARAM_IMAGE_PATH_TYPE, model.getPathType());
			inputs.put(DAOConstants.PARAM_IMAGE_PATH_ALIAS, model.getAlias());
			inputs.put(DAOConstants.PARAM_CREATED_BY, model.getCreatedBy());
		}

		return inputs;
	}

	@Override
	protected Map<String, Object> generateUpdateInput(ImagePath model)
			throws CoreDaoException {
		Map<String, Object> inputs = null;

		if (model != null) {
			inputs = new HashMap<String, Object>();

			inputs.put(DAOConstants.MODEL_ID, model.getId());
			inputs.put(DAOConstants.PARAM_IMAGE_PATH_ID, model.getId());
			inputs.put(DAOConstants.PARAM_STORE_ID, model.getStoreId());
			inputs.put(DAOConstants.PARAM_IMAGE_PATH_ALIAS, model.getAlias());
			inputs.put(DAOConstants.PARAM_LAST_UPDATED_BY,
					model.getLastModifiedBy());
		}

		return inputs;
	}

	@Override
	protected Map<String, Object> generateDeleteInput(ImagePath model)
			throws CoreDaoException {
		throw new CoreDaoException("Unimplemented method.");
	}

	@Override
	protected Search generateSearchInput(ImagePath model)
			throws CoreDaoException {
		if (model != null) {
			Search search = new Search(ImagePath.class);

			if (StringUtils.isNotBlank(model.getId())) {
				search.addFilter(new Filter(DAOConstants.PARAM_IMAGE_PATH_ID,
						model.getId()));
			}
			if (StringUtils.isNotBlank(model.getStoreId())) {
				search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, model
						.getStoreId()));
			}
			if (StringUtils.isNotBlank(model.getPath())) {
				search.addFilter(new Filter(DAOConstants.PARAM_IMAGE_PATH,
						model.getPath()));
			}
			if (StringUtils.isNotBlank(model.getSize())) {
				search.addFilter(new Filter(DAOConstants.PARAM_IMAGE_SIZE,
						model.getSize()));
			}
			if (StringUtils.isNotBlank(model.getPathType().toString())) {
				search.addFilter(new Filter(DAOConstants.PARAM_IMAGE_PATH_TYPE,
						model.getPathType().toString()));
			}
			if (StringUtils.isNotBlank(model.getAlias())) {
				search.addFilter(new Filter(
						DAOConstants.PARAM_IMAGE_PATH_ALIAS, model.getAlias()));
			}
			return search;
		}
		return null;
	}

	@Override
	protected Search generateSearchById(String id, String storeId)
			throws CoreDaoException {
		if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(storeId)) {
			ImagePath imagePath = new ImagePath();
			imagePath.setStoreId(storeId);
			imagePath.setId(id);
			return generateSearchInput(imagePath);
		}

		return null;
	}

	@Override
	protected Map<String, Object> getDefaultInParam() throws CoreDaoException {
		Map<String, Object> inParam = new HashMap<String, Object>();

		inParam.put(DAOConstants.PARAM_IMAGE_PATH_ID, null);
		inParam.put(DAOConstants.PARAM_STORE_ID, null);
		inParam.put(DAOConstants.PARAM_IMAGE_PATH, null);
		inParam.put(DAOConstants.PARAM_IMAGE_SIZE, null);
		inParam.put(DAOConstants.PARAM_IMAGE_PATH_TYPE, null);
		inParam.put(DAOConstants.PARAM_IMAGE_PATH_ALIAS, null);
		inParam.put(DAOConstants.PARAM_START_ROW, 0);
		inParam.put(DAOConstants.PARAM_END_ROW, 0);

		return inParam;
	}

}