package com.search.manager.model;

import java.io.Serializable;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class TopKeyword implements Serializable, Comparable<TopKeyword> {

	private static final long serialVersionUID = 5855294022170978094L;

	private String keyword;
	private int count;
	private int resultCount;
	private String sku;

	// tries
	private int tries;

	public TopKeyword(){
	}

	public TopKeyword(String keyword, int count) {
		super();
		this.keyword = keyword;
		this.count = count;
	}

	public TopKeyword(String keyword, int count, int resultCount, String sku) {
		super();
		this.keyword = keyword;
		this.count = count;
		this.resultCount = resultCount;
		this.sku = sku;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public void incrementTries() {
	    tries++;
	}
	
	public void stopProcessing() {
	    tries = -1;
	}
	
	public boolean continueProcessing(int max) {
	    return tries < max;
	}
    @Override
    public int compareTo(TopKeyword kc) {
        if (count > kc.count) {
            return -1;
        } else if (count < kc.count) {
            return 1;
        } else {
            return keyword.compareTo(kc.keyword);
        }
    }
    
    public String[] toStringArray() {
        return new String[] {String.valueOf(count), keyword, String.valueOf(resultCount), sku};
    }
}