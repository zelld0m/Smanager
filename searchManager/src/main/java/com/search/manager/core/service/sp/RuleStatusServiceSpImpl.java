package com.search.manager.core.service.sp;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.core.dao.RuleStatusDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.RuleStatus;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.RuleStatusService;
import com.search.manager.enums.ExportType;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.service.UtilityService;

@Service("ruleStatusServiceSp")
public class RuleStatusServiceSpImpl implements RuleStatusService {

	@Autowired
	@Qualifier("ruleStatusDaoSp")
	private RuleStatusDao ruleStatusDao;

	// a setter method so that the Spring container can 'inject'
	public void setRuleStatusDao(RuleStatusDao ruleStatusDao) {
		this.ruleStatusDao = ruleStatusDao;
	}

	@Override
	public RuleStatus add(RuleStatus model) throws CoreServiceException {
		try {
			// TODO validation here...

			// Validate required fields.

			return ruleStatusDao.add(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public List<RuleStatus> add(Collection<RuleStatus> models)
			throws CoreServiceException {
		try {
			// TODO validation here...

			// Validate required fields.
			return (List<RuleStatus>) ruleStatusDao.add(models);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public RuleStatus update(RuleStatus model) throws CoreServiceException {
		try {
			// TODO validation here...

			// Validate required fields for update.

			return ruleStatusDao.update(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public List<RuleStatus> update(Collection<RuleStatus> models)
			throws CoreServiceException {
		try {
			// TODO validation here...

			// Validate required fields for update.

			return (List<RuleStatus>) ruleStatusDao.update(models);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public boolean delete(RuleStatus model) throws CoreServiceException {
		if (model != null) {
			try {
				// Validate required fields for delete.

				return ruleStatusDao.delete(model);
			} catch (CoreDaoException e) {
				throw new CoreServiceException(e);
			}
		}
		return false;
	}

	@Override
	public Map<RuleStatus, Boolean> delete(Collection<RuleStatus> models)
			throws CoreServiceException {
		try {
			// TODO validation here...

			// Validate required fields for update.

			return ruleStatusDao.delete(models);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public SearchResult<RuleStatus> search(Search search)
			throws CoreServiceException {
		try {
			return ruleStatusDao.search(search);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public SearchResult<RuleStatus> search(RuleStatus model)
			throws CoreServiceException {
		if (model != null) {
			try {
				return ruleStatusDao.search(model);
			} catch (CoreDaoException e) {
				throw new CoreServiceException(e);
			}
		}
		return null;
	}

	@Override
	public SearchResult<RuleStatus> search(RuleStatus model, int pageNumber,
			int maxRowCount) throws CoreServiceException {
		if (model != null) {
			try {
				return ruleStatusDao.search(model, pageNumber, maxRowCount);
			} catch (CoreDaoException e) {
				throw new CoreServiceException(e);
			}
		}
		return null;
	}

	@Override
	public RuleStatus searchById(String storeId, String id)
			throws CoreServiceException {
		if (StringUtils.isNotBlank(storeId) && StringUtils.isNotBlank(id)) {
			RuleStatus ruleStatus = new RuleStatus();
			ruleStatus.setStoreId(storeId);
			// TODO check if ruleRefId or ruleStatusId
			// ruleStatus.setRuleStatusId(id);
			ruleStatus.setRuleRefId(id);

			SearchResult<RuleStatus> searchResult = search(ruleStatus, 1, 1);
			if (searchResult.getTotalCount() > 0) {
				return (RuleStatus) CollectionUtils.get(
						searchResult.getResult(), 0);
			}
		}

		return null;
	}

	// RuleStatusService specific method here...

	private RuleStatus getRuleStatusPK(RuleStatus ruleStatus) {
		RuleStatus updateRuleStatus = new RuleStatus();
		if (ruleStatus != null) {
			updateRuleStatus.setStoreId(ruleStatus.getStoreId());
			updateRuleStatus.setRuleTypeId(ruleStatus.getRuleTypeId());
			updateRuleStatus.setRuleRefId(ruleStatus.getRuleRefId());
			updateRuleStatus.setRuleStatusId(ruleStatus.getRuleStatusId());
			updateRuleStatus.setDescription(ruleStatus.getDescription());
		}
		return updateRuleStatus;
	}

	@Override
	public List<String> getCleanList(List<String> ruleRefIds,
			Integer ruleTypeId, String pStatus, String aStatus)
			throws CoreServiceException {
		try {
			// TODO validation here...

			return ruleStatusDao.getCleanList(ruleRefIds, ruleTypeId, pStatus,
					aStatus);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public Map<String, Boolean> updateRuleStatus(RuleStatusEntity status,
			List<RuleStatus> ruleStatusList, String requestBy,
			DateTime requestDateTime) throws CoreServiceException {
		Map<String, Boolean> statusMap = new HashMap<String, Boolean>();
		// TODO validation here...
		for (RuleStatus ruleStatus : ruleStatusList) {
			statusMap.put(
					ruleStatus.getRuleRefId(),
					updateRuleStatusApprovalInfo(ruleStatus, status, requestBy,
							requestDateTime) != null ? true : false);
		}

		return statusMap;
	}

	@Override
	public RuleStatus updateRuleStatusExportInfo(RuleStatus ruleStatus,
			String exportBy, ExportType exportType, DateTime exportDateTime)
			throws CoreServiceException {
		if (ruleStatus != null) {
			RuleStatus updateRuleStatus = getRuleStatusPK(ruleStatus);
			updateRuleStatus.setExportBy(exportBy);
			updateRuleStatus.setExportType(exportType);
			updateRuleStatus.setLastModifiedBy(exportBy);
			updateRuleStatus.setLastExportDate(exportDateTime);
			updateRuleStatus.setLastModifiedDate(exportDateTime);
			// TODO validation here...
			try {
				return ruleStatusDao.update(updateRuleStatus);
			} catch (CoreDaoException e) {
				throw new CoreServiceException(e);
			}
		}

		return null;
	}

	@Override
	public RuleStatus updateRuleStatusPublishInfo(RuleStatus ruleStatus,
			RuleStatusEntity requestedPublishStatus, String requestBy,
			DateTime requestDateTime) throws CoreServiceException {
		// TODO validation here...
		RuleStatus updateRuleStatus = getRuleStatusPK(ruleStatus);
		updateRuleStatus.setApprovalStatus("");
		updateRuleStatus.setPublishedStatus(String
				.valueOf(requestedPublishStatus));
		updateRuleStatus.setPublishedBy(requestBy);
		updateRuleStatus.setLastModifiedBy(requestBy);
		updateRuleStatus.setLastPublishedDate(requestDateTime);
		updateRuleStatus.setLastModifiedDate(requestDateTime);

		try {
			return ruleStatusDao.update(updateRuleStatus);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public RuleStatus updateRuleStatusApprovalInfo(RuleStatus ruleStatus,
			RuleStatusEntity requestedApprovalStatus, String requestBy,
			DateTime requestDateTime) throws CoreServiceException {
		try {
			if (requestedApprovalStatus != null) {

				RuleStatus updateRuleStatus = getRuleStatusPK(ruleStatus);
				updateRuleStatus.setApprovalStatus(String
						.valueOf(requestedApprovalStatus));
				updateRuleStatus.setLastModifiedDate(requestDateTime);
				switch (requestedApprovalStatus) {
				case APPROVED:
				case REJECTED:
					updateRuleStatus.setApprovalBy(requestBy);
					updateRuleStatus.setLastApprovalDate(requestDateTime);
					break;
				case PENDING:
					updateRuleStatus.setRequestBy(requestBy);
					updateRuleStatus.setLastRequestDate(requestDateTime);
					break;
				default:
					return null;
				}

				RuleStatus ruleStatusFilter = new RuleStatus();
				ruleStatusFilter.setStoreId(ruleStatus.getStoreId());
				ruleStatusFilter.setRuleTypeId(ruleStatus.getRuleTypeId());
				ruleStatusFilter.setRuleRefId(ruleStatus.getRuleRefId());

				SearchResult<RuleStatus> searchResult = search(
						ruleStatusFilter, 1, 1);

				if (searchResult.getTotalCount() > 0) {
					// existing rule
					RuleStatus existingRuleStatus = searchResult.getResult()
							.get(0);

					// if rule is not for deletion do not change update status.
					if (!StringUtils.equalsIgnoreCase(
							existingRuleStatus.getUpdateStatus(),
							String.valueOf(RuleStatusEntity.DELETE))
							&& (StringUtils.isBlank(existingRuleStatus
									.getUpdateStatus()) || StringUtils
									.equalsIgnoreCase(
											existingRuleStatus
													.getPublishedStatus(),
											String.valueOf(RuleStatusEntity.PUBLISHED)))) {
						updateRuleStatus.setUpdateStatus(String
								.valueOf(RuleStatusEntity.UPDATE));
					}
					updateRuleStatus.setLastModifiedBy(requestBy);

					return ruleStatusDao.update(updateRuleStatus);
				} else {
					// new rule
					updateRuleStatus.setUpdateStatus(RuleStatusEntity.ADD
							.toString());
					updateRuleStatus
							.setPublishedStatus(RuleStatusEntity.UNPUBLISHED
									.toString());
					updateRuleStatus.setCreatedBy(requestBy);
					updateRuleStatus.setCreatedDate(requestDateTime);

					return ruleStatusDao.add(updateRuleStatus);
				}
			}
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}

		return null;
	}

	@Override
	public boolean updateRuleStatusDeletedInfo(RuleStatus ruleStatus,
			String deletedBy) throws CoreServiceException {
		// TODO validation here...
		RuleStatus ruleStatusFilter = new RuleStatus();
		ruleStatusFilter.setRuleTypeId(ruleStatus.getRuleTypeId());
		ruleStatusFilter.setStoreId(ruleStatus.getStoreId());
		ruleStatusFilter.setRuleRefId(ruleStatus.getRuleRefId());

		SearchResult<RuleStatus> searchResult = search(ruleStatusFilter);

		if (searchResult.getTotalCount() > 0) {
			RuleStatus updateRuleStatus = getRuleStatusPK(ruleStatus);
			updateRuleStatus.setApprovalStatus(StringUtils.equalsIgnoreCase(
					searchResult.getResult().get(0).getPublishedStatus(),
					String.valueOf(RuleStatusEntity.UNPUBLISHED)) ? "" : String
					.valueOf(RuleStatusEntity.PENDING));
			updateRuleStatus
					.setUpdateStatus(RuleStatusEntity.DELETE.toString());
			updateRuleStatus.setLastModifiedBy(deletedBy);

			try {
				updateRuleStatus = ruleStatusDao.update(updateRuleStatus);
				if (updateRuleStatus != null) {
					return true;
				}
			} catch (CoreDaoException e) {
				throw new CoreServiceException(e);
			}
		}

		return false;
	}

	@Override
	public RuleStatus getRuleStatus(String ruleType, String ruleRefId)
			throws CoreServiceException {
		if (StringUtils.isNotBlank(ruleType)
				&& StringUtils.isNotBlank(ruleRefId)) {
			RuleStatus ruleStatus = new RuleStatus();
			ruleStatus.setRuleTypeId(RuleEntity.getId(ruleType));
			ruleStatus.setRuleRefId(ruleRefId);
			ruleStatus.setStoreId(UtilityService.getStoreId());

			try {
				SearchResult<RuleStatus> searchResult = ruleStatusDao
						.search(ruleStatus);
				if (searchResult.getTotalCount() > 0) {
					return searchResult.getResult().get(0);
				}
			} catch (CoreDaoException e) {
				throw new CoreServiceException(e);
			}
		}

		return null;
	}

}
