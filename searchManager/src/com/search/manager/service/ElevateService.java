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
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.model.Comment;
import com.search.manager.model.ElevateProduct;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRuleCondition;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.utility.DateAndTimeUtils;
import com.search.ws.SearchHelper;

@Service("elevateService")
@RemoteProxy(
		name = "ElevateServiceJS",
		creator = SpringCreator.class,
		creatorParams = @Param(name = "beanName", value = "elevateService")
)
public class ElevateService extends RuleService{

	private static final Logger logger = Logger.getLogger(ElevateService.class);

	@Autowired private DaoService daoService;

	@Override
	public RuleEntity getRuleEntity() {
		return RuleEntity.ELEVATE;
	}

	@RemoteMethod
	public int updateElevateItem(String keyword, String memberId, int position, String comment, String expiryDate, String condition){
		int changes = 0;

		ElevateResult elevate = new ElevateResult();
		elevate.setStoreKeyword(new StoreKeyword(UtilityService.getStoreId(), keyword));
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

		String storeId = UtilityService.getStoreId();
		ElevateResult elevate = new ElevateResult();
		elevate.setStoreKeyword(new StoreKeyword(storeId, keyword));
		elevate.setMemberId(memberId);
		
		RedirectRuleCondition rrCondition = new RedirectRuleCondition();
		rrCondition.setStoreId(storeId);
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

	private int addItem(String keyword, String edp, RedirectRuleCondition condition, int sequence, String expiryDate, String comment, MemberTypeEntity entity, boolean forceAdd) {
		int result = -1;
		try {
			logger.info(String.format("%s %s %s %d %s %s", keyword, edp, condition != null ? condition.getCondition() : "", sequence, expiryDate, comment));
			String store = UtilityService.getStoreId();
			String userName = UtilityService.getUsername();
			daoService.addKeyword(new StoreKeyword(store, keyword)); // TODO: What if keyword is not added?

			ElevateResult e = new ElevateResult(new StoreKeyword(store, keyword));
			e.setLocation(sequence);
			e.setExpiryDate(StringUtils.isEmpty(expiryDate) ? null : DateAndTimeUtils.toSQLDate(store, expiryDate));
			e.setCreatedBy(userName);
			e.setComment(UtilityService.formatComment(comment));
			e.setElevateEntity(entity);
			e.setForceAdd(forceAdd);
			switch (entity) {
			case PART_NUMBER:
				e.setEdp(edp);
				break;
			case FACET:
				e.setCondition(condition);
				break;
			}

			result  = daoService.addElevateResult(e);
			if (result > 0) {
				if (!StringUtils.isBlank(comment)) {
					addComment(comment, e);
				}
				if (e.isForceAdd()) {
					result = 2;
				}
				try {
					// TODO: add checking if existing rule status?
					daoService.addRuleStatus(new RuleStatus(RuleEntity.ELEVATE, DAOUtils.getStoreId(e.getStoreKeyword()), 
							keyword, keyword, userName, userName, RuleStatusEntity.ADD, RuleStatusEntity.UNPUBLISHED));
				} catch (DaoException de) {
					logger.error("Failed to create rule status for elevate: " + keyword);
				}
			}
		} catch (DaoException e) {
			logger.error("Failed during addItem()",e);
		}
		return result;
	}


	@RemoteMethod
	public int addProductItemForceAdd(String keyword, String edp, int sequence, String expiryDate, String comment) {
		return addItem(keyword, edp, null, sequence, expiryDate, comment, MemberTypeEntity.PART_NUMBER, true);
	}

	@RemoteMethod
	public int addProductItem(String keyword, String edp, int sequence, String expiryDate, String comment) {
		return addItem(keyword, edp, null, sequence, expiryDate, comment, MemberTypeEntity.PART_NUMBER, false);
	}

	@RemoteMethod
	public Map<String, List<String>> addItemToRuleUsingPartNumber(String keyword, int sequence, String expiryDate, String comment, String[] partNumbers) {

		logger.info(String.format("%s %s %d", keyword, partNumbers, sequence));
		HashMap<String, List<String>> resultMap = new HashMap<String, List<String>>();

		ArrayList<String> passedList = new ArrayList<String>();
		ArrayList<String> failedList = new ArrayList<String>();

		resultMap.put("PASSED", passedList);
		resultMap.put("FAILED", failedList);

		String server = UtilityService.getServerName();
		String store = UtilityService.getStoreId();

		int count = 0;
		comment = comment.replaceAll("%%timestamp%%", DateAndTimeUtils.formatDateTimeUsingConfig(store, new Date()));
		comment = comment.replaceAll("%%commentor%%", UtilityService.getUsername());

		sequence = (sequence==0)? 1: sequence;
		for(String partNumber: partNumbers){
			count = 0;
			try {
				String edp = daoService.getEdpByPartNumber(server, store, "", StringUtils.trim(partNumber));
				if (StringUtils.isNotBlank(edp)) {
					count = addItem(keyword, edp, null, sequence++, expiryDate, comment, MemberTypeEntity.PART_NUMBER, false);
				}
			} catch (DaoException de) {
				logger.error("Failed during addItemToRuleUsingPartNumber()",de);
			}
			if (count > 0) {
				passedList.add(StringUtils.trim(partNumber));						
			}
			else {
				failedList.add(StringUtils.trim(partNumber));
			}				

		}
		return resultMap;
	}

	@RemoteMethod
	public int addFacetRule(String keyword, int sequence, String expiryDate, String comment,  Map<String, List<String>> filter) {
		RedirectRuleCondition rrCondition = new RedirectRuleCondition(filter);
		rrCondition.setStoreId(UtilityService.getStoreId());
		return addItem(keyword, null, rrCondition, sequence, expiryDate, comment, MemberTypeEntity.FACET, false);
	}

	@RemoteMethod
	public int updateExpiryDate(String keyword, String memberId, String expiryDate){
		int result = -1;
		try {
			logger.info(String.format("%s %s %s", keyword, memberId, expiryDate));
			String store = UtilityService.getStoreId();
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
			String store = UtilityService.getStoreId();

			if(StringUtils.isNotBlank(comment)){
				comment = comment.replaceAll("%%timestamp%%", DateAndTimeUtils.formatDateTimeUsingConfig(store, new Date()));
				comment = comment.replaceAll("%%commentor%%", UtilityService.getUsername());
			}

			ElevateResult e = new ElevateResult(new StoreKeyword(store, keyword), memberId);
			e.setLastModifiedBy(UtilityService.getUsername());
			e.setComment(UtilityService.formatComment(comment));
			daoService.appendElevateResultComment(e);
			return addComment(comment, e);
		} catch (DaoException e) {
			logger.error("Failed during addComment()",e);
		}
		return -1;
	}

	@RemoteMethod
	public int deleteItemInRule(String keyword, String memberId) {
		try {
			logger.info(String.format("%s %s", keyword, memberId));
			String store = UtilityService.getStoreId();
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
			ElevateResult elevate = new ElevateResult(new StoreKeyword(UtilityService.getStoreId(), keyword), memberId);

			try {
				elevate = daoService.getElevateItem(elevate);
			} catch (DaoException e) {
				elevate = null;
			}
			if (elevate!=null) {
				if (!StringUtils.isBlank(condition)) {
					elevate.setCondition(new RedirectRuleCondition(condition));
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
			ElevateResult elevate = new ElevateResult(new StoreKeyword(UtilityService.getStoreId(), keyword), memberId);

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
			String store = UtilityService.getStoreId();

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
			String store = UtilityService.getStoreId();

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
			String store = UtilityService.getStoreId();
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
			String store = UtilityService.getStoreId();
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
			String store = UtilityService.getStoreId();
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
			String store = UtilityService.getStoreId();
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
			String store = UtilityService.getStoreId();
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
			String store = UtilityService.getStoreId();
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
			String store = UtilityService.getStoreId();
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
			return daoService.clearElevateResult(new StoreKeyword(UtilityService.getStoreId(), keyword));
		} catch (DaoException e) {
			logger.error("Failed during clearRule()",e);
		}
		return -1;
	}

	private int addComment(String comment, ElevateResult e) throws DaoException {
		Comment com = new Comment();
		com.setComment(comment);
		com.setUsername(UtilityService.getUsername());
		com.setReferenceId(e.getMemberId());
		com.setRuleTypeId(RuleEntity.ELEVATE.getCode());
		com.setStore(new Store(UtilityService.getStoreId()));
		return daoService.addComment(com);
	}

	@RemoteMethod
	public int addRuleComment(String keyword, String memberId, String pComment) {
		int result = -1;
		String store = UtilityService.getStoreId();

		try {
			ElevateResult elevate = new ElevateResult();
			elevate.setStoreKeyword(new StoreKeyword(store, keyword));
			elevate.setMemberId(memberId);
			elevate = daoService.getElevateItem(elevate);
			if (elevate != null) {
				elevate.setComment(pComment);
				elevate.setLastModifiedBy(UtilityService.getUsername());
				daoService.updateElevateResultComment(elevate);
				result = addComment(pComment, elevate);
			}
		} catch (DaoException e) {
			logger.error("Failed during addRuleItemComment()",e);
		}
		return result;
	}

	@RemoteMethod
	public Map<String, Boolean> isRequireForceAdd(String keyword, String[] memberIds) {
		String storeName = UtilityService.getStoreId();
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		try {
			for (String memberId: memberIds) {
				ElevateResult elevate = new ElevateResult(new StoreKeyword(storeName, keyword), memberId);
				elevate = daoService.getElevateItem(elevate);
				if (elevate != null) {
					String condition = elevate.getEntity() == MemberTypeEntity.FACET ? elevate.getCondition().getConditionForSolr() 
							: String.format("EDP:%s", elevate.getEdp());
					map.put(memberId ,SearchHelper.isForceAddCondition(UtilityService.getServerName(), storeName, keyword, condition));
				}
			}
		} catch (DaoException e) {
			logger.error("Failed during addRuleItemComment()",e);
		}
		return map;
	}
	
	@RemoteMethod
	public Map<String, Boolean> isItemRequireForceAdd(String keyword, String[] memberIds, String[] conditions) {
		String storeName = UtilityService.getStoreId();
		Map<String, Boolean> map = new HashMap<String, Boolean>();

		int i = 0;
		for (String memberId: memberIds) {
			String condition = conditions[i]; 
			if (StringUtils.isNotBlank(condition)) {
				map.put(memberId ,SearchHelper.isForceAddCondition(UtilityService.getServerName(), storeName, keyword, condition));
			}
			i++;
		}

		return map;
	}
}