package com.search.manager.core.processor;


public class RequestPropertyBean {
	private String storeId;
	private String keyword;
	private boolean isKeywordPresent;
	private boolean isGuiRequest;
	private boolean isDisableRule;
	
	public RequestPropertyBean(String storeId){
		super();
		this.storeId = storeId;
	}
	
	public RequestPropertyBean(String storeId, boolean isDisableRule){
		this(storeId);
		this.isDisableRule = isDisableRule;
	}
	
	public RequestPropertyBean(String storeId, String keyword,
			boolean isKeywordPresent, boolean isGuiRequest, boolean isDisableRule) {
		this(storeId, isDisableRule);
		this.keyword = keyword;
		this.isKeywordPresent = isKeywordPresent;
		this.isGuiRequest = isGuiRequest;
	}

	public String getStoreId() {
		return storeId;
	}
	
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	
	public String getKeyword() {
		return keyword;
	}
	
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public boolean isKeywordPresent() {
		return isKeywordPresent;
	}
	
	public void setKeywordPresent(boolean isKeywordPresent) {
		this.isKeywordPresent = isKeywordPresent;
	}
	
	public boolean isGuiRequest() {
		return isGuiRequest;
	}
	
	public boolean isDisableRule() {
		return isDisableRule;
	}

	public void setDisableRule(boolean isDisableRule) {
		this.isDisableRule = isDisableRule;
	}

	public void setGuiRequest(boolean isGuiRequest) {
		this.isGuiRequest = isGuiRequest;
	}
}