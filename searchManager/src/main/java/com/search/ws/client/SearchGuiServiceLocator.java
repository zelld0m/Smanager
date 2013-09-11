package com.search.ws.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Remote;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Service;
import org.apache.axis.client.Stub;

public class SearchGuiServiceLocator extends Service implements SearchGuiService {

	private static final long serialVersionUID = 1L;

	public SearchGuiServiceLocator() {
	}

	public SearchGuiServiceLocator(EngineConfiguration config) {
		super(config);
	}

	public SearchGuiServiceLocator(String wsdlLoc, QName sName) throws ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for SearchGuiServiceHttpPort
	private String SearchGuiServiceHttpPort_address = "http://localhost:8081/SearchGuiWS/services/SearchGuiService";

	@Override
	public String getSearchGuiServiceHttpPortAddress() {
		return SearchGuiServiceHttpPort_address;
	}

	// The WSDD service name defaults to the port name.
	private String SearchGuiServiceHttpPortWSDDServiceName = "SearchGuiServiceHttpPort";

	public String getSearchGuiServiceHttpPortWSDDServiceName() {
		return SearchGuiServiceHttpPortWSDDServiceName;
	}

	public void setSearchGuiServiceHttpPortWSDDServiceName(String name) {
		SearchGuiServiceHttpPortWSDDServiceName = name;
	}

	@Override
	public SearchGuiServicePortType getSearchGuiServiceHttpPort() throws ServiceException {
		URL endpoint;
		try {
			endpoint = new URL(SearchGuiServiceHttpPort_address);
		} catch (MalformedURLException e) {
			throw new ServiceException(e);
		}
		return getSearchGuiServiceHttpPort(endpoint);
	}

	@Override
	public SearchGuiServicePortType getSearchGuiServiceHttpPort(URL portAddress) throws ServiceException {
		try {
			SearchGuiServiceHttpBindingStub _stub = new SearchGuiServiceHttpBindingStub(portAddress, this);
			_stub.setPortName(getSearchGuiServiceHttpPortWSDDServiceName());
			return _stub;
		} catch (AxisFault e) {
			return null;
		}
	}

	public void setSearchGuiServiceHttpPortEndpointAddress(String address) {
		SearchGuiServiceHttpPort_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has
	 * no port for the given interface, then ServiceException is thrown.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Remote getPort(Class serviceEndpointInterface) throws ServiceException {
		try {
			if (SearchGuiServicePortType.class.isAssignableFrom(serviceEndpointInterface)) {
				SearchGuiServiceHttpBindingStub _stub = new SearchGuiServiceHttpBindingStub(new URL(
				        SearchGuiServiceHttpPort_address), this);
				_stub.setPortName(getSearchGuiServiceHttpPortWSDDServiceName());
				return _stub;
			}
		} catch (Throwable t) {
			throw new ServiceException(t);
		}
		throw new ServiceException("There is no stub implementation for the interface:  "
		        + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
	}

	/**
	 * For the given interface, get the stub implementation. If this service has
	 * no port for the given interface, then ServiceException is thrown.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Remote getPort(QName portName, Class serviceEndpointInterface) throws ServiceException {
		if (portName == null) {
			return getPort(serviceEndpointInterface);
		}
		String inputPortName = portName.getLocalPart();
		if ("SearchGuiServiceHttpPort".equals(inputPortName)) {
			return getSearchGuiServiceHttpPort();
		} else {
			Remote _stub = getPort(serviceEndpointInterface);
			((Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	@Override
	public QName getServiceName() {
		return new QName("http://ws.search.com/client", "SearchGuiService");
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	// public void setEndpointAddress(java.lang.String portName,
	// java.lang.String address) throws javax.xml.rpc.ServiceException {
	//
	// if ("SearchGuiServiceHttpPort".equals(portName)) {
	// setSearchGuiServiceHttpPortEndpointAddress(address);
	// }
	// else
	// { // Unknown Port Name
	// throw new
	// javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port"
	// + portName);
	// }
	// }

	// /**
	// * Set the endpoint address for the specified port name.
	// */
	// public void setEndpointAddress(javax.xml.namespace.QName portName,
	// java.lang.String address) throws javax.xml.rpc.ServiceException {
	// setEndpointAddress(portName.getLocalPart(), address);
	// }

}
