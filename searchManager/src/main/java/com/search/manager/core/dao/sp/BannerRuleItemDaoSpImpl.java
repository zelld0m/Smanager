package com.search.manager.core.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Repository;

import com.search.manager.core.annotation.Auditable;
import com.search.manager.core.dao.BannerRuleItemDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.model.BannerRule;
import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.core.model.ImagePath;
import com.search.manager.core.model.ImagePathType;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.dao.sp.CUDStoredProcedure;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.dao.sp.GetStoredProcedure;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.model.constants.AuditTrailConstants.Entity;

@Auditable(entity = Entity.bannerRuleItem)
@Repository("bannerRuleItemDaoSp")
public class BannerRuleItemDaoSpImpl extends GenericDaoSpImpl<BannerRuleItem> implements BannerRuleItemDao {

    @Autowired
    private JodaDateTimeUtil jodaDateTimeUtil;

    private AddStoredProcedure addSp;
    private UpdateStoredProcedure updateSp;
    private DeleteStoredProcedure deleteSp;
    private SearchStoredProcedure searchSp;

    @SuppressWarnings("unused")
    private BannerRuleItemDaoSpImpl() {
        // do nothing...
    }

    @Autowired(required = true)
    public BannerRuleItemDaoSpImpl(JdbcTemplate jdbcTemplate) {
        addSp = new AddStoredProcedure(jdbcTemplate);
        updateSp = new UpdateStoredProcedure(jdbcTemplate);
        deleteSp = new DeleteStoredProcedure(jdbcTemplate);
        searchSp = new SearchStoredProcedure(jdbcTemplate);
    }

    private class AddStoredProcedure extends GetStoredProcedure {

        public AddStoredProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_ADD_BANNER_RULE_ITEM);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_PRIORITY, Types.INTEGER));
            declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.TIMESTAMP));
            declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.TIMESTAMP));
            declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_ALT, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_LINK_PATH, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_NEW_WINDOW, Types.INTEGER));
            declareParameter(new SqlParameter(DAOConstants.PARAM_DESCRIPTION, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_DISABLED, Types.INTEGER));
            declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_SIZE, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_CREATED_BY, Types.VARCHAR));
        }

        @Override
        protected void declareSqlReturnResultSetParameters() {
            declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<BannerRuleItem>() {
                public BannerRuleItem mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return buildModel(rs, rowNum);
                }
            }));
        }
    }

    private class UpdateStoredProcedure extends GetStoredProcedure {

        public UpdateStoredProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_UPDATE_BANNER_RULE_ITEM);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_PRIORITY, Types.INTEGER));
            declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.TIMESTAMP));
            declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.TIMESTAMP));
            declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_ALT, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_LINK_PATH, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_NEW_WINDOW, Types.INTEGER));
            declareParameter(new SqlParameter(DAOConstants.PARAM_DESCRIPTION, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_DISABLED, Types.INTEGER));
            declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_SIZE, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_LAST_UPDATED_BY, Types.VARCHAR));
        }

        @Override
        protected void declareSqlReturnResultSetParameters() {
            declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<BannerRuleItem>() {
                public BannerRuleItem mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return buildModel(rs, rowNum);
                }
            }));
        }
    }

    private class DeleteStoredProcedure extends CUDStoredProcedure {

        public DeleteStoredProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_DELETE_BANNER_RULE_ITEM);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_SIZE, Types.VARCHAR));
        }
    }

    private class SearchStoredProcedure extends GetStoredProcedure {

        public SearchStoredProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, DAOConstants.SP_GET_BANNER_RULE_ITEM);
        }

        @Override
        protected void declareParameters() {
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_RULE_NAME, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_STORE_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_MEMBER_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_START_DATE, Types.TIMESTAMP));
            declareParameter(new SqlParameter(DAOConstants.PARAM_END_DATE, Types.TIMESTAMP));
            declareParameter(new SqlParameter(DAOConstants.PARAM_DISABLED, Types.INTEGER));
            declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_PATH_ID, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_IMAGE_SIZE, Types.VARCHAR));
            declareParameter(new SqlParameter(DAOConstants.PARAM_START_ROW, Types.INTEGER));
            declareParameter(new SqlParameter(DAOConstants.PARAM_END_ROW, Types.INTEGER));
        }

        @Override
        protected void declareSqlReturnResultSetParameters() {
            declareParameter(new SqlReturnResultSet(DAOConstants.RESULT_SET_1, new RowMapper<BannerRuleItem>() {
                public BannerRuleItem mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return buildModel(rs, rowNum);
                }
            }));
        }
    }

    private BannerRuleItem buildModel(ResultSet rs, int rowNum) throws SQLException {
        BannerRuleItem ruleItem = null;

        if (rs != null && rs.getMetaData().getColumnCount() > 1) {
            BannerRule rule = new BannerRule(rs.getString(DAOConstants.COLUMN_STORE_ID),
                    rs.getString(DAOConstants.COLUMN_RULE_ID), rs.getString(DAOConstants.COLUMN_RULE_NAME), null);

            ImagePath imagePath = new ImagePath();
            imagePath.setStoreId(rs.getString(DAOConstants.COLUMN_STORE_ID));
            imagePath.setId(rs.getString(DAOConstants.COLUMN_IMAGE_PATH_ID));
            imagePath.setPath(rs.getString(DAOConstants.COLUMN_IMAGE_PATH));
            imagePath.setSize(rs.getString(DAOConstants.COLUMN_IMAGE_SIZE));
            imagePath.setPathType(ImagePathType.get(rs.getString(DAOConstants.COLUMN_IMAGE_PATH_TYPE)));
            imagePath.setAlias(rs.getString(DAOConstants.COLUMN_IMAGE_PATH_ALIAS));

            ruleItem = new BannerRuleItem(rule, rs.getString(DAOConstants.COLUMN_MEMBER_ID),
                    rs.getInt(DAOConstants.COLUMN_PRIORITY), jodaDateTimeUtil.toDateTime(rs
                            .getTimestamp(DAOConstants.COLUMN_START_DATE)), jodaDateTimeUtil.toDateTime(rs
                            .getTimestamp(DAOConstants.COLUMN_END_DATE)), rs.getString(DAOConstants.COLUMN_IMAGE_ALT),
                    rs.getString(DAOConstants.COLUMN_LINK_PATH), rs.getString(DAOConstants.COLUMN_DESCRIPTION),
                    imagePath, BooleanUtils.toBoolean(rs.getInt(DAOConstants.COLUMN_DISABLED)),
                    BooleanUtils.toBoolean(rs.getInt(DAOConstants.COLUMN_OPEN_NEW_WINDOW)));

            ruleItem.setCreatedBy(rs.getString(DAOConstants.COLUMN_CREATED_BY));
            ruleItem.setCreatedDate(jodaDateTimeUtil.toDateTime(rs.getTimestamp(DAOConstants.COLUMN_CREATED_STAMP)));
            ruleItem.setLastModifiedBy(rs.getString(DAOConstants.COLUMN_LAST_UPDATED_BY));
            ruleItem.setLastModifiedDate(jodaDateTimeUtil.toDateTime(rs
                    .getTimestamp(DAOConstants.COLUMN_LAST_UPDATED_STAMP)));
        }

        return ruleItem;
    }

    @Override
    protected StoredProcedure getAddStoredProcedure() throws CoreDaoException {
        return addSp;
    }

    @Override
    protected StoredProcedure getUpdateStoredProcedure() throws CoreDaoException {
        return updateSp;
    }

    @Override
    protected StoredProcedure getDeleteStoredProcedure() throws CoreDaoException {
        return deleteSp;
    }

    @Override
    protected StoredProcedure getSearchStoredProcedure() throws CoreDaoException {
        return searchSp;
    }

    @Override
    protected Map<String, Object> generateAddInput(BannerRuleItem model) throws CoreDaoException {
        Map<String, Object> inputs = null;

        if (model != null) {
            inputs = new HashMap<String, Object>();
            BannerRule bannerRule = model.getRule();
            ImagePath imagePath = model.getImagePath();
            String memberId = model.getMemberId();

            if (StringUtils.isBlank(memberId)) {
                model.setMemberId(DAOUtils.generateUniqueId());
            }

            inputs.put(DAOConstants.PARAM_STORE_ID, bannerRule.getStoreId());
            inputs.put(DAOConstants.PARAM_RULE_ID, bannerRule.getRuleId());
            inputs.put(DAOConstants.PARAM_MEMBER_ID, model.getMemberId());
            inputs.put(DAOConstants.PARAM_PRIORITY, model.getPriority());
            inputs.put(DAOConstants.PARAM_START_DATE, jodaDateTimeUtil.toSqlDate(model.getStartDate()));
            inputs.put(DAOConstants.PARAM_END_DATE, jodaDateTimeUtil.toSqlDate(model.getEndDate()));
            inputs.put(DAOConstants.PARAM_IMAGE_PATH_ID, imagePath.getId());
            inputs.put(DAOConstants.PARAM_IMAGE_ALT, model.getImageAlt());
            inputs.put(DAOConstants.PARAM_LINK_PATH, model.getLinkPath());
            inputs.put(DAOConstants.PARAM_NEW_WINDOW, BooleanUtils.toIntegerObject(model.getOpenNewWindow()));
            inputs.put(DAOConstants.PARAM_DESCRIPTION, model.getDescription());
            inputs.put(DAOConstants.PARAM_DISABLED, BooleanUtils.toIntegerObject(model.getDisabled()));
            inputs.put(DAOConstants.PARAM_IMAGE_SIZE, imagePath.getSize());
            inputs.put(DAOConstants.PARAM_CREATED_BY, model.getCreatedBy());
        }

        return inputs;
    }

    @Override
    protected Map<String, Object> generateUpdateInput(BannerRuleItem model) throws CoreDaoException {
        Map<String, Object> inputs = null;

        if (model != null) {
            inputs = new HashMap<String, Object>();

            BannerRule bannerRule = model.getRule();
            ImagePath imagePath = model.getImagePath();
            String memberId = model.getMemberId();

            inputs.put(DAOConstants.PARAM_STORE_ID, bannerRule.getStoreId());
            inputs.put(DAOConstants.PARAM_RULE_ID, bannerRule.getRuleId());
            inputs.put(DAOConstants.PARAM_MEMBER_ID, memberId);
            inputs.put(DAOConstants.PARAM_PRIORITY, model.getPriority());
            inputs.put(DAOConstants.PARAM_START_DATE, jodaDateTimeUtil.toSqlDate(model.getStartDate()));
            inputs.put(DAOConstants.PARAM_END_DATE, jodaDateTimeUtil.toSqlDate(model.getEndDate()));
            inputs.put(DAOConstants.PARAM_IMAGE_PATH_ID, imagePath != null ? imagePath.getId() : null);
            inputs.put(DAOConstants.PARAM_IMAGE_ALT, model.getImageAlt());
            inputs.put(DAOConstants.PARAM_LINK_PATH, model.getLinkPath());
            inputs.put(DAOConstants.PARAM_NEW_WINDOW, BooleanUtils.toIntegerObject(model.getOpenNewWindow()));
            inputs.put(DAOConstants.PARAM_DESCRIPTION, model.getDescription());
            inputs.put(DAOConstants.PARAM_DISABLED, BooleanUtils.toIntegerObject(model.getDisabled()));
            inputs.put(DAOConstants.PARAM_IMAGE_SIZE, imagePath != null ? imagePath.getSize() : null);
            inputs.put(DAOConstants.PARAM_LAST_UPDATED_BY, model.getLastModifiedBy());
        }

        return inputs;
    }

    @Override
    protected Map<String, Object> generateDeleteInput(BannerRuleItem model) throws CoreDaoException {
        Map<String, Object> inputs = null;

        if (model != null) {
            inputs = new HashMap<String, Object>();
            BannerRule bannerRule = model.getRule();
            ImagePath imagePath = model.getImagePath();

            inputs.put(DAOConstants.PARAM_STORE_ID, bannerRule.getStoreId());
            inputs.put(DAOConstants.PARAM_RULE_ID, bannerRule.getRuleId());
            inputs.put(DAOConstants.PARAM_MEMBER_ID, model.getMemberId());
            inputs.put(DAOConstants.PARAM_IMAGE_PATH_ID, imagePath != null ? imagePath.getId() : null);
            inputs.put(DAOConstants.PARAM_IMAGE_SIZE, imagePath != null ? imagePath.getSize() : null);
        }

        return inputs;
    }

    @Override
    protected Search generateSearchInput(BannerRuleItem model) throws CoreDaoException {
        if (model != null) {
            Search search = new Search(BannerRuleItem.class);
            // Banner Rule Field
            if (model.getRule() != null) {
                if (StringUtils.isNotBlank(model.getRule().getRuleId())) {
                    search.addFilter(new Filter(DAOConstants.PARAM_RULE_ID, model.getRule().getRuleId()));
                }
                if (StringUtils.isNotBlank(model.getRule().getRuleName())) {
                    search.addFilter(new Filter(DAOConstants.PARAM_RULE_NAME, model.getRule().getRuleName()));
                }
                if (StringUtils.isNotBlank(model.getRule().getStoreId())) {
                    search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, model.getRule().getStoreId()));
                }
            }

            // Banner Rule Item Field
            if (StringUtils.isNotBlank(model.getMemberId())) {
                search.addFilter(new Filter(DAOConstants.PARAM_MEMBER_ID, model.getMemberId()));
            }
            if (model.getStartDate() != null) {
                search.addFilter(new Filter(DAOConstants.PARAM_START_DATE, jodaDateTimeUtil.toSqlDate(model
                        .getStartDate())));
            }
            if (model.getEndDate() != null) {
                search.addFilter(new Filter(DAOConstants.PARAM_END_DATE, jodaDateTimeUtil.toSqlDate(model.getEndDate())));
            }
            search.addFilter(new Filter(DAOConstants.PARAM_DISABLED, BooleanUtils.toIntegerObject(model.getDisabled(),
                    1, 0, null)));

            // Image Path
            if (model.getImagePath() != null) {
                if (StringUtils.isNotBlank(model.getImagePath().getId())) {
                    search.addFilter(new Filter(DAOConstants.PARAM_IMAGE_PATH_ID, model.getImagePath().getId()));
                }
                if (StringUtils.isNotBlank(model.getImagePath().getSize())) {
                    search.addFilter(new Filter(DAOConstants.PARAM_IMAGE_SIZE, model.getImagePath().getSize()));
                }
            }

            return search;
        }

        return null;
    }

    @Override
    protected Search generateSearchById(String id, String storeId) throws CoreDaoException {
        if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(storeId)) {
            BannerRule bannerRule = new BannerRule();
            bannerRule.setStoreId(storeId);

            BannerRuleItem bannerRuleItem = new BannerRuleItem();
            bannerRuleItem.setMemberId(id);
            bannerRuleItem.setRule(bannerRule);

            return generateSearchInput(bannerRuleItem);
        }

        return null;
    }

    @Override
    protected Map<String, Object> getDefaultInParam() throws CoreDaoException {
        Map<String, Object> inParam = new HashMap<String, Object>();

        inParam.put(DAOConstants.PARAM_RULE_ID, null);
        inParam.put(DAOConstants.PARAM_RULE_NAME, null);
        inParam.put(DAOConstants.PARAM_STORE_ID, null);
        inParam.put(DAOConstants.PARAM_MEMBER_ID, null);
        inParam.put(DAOConstants.PARAM_START_DATE, null);
        inParam.put(DAOConstants.PARAM_END_DATE, null);
        inParam.put(DAOConstants.PARAM_DISABLED, null);
        inParam.put(DAOConstants.PARAM_IMAGE_PATH_ID, null);
        inParam.put(DAOConstants.PARAM_IMAGE_SIZE, null);
        inParam.put(DAOConstants.PARAM_START_ROW, 0);
        inParam.put(DAOConstants.PARAM_END_ROW, 0);

        return inParam;
    }

}
