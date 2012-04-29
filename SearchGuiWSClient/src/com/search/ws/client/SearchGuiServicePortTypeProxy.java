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
  
  public com.search.ws.client.AnyType2AnyTypeMapEntry[] recallRulesMap(com.search.webservice.model.TransportList in0) throws java.rmi.RemoteException{
    if (searchGuiServicePortType == null)
      _initSearchGuiServicePortTypeProxy();
    return searchGuiServicePortType.recallRulesMap(in0);
  }
  
  public boolean recallRules(com.search.webservice.model.TransportList in0) throws java.rmi.RemoteException{
    if (searchGuiServicePortType == null)
      _initSearchGuiServicePortTypeProxy();
    return searchGuiServicePortType.recallRules(in0);
  }
  
  public com.search.ws.client.AnyType2AnyTypeMapEntry[] deployRulesMap(com.search.webservice.model.TransportList in0) throws java.rmi.RemoteException{
    if (searchGuiServicePortType == null)
      _initSearchGuiServicePortTypeProxy();
    return searchGuiServicePortType.deployRulesMap(in0);
  }
  
  public com.search.webservice.model.BackupInfo[] getBackupInfo(com.search.webservice.model.TransportList in0) throws java.rmi.RemoteException{
    if (searchGuiServicePortType == null)
      _initSearchGuiServicePortTypeProxy();
    return searchGuiServicePortType.getBackupInfo(in0);
  }
  
  public boolean deployRules(com.search.webservice.model.TransportList in0) throws java.rmi.RemoteException{
    if (searchGuiServicePortType == null)
      _initSearchGuiServicePortTypeProxy();
    return searchGuiServicePortType.deployRules(in0);
  }
  
  public boolean unDeployRules(com.search.webservice.model.TransportList in0) throws java.rmi.RemoteException{
    if (searchGuiServicePortType == null)
      _initSearchGuiServicePortTypeProxy();
    return searchGuiServicePortType.unDeployRules(in0);
  }
  
  public com.search.ws.client.AnyType2AnyTypeMapEntry[] unDeployRulesMap(com.search.webservice.model.TransportList in0) throws java.rmi.RemoteException{
    if (searchGuiServicePortType == null)
      _initSearchGuiServicePortTypeProxy();
    return searchGuiServicePortType.unDeployRulesMap(in0);
  }
  
  
}