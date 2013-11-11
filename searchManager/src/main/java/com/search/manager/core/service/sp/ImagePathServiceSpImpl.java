package com.search.manager.core.service.sp;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.core.dao.ImagePathDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.ImagePath;
import com.search.manager.core.model.ImagePathType;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.ImagePathService;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.response.ServiceResponse;
import com.search.manager.service.UtilityService;

@Service("imagePathServiceSp")
@RemoteProxy(name = "ImagePathServiceJS", creator = SpringCreator.class, creatorParams = @Param(name = "beanName", value = "imagePathService"))
public class ImagePathServiceSpImpl implements ImagePathService {

	private static final String MSG_FAILED_UPDATE_IMAGE_ALIAS = "Failed to update image alias to %s";
	private static final String MSG_FAILED_ADD_IMAGE = "Failed to add image link %s : %s";
	private static final String MSG_FAILED_GET_IMAGE = "Failed to retrieve record for %s";

	@Autowired
	@Qualifier("imagePathDaoSp")
	private ImagePathDao imagePathDao;

	@RemoteMethod
	@Override
	public ImagePath add(ImagePath model) throws CoreServiceException {
		try {
			// TODO validation here...

			// Validate required fields.

			// Set CreatedBy and CreatedDate
			if (StringUtils.isBlank(model.getCreatedBy())) {
				model.setCreatedBy(UtilityService.getUsername());
			}
			if (model.getCreatedDate() == null) {
				model.setCreatedDate(new DateTime());
			}

			return imagePathDao.add(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@RemoteMethod
	@Override
	public ImagePath update(ImagePath model) throws CoreServiceException {
		try {
			// TODO validation here...

			// Validate required field for update.

			// Set LastModifiedBy and LastModifiedDate
			if (StringUtils.isBlank(model.getLastModifiedBy())) {
				model.setLastModifiedBy(UtilityService.getUsername());
			}
			if (model.getLastModifiedDate() == null) {
				model.setLastModifiedDate(new DateTime());
			}

			return imagePathDao.update(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@RemoteMethod
	@Override
	public boolean delete(ImagePath model) throws CoreServiceException {
		try {
			// TODO validation here...
			return imagePathDao.delete(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@RemoteMethod
	@Override
	public SearchResult<ImagePath> search(Search search)
			throws CoreServiceException {
		try {
			// TODO validation here...
			return imagePathDao.search(search);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public ImagePath searchById(String storeId, String id)
			throws CoreServiceException {
		// TODO Auto-generated method stub

		if (StringUtils.isBlank(storeId) || StringUtils.isBlank(id)) {
			return null;
		}

		Search search = new Search(ImagePath.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_IMAGE_PATH_ID, id));
		search.setPageNumber(1);
		search.setMaxRowCount(1);

		SearchResult<ImagePath> searchResult = search(search);
		if (searchResult.getTotalCount() > 0) {
			return (ImagePath) CollectionUtils.get(searchResult.getResult(), 0);
		}

		return null;
	}

	// ImagePathService specific method here...

	@RemoteMethod
	@Override
	public ServiceResponse<ImagePath> addImagePathLink(String imageUrl,
			String alias, String imageSize) throws CoreServiceException {
		ServiceResponse<ImagePath> serviceResponse = new ServiceResponse<ImagePath>();

		String storeId = UtilityService.getStoreId();
		String username = UtilityService.getUsername();

		ImagePath imagePath = new ImagePath(storeId, null, imageUrl, imageSize,
				ImagePathType.IMAGE_LINK, alias, username);

		try {
			imagePath = add(imagePath);
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
			String alias) throws CoreServiceException {
		ServiceResponse<ImagePath> serviceResponse = new ServiceResponse<ImagePath>();

		String storeId = UtilityService.getStoreId();
		String username = UtilityService.getUsername();

		ImagePath imagePath = new ImagePath(storeId, imagePathId, null, null,
				null, alias, null, username);

		try {
			imagePath = update(imagePath);
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
			String imageUrl) throws CoreServiceException {
		ServiceResponse<ImagePath> serviceResponse = new ServiceResponse<ImagePath>();

		Search search = new Search(ImagePath.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_IMAGE_PATH, imageUrl));
		search.setPageNumber(1);
		search.setMaxRowCount(1);

		try {
			SearchResult<ImagePath> searchResult = search(search);
			if (searchResult.getTotalCount() > 0) {
				serviceResponse.success((ImagePath) CollectionUtils.get(
						searchResult.getResult(), 0));
			}
		} catch (CoreServiceException e) {
			serviceResponse.error(
					String.format(MSG_FAILED_GET_IMAGE, imageUrl), e);
		}

		return serviceResponse;
	}

}
