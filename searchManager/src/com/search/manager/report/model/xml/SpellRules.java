package com.search.manager.report.model.xml;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.enums.RuleEntity;

@XmlRootElement(name = "spellRules")
@DataTransferObject(converter = BeanConverter.class)
public class SpellRules extends RuleXml {

    private static final long serialVersionUID = 1L;

    private int maxSuggest = 5;
    private List<SpellRuleXml> spellRule = new ArrayList<SpellRuleXml>();

    private static final RuleEntity RULE_ENTITY = RuleEntity.SPELL;

    public SpellRules() {
        super(serialVersionUID);
        setRuleEntity(RULE_ENTITY);
    }

    public SpellRules(String store, long version, String name, String notes, String username, Date date, String ruleId,
            int maxSuggest, List<SpellRuleXml> spellRule) {
        this();
        this.setSpellRule(spellRule);
        this.setRuleId(ruleId);
        this.setRuleName("Did You Mean Rules");
        this.setName(name);
        this.setNotes(notes);
        this.setCreatedBy(username);
        this.setCreatedDate(date);
        this.setStore(store);
        this.setVersion(version);
        this.setMaxSuggest(maxSuggest);
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

    public void setSpellRule(List<SpellRuleXml> spellRule) {
        this.spellRule = spellRule;
    }
}
