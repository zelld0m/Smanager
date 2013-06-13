package com.search.manager.model;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.joda.time.DateTime;

import com.search.manager.enums.RuleEntity;

@DataTransferObject(converter = BeanConverter.class)
public class ExportRuleMap {
	
	private static final long serialVersionUID = 1L;
	
	private String 		storeIdOrigin;
	private String 		ruleIdOrigin;
	private String 		ruleNameOrigin;
	private String 		storeIdTarget;
	private String 		ruleIdTarget;
	private String 		ruleNameTarget;
	private RuleEntity 	ruleType;
	
	private DateTime	publishedDateTime;
	private DateTime	exportDateTime;
	private DateTime	importDateTime;
	private Boolean		deleted;
	private Boolean		rejected;
	
	public ExportRuleMap(String storeIdOrigin, String ruleIdOrigin, String ruleNameOrigin, String storeIdTarget, String ruleIdTarget, String ruleNameTarget,
				DateTime publishedDateTime, DateTime exportDateTime, DateTime importDateTime, Boolean deleted, Boolean rejected) {
		super();
		this.storeIdOrigin = storeIdOrigin;
		this.ruleIdOrigin = ruleIdOrigin;
		this.ruleNameOrigin = ruleNameOrigin;
		this.storeIdTarget = storeIdTarget;
		this.ruleIdTarget = ruleIdTarget;
		this.ruleNameTarget = ruleNameTarget;
		this.publishedDateTime = publishedDateTime;
		this.exportDateTime = exportDateTime;
		this.importDateTime = importDateTime;
		this.deleted = deleted;
		this.rejected = rejected;
	}
	
	public ExportRuleMap(String storeIdOrigin, String ruleIdOrigin, String ruleNameOrigin, String storeIdTarget, String ruleIdTarget, String ruleNameTarget, RuleEntity ruleType) {
		this(storeIdOrigin, ruleIdOrigin, ruleNameOrigin, storeIdTarget, ruleIdTarget, ruleNameTarget, null, null, null, null, null, ruleType);
	}
	
	public ExportRuleMap(String storeIdOrigin, String ruleIdOrigin, String ruleNameOrigin, String storeIdTarget, String ruleIdTarget, String ruleNameTarget,
			DateTime publishedDateTime, DateTime exportDateTime, DateTime importDateTime, Boolean deleted, Boolean rejected, RuleEntity ruleType) {
		this(storeIdOrigin, ruleIdOrigin, ruleNameOrigin, storeIdTarget, ruleIdTarget, ruleNameTarget, publishedDateTime, exportDateTime, importDateTime, deleted, rejected);
		this.ruleType= ruleType;
	}
	
	public ExportRuleMap(String storeIdOrigin, String ruleIdOrigin, String ruleNameOrigin, String storeIdTarget, String ruleIdTarget, String ruleNameTarget, Integer ruleType) {
		this(storeIdOrigin, ruleIdOrigin, ruleNameOrigin, storeIdTarget, ruleIdTarget, ruleNameTarget, null, null, null, null, null, ruleType);
	}

	public ExportRuleMap(String storeIdOrigin, String ruleIdOrigin, String ruleNameOrigin, String storeIdTarget, String ruleIdTarget, String ruleNameTarget, 
			DateTime publishedDateTime, DateTime exportDateTime, DateTime importDateTime, Boolean deleted, Boolean rejected, Integer ruleType) {
		this(storeIdOrigin, ruleIdOrigin, ruleNameOrigin, storeIdTarget, ruleIdTarget, ruleNameTarget, publishedDateTime, exportDateTime, importDateTime, deleted, rejected);
		if (ruleType != null) {
			RuleEntity ruleTypeObject = RuleEntity.find(RuleEntity.getValue(ruleType));
			if (ruleTypeObject != null) {
				this.ruleType = ruleTypeObject;
			}			
		}
	}
	
	public String getStoreIdOrigin() {
		return storeIdOrigin;
	}
	
	public void setStoreIdOrigin(String storeIdOrigin) {
		this.storeIdOrigin = storeIdOrigin;
	}
	
	public String getRuleIdOrigin() {
		return ruleIdOrigin;
	}
	
	public void setRuleIdOrigin(String ruleIdOrigin) {
		this.ruleIdOrigin = ruleIdOrigin;
	}
	
	public String getRuleNameOrigin() {
		return ruleNameOrigin;
	}
	
	public void setRuleNameOrigin(String ruleNameOrigin) {
		this.ruleNameOrigin = ruleNameOrigin;
	}
	
	public String getStoreIdTarget() {
		return storeIdTarget;
	}
	
	public void setStoreIdTarget(String storeIdTarget) {
		this.storeIdTarget = storeIdTarget;
	}
	
	public String getRuleIdTarget() {
		return ruleIdTarget;
	}
	
	public void setRuleIdTarget(String ruleIdTarget) {
		this.ruleIdTarget = ruleIdTarget;
	}
	
	public String getRuleNameTarget() {
		return ruleNameTarget;
	}
	
	public void setRuleNameTarget(String ruleNameTarget) {
		this.ruleNameTarget = ruleNameTarget;
	}
	
	public RuleEntity getRuleType() {
		return ruleType;
	}
	
	public void setRuleType(RuleEntity ruleType) {
		this.ruleType = ruleType;
	}

	public DateTime getPublishedDateTime() {
		return publishedDateTime;
	}

	public void setPublishedDateTime(DateTime publishedDateTime) {
		this.publishedDateTime = publishedDateTime;
	}

	public DateTime getExportDateTime() {
		return exportDateTime;
	}

	public void setExportDateTime(DateTime exportDateTime) {
		this.exportDateTime = exportDateTime;
	}

	public DateTime getImportDateTime() {
		return importDateTime;
	}

	public void setImportDateTime(DateTime importDateTime) {
		this.importDateTime = importDateTime;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Boolean getRejected() {
		return rejected;
	}

	public void setRejected(Boolean rejected) {
		this.rejected = rejected;
	}
}