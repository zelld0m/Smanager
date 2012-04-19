package com.search.manager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoService;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RuleStatus;

@Service(value = "deploymentService")
@RemoteProxy(
		name = "DeploymentServiceJS",
	    creator = SpringCreator.class,
	    creatorParams = @Param(name = "beanName", value = "deploymentService")
	)
public class DeploymentService {

	private static final Logger logger = Logger.getLogger(DeploymentService.class);
	
	@Autowired private DaoService daoService;

	public DaoService getDaoService() {
		return daoService;
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}

	@RemoteMethod
	public RecordSet<RuleStatus> getApprovalList(String entity, Boolean includeApprovedFlag) {
		List<RuleStatus> list = new ArrayList<RuleStatus>();
		RuleStatus r = new RuleStatus("123", "elevate", "ipad", "Elevate rule for ipod", "P", "add", "U", new Date(), "test comment");
		list.add(r);
		r = new RuleStatus("123", "elevate", "ipod", "Elevate rule for ipad", "P", "update", "U", new Date(), "sample comment");
		list.add(r);
		return new RecordSet<RuleStatus>(list, 2);
	}

	@RemoteMethod
	public int approveRule(List<String> ruleRefId) {
		int result = -1;
//		try {
//			
//		} catch (DaoException e) {
//			logger.error("Failed during updateRedirectRule()",e);
//		}
		return result;
	}

	@RemoteMethod
	public int approveRule(String ruleRefId, String comment) {
		int result = -1;
//		try {
//			
//		} catch (DaoException e) {
//			logger.error("Failed during updateRedirectRule()",e);
//		}
		return result;
	}

	@RemoteMethod
	public int unapproveRule(List<String> ruleRefId) {
		int result = -1;
//		try {
//			
//		} catch (DaoException e) {
//			logger.error("Failed during updateRedirectRule()",e);
//		}
		return result;
	}

	@RemoteMethod
	public RecordSet<RuleStatus> getDeployedRules(String entity) {
		List<RuleStatus> list = new ArrayList<RuleStatus>();
		RuleStatus r = new RuleStatus("123", "elevate", "ipad", "Elevate rule for ipod", "A", "add", "U", new Date(), "test comment");
		list.add(r);
		r = new RuleStatus("123", "elevate", "ipod", "Elevate rule for ipad", "A", "update", "P", new Date(), "sample comment");
		list.add(r);
		return new RecordSet<RuleStatus>(list, 2);
	}

	@RemoteMethod
	public int publishRule(List<String> ruleRefId) {
		int result = -1;
//		try {
//			
//		} catch (DaoException e) {
//			logger.error("Failed during updateRedirectRule()",e);
//		}
		return result;
	}

	@RemoteMethod
	public int unpublishRule(List<String> ruleRefId) {
		int result = -1;
//		try {
//			
//		} catch (DaoException e) {
//			logger.error("Failed during updateRedirectRule()",e);
//		}
		return result;
	}

	@RemoteMethod
	public int recallRule(List<String> ruleRefId) {
		int result = -1;
//		try {
//			
//		} catch (DaoException e) {
//			logger.error("Failed during updateRedirectRule()",e);
//		}
		return result;
	}

	@RemoteMethod
	public int AddComment(String ruleRefId) {
		int result = -1;
//		try {
//			
//		} catch (DaoException e) {
//			logger.error("Failed during updateRedirectRule()",e);
//		}
		return result;
	}

	@RemoteMethod
	public int removeComment(String commentId) {
		int result = -1;
//		try {
//			
//		} catch (DaoException e) {
//			logger.error("Failed during updateRedirectRule()",e);
//		}
		return result;
	}

}