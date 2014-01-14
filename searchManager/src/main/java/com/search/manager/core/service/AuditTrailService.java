package com.search.manager.core.service;

import java.util.List;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.AuditTrail;

public interface AuditTrailService extends GenericService<AuditTrail> {

	// Add AuditTrailService specific method here...

	List<String> getRefIDs(String entity, String operation, String storeId)
			throws CoreServiceException;

	List<String> getDropdownValues(int type, String storeId, boolean adminFlag)
			throws CoreServiceException;

}
