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
import com.search.manager.model.DemoteProduct;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRuleCondition;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.utility.DateAndTimeUtils;

@Service(value = "demoteService")
@RemoteProxy(
		name = "DemoteServiceJS",
		creator = SpringCreator.class,
		creatorParams = @Param(name = "beanName", value = "demoteService")
)
public class DemoteService extends RuleService{

	private static final Logger logger = Logger.getLogger(DemoteService.class);

	@Autowired private DaoService daoService;

	@Override
	public RuleEntity getRuleEntity() {
		return RuleEntity.DEMOTE;
	}
	
	@RemoteMethod
	public int updateItem(String keyword, String memberId, int position, String comment, String expiryDate, String condition){
		int changes = 0;
		
		DemoteResult demote = new DemoteResult();
		demote.setStoreKeyword(new StoreKeyword(UtilityService.getStoreName(), keyword));
		demote.setMemberId(memberId);
		try {
			demote = daoService.getDemoteItem(demote);
		} catch (DaoException e) {
			demote = null;
		}

		if(demote==null){
			return changes;
		}
		
		DemoteProduct demoteProduct = new DemoteProduct(demote);
		
		if (position!=demote.getLocation()){
			changes += ((update(keyword, memberId, position, null) > 0)? 1 : 0);
		}
		
		if (StringUtils.isNotBlank(comment)){
			changes += ((addComment(keyword, memberId, comment) > 0)? 1 : 0);
		}
		
		if (StringUtils.isNotBlank(condition)){
			changes += ((update(keyword, memberId, position, condition) > 0)? 1 : 0);
		}
		
		if (!StringUtils.equalsIgnoreCase(StringUtils.trimToEmpty(expiryDate), StringUtils.trimToEmpty(demoteProduct.getFormattedExpiryDate()))) {
			changes += ((updateExpiryDate(keyword, memberId, expiryDate) > 0)? 1 : 0);
		}
		
		return changes;
	}

	@RemoteMethod
	public int updateFacet(String keyword, String memberId, int position, String comment, String expiryDate, Map<String, List<String>> filter){
		int changes = 0;
		
		DemoteResult demote = new DemoteResult();
		demote.setStoreKeyword(new StoreKeyword(UtilityService.getStoreName(), keyword));
		demote.setMemberId(memberId);
		RedirectRuleCondition rrCondition = new RedirectRuleCondition();
		rrCondition.setFilter(filter);
		try {
			demote = daoService.getDemoteItem(demote);
		} catch (DaoException e) {
			demote = null;
		}

		if(demote==null){
			return changes;
		}
		
		DemoteProduct demoteProduct = new DemoteProduct(demote);
		
		if (position!=demote.getLocation()){
			changes += ((update(keyword, memberId, position, null) > 0)? 1 : 0);
		}
		
		if (StringUtils.isNotBlank(comment)){
			try {
				addComment(comment,demote);
				changes++;
			} catch (DaoException e) {
				logger.error("Error adding comment in updateFacet()",e);
			}
		}
		
		if (!rrCondition.getCondition().equals(demote.getCondition().getCondition())){
			changes += ((update(keyword, memberId, position, rrCondition.getCondition()) > 0)? 1 : 0);
		}
		
		if (!StringUtils.equalsIgnoreCase(StringUtils.trimToEmpty(expiryDate), StringUtils.trimToEmpty(demoteProduct.getFormattedExpiryDate()))) {
			changes += ((updateExpiryDate(keyword, memberId, expiryDate) > 0)? 1 : 0);
		}
		
		return changes;
	}
	
	@RemoteMethod
	public int add(String keyword, String memberTypeId, String value, int sequence, String expiryDate, String comment) {
		int result = -1;
		try {
			logger.info(String.format("%s %s %s %d %s %s", keyword, memberTypeId, value, sequence, expiryDate, comment));
			String store = UtilityService.getStoreName();

			DemoteResult e = new DemoteResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			e.setLocation(sequence);
			e.setExpiryDate(StringUtils.isEmpty(expiryDate) ? null : DateAndTimeUtils.toSQLDate(store, expiryDate));
			e.setCreatedBy(UtilityService.getUsername());
			e.setComment(UtilityService.formatComment(comment));
			if (MemberTypeEntity.PART_NUMBER.toString().equalsIgnoreCase(memberTypeId)) {
				e.setEdp(value);
				e.setDemoteEntity(MemberTypeEntity.PART_NUMBER);
			} else {
				e.setCondition(new RedirectRuleCondition(value));
				e.setDemoteEntity(MemberTypeEntity.FACET);
			}
			daoService.addKeyword(new StoreKeyword(store, keyword)); // TODO: What if keyword is not added?
			result  = daoService.addDemoteResult(e);
			if (result > 0 && !StringUtils.isBlank(comment)) {
				addComment(comment, e);
			}
		} catch (DaoException e) {
			logger.error("Failed during addDemote()",e);
		}
		return result;

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
		String store = UtilityService.getStoreName();
		
		int count = 0;
		comment = comment.replaceAll("%%timestamp%%", DateAndTimeUtils.formatDateTimeUsingConfig(store, new Date()));
		comment = comment.replaceAll("%%commentor%%", UtilityService.getUsername());
			
		sequence = (sequence==0)? 1: sequence;
		for(String partNumber: partNumbers){
			count = 0;
			DemoteResult e = new DemoteResult();
			try {
				String edp = daoService.getEdpByPartNumber(server, store, keyword, StringUtils.trim(partNumber));

				if (StringUtils.isNotBlank(edp)) {
					e.setStoreKeyword(new StoreKeyword(store, keyword));
					e.setEdp(edp);
					e.setLocation(sequence++);
					e.setExpiryDate(StringUtils.isBlank(expiryDate) ? null : DateAndTimeUtils.toSQLDate(store, expiryDate));
					e.setCreatedBy(UtilityService.getUsername());
					e.setComment(UtilityService.formatComment(comment));
					e.setDemoteEntity(MemberTypeEntity.PART_NUMBER);
					if (StringUtils.isNotBlank(edp)){
						count = daoService.addDemoteResult(e);
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
		
		int count = 0;
		try {
			String store = UtilityService.getStoreName();
			DemoteResult e = new DemoteResult();
			RedirectRuleCondition condition = new RedirectRuleCondition();
			condition.setFilter(filter);
			e.setCondition(condition );
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			e.setLocation(sequence++);
			e.setExpiryDate(StringUtils.isBlank(expiryDate) ? null : DateAndTimeUtils.toSQLDate(store, expiryDate));
			e.setCreatedBy(UtilityService.getUsername());
			e.setComment(UtilityService.formatComment(comment));
			e.setDemoteEntity(MemberTypeEntity.FACET);
			count = daoService.addDemoteResult(e);
			if (!StringUtils.isBlank(comment)) {
				addComment(comment, e);
			}
		} catch (DaoException de) {
				logger.error("Failed during addItemToRuleUsingPartNumber()",de);
		}
		return count;
	}

	@RemoteMethod
	public int updateExpiryDate(String keyword, String memberId, String expiryDate){
		try {
			logger.info(String.format("%s %s %s", keyword, memberId, expiryDate));
			String store = UtilityService.getStoreName();
			DemoteResult e = new DemoteResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			e.setMemberId(memberId);
			e.setExpiryDate(DateAndTimeUtils.toSQLDate(store, expiryDate));
			e.setLastModifiedBy(UtilityService.getUsername());
			return daoService.updateDemoteResultExpiryDate(e);
		} catch (DaoException e) {
			logger.error("Failed during updateExpiryDate()",e);
		}
		return -1;
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
			
			DemoteResult e = new DemoteResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			e.setMemberId(memberId);
			e.setLastModifiedBy(UtilityService.getUsername());
			e.setComment(UtilityService.formatComment(comment));
			return daoService.appendDemoteResultComment(e);
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
			DemoteResult e = new DemoteResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			e.setMemberId(memberId);
			e.setLastModifiedBy(UtilityService.getUsername());
			e = daoService.getDemoteItem(e);
			return daoService.deleteDemoteResult(e);
		} catch (DaoException e) {
			logger.error("Failed during removeDemote()",e);
		}
		return -1;
	}

	@RemoteMethod
	public int update(String keyword, String memberId, int sequence, String condition) {
		try {
			logger.info(String.format("%s %s %d", keyword, memberId, sequence));
			DemoteResult demote = new DemoteResult();
			demote.setStoreKeyword(new StoreKeyword(UtilityService.getStoreName(), keyword));
			demote.setMemberId(memberId);
			try {
				demote = daoService.getDemoteItem(demote);
			} catch (DaoException e) {
				demote = null;
			}
			if (demote!=null) {
				if (!StringUtils.isBlank(condition)) {
					demote.setCondition(new RedirectRuleCondition((condition)));
				}
				demote.setLocation(sequence);
				demote.setLastModifiedBy(UtilityService.getUsername());
				return daoService.updateDemoteResult(demote);
			}
		} catch (DaoException e) {
			logger.error("Failed during updateDemote()",e);
		}
		return -1;
	}

	@RemoteMethod
	public RecordSet<DemoteProduct> getProducts(String filter, String keyword, int page, int itemsPerPage) {

		if (StringUtils.isBlank(filter) || StringUtils.equalsIgnoreCase("all", filter))
			return getAllProducts(keyword, page, itemsPerPage);

		if (StringUtils.equalsIgnoreCase("active", filter))
			return getActiveProducts(keyword, page, itemsPerPage);

		if (StringUtils.equalsIgnoreCase("expired", filter))
			return getExpiredProducts(keyword, page, itemsPerPage);

		return null;
	}

	@RemoteMethod
	public DemoteProduct getProductByEdp(String keyword, String edp) {

		RecordSet<DemoteProduct> products = getAllProducts(keyword, 0, 100);
		DemoteProduct product = null;
		for (DemoteProduct  prod: products.getList()) {
			if (prod.getMemberTypeEntity() == MemberTypeEntity.PART_NUMBER && prod.getEdp().equals(StringUtils.trim(edp))) {
				product = prod;
				break;
			}
		}
		return product;
	}

	@RemoteMethod
	public RecordSet<DemoteProduct> getAllProducts(String keyword, int page,int itemsPerPage) {
		RecordSet<DemoteProduct> result = null;
		try {
			logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();

			DemoteResult e = new DemoteResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(e, null, null,  page, itemsPerPage);
			result  = daoService.getDemotedProductsIgnoreKeyword(server, criteria);
		} catch (DaoException e) {
			logger.error("Failed during getAllDemotedProducts()",e);
		}
		return result;
	}

	@RemoteMethod
	public RecordSet<DemoteProduct> getAllProductsIgnoreKeyword(String keyword, int page,int itemsPerPage) {
		try {
			logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();

			DemoteResult e = new DemoteResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(e, null, null,  page, itemsPerPage);
			return daoService.getDemotedProductsIgnoreKeyword(server, criteria);
		} catch (DaoException e) {
			logger.error("Failed during getAllDemotedProducts()",e);
		}
		return null;
	}
	
	@RemoteMethod
	public RecordSet<DemoteProduct> getActiveProducts(String keyword, int page,int itemsPerPage) {
		try {
			logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();
			DemoteResult e = new DemoteResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(e, new Date(), null,  page, itemsPerPage);
			return daoService.getDemotedProductsIgnoreKeyword(server, criteria);
		} catch (DaoException e) {
			logger.error("Failed during getActiveProducts()",e);
		}
		return null;
	}

	@RemoteMethod
	public RecordSet<DemoteProduct> getExpiredProducts(String keyword, int page,int itemsPerPage) {
		try {
			logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();
			DemoteResult e = new DemoteResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(e, null, DateAndTimeUtils.getDateYesterday(),  page, itemsPerPage);
			return daoService.getDemotedProductsIgnoreKeyword(server, criteria);
		} catch (DaoException e) {
			logger.error("Failed during getExpiredProducts()",e);
		}
		return null;
	}

	@RemoteMethod
	public RecordSet<DemoteProduct> getNoExpiryProducts(String keyword, int page,int itemsPerPage) {
		try {
			logger.info(String.format("%s %d %d", keyword, page, itemsPerPage));
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();
			DemoteResult e = new DemoteResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(e, null, null,  page, itemsPerPage);
			return daoService.getNoExpiryDemotedProducts(server, criteria);
		} catch (DaoException e) {
			logger.error("Failed during getNoExpiryProducts()",e);
		}
		return null;
	}

	@RemoteMethod
	public Integer getTotalProductInRule(String keyword) {
		try {
			logger.info(String.format("%s", keyword));
			String store = UtilityService.getStoreName();
			DemoteResult e = new DemoteResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(e, null, null, null, null);
			return daoService.getDemoteResultCount(criteria);
		} catch (DaoException e) {
			logger.error("Failed during getTotalProductInRule()",e);
		}
		return Integer.valueOf(0);
	}

	@RemoteMethod
	public DemoteProduct getProduct(String keyword, String memberId) {
		try {
			logger.info(String.format("%s %s", keyword, memberId));
			String server = UtilityService.getServerName();
			String store = UtilityService.getStoreName();
			DemoteResult e = new DemoteResult();
			e.setStoreKeyword(new StoreKeyword(store, keyword));
			e.setMemberId(memberId);
			return daoService.getDemotedProduct(server, e);
		} catch (DaoException e) {
			logger.error("Failed during getDemotedProduct()",e);
		}
		return null;
	}

	@RemoteMethod
	public String getComment(String keyword, String memberId) {
		DemoteProduct demotedProduct = getProduct(keyword, memberId);
		if (demotedProduct == null)
			return StringUtils.EMPTY;

		return StringUtils.trimToEmpty(demotedProduct.getComment());
	}

	@RemoteMethod
	public int clearRule(String keyword) {
		try {
			logger.info(String.format("%s", keyword));
			return daoService.clearDemoteResult(new StoreKeyword(UtilityService.getStoreName(), keyword));
		} catch (DaoException e) {
			logger.error("Failed during clearRule()",e);
		}
		return -1;
	}

	public DaoService getDaoService() {
		return daoService;
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}

	private Comment addComment(String comment, DemoteResult e) throws DaoException {
		Comment com = new Comment();
		com.setComment(comment);
		com.setUsername(UtilityService.getUsername());
		com.setReferenceId(e.getMemberId());
		com.setRuleTypeId(RuleEntity.DEMOTE.getCode());
		com.setStore(new Store(UtilityService.getStoreName()));
		daoService.addComment(com);
		return com;
	}

	@RemoteMethod
	public int addRuleComment(String keyword, String memberId, String pComment) {
		int result = -1;
		String store = UtilityService.getStoreName();
		try {
			DemoteResult demote = new DemoteResult();
			demote.setStoreKeyword(new StoreKeyword(store, keyword));
			demote.setMemberId(memberId);
			try {
				demote = daoService.getDemoteItem(demote);
			} catch (DaoException e) {
				demote = null;
			}
			if (demote != null) {
				demote.setComment(pComment);
				demote.setLastModifiedBy(UtilityService.getUsername());
				daoService.updateDemoteResultComment(demote);
				Comment com = new Comment();
				com.setComment(pComment);
				com.setUsername(UtilityService.getUsername());
				com.setReferenceId(demote.getMemberId());
				com.setRuleTypeId(RuleEntity.DEMOTE.getCode());
				com.setStore(new Store(store));
				result = daoService.addComment(com);
			}
		} catch (DaoException e) {
			logger.error("Failed during addRuleItemComment()",e);
		}
		return result;
	}
}