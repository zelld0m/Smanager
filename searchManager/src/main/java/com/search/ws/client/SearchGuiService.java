package com.search.ws.client;

import java.net.URL;

import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceException;

public interface SearchGuiService extends Service {
    public String getSearchGuiServiceHttpPortAddress();

    public SearchGuiServicePortType getSearchGuiServiceHttpPort() throws ServiceException;

    public SearchGuiServicePortType getSearchGuiServiceHttpPort(URL portAddress) throws ServiceException;
}
