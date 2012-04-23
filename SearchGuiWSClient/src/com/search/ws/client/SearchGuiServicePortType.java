/**
 * SearchGuiServicePortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.search.ws.client;

public interface SearchGuiServicePortType extends java.rmi.Remote {
    public boolean recallRules(com.search.webservice.model.TransportList in0) throws java.rmi.RemoteException;
    public com.search.webservice.model.BackupInfo[] getBackupInfo(com.search.webservice.model.TransportList in0) throws java.rmi.RemoteException;
    public boolean deployRules(com.search.webservice.model.TransportList in0) throws java.rmi.RemoteException;
}
