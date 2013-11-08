package com.search.manager.core.service.sp;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.search.manager.core.BaseIntegrationTest;
import com.search.manager.core.service.ImagePathService;

public class ImagePathServiceSpImplTest extends BaseIntegrationTest {

	@Autowired
	@Qualifier("imagePathServiceSp")
	private ImagePathService imagePathService;

	@Test
	public void testWiring() {
		assertNotNull(imagePathService);
	}

}
