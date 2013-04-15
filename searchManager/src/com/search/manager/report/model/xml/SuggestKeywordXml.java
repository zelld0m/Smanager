package com.search.manager.report.model.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@XmlRootElement(name = "suggests")
@DataTransferObject(converter = BeanConverter.class)
public class SuggestKeywordXml extends BaseEntityXml {

    private static final long serialVersionUID = -7421229784911639782L;
    private List<String> suggest;

    public SuggestKeywordXml() {
        super();
    }

    public SuggestKeywordXml(List<String> suggest) {
        super();
        this.suggest = suggest;
    }

    public List<String> getSuggest() {
        return suggest;
    }

    public void setSuggest(List<String> suggest) {
        this.suggest = suggest;
    }
}