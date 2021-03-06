package com.search.manager.core.service.sp;

import java.util.ArrayList;
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
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.ImagePathService;
import com.search.manager.service.UtilityService;

@Service("imagePathServiceSp")
public class ImagePathServiceSpImpl implements ImagePathService {

    @Autowired
    @Qualifier("imagePathDaoSp")
    private ImagePathDao imagePathDao;
    @Autowired
    private UtilityService utilityService;

    // a setter method so that the Spring container can 'inject'
    public void setImagePathDao(ImagePathDao imagePathDao) {
        this.imagePathDao = imagePathDao;
    }

    @Override
    public ImagePath add(ImagePath model) throws CoreServiceException {
        try {
            // Validate required fields.
            if (!validateRequiredField(model, false)) {
                throw new CoreServiceException("Required Field Exception.");
            }

            // Set CreatedBy and CreatedDate
            if (StringUtils.isBlank(model.getCreatedBy())) {
                model.setCreatedBy(utilityService.getUsername());
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
    public List<ImagePath> add(Collection<ImagePath> models) throws CoreServiceException {
        try {
            List<ImagePath> validatedModels = new ArrayList<ImagePath>();
            for (ImagePath imagePath : models) {
                if (validateRequiredField(imagePath, false)) {
                    // Set CreatedBy and CreatedDate
                    if (StringUtils.isBlank(imagePath.getCreatedBy())) {
                        imagePath.setCreatedBy(utilityService.getUsername());
                    }
                    if (imagePath.getCreatedDate() == null) {
                        imagePath.setCreatedDate(new DateTime());
                    }
                    validatedModels.add(imagePath);
                }
            }
            return (List<ImagePath>) imagePathDao.add(validatedModels);
        } catch (CoreDaoException e) {
            throw new CoreServiceException(e);
        }
    }

    @Override
    public ImagePath update(ImagePath model) throws CoreServiceException {
        try {
            // Validate required field for update.
            if (!validateRequiredField(model, true)) {
                throw new CoreServiceException("Required Field Exception.");
            }

            // Set LastModifiedBy and LastModifiedDate
            if (StringUtils.isBlank(model.getLastModifiedBy())) {
                model.setLastModifiedBy(utilityService.getUsername());
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
    public List<ImagePath> update(Collection<ImagePath> models) throws CoreServiceException {
        try {
            // Validate required field for update.
            List<ImagePath> validatedModels = new ArrayList<ImagePath>();
            for (ImagePath imagePath : models) {
                if (validateRequiredField(imagePath, true)) {
                    // Set LastModifiedBy and LastModifiedDate
                    if (StringUtils.isBlank(imagePath.getLastModifiedBy())) {
                        imagePath.setLastModifiedBy(utilityService.getUsername());
                    }
                    if (imagePath.getLastModifiedDate() == null) {
                        imagePath.setLastModifiedDate(new DateTime());
                    }
                    validatedModels.add(imagePath);
                }
            }

            return (List<ImagePath>) imagePathDao.update(validatedModels);
        } catch (CoreDaoException e) {
            throw new CoreServiceException(e);
        }
    }

    @Override
    public boolean delete(ImagePath model) throws CoreServiceException {
        if (model != null) {
            try {
                return imagePathDao.delete(model);
            } catch (CoreDaoException e) {
                throw new CoreServiceException(e);
            }
        }

        return false;
    }

    @Override
    public Map<ImagePath, Boolean> delete(Collection<ImagePath> models) throws CoreServiceException {
        if (models != null) {
            try {
                return imagePathDao.delete(models);
            } catch (CoreDaoException e) {
                throw new CoreServiceException(e);
            }
        }
        return null;
    }

    @Override
    public SearchResult<ImagePath> search(Search search) throws CoreServiceException {
        try {
            return imagePathDao.search(search);
        } catch (CoreDaoException e) {
            throw new CoreServiceException(e);
        }
    }

    @Override
    public SearchResult<ImagePath> search(ImagePath model) throws CoreServiceException {
        if (model != null) {
            try {
                return imagePathDao.search(model);
            } catch (CoreDaoException e) {
                throw new CoreServiceException(e);
            }
        }
        return null;
    }

    @Override
    public SearchResult<ImagePath> search(ImagePath model, int pageNumber, int maxRowCount) throws CoreServiceException {
        if (model != null) {
            try {
                return imagePathDao.search(model, pageNumber, maxRowCount);
            } catch (CoreDaoException e) {
                throw new CoreServiceException(e);
            }
        }
        return null;
    }

    @Override
    public ImagePath searchById(String storeId, String id) throws CoreServiceException {

        if (StringUtils.isNotBlank(storeId) && StringUtils.isNotBlank(id)) {
            ImagePath imagePath = new ImagePath();
            imagePath.setStoreId(storeId);
            imagePath.setId(id);

            SearchResult<ImagePath> searchResult = search(imagePath, 1, 1);
            if (searchResult.getTotalCount() > 0) {
                return (ImagePath) CollectionUtils.get(searchResult.getResult(), 0);
            }
        }

        return null;
    }

    // ImagePathService specific method here...

    @Override
    public ImagePath transfer(ImagePath imagePath) throws CoreServiceException {

        if (StringUtils.isNotBlank(imagePath.getId()) && StringUtils.isNotBlank(imagePath.getStoreId())
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
    public ImagePath addImagePathLink(String imageUrl, String alias, String imageSize) throws CoreServiceException {
        String storeId = utilityService.getStoreId();
        String username = utilityService.getUsername();

        ImagePath imagePath = new ImagePath(storeId, null, imageUrl, imageSize, ImagePathType.IMAGE_LINK, alias,
                username);

        return add(imagePath);
    }

    @Override
    public ImagePath updateImagePathAlias(String imagePathId, String alias) throws CoreServiceException {
        String storeId = utilityService.getStoreId();
        String username = utilityService.getUsername();

        ImagePath imagePath = new ImagePath(storeId, imagePathId, null, null, null, alias, null, username);

        ImagePath existing = searchById(storeId, imagePathId);
        
        if(existing != null) {
        	existing.setAlias(alias);
        	imagePath = existing;
        }
        
        return update(imagePath);
    }

    @Override
    public ImagePath getImagePath(String storeId, String imageUrl) throws CoreServiceException {
        ImagePath imagePath = new ImagePath();
        imagePath.setStoreId(storeId);
        imagePath.setPath(imageUrl);

        SearchResult<ImagePath> searchResult = search(imagePath, 1, 1);
        if (searchResult.getTotalCount() > 0) {
            return (ImagePath) CollectionUtils.get(searchResult.getResult(), 0);
        }

        return null;
    }

    public boolean validateRequiredField(ImagePath imagePath, boolean updateFlag) {
        boolean valid = true;

        if (StringUtils.isBlank(imagePath.getPath()) || StringUtils.isBlank(imagePath.getAlias())
                || StringUtils.isBlank(imagePath.getStoreId()) || StringUtils.isBlank(imagePath.getSize())
                || imagePath.getPathType() == null) {
            valid = false;
        }

        if (updateFlag && StringUtils.isBlank(imagePath.getId())) {
            valid = false;
        }

        return valid;
    }

}
