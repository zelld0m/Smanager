package com.search.manager.core.dao.sp;

import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.ExpectedException;

import com.search.manager.AuditTrailTestData;
import com.search.manager.core.BaseIntegrationTest;
import com.search.manager.core.dao.AuditTrailDao;
import com.search.manager.core.dao.BasicDaoTest;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.model.AuditTrail;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;

public class AuditTrailDaoSpImplTest extends BaseIntegrationTest implements
		BasicDaoTest<AuditTrail> {

	@Autowired
	@Qualifier("auditTrailDaoSp")
	private AuditTrailDao auditTrailDao;

	@Test
	@Override
	public void daoWiringTest() throws CoreDaoException {
		assertNotNull(auditTrailDao);
	}

	@Test
	@Override
	public void addTest() throws CoreDaoException {
		AuditTrail auditTrail = AuditTrailTestData.getNewAuditTrail();

		auditTrail = auditTrailDao.add(auditTrail);
		assertNotNull(auditTrail);
	}

	@Test
	@ExpectedException(CoreDaoException.class)
	@Override
	public void updateTest() throws CoreDaoException {
		AuditTrail auditTrail = AuditTrailTestData.getExistingAuditTrail();
		auditTrail = auditTrailDao.update(auditTrail);
	}

	@Test
	@ExpectedException(CoreDaoException.class)
	@Override
	public void deleteTest() throws CoreDaoException {
		auditTrailDao.delete(AuditTrailTestData.getExistingAuditTrail());
	}

	@Test
	@Override
	public void searchTest() throws CoreDaoException {
		Search search = new Search(AuditTrail.class);
		search.setPageNumber(1);
		search.setMaxRowCount(10);
		SearchResult<AuditTrail> auditTrails = auditTrailDao.search(search);
		assertNotNull(auditTrails);
		Assert.assertTrue(auditTrails.getTotalCount() > 0);
		for (AuditTrail auditTrail : auditTrails.getResult()) {
			assertNotNull(auditTrail);
		}
	}

	@Test
	@Override
	public void searchModelTest() throws CoreDaoException {
		SearchResult<AuditTrail> auditTrails = auditTrailDao
				.search(AuditTrailTestData.getExistingAuditTrail());
		assertNotNull(auditTrails);
		Assert.assertTrue(auditTrails.getTotalCount() > 0);
		for (AuditTrail auditTrail : auditTrails.getResult()) {
			assertNotNull(auditTrail);
		}
	}

}
