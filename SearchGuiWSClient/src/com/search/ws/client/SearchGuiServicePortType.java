package com.search.ws.client;

public interface SearchGuiServicePortType extends java.rmi.Remote {
    public com.search.ws.client.AnyType2AnyTypeMapEntry[] deployRulesMap(com.search.webservice.model.TransportList in0) throws java.rmi.RemoteException;
    public com.search.ws.client.AnyType2AnyTypeMapEntry[] unDeployRulesMap(com.search.webservice.model.TransportList in0) throws java.rmi.RemoteException;
}
