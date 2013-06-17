package com.search.manager.report.model;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Function;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.jodatime.JodaPatternType;
import com.search.manager.report.annotation.ReportField;
import com.search.manager.report.model.xml.SpellRuleXml;

public class SpellReportBean extends ReportBean<SpellRuleXml> {

	public static final Function<SpellRuleXml, SpellReportBean> transformer = new Function<SpellRuleXml, SpellReportBean>() {
		public SpellReportBean apply(SpellRuleXml xml) {
			return new SpellReportBean(xml);
		}
	};

	public SpellReportBean(SpellRuleXml model) {
		super(model);
	}

	@ReportField(label = "ID", size = 20, sortOrder = 1)
	public String getId() {
		return model.getRuleId();
	}

    @ReportField(label = "Search Terms", size = 20, sortOrder = 2)
    public String getSearchTerms() {
        return StringUtils.join(model.getRuleKeyword(), ',');
    }

    @ReportField(label = "Suggestions", size = 20, sortOrder = 3)
    public String getSuggestions() {
        return StringUtils.join(model.getSuggestKeyword(), ',');
    }

	@ReportField(label = "Status", size = 20, sortOrder = 4)
	public String getStatus() {
		return model.getStatus();
	}

	@ReportField(label = "Created By", size = 20, sortOrder = 5)
	public String getCreatedBy() {
		return model.getCreatedBy();
	}

	@ReportField(label = "Created Date", size = 20, sortOrder = 6)
	public String getCreatedDate() {
		return JodaDateTimeUtil.formatFromStorePattern(model.getCreatedDate(), JodaPatternType.DATE_TIME);
	}

	@ReportField(label = "Modified By", size = 20, sortOrder = 7)
	public String getModifiedBy() {
		return model.getLastModifiedBy();
	}

	@ReportField(label = "Modified Date", size = 20, sortOrder = 8)
	public String getModifiedDate() {
		return JodaDateTimeUtil.formatFromStorePattern(model.getLastModifiedDate(), JodaPatternType.DATE_TIME);
	}
}