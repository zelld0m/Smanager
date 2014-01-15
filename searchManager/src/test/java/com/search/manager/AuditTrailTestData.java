package com.search.manager;

import org.joda.time.DateTime;

import com.search.manager.core.model.AuditTrail;

public class AuditTrailTestData {

	public static final String DEFAULT_ID = "auditTrail1234";

	public static AuditTrail getNewAuditTrail() {
		AuditTrail auditTrail = new AuditTrail();
		auditTrail.setUsername("ecost_admin");
		auditTrail.setOperation("add");
		auditTrail.setEntity("bannerRuleItem");
		auditTrail.setStoreId("ecost");
		auditTrail.setKeyword("testbanner1234");
		auditTrail.setCreatedDate(new DateTime());
		auditTrail.setDetails("test details");

		return auditTrail;
	}

	public static AuditTrail getExistingAuditTrail() {
		AuditTrail auditTrail = getNewAuditTrail();
		auditTrail.setReferenceId("referenceid1234");

		return auditTrail;
	}
}
