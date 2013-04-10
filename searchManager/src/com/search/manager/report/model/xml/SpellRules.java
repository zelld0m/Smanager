package com.search.manager.report.model.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@XmlRootElement(name = "spellRules")
@DataTransferObject(converter = BeanConverter.class)
public class SpellRules extends RuleXml {

    private static final long serialVersionUID = 1L;

    private List<SpellRuleXml> spellRule = new ArrayList<SpellRuleXml>();
    private Map<String, SpellRuleXml> ruleMap = new HashMap<String, SpellRuleXml>();
    private Map<String, SpellRuleXml> searchTermMap = new TreeMap<String, SpellRuleXml>();
    private Map<String, List<SpellRuleXml>> statusMap = new HashMap<String, List<SpellRuleXml>>();

    private int maxSuggest = 5;

    public SpellRules() {
        super(serialVersionUID);
    }

    @XmlAttribute(name = "maxSuggest")
    public int getMaxSuggest() {
        return maxSuggest;
    }

    public void setMaxSuggest(int maxSuggest) {
        this.maxSuggest = maxSuggest;
    }

    public List<SpellRuleXml> getSpellRule() {
        return spellRule;
    }

    public SpellRuleXml getSpellRule(String ruleId) {
        return ruleMap.get(ruleId);
    }

    public void setSpellRule(List<SpellRuleXml> spellRule) {
        this.spellRule = spellRule;
    }

    public void generateSecondaryIndex() {
        statusMap.put("new", new ArrayList<SpellRuleXml>());
        statusMap.put("modified", new ArrayList<SpellRuleXml>());
        statusMap.put("published", new ArrayList<SpellRuleXml>());
        statusMap.put("deleted", new ArrayList<SpellRuleXml>());

        if (spellRule != null) {
            for (SpellRuleXml xml : spellRule) {
                ruleMap.put(xml.getRuleId(), xml);

                if (! "deleted".equalsIgnoreCase(xml.getStatus())) {
                    for (String term : xml.getRuleKeyword().getKeyword()) {
                        searchTermMap.put(term, xml);
                    }
                }

                statusMap.get(xml.getStatus()).add(xml);
            }
        }
    }

    public void addRule(SpellRuleXml ruleXml) {
        spellRule.add(ruleXml);
        ruleMap.put(ruleXml.getRuleId(), ruleXml);

        for (String term : ruleXml.getRuleKeyword().getKeyword()) {
            searchTermMap.put(term, ruleXml);
        }

        statusMap.get("new").add(ruleXml);
    }

    public void deleteFromSearchTermIndex(SpellRuleXml ruleXml) {
        for (String term : ruleXml.getRuleKeyword().getKeyword()) {
            searchTermMap.remove(term);
        }
    }

    public SpellRuleXml checkSearchTerm(String searchTerm) {
        return searchTermMap.get(searchTerm);
    }

    public void updateStatusIndex(String oldStatus, SpellRuleXml xml) {
        statusMap.get(oldStatus).remove(xml);
        statusMap.get(xml.getStatus()).add(xml);
    }

    public void deleteFromStatusIndex(SpellRuleXml xml) {
        statusMap.get(xml.getStatus()).remove(xml);
    }

    public void deletePhysically(SpellRuleXml xml) {
        spellRule.remove(xml);
        deleteFromStatusIndex(xml);
        deleteFromSearchTermIndex(xml);
        ruleMap.remove(xml.getRuleId());
    }

    public List<SpellRuleXml> selectActiveRules() {
        List<SpellRuleXml> rules = new ArrayList<SpellRuleXml>();

        rules.addAll(spellRule);
        rules.removeAll(statusMap.get("deleted"));

        return rules;
    }

    public List<SpellRuleXml> selectRulesByStatus(String status) {
        return statusMap.get(status);
    }
}
