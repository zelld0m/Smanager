package com.search.manager.core.service;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.ImagePath;

public interface ImagePathService extends GenericService<ImagePath> {

	// Add ImagePathService specific method here...

	ImagePath transfer(ImagePath imagePath) throws CoreServiceException;

	ImagePath addImagePathLink(String imageUrl, String alias, String imageSize)
			throws CoreServiceException;

	ImagePath updateImagePathAlias(String imagePathId, String alias)
			throws CoreServiceException;

	ImagePath getImagePath(String storeId, String imageUrl)
			throws CoreServiceException;

}
