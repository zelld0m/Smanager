package com.search.ws.client;

public class SearchGuiServicePortTypeProxy implements com.search.ws.client.SearchGuiServicePortType {
  private String _endpoint = null;
  private com.search.ws.client.SearchGuiServicePortType searchGuiServicePortType = null;
  
  public SearchGuiServicePortTypeProxy() {
    _initSearchGuiServicePortTypeProxy();
  }
  
  public SearchGuiServicePortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initSearchGuiServicePortTypeProxy();
  }
  
  private void _initSearchGuiServicePortTypeProxy() {
    try {
      searchGuiServicePortType = (new com.search.ws.client.SearchGuiServiceLocator()).getSearchGuiServiceHttpPort();
      if (searchGuiServicePortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)searchGuiServicePortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)searchGuiServicePortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (searchGuiServicePortType != null)
      ((javax.xml.rpc.Stub)searchGuiServicePortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.search.ws.client.SearchGuiServicePortType getSearchGuiServicePortType() {
    if (searchGuiServicePortType == null)
      _initSearchGuiServicePortTypeProxy();
    return searchGuiServicePortType;
  }
  
  public boolean pushElevateList1(com.search.webservice.model.TransportList in0) throws java.rmi.RemoteException{
    if (searchGuiServicePortType == null)
      _initSearchGuiServicePortTypeProxy();
    return searchGuiServicePortType.pushElevateList1(in0);
  }
  
  public boolean pushExcludeList(com.search.webservice.model.ExcludedList in0) throws java.rmi.RemoteException{
    if (searchGuiServicePortType == null)
      _initSearchGuiServicePortTypeProxy();
    return searchGuiServicePortType.pushExcludeList(in0);
  }
  
  public boolean loadRelevancyList(java.lang.String in0, java.lang.String in1) throws java.rmi.RemoteException{
    if (searchGuiServicePortType == null)
      _initSearchGuiServicePortTypeProxy();
    return searchGuiServicePortType.loadRelevancyList(in0, in1);
  }
  
  public boolean loadExcludeList(java.lang.String in0, java.lang.String in1) throws java.rmi.RemoteException{
    if (searchGuiServicePortType == null)
      _initSearchGuiServicePortTypeProxy();
    return searchGuiServicePortType.loadExcludeList(in0, in1);
  }
  
  public boolean pushExcludeList1(com.search.webservice.model.TransportList in0) throws java.rmi.RemoteException{
    if (searchGuiServicePortType == null)
      _initSearchGuiServicePortTypeProxy();
    return searchGuiServicePortType.pushExcludeList1(in0);
  }
  
  public boolean pushElevateList(com.search.webservice.model.ElevatedList in0) throws java.rmi.RemoteException{
    if (searchGuiServicePortType == null)
      _initSearchGuiServicePortTypeProxy();
    return searchGuiServicePortType.pushElevateList(in0);
  }
  
  public boolean loadRelevancyDetails(java.lang.String in0, java.lang.String in1) throws java.rmi.RemoteException{
    if (searchGuiServicePortType == null)
      _initSearchGuiServicePortTypeProxy();
    return searchGuiServicePortType.loadRelevancyDetails(in0, in1);
  }
  
  public boolean loadElevateList(java.lang.String in0, java.lang.String in1) throws java.rmi.RemoteException{
    if (searchGuiServicePortType == null)
      _initSearchGuiServicePortTypeProxy();
    return searchGuiServicePortType.loadElevateList(in0, in1);
  }
  
  
}