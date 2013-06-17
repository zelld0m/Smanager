package com.search.manager.model;

import java.io.Serializable;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteProperty;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class Keyword implements Serializable {

	private static final long serialVersionUID = -489935522624703568L;

	private String keywordId;
	private String keyword;

	public Keyword(String keywordId, String keyword) {
		this.keywordId = keywordId;
		this.keyword = keyword;
	}

	public Keyword(String keywordId) {
		this.keywordId = keywordId;
		this.keyword = keywordId;
	}

	public Keyword() {
		super();
	}

	@RemoteProperty
	public String getKeywordId() {
		return keywordId;
	}

	public void setKeywordId(String keywordId) {
		this.keywordId = keywordId;
	}

	@RemoteProperty
	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	@RemoteProperty
	public String getRuleId(){
		return getKeywordId();
	}

	@RemoteProperty
	public String getRuleName(){
		return getKeyword();
	}

	@Override
	public String toString() {
		return "(Keyword Id: " + keywordId + "\tKeyword: " + keyword + ")";
	}

}
