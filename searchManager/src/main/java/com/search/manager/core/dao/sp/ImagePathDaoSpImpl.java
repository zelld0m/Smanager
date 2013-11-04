package com.search.manager.core.dao.sp;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Repository;

import com.search.manager.core.dao.ImagePathDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.model.ImagePath;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.dao.sp.CUDStoredProcedure;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.dao.sp.DAOUtils;

@Repository("imagePathDaoSp")
public class ImagePathDaoSpImpl extends GenericDaoSpImpl<ImagePath> implements
		ImagePathDao {

	private AddStoredProcedure addSp;
	private UpdateStoredProcedure updateSp;
	@SuppressWarnings("unused")
	private DeleteStoredProcedure deleteSp;

	// private SearchStoredProcedure searchSp;

	@SuppressWarnings("unused")
	private ImagePathDaoSpImpl() {
		// do nothing...
	}

	public ImagePathDaoSpImpl(JdbcTemplate jdbcTemplate) {
		addSp = new AddStoredProcedure(jdbcTemplate);
		updateSp = new UpdateStoredProcedure(jdbcTemplate);
		deleteSp = new DeleteStoredProcedure(jdbcTemplate);
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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Search generateSearchById(String id) {
		Search search = new Search(ImagePath.class);
		search.addFilter(new Filter(DAOConstants.PARAM_IMAGE_PATH_ID, id));
		return search;
	}

	@Override
	protected Map<String, Object> getDefaultInParam() throws CoreDaoException {
		// TODO Auto-generated method stub
		return null;
	}

}
