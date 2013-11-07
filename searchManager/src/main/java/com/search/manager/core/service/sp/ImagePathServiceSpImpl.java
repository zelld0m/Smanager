package com.search.manager.core.service.sp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.core.dao.ImagePathDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.ImagePath;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.ImagePathService;

@Service("imagePathServiceSp")
public class ImagePathServiceSpImpl implements ImagePathService {

	@Autowired
	@Qualifier("imagePathDaoSp")
	private ImagePathDao imagePathDao;

	@Override
	public ImagePath add(ImagePath model) throws CoreServiceException {
		try {
			return imagePathDao.add(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public ImagePath update(ImagePath model) throws CoreServiceException {
		try {
			return imagePathDao.update(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public boolean delete(ImagePath model) throws CoreServiceException {
		try {
			return imagePathDao.delete(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public SearchResult<ImagePath> search(Search search)
			throws CoreServiceException {
		try {
			return imagePathDao.search(search);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

}
