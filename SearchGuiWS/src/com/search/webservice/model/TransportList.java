package com.search.webservice.model;

import java.util.List;

public class TransportList extends UserToken{

	private static final long serialVersionUID = -1763176326995632123L;

	private List<String> list;

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}
}
