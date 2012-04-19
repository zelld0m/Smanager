package com.search.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.search.webservice.model.ElevateResult;
import com.search.webservice.model.ExcludeResult;
import com.search.ws.client.SearchGuiClientService;
import com.search.ws.client.SearchGuiClientServiceImpl;


public class Main{
	
	public static void main(String[] args) {
		SearchGuiClientService service = new SearchGuiClientServiceImpl();
		//System.out.println(service.loadElevateList("macmall"));
		//System.out.println(service.loadExcludeList("macmall"));
		
//		Map<String, List<ElevateResult>> map =	new HashMap<String, List<ElevateResult>>();
//		map.put("apple", new ArrayList<ElevateResult>());
//
//		System.out.println(service.pushElevateList("macmall", map));
//		
//		Map<String, List<ExcludeResult>> map2 =	new HashMap<String, List<ExcludeResult>>();
//		map2.put("apple", new ArrayList<ExcludeResult>());
//		
//		System.out.println(service.pushExcludeList("macmall", map2));
		
		List<String> elevatedKeyList = new ArrayList<String>();
		elevatedKeyList.add("computer");
		
		System.out.println(service.pushElevateList("macmall", elevatedKeyList));
//		
//		List<String> excludedKeyList = new ArrayList<String>();
//		excludedKeyList.add("apple");
//		
//		System.out.println(service.pushExcludeList("macmall", excludedKeyList));
		
	}
}
