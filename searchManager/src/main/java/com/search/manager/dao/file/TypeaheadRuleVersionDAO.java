package com.search.manager.dao.file;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.TypeaheadRule;
import com.search.manager.core.service.TypeaheadBrandService;
import com.search.manager.core.service.TypeaheadRuleService;
import com.search.manager.core.service.TypeaheadSuggestionService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.report.model.xml.TypeaheadRuleXml;

@Component("typeaheadRuleVersionDAO")
public class TypeaheadRuleVersionDAO extends AbstractRuleVersionDAO<TypeaheadRuleXml>{

	private static final Logger logger = LoggerFactory
			.getLogger(TypeaheadRuleVersionDAO.class);

	@Autowired
	@Qualifier("typeaheadRuleServiceSp")
	private TypeaheadRuleService typeaheadRuleService;
	@Autowired
	@Qualifier("typeaheadBrandServiceSp")
	private TypeaheadBrandService typeaheadBrandService;
	@Autowired
	@Qualifier("typeaheadSuggestionServiceSp")
	private TypeaheadSuggestionService typeaheadSuggestionService;

	@Override
	protected RuleEntity getRuleEntity() {
		return RuleEntity.TYPEAHEAD;
	}

	@Override
	protected boolean addLatestVersion(
			RuleVersionListXml<?> ruleVersionListXml, String store,
			String ruleId, String username, String name, String notes,
			boolean isVersion) {
		if(ruleVersionListXml != null) {

			try {
				@SuppressWarnings("unchecked")
				List<TypeaheadRuleXml> ruleXmlList = (List<TypeaheadRuleXml>)ruleVersionListXml.getVersions();

				long version = ruleVersionListXml.getNextVersion();

				TypeaheadRule typeaheadRule = typeaheadRuleService.searchById(store, ruleId);
				TypeaheadRuleXml xml = new TypeaheadRuleXml(typeaheadRule, typeaheadBrandService.searchByRuleId(ruleId).getList(), typeaheadSuggestionService.searchByRuleId(ruleId).getList());
				xml.setVersion(version);
				xml.setName(name);
				xml.setNotes(notes);
				xml.setStore(store);
				xml.setRuleId(ruleId);
				xml.setRuleEntity(RuleEntity.TYPEAHEAD);
				ruleXmlList.add(xml);
				ruleVersionListXml.setRuleId(ruleId);
				ruleVersionListXml.setRuleName(typeaheadRule.getRuleName());

				return true;

			} catch (CoreServiceException e) {
				logger.error("Error occurred in addLatestVersion.", e);
			}
		}
		return false;
	}

}
