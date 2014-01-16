package com.search.manager.core.service.sp;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.core.dao.ImportRuleTaskDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.ImportRuleTaskService;
import com.search.manager.workflow.constant.WorkflowConstants;
import com.search.manager.workflow.model.ImportRuleTask;

@Service
public class ImportRuleTaskServiceImpl implements ImportRuleTaskService{

	@Autowired
	private ImportRuleTaskDao importRuleTaskDao;
	
	@Override
	public ImportRuleTask add(ImportRuleTask model) throws CoreServiceException {
		try {
			return importRuleTaskDao.add(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public List<ImportRuleTask> add(Collection<ImportRuleTask> models)
			throws CoreServiceException {
		try {
			return (List<ImportRuleTask>)importRuleTaskDao.add(models);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public ImportRuleTask update(ImportRuleTask model)
			throws CoreServiceException {
		try {
			return importRuleTaskDao.update(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public List<ImportRuleTask> update(Collection<ImportRuleTask> models)
			throws CoreServiceException {
		try {
			return (List<ImportRuleTask>) importRuleTaskDao.update(models);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public boolean delete(ImportRuleTask model) throws CoreServiceException {
		try {
			return importRuleTaskDao.delete(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public Map<ImportRuleTask, Boolean> delete(Collection<ImportRuleTask> models)
			throws CoreServiceException {
		
		try {
			return importRuleTaskDao.delete(models);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public SearchResult<ImportRuleTask> search(Search search)
			throws CoreServiceException {
		try {
			return importRuleTaskDao.search(search);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public SearchResult<ImportRuleTask> search(ImportRuleTask model)
			throws CoreServiceException {
		try {
			return importRuleTaskDao.search(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public SearchResult<ImportRuleTask> search(ImportRuleTask model,
			int pageNumber, int maxRowCount) throws CoreServiceException {
		try {
			return importRuleTaskDao.search(model, pageNumber, maxRowCount);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public ImportRuleTask searchById(String storeId, String id)
			throws CoreServiceException {
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

}
