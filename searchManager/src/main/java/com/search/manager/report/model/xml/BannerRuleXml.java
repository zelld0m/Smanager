package com.search.manager.report.model.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.joda.time.DateTime;

import com.search.manager.enums.RuleEntity;
import com.search.manager.core.model.BannerRule;

@XmlRootElement(name = "banner")
@XmlType(propOrder={"itemXml"})
@DataTransferObject(converter = BeanConverter.class)
public class BannerRuleXml extends RuleXml {

    private static final long serialVersionUID = 1L;
    private static final RuleEntity RULE_ENTITY = RuleEntity.BANNER;

    private List<BannerItemXml> itemXml;

    public BannerRuleXml() {
        super(serialVersionUID);
        this.setRuleEntity(RULE_ENTITY);
    }

    public BannerRuleXml(String store, String ruleId, String ruleName, String name, String notes, String createdBy,
            List<BannerItemXml> itemXml, long version) {
        super(store, name, notes, createdBy);
        this.setSerial(serialVersionUID);
        this.setRuleEntity(RULE_ENTITY);
        this.setVersion(version);
        this.setCreatedDate(DateTime.now());
        this.setRuleId(ruleId);
        this.setItemXml(itemXml);
        this.setRuleName(ruleName);
    }

    public BannerRuleXml(BannerRule rule) {
        super(rule.getStoreId(), "", "", "");
        this.setSerial(serialVersionUID);
        this.setRuleEntity(RULE_ENTITY);
        this.setCreatedDate(rule.getCreatedDate());
        this.setCreatedBy(rule.getCreatedBy());
        this.setLastModifiedBy(rule.getLastModifiedBy());
        this.setLastModifiedDate(rule.getLastModifiedDate());
        this.setRuleName(rule.getRuleName());
        this.setRuleId(rule.getRuleId());
    }

    @XmlElementRef(type=BannerItemXml.class)
    public List<BannerItemXml> getItemXml() {
        return itemXml;
    }

    public void setItemXml(List<BannerItemXml> itemXml) {
        this.itemXml = itemXml;
    }
}
