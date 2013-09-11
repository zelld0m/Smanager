package com.search.manager.utility;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import java.util.WeakHashMap;

public class BeanUtil {
  
	private BeanUtil() {}
	
  private static Map<String, Method> map = new WeakHashMap<String, Method>();
  
  public static Object getProperty(String name, Object instance)  
     {
      Object obj[] = null;
	  try {
      Method read = getReadMethod(name,instance);
      if(read == null) 
    	  throw new IllegalArgumentException("Cannot find instance with property '" + name + "'");
      if(read.getReturnType().equals(Integer.class)){
      	return read.invoke(instance,obj);
	  }else if(read.getReturnType().equals(Date.class)){
		return read.invoke(instance,obj);  
	  }else if(read.getReturnType().equals(BigInteger.class)){
		return read.invoke(instance,obj);  
	  }else{
		return read.invoke(instance,obj).toString().toLowerCase();  
	  }
    } catch (Exception e) {
    	//throw new IllegalArgumentException("Problem accessing property '" + name + "': " + e.getMessage());
    	return "";
    }
  }
  
  private static Method getReadMethod(String name, Object instance) 
    throws IllegalArgumentException, IntrospectionException {
    String id = instance.getClass() + "#" + name;
    Method read = map.get(id);
    if (read == null) {
      BeanInfo info = Introspector.getBeanInfo(
        instance.getClass(),Introspector.USE_ALL_BEANINFO);
      PropertyDescriptor pds[] = info.getPropertyDescriptors();
      for(int i=0;i<pds.length;i++) {
        PropertyDescriptor pd = pds[i];
        if(name.equals(pd.getName())) {
          read = pd.getReadMethod();
          map.put(id,read);
          break;
        }
      }
    }
    if (read == null) 
      throw new IllegalArgumentException("Cannot find instance with property '" + name + "'");
    return read;
  }
}
