package com.search.manager.web.service;

import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.ImagePath;
import com.search.manager.core.service.ImagePathService;
import com.search.manager.response.ServiceResponse;

@Service("imagePathServiceGui")
@RemoteProxy(name = "ImagePathServiceJS", creator = SpringCreator.class, creatorParams = @Param(name = "beanName", value = "imagePathService"))
public class ImagePathServiceGuiImpl implements ImagePathServiceGui {

	@Autowired
	@Qualifier("imagePathServiceSp")
	private ImagePathService imagePathService;

	// TODO Transfer to message configuration file
	private static final String MSG_FAILED_ADD_IMAGE = "Failed to add image link %s : %s";
	private static final String MSG_FAILED_UPDATE_IMAGE_ALIAS = "Failed to update image alias to %s";
	private static final String MSG_FAILED_GET_IMAGE = "Failed to retrieve record for %s";

	@RemoteMethod
	@Override
	public ServiceResponse<ImagePath> addImagePathLink(String imageUrl,
			String alias, String imageSize) {
		ServiceResponse<ImagePath> serviceResponse = new ServiceResponse<ImagePath>();
		try {
			ImagePath imagePath = imagePathService.addImagePathLink(imageUrl,
					alias, imageSize);
			if (imagePath != null) {
				serviceResponse.success(imagePath);
			} else {
				serviceResponse.error(String
						.format(MSG_FAILED_ADD_IMAGE, alias));
			}
		} catch (CoreServiceException e) {
			serviceResponse
					.error(String.format(MSG_FAILED_ADD_IMAGE, alias), e);
		}
		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<ImagePath> updateImagePathAlias(String imagePathId,
			String alias) {
		ServiceResponse<ImagePath> serviceResponse = new ServiceResponse<ImagePath>();
		try {
			ImagePath imagePath = imagePathService.updateImagePathAlias(
					imagePathId, alias);
			if (imagePath != null) {
				serviceResponse.success(imagePath);
			} else {
				serviceResponse.error(String.format(
						MSG_FAILED_UPDATE_IMAGE_ALIAS, alias));
			}
		} catch (CoreServiceException e) {
			serviceResponse.error(
					String.format(MSG_FAILED_UPDATE_IMAGE_ALIAS, alias), e);
		}

		return serviceResponse;
	}

	@RemoteMethod
	@Override
	public ServiceResponse<ImagePath> getImagePath(String storeId,
			String imageUrl) {
		ServiceResponse<ImagePath> serviceResponse = new ServiceResponse<ImagePath>();

		try {
			serviceResponse.success(imagePathService.getImagePath(storeId,
					imageUrl));
		} catch (CoreServiceException e) {
			serviceResponse.error(
					String.format(MSG_FAILED_GET_IMAGE, imageUrl), e);
		}

		return serviceResponse;
	}

}
