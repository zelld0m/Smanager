package com.search.webservice;

import com.search.webservice.model.ElevatedList;
import com.search.webservice.model.ExcludedList;
import com.search.webservice.model.TransportList;

public interface SearchGuiService{
	public boolean loadElevateList(String store, String token);
	public boolean loadExcludeList(String store, String token);
	public boolean loadRelevancyList(String store, String token);
	public boolean loadRelevancyDetails(String store, String token);;
	public boolean pushElevateList(ElevatedList list);
	public boolean pushExcludeList(ExcludedList list);
	public boolean pushElevateList(TransportList list);
	public boolean pushExcludeList(TransportList list);
}
