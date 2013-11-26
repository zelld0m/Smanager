package com.search.manager.core.service.sp;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
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
import com.search.manager.service.UtilityService;

@Service("auditTrailServiceSp")
public class AuditTrailServiceSpImpl implements AuditTrailService {

	@Autowired
	@Qualifier("auditTrailDaoSp")
	private AuditTrailDao auditTrailDao;

	@Override
	public AuditTrail add(AuditTrail model) throws CoreServiceException {
		try {
			// Validate required fields.
			if (!validateRequiredField(model, false)) {
				throw new CoreServiceException("Required Field Exception.");
			}

			// Set CreatedBy and CreatedDate
			if (StringUtils.isBlank(model.getCreatedBy())) {
				model.setCreatedBy(UtilityService.getUsername());
			}
			if (model.getCreatedDate() == null) {
				model.setCreatedDate(new DateTime());
			}

			return auditTrailDao.add(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public Collection<AuditTrail> add(Collection<AuditTrail> models)
			throws CoreServiceException {
		if (models != null) {
			try {
				List<AuditTrail> validatedModels = (List<AuditTrail>) models;
				// Validate required fields.
				for (AuditTrail auditTrail : models) {
					if (validateRequiredField(auditTrail, false)) {
						// Set CreatedBy and CreatedDate
						if (StringUtils.isBlank(auditTrail.getCreatedBy())) {
							auditTrail.setCreatedBy(UtilityService
									.getUsername());
						}
						if (auditTrail.getCreatedDate() == null) {
							auditTrail.setCreatedDate(new DateTime());
						}
						validatedModels.add(auditTrail);
					}
				}
				return auditTrailDao.add(validatedModels);
			} catch (CoreDaoException e) {
				throw new CoreServiceException(e);
			}
		}
		return null;
	}

	@Override
	public AuditTrail update(AuditTrail model) throws CoreServiceException {
		try {
			// Validate required field for update.
			if (!validateRequiredField(model, true)) {
				throw new CoreServiceException("Required Field Exception.");
			}

			// Set LastModifiedBy and LastModifiedDate
			if (StringUtils.isBlank(model.getLastModifiedBy())) {
				model.setLastModifiedBy(UtilityService.getUsername());
			}
			if (model.getLastModifiedDate() == null) {
				model.setLastModifiedDate(new DateTime());
			}

			return auditTrailDao.update(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public Collection<AuditTrail> update(Collection<AuditTrail> models)
			throws CoreServiceException {
		if (models != null) {
			try {
				List<AuditTrail> validatedModels = (List<AuditTrail>) models;
				// Validate required field for update.
				for (AuditTrail auditTrail : models) {
					if (validateRequiredField(auditTrail, true)) {
						// Set LastModifiedBy and LastModifiedDate
						if (StringUtils.isBlank(auditTrail.getLastModifiedBy())) {
							auditTrail.setLastModifiedBy(UtilityService
									.getUsername());
						}
						if (auditTrail.getLastModifiedDate() == null) {
							auditTrail.setLastModifiedDate(new DateTime());
						}
						validatedModels.add(auditTrail);
					}
				}
				return auditTrailDao.update(validatedModels);
			} catch (CoreDaoException e) {
				throw new CoreServiceException(e);
			}
		}
		return null;
	}

	@Override
	public boolean delete(AuditTrail model) throws CoreServiceException {
		try {
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
			return auditTrailDao.search(search);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public SearchResult<AuditTrail> search(AuditTrail model)
			throws CoreServiceException {
		try {
			return auditTrailDao.search(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public SearchResult<AuditTrail> search(AuditTrail model, int pageNumber,
			int maxRowCount) throws CoreServiceException {
		try {
			return auditTrailDao.search(model, pageNumber, maxRowCount);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public AuditTrail searchById(String storeId, String id)
			throws CoreServiceException {

		if (StringUtils.isNotBlank(storeId) && StringUtils.isNotBlank(id)) {
			Search search = new Search(AuditTrail.class);
			search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
			// TODO add audit trail id, using reference for temporary only.
			search.addFilter(new Filter(DAOConstants.PARAM_REFERENCE, id));
			search.setPageNumber(1);
			search.setMaxRowCount(1);

			SearchResult<AuditTrail> searchResult = search(search);

			if (searchResult.getTotalCount() > 0) {
				return (AuditTrail) CollectionUtils.get(
						searchResult.getResult(), 0);
			}
		}

		return null;
	}

	@Override
	public List<String> getRefIDs(String entity, String operation,
			String storeId) throws CoreServiceException {
		try {
			return auditTrailDao.getRefIDs(entity, operation, storeId);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public List<String> getDropdownValues(int type, String storeId,
			boolean adminFlag) throws CoreServiceException {
		try {
			return auditTrailDao.getDropdownValues(type, storeId, adminFlag);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	public boolean validateRequiredField(AuditTrail auditTrail,
			boolean updateFlag) throws CoreServiceException {
		boolean valid = true;

		if (StringUtils.isBlank(auditTrail.getEntity())
				|| StringUtils.isBlank(auditTrail.getOperation())
				|| StringUtils.isBlank(auditTrail.getUsername())) {
			valid = false;
		}

		if (updateFlag && StringUtils.isBlank(auditTrail.getReferenceId())) {
			valid = false;
		}

		return valid;
	}

}
