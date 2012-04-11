package com.search.ws.client;

import java.rmi.RemoteException;

import javax.xml.rpc.Stub;

import org.apache.log4j.Logger;

import com.search.manager.utility.PropsUtils;

public class SearchGuiClientServiceImpl implements SearchGuiClientService{
	
	private static Logger logger = Logger.getLogger(SearchGuiClientServiceImpl.class);
	private static String clientServer = PropsUtils.getValue("guiwsclient");
	
	@Override
	public boolean loadElevateList(String store, String token) {
		try {
			Stub stub = createStoreProxy();
			stub._setProperty(javax.xml.rpc.Stub.ENDPOINT_ADDRESS_PROPERTY, clientServer);
			SearchGuiServicePortType search = (SearchGuiServicePortType) stub;

			return search.loadElevateList(store,token);
		} catch (RemoteException e) {
			logger.error(e);
		}
		return false;
	}

	@Override
	public boolean loadExcludeList(String store, String token) {
		try {
			Stub stub = createStoreProxy();
			stub._setProperty(javax.xml.rpc.Stub.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8080/SearchGuiWS/services/SearchGuiService");
			SearchGuiServicePortType search = (SearchGuiServicePortType) stub;
			return search.loadExcludeList(store,token);
		} catch (RemoteException e) {
			logger.error(e);
		}
		return false;
	}
	
	private static Stub createStoreProxy() {
		return (Stub) (new SearchGuiServicePortTypeProxy().getSearchGuiServicePortType());
	}
}
