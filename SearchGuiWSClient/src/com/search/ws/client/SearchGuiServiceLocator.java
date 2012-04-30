package com.search.ws.client;

public class SearchGuiServiceLocator extends org.apache.axis.client.Service implements com.search.ws.client.SearchGuiService {

    public SearchGuiServiceLocator() {
    }


    public SearchGuiServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SearchGuiServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SearchGuiServiceHttpPort
    private java.lang.String SearchGuiServiceHttpPort_address = "http://localhost:8081/SearchGuiWS/services/SearchGuiService";

    public java.lang.String getSearchGuiServiceHttpPortAddress() {
        return SearchGuiServiceHttpPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SearchGuiServiceHttpPortWSDDServiceName = "SearchGuiServiceHttpPort";

    public java.lang.String getSearchGuiServiceHttpPortWSDDServiceName() {
        return SearchGuiServiceHttpPortWSDDServiceName;
    }

    public void setSearchGuiServiceHttpPortWSDDServiceName(java.lang.String name) {
        SearchGuiServiceHttpPortWSDDServiceName = name;
    }

    public com.search.ws.client.SearchGuiServicePortType getSearchGuiServiceHttpPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SearchGuiServiceHttpPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSearchGuiServiceHttpPort(endpoint);
    }

    public com.search.ws.client.SearchGuiServicePortType getSearchGuiServiceHttpPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.search.ws.client.SearchGuiServiceHttpBindingStub _stub = new com.search.ws.client.SearchGuiServiceHttpBindingStub(portAddress, this);
            _stub.setPortName(getSearchGuiServiceHttpPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSearchGuiServiceHttpPortEndpointAddress(java.lang.String address) {
        SearchGuiServiceHttpPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.search.ws.client.SearchGuiServicePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.search.ws.client.SearchGuiServiceHttpBindingStub _stub = new com.search.ws.client.SearchGuiServiceHttpBindingStub(new java.net.URL(SearchGuiServiceHttpPort_address), this);
                _stub.setPortName(getSearchGuiServiceHttpPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("SearchGuiServiceHttpPort".equals(inputPortName)) {
            return getSearchGuiServiceHttpPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://ws.search.com/client", "SearchGuiService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://ws.search.com/client", "SearchGuiServiceHttpPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("SearchGuiServiceHttpPort".equals(portName)) {
            setSearchGuiServiceHttpPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
