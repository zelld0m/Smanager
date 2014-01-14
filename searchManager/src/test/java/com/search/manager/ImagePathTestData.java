package com.search.manager;

import com.search.manager.core.model.ImagePath;
import com.search.manager.core.model.ImagePathType;

public class ImagePathTestData {

	public static final String DEFAULT_ID = "imagepath1234";

	public static ImagePath getNewImagePath() {
		ImagePath imagePath = new ImagePath();
		imagePath.setStoreId("ecost");
		imagePath
				.setPath("http://www.dep.state.fl.us/waste/quick_topics/publications/shw/recycling/ARD/2007/728X90_Banner.jpg-new");
		imagePath.setSize("728x90");
		imagePath.setPathType(ImagePathType.IMAGE_LINK);
		imagePath.setAlias("alias - new");
		imagePath.setComment("test comment");
		imagePath.setCreatedBy("ecost_admin");

		return imagePath;
	}

	public static ImagePath getExistingImagePath() {
		ImagePath imagePath = getNewImagePath();
		imagePath.setId(DEFAULT_ID);
		imagePath
				.setPath("http://www.dep.state.fl.us/waste/quick_topics/publications/shw/recycling/ARD/2007/728X90_Banner.jpg");
		imagePath.setAlias("alias");

		return imagePath;
	}

}
