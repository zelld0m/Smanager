package com.search.ws.client;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import javax.xml.rpc.Stub;
import org.apache.log4j.Logger;

import com.search.manager.utility.PropsUtils;
import com.search.webservice.model.ElevateResult;
import com.search.webservice.model.ElevatedList;
import com.search.webservice.model.ExcludeResult;
import com.search.webservice.model.ExcludedList;
import com.search.webservice.model.TransportList;

public class SearchGuiClientServiceImpl implements SearchGuiClientService{
	
	private static Logger logger = Logger.getLogger(SearchGuiClientServiceImpl.class);
	private static String WS_CLIENT = PropsUtils.getValue("guiwsclient");
	private static String TOKEN = PropsUtils.getValue("token");
	
	// for testing only: do not use in prod
//	static{
//		WS_CLIENT = "http://localhost:8081/SearchGuiWS/services/SearchGuiService";	
//		TOKEN = "Hzwviq%2FMwKMpephPCMpavg%3D%3D";
//	}
	
	@Override
	public boolean loadElevateList(String store) {
		try {
			Stub stub = createStoreProxy();
			stub._setProperty(javax.xml.rpc.Stub.ENDPOINT_ADDRESS_PROPERTY, WS_CLIENT);
			SearchGuiServicePortType search = (SearchGuiServicePortType) stub;

			return search.loadElevateList(store,TOKEN);
		} catch (RemoteException e) {
			logger.error(e);
		}
		return false;
	}

	@Override
	public boolean loadExcludeList(String store) {
		try {
			Stub stub = createStoreProxy();
			stub._setProperty(javax.xml.rpc.Stub.ENDPOINT_ADDRESS_PROPERTY, WS_CLIENT);
			SearchGuiServicePortType search = (SearchGuiServicePortType) stub;
			return search.loadExcludeList(store,TOKEN);
		} catch (RemoteException e) {
			logger.error(e);
		}
		return false;
	}
	
	private static Stub createStoreProxy() {
		return (Stub) (new SearchGuiServicePortTypeProxy().getSearchGuiServicePortType());
	}

	@Override
	public boolean pushElevateList(String store, Map<String, List<ElevateResult>> map) {
		try{
			if(map != null && map.size() > 0){
				Stub stub = createStoreProxy();
				stub._setProperty(javax.xml.rpc.Stub.ENDPOINT_ADDRESS_PROPERTY, WS_CLIENT);
				SearchGuiServicePortType search = (SearchGuiServicePortType) stub;
				
				ElevatedList list = new ElevatedList();
				list.setToken(TOKEN);
				list.setStore(store);
				
				AnyType2AnyTypeMapEntry[] entArr = new AnyType2AnyTypeMapEntry[map.size()];
				int arrCnt = 0;

				for(String key : map.keySet()){
					ElevateResult[] eArr = new ElevateResult[map.get(key).size()];
					AnyType2AnyTypeMapEntry entry = new AnyType2AnyTypeMapEntry();
					entry.setKey(key);
					int eCnt = 0;
					
					for(ElevateResult e : map.get(key)){
						eArr[eCnt] = e;
						eCnt++;
					}
					entry.setValue(eArr);
					entArr[arrCnt] = entry;
					arrCnt++;
				}	

				list.setMap(entArr);
				return search.pushElevateList(list);	
			}
		}catch (Exception e) {
			logger.error(e);
		}
		return false;
	}

	@Override
	public boolean pushExcludeList(String store, Map<String, List<ExcludeResult>> map) {
		try{
			if(map != null && map.size() > 0){
				Stub stub = createStoreProxy();
				stub._setProperty(javax.xml.rpc.Stub.ENDPOINT_ADDRESS_PROPERTY, WS_CLIENT);
				SearchGuiServicePortType search = (SearchGuiServicePortType) stub;
				
				ExcludedList list = new ExcludedList();
				list.setToken(TOKEN);
				list.setStore(store);
				
				AnyType2AnyTypeMapEntry[] entArr = new AnyType2AnyTypeMapEntry[map.size()];
				int arrCnt = 0;

				for(String key : map.keySet()){
					ExcludeResult[] eArr = new ExcludeResult[map.get(key).size()];
					AnyType2AnyTypeMapEntry entry = new AnyType2AnyTypeMapEntry();
					entry.setKey(key);
					int eCnt = 0;
					
					for(ExcludeResult e : map.get(key)){
						eArr[eCnt] = e;
						eCnt++;
					}
					entry.setValue(eArr);
					entArr[arrCnt] = entry;
					arrCnt++;
				}	

				list.setMap(entArr);
				return search.pushExcludeList(list);	
			}
		}catch (Exception e) {
			logger.error(e);
		}
		return false;
	}

	@Override
	public boolean pushElevateList(String store, List<String> list){
		
		try{
			if(list != null && list.size() > 0){
				Stub stub = createStoreProxy();
				stub._setProperty(javax.xml.rpc.Stub.ENDPOINT_ADDRESS_PROPERTY, WS_CLIENT);
				SearchGuiServicePortType search = (SearchGuiServicePortType) stub;
				
				TransportList list_ = new TransportList();
				list_.setToken(TOKEN);
				list_.setStore(store);
				
				String[] entArr = new String[list.size()];
				int arrCnt = 0;

				for(String key : list){
					entArr[arrCnt] = key;
					arrCnt++;
				}	

				list_.setList(entArr);
				return search.pushElevateList1(list_);
			}
		}catch (Exception e) {
			logger.error(e);
		}
		return false;
	}

	@Override
	public boolean pushExcludeList(String store, List<String> list) {
		
		try{
			if(list != null && list.size() > 0){
				Stub stub = createStoreProxy();
				stub._setProperty(javax.xml.rpc.Stub.ENDPOINT_ADDRESS_PROPERTY, WS_CLIENT);
				SearchGuiServicePortType search = (SearchGuiServicePortType) stub;
				
				TransportList list_ = new TransportList();
				list_.setToken(TOKEN);
				list_.setStore(store);
				
				String[] entArr = new String[list.size()];
				int arrCnt = 0;

				for(String key : list){
					entArr[arrCnt] = key;
					arrCnt++;
				}	

				list_.setList(entArr);
				return search.pushExcludeList1(list_);
			}
		}catch (Exception e) {
			logger.error(e);
		}
		return false;
	}
}
