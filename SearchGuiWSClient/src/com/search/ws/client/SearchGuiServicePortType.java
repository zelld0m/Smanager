/**
 * SearchGuiServicePortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.search.ws.client;

public interface SearchGuiServicePortType extends java.rmi.Remote {
    public boolean pushElevateList1(com.search.webservice.model.TransportList in0) throws java.rmi.RemoteException;
    public boolean pushExcludeList(com.search.webservice.model.ExcludedList in0) throws java.rmi.RemoteException;
    public boolean loadRelevancyList(java.lang.String in0, java.lang.String in1) throws java.rmi.RemoteException;
    public boolean loadExcludeList(java.lang.String in0, java.lang.String in1) throws java.rmi.RemoteException;
    public boolean pushExcludeList1(com.search.webservice.model.TransportList in0) throws java.rmi.RemoteException;
    public boolean pushElevateList(com.search.webservice.model.ElevatedList in0) throws java.rmi.RemoteException;
    public boolean loadRelevancyDetails(java.lang.String in0, java.lang.String in1) throws java.rmi.RemoteException;
    public boolean loadElevateList(java.lang.String in0, java.lang.String in1) throws java.rmi.RemoteException;
}
