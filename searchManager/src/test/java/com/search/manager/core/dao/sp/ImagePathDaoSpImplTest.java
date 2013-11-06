package com.search.manager.core.dao.sp;

import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.ExpectedException;

import com.search.manager.ImagePathTestData;
import com.search.manager.core.BaseIntegrationTest;
import com.search.manager.core.dao.ImagePathDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.model.ImagePath;
import com.search.manager.core.model.ImagePathType;

public class ImagePathDaoSpImplTest extends BaseIntegrationTest {

	@Autowired
	@Qualifier("imagePathDaoSp")
	private ImagePathDao imagePathDao;

	@Test
	public void testWiring() {
		assertNotNull(imagePathDao);
	}

	@Ignore
	@Test
	public void addDeleteTest() throws CoreDaoException {
		ImagePath imagePath = imagePathDao.add(ImagePathTestData
				.getNewImagePath());

		// Test successful add
		assertNotNull(imagePath);
		assertNotNull(imagePath.getId());

		// Remove added image path
		// TODO unimplemented method
		Assert.assertTrue(imagePathDao.delete(imagePath));
	}

	@Test
	public void updateTest() throws CoreDaoException {
		ImagePath imagePath = ImagePathTestData.getExistingImagePath();

		// Update fields
		imagePath.setAlias("alias - updated");
		imagePath.setComment("comment - updated");
		imagePath.setCreatedBy("ecost_admin - updated");
		imagePath.setPath("path - updated");
		imagePath.setPathType(ImagePathType.UPLOAD_LINK);
		imagePath.setSize("1x1");

		imagePath = imagePathDao.update(imagePath);

		// Test successful update
		assertNotNull(imagePath);
		assertNotNull(imagePath.getId());
		// assertNotNull(imagePath.getLastModifiedDate());

		Assert.assertEquals("alias - updated", imagePath.getAlias());
		// TODO comment, created by, path type, size and path not supported on SP update
		// Assert.assertEquals("comment - updated", imagePath.getComment());
		// Assert.assertEquals("ecost_admin - updated", imagePath.getCreatedBy());
		// Assert.assertEquals("path - updated", imagePath.getPath());
		// Assert.assertEquals(ImagePathType.UPLOAD_LINK.name(), imagePath.getPathType().name());
		// Assert.assertEquals("1x1", imagePath.getSize());

		// Revert field
		imagePath = imagePathDao.update(ImagePathTestData
				.getExistingImagePath());

		// Test successful revert
		assertNotNull(imagePath);
		assertNotNull(imagePath.getId());
		// TODO assertNotNull(imagePath.getLastModifiedDate());

		Assert.assertEquals(
				ImagePathTestData.getExistingImagePath().getAlias(),
				imagePath.getAlias());
		// Assert.assertEquals(ImagePathTestData.getExistingImagePath().getComment(), imagePath.getComment());
		Assert.assertEquals(ImagePathTestData.getExistingImagePath()
				.getCreatedBy(), imagePath.getCreatedBy());
		Assert.assertEquals(ImagePathTestData.getExistingImagePath().getPath(),
				imagePath.getPath());
		Assert.assertEquals(ImagePathTestData.getExistingImagePath()
				.getPathType().name(), imagePath.getPathType().name());
		Assert.assertEquals(ImagePathTestData.getExistingImagePath().getSize(),
				imagePath.getSize());
	}

	@Test
	@ExpectedException(CoreDaoException.class)
	public void deleteTest() throws CoreDaoException {
		Assert.assertTrue(imagePathDao.delete(ImagePathTestData
				.getNewImagePath()));
	}

}
