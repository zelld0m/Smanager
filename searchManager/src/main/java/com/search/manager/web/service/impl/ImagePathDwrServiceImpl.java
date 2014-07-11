package com.search.manager.web.service.impl;

import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.ImagePath;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.ImagePathService;
import com.search.manager.response.ServiceResponse;
import com.search.manager.web.service.ImagePathDwrService;

@Component
@RemoteProxy(name = "ImagePathService", creator = SpringCreator.class, creatorParams = @Param(name = "beanName", value = "imagePathService"))
public class ImagePathDwrServiceImpl implements ImagePathDwrService {

    @Autowired
    @Qualifier("imagePathServiceSp")
    private ImagePathService imagePathService;

    // TODO Transfer to message configuration file
    private static final String MSG_FAILED_ADD_IMAGE = "Failed to add image link %s : %s";
    private static final String MSG_FAILED_UPDATE_IMAGE_ALIAS = "Failed to update image alias to %s";
    private static final String MSG_FAILED_GET_IMAGE = "Failed to retrieve record for %s";
    private static final String MSG_FAILED_GET_BY_ALIAS = "Failed to get record via alias %s";

    @RemoteMethod
    @Override
    public ServiceResponse<ImagePath> addImagePathLink(String imageUrl, String alias, String imageSize) {
        ServiceResponse<ImagePath> serviceResponse = new ServiceResponse<ImagePath>();
        try {
            ImagePath imagePath = imagePathService.addImagePathLink(imageUrl, alias, imageSize);
            if (imagePath != null) {
                serviceResponse.success(imagePath);
            } else {
                serviceResponse.error(String.format(MSG_FAILED_ADD_IMAGE, alias));
            }
        } catch (CoreServiceException e) {
            serviceResponse.error(String.format(MSG_FAILED_ADD_IMAGE, alias), e);
        }
        return serviceResponse;
    }

    @RemoteMethod
    @Override
    public ServiceResponse<ImagePath> updateImagePathAlias(String imagePathId, String alias) {
        ServiceResponse<ImagePath> serviceResponse = new ServiceResponse<ImagePath>();
        try {
            ImagePath imagePath = imagePathService.updateImagePathAlias(imagePathId, alias);
            if (imagePath != null) {
                serviceResponse.success(imagePath);
            } else {
                serviceResponse.error(String.format(MSG_FAILED_UPDATE_IMAGE_ALIAS, alias));
            }
        } catch (CoreServiceException e) {
            serviceResponse.error(String.format(MSG_FAILED_UPDATE_IMAGE_ALIAS, alias), e);
        }

        return serviceResponse;
    }

    @RemoteMethod
    @Override
    public ServiceResponse<ImagePath> getImagePath(String storeId, String imageUrl) {
        ServiceResponse<ImagePath> serviceResponse = new ServiceResponse<ImagePath>();

        try {
            serviceResponse.success(imagePathService.getImagePath(storeId, imageUrl));
        } catch (CoreServiceException e) {
            serviceResponse.error(String.format(MSG_FAILED_GET_IMAGE, imageUrl), e);
        }

        return serviceResponse;
    }
    
    @RemoteMethod
    @Override
    public ServiceResponse<ImagePath> getImagePathByAlias(String storeId, String alias) {
    	ServiceResponse<ImagePath> serviceResponse = new ServiceResponse<ImagePath>();
    	ImagePath imagePath = new ImagePath();
    	
    	imagePath.setStoreId(storeId);
    	imagePath.setAlias(alias);
    	
    	try {
    		SearchResult<ImagePath> result = imagePathService.search(imagePath);
    		if(result.getTotalCount() > 0) {
    			serviceResponse.success(result.getList().get(0));
    		} else {
    			serviceResponse.success(null);
    		}
		} catch (CoreServiceException e) {
			serviceResponse.error(String.format(MSG_FAILED_GET_BY_ALIAS, alias), e);
		}
    	
    	return serviceResponse;
    }

}
