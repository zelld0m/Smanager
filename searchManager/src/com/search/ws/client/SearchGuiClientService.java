package com.search.ws.client;

public interface SearchGuiClientService {
	public boolean loadElevateList(String store, String token);
	public boolean loadExcludeList(String store, String token);
}
