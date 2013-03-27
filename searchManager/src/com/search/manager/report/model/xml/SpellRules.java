package com.search.manager.report.model.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "spellRules")
public class SpellRules {

    private List<SpellRuleXml> spellRule = new ArrayList<SpellRuleXml>();
    private Map<String, SpellRuleXml> ruleMap = new HashMap<String, SpellRuleXml>();
    private Map<String, SpellRuleXml> searchTermMap = new TreeMap<String, SpellRuleXml>();

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
        if (spellRule != null) {
            for (SpellRuleXml xml : spellRule) {
                ruleMap.put(xml.getRuleId(), xml);

                if (!xml.isDeleted()) {
                    for (String term : xml.getRuleKeyword().getKeyword()) {
                        searchTermMap.put(term, xml);
                    }
                }
            }
        }
    }

    public void addRule(SpellRuleXml ruleXml) {
        spellRule.add(ruleXml);
        ruleMap.put(ruleXml.getRuleId(), ruleXml);

        for (String term : ruleXml.getRuleKeyword().getKeyword()) {
            searchTermMap.put(term, ruleXml);
        }
    }

    public void deleteFromSecondaryIndex(SpellRuleXml ruleXml) {
        for (String term : ruleXml.getRuleKeyword().getKeyword()) {
            searchTermMap.remove(term);
        }
    }

    public SpellRuleXml checkSearchTerm(String searchTerm) {
        return searchTermMap.get(searchTerm);
    }
}
