Important:

1. Copy solr.xml, relevancy.xml to the path specified for config servlet in web.xml. For example, in the code below

	 <servlet>
	    <servlet-name>config</servlet-name>
	    <servlet-class>com.search.ws.ConfigServlet</servlet-class>
	    <init-param>
	        <param-name>config-folder</param-name>
	        <param-value>C:\conf</param-value>
	    </init-param>
	    <init-param>
	        <param-name>config-file</param-name>
	        <param-value>solr.xml</param-value>
	    </init-param>
	    <init-param>
	        <param-name>relevancy-config-file</param-name>
	        <param-value>solr.xml</param-value>
	    </init-param>
	    <load-on-startup>2</load-on-startup>
	  </servlet>
	  
copy solr.xml to c:\conf


2. Update server.xml of your tomcat server.

Add the line in red

    <Connector connectionTimeout="20000" port="8080" URIEncoding="UTF-8" protocol="HTTP/1.1" redirectPort="8443"/>

    
    