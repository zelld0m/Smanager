package com.search.manager.core.service.sp;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.core.dao.ImportRuleTaskDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.ImportRuleTask;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.ImportRuleTaskService;
import com.search.manager.workflow.constant.WorkflowConstants;

@Service("importRuleTaskServiceSp")
public class ImportRuleTaskServiceSpImpl extends GenericServiceSpImpl<ImportRuleTask> implements ImportRuleTaskService {

	@Autowired
    public ImportRuleTaskServiceSpImpl(@Qualifier("importRuleTaskDaoSp") ImportRuleTaskDao dao) {
		super(dao);
	}
	
    @Override
    public ImportRuleTask searchById(String storeId, String id) throws CoreServiceException {
        if (StringUtils.isBlank(id) || StringUtils.isBlank(storeId)) {
            return null;
        }
        Search search = new Search(ImportRuleTask.class);
        search.addFilter(new Filter(WorkflowConstants.COLUMN_SOURCE_RULE_STORE_ID, storeId));
        search.addFilter(new Filter(WorkflowConstants.COLUMN_TASK_ID, id));
        search.setPageNumber(1);
        search.setMaxRowCount(1);

        SearchResult<ImportRuleTask> searchResult = search(search);

        if (searchResult.getTotalCount() > 0) {
            return (ImportRuleTask) CollectionUtils.get(searchResult.getResult(), 0);
        }
        return null;
    }
    @Override
    public ImportRuleTask searchById(String id) throws CoreServiceException {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        Search search = new Search(ImportRuleTask.class);
        search.addFilter(new Filter(WorkflowConstants.COLUMN_TASK_ID, id));
        search.setPageNumber(1);
        search.setMaxRowCount(1);

        SearchResult<ImportRuleTask> searchResult = search(search);

        if (searchResult.getTotalCount() > 0) {
            return (ImportRuleTask) CollectionUtils.get(searchResult.getResult(), 0);
        }
        return null;
    }

}
