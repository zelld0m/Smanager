package com.search.ws.client;

public interface SearchGuiService extends javax.xml.rpc.Service {
    public java.lang.String getSearchGuiServiceHttpPortAddress();

    public com.search.ws.client.SearchGuiServicePortType getSearchGuiServiceHttpPort() throws javax.xml.rpc.ServiceException;

    public com.search.ws.client.SearchGuiServicePortType getSearchGuiServiceHttpPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
