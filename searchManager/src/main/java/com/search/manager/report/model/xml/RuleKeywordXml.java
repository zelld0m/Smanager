package com.search.manager.report.model.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@XmlRootElement(name="keywords")
@DataTransferObject(converter = BeanConverter.class)
public class RuleKeywordXml extends BaseEntityXml{
	
	private static final long serialVersionUID = -7421229784911639782L;
	private List<String> keyword;
	
	public RuleKeywordXml() {
		super();
	}
	
	public RuleKeywordXml(List<String> keyword) {
		super();
		this.keyword = keyword;
	}

	public List<String> getKeyword() {
		return keyword;
	}

	public void setKeyword(List<String> keyword) {
		this.keyword = keyword;
	}
}