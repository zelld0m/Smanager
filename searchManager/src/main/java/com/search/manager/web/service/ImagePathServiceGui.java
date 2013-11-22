package com.search.manager.web.service;

import com.search.manager.core.model.ImagePath;
import com.search.manager.response.ServiceResponse;

public interface ImagePathServiceGui {

	// Add ImagePathServiceGui specific method here...

	ServiceResponse<ImagePath> addImagePathLink(String imageUrl, String alias,
			String imageSize);

	ServiceResponse<ImagePath> updateImagePathAlias(String imagePathId,
			String alias);

	ServiceResponse<ImagePath> getImagePath(String storeId, String imageUrl);

}
