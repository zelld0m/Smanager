package com.search.ws.client;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.NoEndPointException;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.client.Stub;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.SerializerFactory;
import org.apache.axis.encoding.ser.ArrayDeserializerFactory;
import org.apache.axis.encoding.ser.ArraySerializerFactory;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.encoding.ser.EnumDeserializerFactory;
import org.apache.axis.encoding.ser.EnumSerializerFactory;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.JavaUtils;

import com.search.manager.enums.RuleEntity;
import com.search.webservice.model.TransportList;

public class SearchGuiServiceHttpBindingStub extends Stub implements SearchGuiServicePortType {
	private Vector<Class<?>> cachedSerClasses = new Vector<Class<?>>();
	private Vector<QName> cachedSerQNames = new Vector<QName>();
	private Vector<Object> cachedSerFactories = new Vector<Object>();
	private Vector<Object> cachedDeserFactories = new Vector<Object>();

	static OperationDesc[] _operations;

	static {
		_operations = new OperationDesc[2];
		_initOperationDesc1();
	}

	private static void _initOperationDesc1() {
		OperationDesc oper;
		ParameterDesc param;

		oper = new OperationDesc();
		oper.setName("deployRulesMap");
		param = new ParameterDesc(new QName("http://ws.search.com/client", "in0"), ParameterDesc.IN, new QName(
		        "http://model.webservice.search.com", "TransportList"), TransportList.class, false, false);
		param.setNillable(true);
		oper.addParameter(param);
		oper.setReturnType(new QName("http://ws.search.com/client", "anyType2anyTypeMap"));
		oper.setReturnClass(AnyType2AnyTypeMapEntry[].class);
		oper.setReturnQName(new QName("http://ws.search.com/client", "out"));
		param = oper.getReturnParamDesc();
		param.setItemQName(new QName("http://ws.search.com/client", "entry"));
		oper.setStyle(Style.WRAPPED);
		oper.setUse(Use.LITERAL);
		_operations[0] = oper;

		oper = new OperationDesc();
		oper.setName("unDeployRulesMap");
		param = new ParameterDesc(new QName("http://ws.search.com/client", "in0"), ParameterDesc.IN, new QName(
		        "http://model.webservice.search.com", "TransportList"), TransportList.class, false, false);
		param.setNillable(true);
		oper.addParameter(param);
		oper.setReturnType(new QName("http://ws.search.com/client", "anyType2anyTypeMap"));
		oper.setReturnClass(AnyType2AnyTypeMapEntry[].class);
		oper.setReturnQName(new QName("http://ws.search.com/client", "out"));
		param = oper.getReturnParamDesc();
		param.setItemQName(new QName("http://ws.search.com/client", "entry"));
		oper.setStyle(Style.WRAPPED);
		oper.setUse(Use.LITERAL);
		_operations[1] = oper;

	}

	public SearchGuiServiceHttpBindingStub() throws AxisFault {
		this(null);
	}

	public SearchGuiServiceHttpBindingStub(URL endpointURL, javax.xml.rpc.Service service) throws AxisFault {
		this(service);
		super.cachedEndpoint = endpointURL;
	}

	public SearchGuiServiceHttpBindingStub(javax.xml.rpc.Service service) throws AxisFault {
		if (service == null) {
			super.service = new Service();
		} else {
			super.service = service;
		}
		((Service) super.service).setTypeMappingVersion("1.2");
		Class<?> cls;
		QName qName;
		QName qName2;

		Class<BeanSerializerFactory> beansf = BeanSerializerFactory.class;
		Class<BeanDeserializerFactory> beandf = BeanDeserializerFactory.class;
		Class<EnumSerializerFactory> enumsf = EnumSerializerFactory.class;
		Class<EnumDeserializerFactory> enumdf = EnumDeserializerFactory.class;

		qName = new QName("http://enums.manager.search.com", "RuleEntity");
		cachedSerQNames.add(qName);
		cls = RuleEntity.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(enumsf);
		cachedDeserFactories.add(enumdf);

		qName = new QName("http://model.webservice.search.com", "TransportList");
		cachedSerQNames.add(qName);
		cls = TransportList.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new QName("http://ws.search.com/client", ">anyType2anyTypeMap>entry");
		cachedSerQNames.add(qName);
		cls = AnyType2AnyTypeMapEntry.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new QName("http://ws.search.com/client", ">deployRulesMap");
		cachedSerQNames.add(qName);
		cls = DeployRulesMap.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new QName("http://ws.search.com/client", ">deployRulesMapResponse");
		cachedSerQNames.add(qName);
		cls = DeployRulesMapResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new QName("http://ws.search.com/client", ">unDeployRulesMap");
		cachedSerQNames.add(qName);
		cls = UnDeployRulesMap.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new QName("http://ws.search.com/client", ">unDeployRulesMapResponse");
		cachedSerQNames.add(qName);
		cls = UnDeployRulesMapResponse.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new QName("http://ws.search.com/client", "anyType2anyTypeMap");
		cachedSerQNames.add(qName);
		cls = AnyType2AnyTypeMapEntry[].class;
		cachedSerClasses.add(cls);
		qName = new QName("http://ws.search.com/client", ">anyType2anyTypeMap>entry");
		qName2 = new QName("http://ws.search.com/client", "entry");
		cachedSerFactories.add(new ArraySerializerFactory(qName, qName2));
		cachedDeserFactories.add(new ArrayDeserializerFactory());

		qName = new QName("http://ws.search.com/client", "ArrayOfString");
		cachedSerQNames.add(qName);
		cls = String[].class;
		cachedSerClasses.add(cls);
		qName = new QName("http://www.w3.org/2001/XMLSchema", "string");
		qName2 = new QName("http://ws.search.com/client", "string");
		cachedSerFactories.add(new ArraySerializerFactory(qName, qName2));
		cachedDeserFactories.add(new ArrayDeserializerFactory());

	}

	protected Call createCall() throws RemoteException {
		try {
			Call _call = super._createCall();
			if (super.maintainSessionSet) {
				_call.setMaintainSession(super.maintainSession);
			}
			if (super.cachedUsername != null) {
				_call.setUsername(super.cachedUsername);
			}
			if (super.cachedPassword != null) {
				_call.setPassword(super.cachedPassword);
			}
			if (super.cachedEndpoint != null) {
				_call.setTargetEndpointAddress(super.cachedEndpoint);
			}
			if (super.cachedTimeout != null) {
				_call.setTimeout(super.cachedTimeout);
			}
			if (super.cachedPortName != null) {
				_call.setPortName(super.cachedPortName);
			}
			Enumeration<Object> keys = super.cachedProperties.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				_call.setProperty(key, super.cachedProperties.get(key));
			}
			// All the type mapping information is registered
			// when the first call is made.
			// The type mapping information is actually registered in
			// the TypeMappingRegistry of the service, which
			// is the reason why registration is only needed for the first call.
			synchronized (this) {
				if (firstCall()) {
					// must set encoding style before registering serializers
					_call.setEncodingStyle(null);
					for (int i = 0; i < cachedSerFactories.size(); ++i) {
						Class<?> cls = (Class<?>) cachedSerClasses.get(i);
						QName qName = cachedSerQNames.get(i);
						Object x = cachedSerFactories.get(i);
						if (x instanceof Class) {
							Class<?> sf = (Class<?>) cachedSerFactories.get(i);
							Class<?> df = (Class<?>) cachedDeserFactories.get(i);
							_call.registerTypeMapping(cls, qName, sf, df, false);
						} else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
							SerializerFactory sf = (SerializerFactory) cachedSerFactories.get(i);
							DeserializerFactory df = (DeserializerFactory) cachedDeserFactories.get(i);
							_call.registerTypeMapping(cls, qName, sf, df, false);
						}
					}
				}
			}
			return _call;
		} catch (Throwable _t) {
			throw new AxisFault("Failure trying to get the Call object", _t);
		}
	}

	public AnyType2AnyTypeMapEntry[] deployRulesMap(TransportList in0) throws RemoteException {
		if (super.cachedEndpoint == null) {
			throw new NoEndPointException();
		}
		Call _call = createCall();
		_call.setOperation(_operations[0]);
		// _call.setUseSOAPAction(true);
		// _call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new QName("http://ws.search.com/client", "deployRulesMap"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			Object _resp = _call.invoke(new Object[] { in0 });

			if (_resp instanceof RemoteException) {
				throw (RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (AnyType2AnyTypeMapEntry[]) _resp;
				} catch (Exception _exception) {
					return (AnyType2AnyTypeMapEntry[]) JavaUtils.convert(_resp, AnyType2AnyTypeMapEntry[].class);
				}
			}
		} catch (AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

	public AnyType2AnyTypeMapEntry[] unDeployRulesMap(TransportList in0) throws RemoteException {
		if (super.cachedEndpoint == null) {
			throw new NoEndPointException();
		}
		Call _call = createCall();
		_call.setOperation(_operations[1]);
		// _call.setUseSOAPAction(true);
		// _call.setSOAPActionURI("");
		_call.setEncodingStyle(null);
		_call.setProperty(Call.SEND_TYPE_ATTR, Boolean.FALSE);
		_call.setProperty(AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		_call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new QName("http://ws.search.com/client", "unDeployRulesMap"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			Object _resp = _call.invoke(new Object[] { in0 });

			if (_resp instanceof RemoteException) {
				throw (RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (AnyType2AnyTypeMapEntry[]) _resp;
				} catch (Exception _exception) {
					return (AnyType2AnyTypeMapEntry[]) JavaUtils.convert(_resp, AnyType2AnyTypeMapEntry[].class);
				}
			}
		} catch (AxisFault axisFaultException) {
			throw axisFaultException;
		}
	}

}
