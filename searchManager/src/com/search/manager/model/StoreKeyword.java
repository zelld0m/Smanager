package com.search.manager.model;

import java.io.Serializable;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteProperty;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class StoreKeyword implements Serializable {

	private static final long serialVersionUID = -2914958944967297307L;
	
	private Store store;
	private Keyword keyword;
	
	public StoreKeyword(Store store, Keyword keyword) {
		this.store = store;
		this.keyword = keyword;
	}

	public StoreKeyword(String store, String keyword) {
		this(new Store(store), new Keyword(keyword));
	}

	@RemoteProperty
	public Store getStore() {
		return store;
	}
	
	public void setStore(Store store) {
		this.store = store;
	}
	
	@RemoteProperty
	public Keyword getKeyword() {
		return keyword;
	}
	
	public void setKeyword(Keyword keyword) {
		this.keyword = keyword;
	}
	
	public String getStoreId() {
		return (store == null) ? null : store.getStoreId();
	}

	public String getStoreName() {
		return (store == null) ? null : store.getStoreName();
	}

	public String getKeywordId() {
		return (keyword == null) ? null : keyword.getKeywordId();
	}

	public String getKeywordTerm() {
		return (keyword == null) ? null : keyword.getKeyword();
	}
	
	@Override
	public String toString() {
		return "(Store: " + store + "\tKeyword: " + keyword + ")";
	}

}
