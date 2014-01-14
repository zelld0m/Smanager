package com.search.manager.core.dao;

import java.util.List;

import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.model.AuditTrail;

public interface AuditTrailDao extends GenericDao<AuditTrail> {

	// Add AuditTrailDao specific method here...

	List<String> getRefIDs(String ent, String opt, String storeId)
			throws CoreDaoException;

	List<String> getDropdownValues(int type, String storeId, boolean adminFlag)
			throws CoreDaoException;

}
