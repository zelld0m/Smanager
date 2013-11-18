package com.search.manager.core.service.sp;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.core.dao.AuditTrailDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.AuditTrail;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.AuditTrailService;
import com.search.manager.dao.sp.DAOConstants;

@Service("auditTrailServiceSp")
public class AuditTrailServiceSpImpl implements AuditTrailService {

	@Autowired
	@Qualifier("auditTrailDaoSp")
	private AuditTrailDao auditTrailDao;

	@Override
	public AuditTrail add(AuditTrail model) throws CoreServiceException {
		try {
			// TODO validation here...

			// Validate required fields.

			return auditTrailDao.add(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public Collection<AuditTrail> add(Collection<AuditTrail> models)
			throws CoreServiceException {
		try {
			// TODO validation here...

			// Validate required fields.

			return auditTrailDao.add(models);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public AuditTrail update(AuditTrail model) throws CoreServiceException {
		try {
			// TODO validation here...

			// Validate required field for update.

			return auditTrailDao.update(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public Collection<AuditTrail> update(Collection<AuditTrail> models)
			throws CoreServiceException {
		try {
			// TODO validation here...

			// Validate required field for update.

			return auditTrailDao.update(models);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public boolean delete(AuditTrail model) throws CoreServiceException {
		try {
			// TODO validation here...

			// Validate required field for delete.

			return auditTrailDao.delete(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public Map<AuditTrail, Boolean> delete(Collection<AuditTrail> models)
			throws CoreServiceException {
		try {
			// TODO validation here...

			// Validate required field for delete.

			return auditTrailDao.delete(models);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public SearchResult<AuditTrail> search(Search search)
			throws CoreServiceException {
		try {
			// TODO validation here...

			return auditTrailDao.search(search);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public SearchResult<AuditTrail> search(AuditTrail model)
			throws CoreServiceException {
		try {
			// TODO validation here...

			return auditTrailDao.search(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public AuditTrail searchById(String storeId, String id)
			throws CoreServiceException {
		// TODO validation here...

		Search search = new Search(AuditTrail.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		// TODO add audit trail id, using reference for temporary only.
		search.addFilter(new Filter(DAOConstants.PARAM_REFERENCE, id));
		search.setPageNumber(1);
		search.setMaxRowCount(1);

		SearchResult<AuditTrail> searchResult = search(search);

		if (searchResult.getTotalCount() > 0) {
			return (AuditTrail) CollectionUtils
					.get(searchResult.getResult(), 0);
		}

		return null;
	}

	@Override
	public List<String> getRefIDs(String ent, String opt, String storeId)
			throws CoreServiceException {
		try {
			// TODO validation here...

			return auditTrailDao.getRefIDs(ent, opt, storeId);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public List<String> getDropdownValues(int type, String storeId,
			boolean adminFlag) throws CoreServiceException {
		try {
			// TODO validation here...

			return auditTrailDao.getDropdownValues(type, storeId, adminFlag);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

}
