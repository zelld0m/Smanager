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
import com.search.manager.service.UtilityService;

@Service("imagePathServiceSp")
public class ImagePathServiceSpImpl implements ImagePathService {

	@Autowired
	@Qualifier("imagePathDaoSp")
	private ImagePathDao imagePathDao;

	// a setter method so that the Spring container can 'inject'
	public void setImagePathDao(ImagePathDao imagePathDao) {
		this.imagePathDao = imagePathDao;
	}

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

	@Override
	public List<ImagePath> add(Collection<ImagePath> models)
			throws CoreServiceException {
		try {
			return (List<ImagePath>) imagePathDao.add(models);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

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

	@Override
	public Collection<ImagePath> update(Collection<ImagePath> models)
			throws CoreServiceException {
		try {
			// TODO validation here...

			// Validate required field for update.

			// Set LastModifiedBy and LastModifiedDate

			return imagePathDao.update(models);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public boolean delete(ImagePath model) throws CoreServiceException {
		try {
			// TODO validation here...
			return imagePathDao.delete(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public Map<ImagePath, Boolean> delete(Collection<ImagePath> models)
			throws CoreServiceException {
		try {
			// TODO validation here...
			return imagePathDao.delete(models);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

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
	public SearchResult<ImagePath> search(ImagePath model)
			throws CoreServiceException {
		try {
			// TODO validation here...
			return imagePathDao.search(model);
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

	@Override
	public ImagePath transfer(ImagePath imagePath) throws CoreServiceException {

		if (StringUtils.isNotBlank(imagePath.getId())
				&& StringUtils.isNotBlank(imagePath.getStoreId())
				&& StringUtils.isNotBlank(imagePath.getCreatedBy())) {
			try {
				return imagePathDao.add(imagePath);
			} catch (CoreDaoException e) {
				throw new CoreServiceException(e);
			}
		}

		return null;
	}

	@Override
	public ImagePath addImagePathLink(String imageUrl, String alias,
			String imageSize) throws CoreServiceException {
		String storeId = UtilityService.getStoreId();
		String username = UtilityService.getUsername();

		ImagePath imagePath = new ImagePath(storeId, null, imageUrl, imageSize,
				ImagePathType.IMAGE_LINK, alias, username);

		return add(imagePath);
	}

	@Override
	public ImagePath updateImagePathAlias(String imagePathId, String alias)
			throws CoreServiceException {
		String storeId = UtilityService.getStoreId();
		String username = UtilityService.getUsername();

		ImagePath imagePath = new ImagePath(storeId, imagePathId, null, null,
				null, alias, null, username);

		return update(imagePath);
	}

	@Override
	public ImagePath getImagePath(String storeId, String imageUrl)
			throws CoreServiceException {

		Search search = new Search(ImagePath.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_IMAGE_PATH, imageUrl));
		search.setPageNumber(1);
		search.setMaxRowCount(1);

		SearchResult<ImagePath> searchResult = search(search);
		if (searchResult.getTotalCount() > 0) {
			return (ImagePath) CollectionUtils.get(searchResult.getResult(), 0);
		}

		return null;
	}

}
