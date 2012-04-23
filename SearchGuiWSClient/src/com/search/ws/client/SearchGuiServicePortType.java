package com.search.ws.client;

public interface SearchGuiServicePortType extends java.rmi.Remote {
    public boolean recallRules(com.search.webservice.model.TransportList in0) throws java.rmi.RemoteException;
    public com.search.webservice.model.BackupInfo[] getBackupInfo(com.search.webservice.model.TransportList in0) throws java.rmi.RemoteException;
    public boolean deployRules(com.search.webservice.model.TransportList in0) throws java.rmi.RemoteException;
}
