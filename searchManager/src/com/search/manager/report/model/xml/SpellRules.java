package com.search.manager.report.model.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "spellRules")
public class SpellRules {

    private List<SpellRuleXml> spellRule = new ArrayList<SpellRuleXml>();
    private Map<String, SpellRuleXml> ruleMap = new HashMap<String, SpellRuleXml>();

    public List<SpellRuleXml> getSpellRule() {
        return spellRule;
    }

    public SpellRuleXml getSpellRule(String ruleId) {
        return ruleMap.get(ruleId);
    }

    public void setSpellRule(List<SpellRuleXml> spellRule) {
        this.spellRule = spellRule;
    }

    public void generateMap() {
        if (spellRule != null) {
            for (SpellRuleXml xml : spellRule) {
                ruleMap.put(xml.getRuleId(), xml);
            }
        }
    }

    public void addRule(SpellRuleXml ruleXml) {
        spellRule.add(ruleXml);
        ruleMap.put(ruleXml.getRuleId(), ruleXml);
    }
}
