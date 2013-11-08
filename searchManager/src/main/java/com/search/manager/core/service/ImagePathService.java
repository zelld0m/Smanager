package com.search.manager.core.service;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.ImagePath;
import com.search.manager.response.ServiceResponse;

public interface ImagePathService extends GenericService<ImagePath> {

	// Add ImagePathService specific method here...

	ServiceResponse<ImagePath> addImagePathLink(String imageUrl, String alias,
			String imageSize) throws CoreServiceException;

	ServiceResponse<ImagePath> updateImagePathAlias(String imagePathId,
			String alias) throws CoreServiceException;

	ServiceResponse<ImagePath> getImagePath(String storeId, String imageUrl)
			throws CoreServiceException;
	
}
