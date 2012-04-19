package com.search.ws.client;

import java.util.List;
import java.util.Map;
import com.search.webservice.model.ElevateResult;
import com.search.webservice.model.ExcludeResult;

public interface SearchGuiClientService {
	public boolean loadElevateList(String store);
	public boolean loadExcludeList(String store);
	public boolean pushElevateList(String store, Map<String,List<ElevateResult>> map);
	public boolean pushExcludeList(String store, Map<String,List<ExcludeResult>> map);
	public boolean pushElevateList(String store, List<String> list);
	public boolean pushExcludeList(String store, List<String> list);
}
