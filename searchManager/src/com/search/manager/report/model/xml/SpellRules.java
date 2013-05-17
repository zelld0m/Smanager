package com.search.manager.report.model.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.joda.time.DateTime;
import com.search.manager.enums.RuleEntity;

@XmlRootElement(name = "spellRules")
@DataTransferObject(converter = BeanConverter.class)
public class SpellRules extends RuleXml {

    private static final long serialVersionUID = 1L;

    private List<SpellRuleXml> spellRule = new ArrayList<SpellRuleXml>();
    private Map<String, SpellRuleXml> ruleMap = new HashMap<String, SpellRuleXml>();

    private Map<String, List<SpellRuleXml>> statusMap = new HashMap<String, List<SpellRuleXml>>();
    private String searchTerms = "";
    private String ruleIds = "";

    private int maxSuggest = 5;
    private static final RuleEntity RULE_ENTITY = RuleEntity.SPELL;

    public SpellRules() {
        super(serialVersionUID);
        setRuleEntity(RULE_ENTITY);
    }

    public SpellRules(String store, long version, String name, String notes, String username, DateTime date, String ruleId,
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

    public SpellRuleXml getSpellRule(String ruleId) {
        return ruleMap.get(ruleId);
    }

    public void setSpellRule(List<SpellRuleXml> spellRule) {
        this.spellRule = spellRule;
    }

    public void generateSecondaryIndex() {
        // clear secondary index
        statusMap.clear();
        ruleMap.clear();

        ruleIds = "";
        searchTerms = "";

        StringBuilder builder1 = new StringBuilder();
        StringBuilder builder2 = new StringBuilder();

        statusMap.put("new", new ArrayList<SpellRuleXml>());
        statusMap.put("modified", new ArrayList<SpellRuleXml>());
        statusMap.put("published", new ArrayList<SpellRuleXml>());
        statusMap.put("deleted", new ArrayList<SpellRuleXml>());

        if (spellRule != null) {
            for (SpellRuleXml xml : spellRule) {
                ruleMap.put(xml.getRuleId(), xml);

                if (!"deleted".equalsIgnoreCase(xml.getStatus())) {
                    for (String term : xml.getRuleKeyword()) {
                        builder1.append("\013").append(term.toLowerCase());
                        builder2.append("\013").append(xml.getRuleId());
                    }
                }

                statusMap.get(xml.getStatus()).add(xml);
                // called for its side effect
                xml.getSuggestKeyword();
            }

            searchTerms = builder1.toString();
            ruleIds = builder2.toString();
        }
    }

    public void addRule(SpellRuleXml ruleXml) {
        spellRule.add(ruleXml);
        ruleMap.put(ruleXml.getRuleId(), ruleXml);

        for (String term : ruleXml.getRuleKeyword()) {
            searchTerms += "\013" + term.toLowerCase();
            ruleIds += "\013" + ruleXml.getRuleId();
        }

        statusMap.get("new").add(ruleXml);
    }

    public void deleteFromSearchTermIndex(SpellRuleXml ruleXml) {
        for (String term : ruleXml.getRuleKeyword()) {
            removeSearchTerm(term);
        }
    }

    public SpellRuleXml checkSearchTerm(String searchTerm) {
        int idx = findSearchTermIndex(searchTerm);

        if (idx >= 0) {
            int pos = StringUtils.countMatches(searchTerms.substring(0, idx + 1), "\013");
            int ruleIdx = StringUtils.ordinalIndexOf(ruleIds, "\013", pos);
            int ruleIdx2 = StringUtils.ordinalIndexOf(ruleIds, "\013", pos + 1);

            return ruleMap.get(ruleIds.substring(ruleIdx + 1, ruleIdx2 > 0 ? ruleIdx2 : ruleIds.length()));
        }

        return null;
    }

    private int findSearchTermIndex(String searchTerm) {
        String sterm = "\013" + searchTerm.toLowerCase() + "\013";
        int idx = searchTerms.indexOf(sterm);

        if (idx < 0) {
            sterm = "\013" + searchTerm.toLowerCase();
            idx = searchTerms.indexOf(sterm);

            if (idx >= 0 && idx + sterm.length() < searchTerms.length()) {
                idx = -1;
            }
        }

        return idx;
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

    private void removeSearchTerm(String term) {
        int idx = findSearchTermIndex(term);

        if (idx >= 0) {
            int pos = StringUtils.countMatches(searchTerms.substring(0, idx + 1), "\013");
            int ruleIdx = StringUtils.ordinalIndexOf(ruleIds, "\013", pos);
            int ruleIdx2 = StringUtils.ordinalIndexOf(ruleIds, "\013", pos + 1);

            searchTerms = searchTerms.substring(0, idx) + searchTerms.substring(idx + term.length() + 1);
            ruleIds = ruleIds.substring(0, ruleIdx) + (ruleIdx2 > 0 ? ruleIds.substring(ruleIdx2) : "");
        }
    }

    public void updateSearchIndex(List<String> oldSearchTerms, SpellRuleXml xml) {
        for (String oldTerm : oldSearchTerms) {
            removeSearchTerm(oldTerm);
        }

        for (String newTerm : xml.getRuleKeyword()) {
            searchTerms += "\013" + newTerm.toLowerCase();
            ruleIds += "\013" + xml.getRuleId();
        }
    }
}
