package com.search.manager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.Comment;
import com.search.manager.model.ElevateProduct;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRuleCondition;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;
import com.search.manager.utility.DateAndTimeUtils;
import com.search.ws.SearchHelper;

@Service(value = "elevateService")
@RemoteProxy(
		name = "ElevateServiceJS",
		creator = SpringCreator.class,
		creatorParams = @Param(name = "beanName", value = "elevateService")
)
public class ElevateService{

	private static final Logger logger = Logger.getLogger(ElevateService.class);

	@Autowired private DaoService daoService;

	@RemoteMethod
	public int updateElevateItem(String keyword, String memberId, int position, String comment, String expiryDate, String condition){
		int changes = 0;

		ElevateResult elevate = new ElevateResult();
		elevate.setStoreKeyword(new StoreKeyword(UtilityService.getStoreName(), keyword));
		elevate.setMemberId(memberId);
		
		try {
			elevate = daoService.getElevateItem(elevate);
		} catch (DaoException e) {
			elevate = null;
		}
		
		if(elevate==null){
			return changes;
		}
		
		ElevateProduct elevateProduct = new ElevateProduct(elevate);

		if (position!=elevate.getLocation()){
			changes += ((updateElevate(keyword, memberId, position, null) > 0)? 1 : 0);
		}

		if (StringUtils.isNotBlank(comment)){
			changes += ((addComment(keyword, memberId, comment) > 0)? 1 : 0);
		}

		if (StringUtils.isNotBlank(condition)){
			changes += ((updateElevate(keyword, memberId, position, condition) > 0)? 1 : 0);
		}

		if (!StringUtils.equalsIgnoreCase(StringUtils.trimToEmpty(expiryDate), StringUtils.trimToEmpty(elevateProduct.getFormattedExpiryDate()))) {
			changes += ((updateExpiryDate(keyword, memberId, expiryDate) > 0)? 1 : 0);
		}

		return changes;
	}

	@RemoteMethod
	public int updateElevateFacet(String keyword, String memberId, int position, String comment, String expiryDate, Map<String, List<String>> filter){
		int changes = 0;

		ElevateResult elevate = new ElevateResult();
		elevate.setStoreKeyword(new StoreKeyword(UtilityService.getStoreName(), keyword));
		elevate.setMemberId(memberId);
		RedirectRuleCondition rrCondition = new RedirectRuleCondition();
		rrCondition.setFilter(filter);
		try {
			elevate = daoService.getElevateItem(elevate);
		} catch (DaoException e) {
			elevate = null;
		}

		if(elevate==null){
			return changes;
		}

		ElevateProduct elevateProduct = new ElevateProduct(elevate);
		
		if (position!=elevate.getLocation()){
			changes += ((updateElevate(keyword, memberId, position, null) > 0)? 1 : 0);
		}

		if (StringUtils.isNotBlank(comment)){
			try {
				addComment(comment,elevate);
				changes++;
			} catch (DaoException e) {
				logger.error("Error adding comment in updateElevateFacet()",e);
			}
		}

		if (!rrCondition.getCondition().equals(elevate.getCondition().getCondition())){
			changes += ((updateElevate(keyword, memberId, position, rrCondition.getCondition()) > 0)? 1 : 0);
		}

		if (!StringUtils.equalsIgnoreCase(StringUtils.trimToEmpty(expiryDate), StringUtils.trimToEmpty(elevateProduct.getFormattedExpiryDate()))) {
			changes += ((updateExpiryDate(keyword, memberId, expiryDate) > 0)? 1 : 0);
		}

		return changes;
	}

	public int addItem(String keyword, String edp, int sequence, String expiryDate, String comment, boolean forceAdd) {
		int result = -1;
		try {
			logger.info(String.format("%s %s %d %s %s", keyword, edp, sequence, expiryDate, comment));
			String store = UtilityService.getStoreName();

			daoService.addKeyword(new StoreKeyword(store, keyword)); // TODO: What if keyword is not added?

			ElevateResult e = new ElevateResult(new StoreKeyword(store, keyword));

			e.setLocation(sequence);
			e.setExpiryDate(StringUtils.isEmpty(expiryDate) ? null : DateAndTimeUtils.toSQLDate(store, expiryDate));
			e.setCreatedBy(UtilityService.getUsername());
			e.setComment(UtilityService.formatComment(comment));
			e.setEdp(edp);
			e.setElevateEntity(MemberTypeEntity.PART_NUMBER);
			e.setForceAdd(forceAdd);

			result  = daoService.addElevateResult(e);
			if (result > 0) {
				if (!StringUtils.isBlank(comment)) {
					addComment(comment, e);
				}
				if (e.isForceAdd()) {
					result = 2;
				}
			}
		} catch (DaoException e) {
			logger.error("Failed during addProductItem()",e);
		}
		return result;

	}


	@RemoteMethod
	public int addProductItemForceAdd(String keyword, String edp, int sequence, String expiryDate, String comment) {
		return addItem(keyword, edp, sequence, expiryDate, comment, true);
	}

	@RemoteMethod
	public int addProductItem(String keyword, String edp, int sequence, String expiryDate, String comment) {
		return addItem(keyword, edp, sequence, expiryDate, comment, false);
	}

	@RemoteMethod
	public Map<String, List<String>> addItemToRuleUsingPartNumber(String keyword, int sequence, String expiryDate, String comment, String[] partNumbers) {

		logger.info(String.format("%s %s %d", keyword, partNumbers, sequence));
		HashMap<String, List<String>> resultMap = new HashMap<String, List<String>>();

		ArrayList<String> passedList = new ArrayList<String>();
		ArrayList<String> failedList = new ArrayList<String>();
//		ArrayList<String> forcedList = new ArrayList<String>();

		resultMap.put("PASSED", passedList);
//		resultMap.put("FORCED", forcedList);
		resultMap.put("FAILED", failedList);

		String server = UtilityService.getServerName();
		String store = UtilityService.getStoreName();

		int count = 0;
		comment = comment.replaceAll("%%timestamp%%", DateAndTimeUtils.formatDateTimeUsingConfig(store, new Date()));
		comment = comment.replaceAll("%%commentor%%", UtilityService.getUsername());

		sequence = (sequence==0)? 1: sequence;
		for(String partNumber: partNumbers){
			count = 0;
			ElevateResult e = new ElevateResult();
			try {
				String edp = SearchHelper.getEdpViaSim(server, store, keyword, StringUtils.trim(partNumber));
				if (StringUtils.isBlank(edp)) {
					edp = daoService.getEdpByPartNumber(server, store, "", StringUtils.trim(partNumber));
					e.setFoundFlag(false);
				} else {
					e.setFoundFlag(true);
				}
				if (StringUtils.isNotBlank(edp)) {
					e.setStoreKeyword(new StoreKeyword(store, keyword));
					e.setEdp(edp);
					e.setForceAdd(false);
					e.setLocation(sequence++);
					e.setExpiryDate(StringUtils.isBlank(expiryDate) ? null : DateAndTimeUtils.toSQLDate(store, expiryDate));
					e.setCreatedBy(UtilityService.getUsername());
					e.setComment(UtilityService.formatComment(comment));
					e.setElevateEntity(MemberTypeEntity.PART_NUMBER);
					if (StringUtils.isNotBlank(edp)){
						count = daoService.addElevateResult(e);
						if (!StringUtils.isBlank(comment)) {
							addComment(comment, e);
						}
					}
				} else {

				}
			} catch (DaoException de) {
				logger.error("Failed during addItemToRuleUsingPartNumber()",de);
			}
			if (count > 0) {
//				if (e.getFoundFlag()) {
//					forcedList.add(StringUtils.trim(partNumber));						
//				} else {
					passedList.add(StringUtils.trim(partNumber));						
//				}
			}
			else {
				failedList.add(StringUtils.trim(partNumber));
			}
		}
		return resultMap;
	}

	@RemoteMethod
	public String addFacetRule(String keyword, int sequence, String expiryDate, String comment,  Map<String, List<String>> filter) {

		String result = "FAILED";
		try {
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();
			ElevateResult e = new ElevateResult();
			RedirectRuleCondition condition = new RedirectRuleCondition();
			condition.setFilter(filter);
			e.setCondition(condition );
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			e.setLocation(sequence++);
			e.setExpiryDate(StringUtils.isBlank(expiryDate) ? null : DateAndTimeUtils.toSQLDate(store, expiryDate));
			e.setCreatedBy(UtilityService.getUsername());
			e.setComment(UtilityService.formatComment(comment));
			e.setElevateEntity(MemberTypeEntity.FACET);
//			int facetCount = SearchHelper.getFacetCountViaSim(server, store, keyword, e.getCondition().getConditionForSolr());
			e.setForceAdd(false);
//			e.setFoundFlag(facetCount > 0);
			int count = daoService.addElevateResult(e);
			if (count > 0) {
//				result = e.getFoundFlag()?"PASSED":"FORCED";
				result = "PASSED";
				//e.getFoundFlag()?"PASSED":"FORCED";
				if (!StringUtils.isBlank(comment)) {
					addComment(comment, e);
				}
			}
		} catch (DaoException de) {
			logger.error("Failed during addItemToRuleUsingPartNumber()",de);
		}
		return result;
	}

	@RemoteMethod
	public int updateExpiryDate(String keyword, String memberId, String expiryDate){
		int result = -1;
		try {
			logger.info(String.format("%s %s %s", keyword, memberId, expiryDate));
			String store = UtilityService.getStoreName();
			ElevateResult e = new ElevateResult(new StoreKeyword(store, keyword), memberId);
			e = daoService.getElevateItem(e);
			if (e != null) {
				e.setExpiryDate(DateAndTimeUtils.toSQLDate(store, expiryDate));
				e.setLastModifiedBy(UtilityService.getUsername());
				result = daoService.updateElevateResultExpiryDate(e);
			}
		} catch (DaoException e) {
			logger.error("Failed during updateExpiryDate()",e);
		}
		return result;
	}

	@RemoteMethod
	public int addComment(String keyword, String memberId, String comment){
		try {
			logger.info(String.format("%s %s %s", keyword, memberId, comment));
			String store = UtilityService.getStoreName();

			if(StringUtils.isNotBlank(comment)){
				comment = comment.replaceAll("%%timestamp%%", DateAndTimeUtils.formatDateTimeUsingConfig(store, new Date()));
				comment = comment.replaceAll("%%commentor%%", UtilityService.getUsername());
			}

			ElevateResult e = new ElevateResult(new StoreKeyword(store, keyword), memberId);
			e.setLastModifiedBy(UtilityService.getUsername());
			e.setComment(UtilityService.formatComment(comment));
			return daoService.appendElevateResultComment(e);
		} catch (DaoException e) {
			logger.error("Failed during addComment()",e);
		}
		return -1;
	}

	@RemoteMethod
	public int deleteItemInRule(String keyword, String memberId) {
		try {
			logger.info(String.format("%s %s", keyword, memberId));
			String store = UtilityService.getStoreName();
			ElevateResult e = new ElevateResult(new StoreKeyword(store, keyword), memberId);
			e.setLastModifiedBy(UtilityService.getUsername());
			e = daoService.getElevateItem(e);
			return daoService.deleteElevateResult(e);
		} catch (DaoException e) {
			logger.error("Failed during removeElevate()",e);
		}
		return -1;
	}

	@RemoteMethod
	public int updateElevate(String keyword, String memberId, int sequence, String condition) {
		try {
			logger.info(String.format("%s %s %d", keyword, memberId, sequence));
			ElevateResult elevate = new ElevateResult(new StoreKeyword(UtilityService.getStoreName(), keyword), memberId);

			try {
				elevate = daoService.getElevateItem(elevate);
			} catch (DaoException e) {
				elevate = null;
			}
			if (elevate!=null) {
				if (!StringUtils.isBlank(condition)) {
					elevate.setCondition(new RedirectRuleCondition((condition)));
				}
				elevate.setLocation(sequence);
				elevate.setLastModifiedBy(UtilityService.getUsername());
				return daoService.updateElevateResult(elevate);
			}
		} catch (DaoException e) {
			logger.error("Failed during updateElevate()",e);
		}
		return -1;
	}

	@RemoteMethod
	public int updateElevateForceAdd(String keyword, String memberId, boolean forceAddFlag) {
		try {
			logger.info(String.format("%s %s %b", keyword, memberId, forceAddFlag));
			ElevateResult elevate = new ElevateResult(new StoreKeyword(UtilityService.getStoreName(), keyword), memberId);

			try {
				elevate = daoService.getElevateItem(elevate);
			} catch (DaoException e) {
				elevate = null;
			}
			if (elevate!=null) {
				elevate.setForceAdd(forceAddFlag);
				elevate.setLastModifiedBy(UtilityService.getUsername());
				return daoService.updateElevateResult(elevate);
			}
		} catch (DaoException e) {
			logger.error("Failed during updateElevate()",e);
		}
		return -1;
	}

	@RemoteMethod
	public RecordSet<ElevateProduct> getProducts(String filter, String keyword, int page, int itemsPerPage) {

		if (StringUtils.isBlank(filter) || StringUtils.equalsIgnoreCase("all", filter))
			return getAllElevatedProductsIgnoreKeyword(keyword, page, itemsPerPage);

		if (StringUtils.equalsIgnoreCase("active", filter))
			return getActiveElevatedProductsIgnoreKeyword(keyword, page, itemsPerPage);

		if (StringUtils.equalsIgnoreCase("expired", filter))
			return getExpiredElevatedProductsIgnoreKeyword(keyword, page, itemsPerPage);

		return null;
	}

	@RemoteMethod
	public ElevateProduct getProductByEdp(String keyword, String edp) {

		RecordSet<ElevateProduct> products = getAllElevatedProducts(keyword, 0, 100);
		ElevateProduct product = null;
		for (ElevateProduct  prod: products.getList()) {
			if (prod.getMemberTypeEntity() == MemberTypeEntity.PART_NUMBER && prod.getEdp().equals(StringUtils.trim(edp))) {
				product = prod;
				break;
			}
		}
		return product;
	}

	@RemoteMethod
	public RecordSet<ElevateProduct> getAllElevatedProducts(String keyword, int page,int itemsPerPage) {
		RecordSet<ElevateProduct> result = null;
		try {
			logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();

			ElevateResult e = new ElevateResult(new StoreKeyword(store, keyword));
			SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(e, null, null,  page, itemsPerPage);
			result  = daoService.getElevatedProducts(server, criteria);
		} catch (DaoException e) {
			logger.error("Failed during getAllElevatedProducts()",e);
		}
		return result;
	}

	@RemoteMethod
	public RecordSet<ElevateProduct> getAllElevatedProductsIgnoreKeyword(String keyword, int page,int itemsPerPage) {
		try {
			logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();

			ElevateResult e = new ElevateResult(new StoreKeyword(store, keyword));
			SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(e, null, null,  page, itemsPerPage);
			return daoService.getElevatedProductsIgnoreKeyword(server, criteria);
		} catch (DaoException e) {
			logger.error("Failed during getAllElevatedProducts()",e);
		}
		return null;
	}

	@RemoteMethod
	public RecordSet<ElevateProduct> getActiveElevatedProducts(String keyword, int page,int itemsPerPage) {
		try {
			logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();
			ElevateResult e = new ElevateResult(new StoreKeyword(store, keyword));
			SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(e, new Date(), null,  page, itemsPerPage);
			return daoService.getElevatedProducts(server, criteria);
		} catch (DaoException e) {
			logger.error("Failed during getActiveElevatedProducts()",e);
		}
		return null;
	}

	@RemoteMethod
	public RecordSet<ElevateProduct> getActiveElevatedProductsIgnoreKeyword(String keyword, int page,int itemsPerPage) {
		try {
			logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();
			ElevateResult e = new ElevateResult(new StoreKeyword(store, keyword));
			SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(e, new Date(), null,  page, itemsPerPage);
			return daoService.getElevatedProductsIgnoreKeyword(server, criteria);
		} catch (DaoException e) {
			logger.error("Failed during getActiveElevatedProducts()",e);
		}
		return null;
	}

	@RemoteMethod
	public RecordSet<ElevateProduct> getExpiredElevatedProducts(String keyword, int page,int itemsPerPage) {
		try {
			logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();
			ElevateResult e = new ElevateResult(new StoreKeyword(store, keyword));
			SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(e, null, DateAndTimeUtils.getDateYesterday(),  page, itemsPerPage);
			return daoService.getElevatedProducts(server, criteria);
		} catch (DaoException e) {
			logger.error("Failed during getExpiredElevatedProducts()",e);
		}
		return null;
	}

	@RemoteMethod
	public RecordSet<ElevateProduct> getExpiredElevatedProductsIgnoreKeyword(String keyword, int page,int itemsPerPage) {
		try {
			logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();
			ElevateResult e = new ElevateResult(new StoreKeyword(store, keyword));
			SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(e, null, DateAndTimeUtils.getDateYesterday(),  page, itemsPerPage);
			return daoService.getElevatedProductsIgnoreKeyword(server, criteria);
		} catch (DaoException e) {
			logger.error("Failed during getExpiredElevatedProducts()",e);
		}
		return null;
	}

	@RemoteMethod
	public RecordSet<ElevateProduct> getNoExpiryElevatedProducts(String keyword, int page,int itemsPerPage) {
		try {
			logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();
			ElevateResult e = new ElevateResult(new StoreKeyword(store, keyword));
			SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(e, null, null,  page, itemsPerPage);
			return daoService.getNoExpiryElevatedProducts(server, criteria);
		} catch (DaoException e) {
			logger.error("Failed during getNoExpiryElevatedProducts()",e);
		}
		return null;
	}

	@RemoteMethod
	public Integer getTotalProductInRule(String keyword) {
		try {
			logger.info(String.format("%s", keyword));
			String store = UtilityService.getStoreName();
			ElevateResult e = new ElevateResult(new StoreKeyword(store, keyword));
			SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(e, null, null, null, null);
			return daoService.getElevateResultCount(criteria);
		} catch (DaoException e) {
			logger.error("Failed during getTotalProductInRule()",e);
		}
		return null;
	}

	@RemoteMethod
	public ElevateProduct getElevatedProduct(String keyword, String memberId) {
		try {
			logger.info(String.format("%s %s", keyword, memberId));
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();
			ElevateResult e = new ElevateResult(new StoreKeyword(store, keyword));
			e.setMemberId(memberId);
			return daoService.getElevatedProduct(server, e);
		} catch (DaoException e) {
			logger.error("Failed during getElevatedProduct()",e);
		}
		return null;
	}

	@RemoteMethod
	public String getComment(String keyword, String memberId) {
		ElevateProduct elevatedProduct = getElevatedProduct(keyword, memberId);
		if (elevatedProduct == null)
			return StringUtils.EMPTY;

		return StringUtils.trimToEmpty(elevatedProduct.getComment());
	}

	@RemoteMethod
	public int clearRule(String keyword) {
		try {
			logger.info(String.format("%s", keyword));
			return daoService.clearElevateResult(new StoreKeyword(UtilityService.getStoreName(), keyword));
		} catch (DaoException e) {
			logger.error("Failed during clearRule()",e);
		}
		return -1;
	}

	private Comment addComment(String comment, ElevateResult e) throws DaoException {
		Comment com = new Comment();
		com.setComment(comment);
		com.setUsername(UtilityService.getUsername());
		com.setReferenceId(e.getMemberId());
		com.setRuleTypeId(RuleEntity.ELEVATE.getCode());
		daoService.addComment(com);
		return com;
	}

	@RemoteMethod
	public int addRuleComment(String keyword, String memberId, String pComment) {
		int result = -1;
		try {
			ElevateResult elevate = new ElevateResult();
			elevate.setStoreKeyword(new StoreKeyword(UtilityService.getStoreName(), keyword));
			elevate.setMemberId(memberId);
			try {
				elevate = daoService.getElevateItem(elevate);
			} catch (DaoException e) {
				elevate = null;
			}
			if (elevate != null) {
				elevate.setComment(pComment);
				elevate.setLastModifiedBy(UtilityService.getUsername());
				daoService.updateElevateResultComment(elevate);
				Comment com = new Comment();
				com.setComment(pComment);
				com.setUsername(UtilityService.getUsername());
				com.setReferenceId(elevate.getMemberId());
				com.setRuleTypeId(RuleEntity.ELEVATE.getCode());
				result = daoService.addComment(com);
			}
		} catch (DaoException e) {
			logger.error("Failed during addRuleItemComment()",e);
		}
		return result;
	}

	public DaoService getDaoService() {
		return daoService;
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}
}
