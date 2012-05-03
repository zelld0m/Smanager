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


2. Update server.xml of your tomcat server. This is for handling UTF-8 strings

Add the line in red

    <Connector connectionTimeout="20000" port="8080" URIEncoding="UTF-8" protocol="HTTP/1.1" redirectPort="8443"/>


3. When asking DBA to clear the database, make sure they don't clear the tables listed below.
   Also if there are records added to these tables, do not forget to replicate the records in all servers (development, staging and production):
 		PRODUCT_CATEGORY
 		PRODUCT_CATEGORY_TYPE
		PROD_KEYWORD_MEMBER_TYPE
 		PRODUCT_STORE
 		REDIRECT_TYPE
 		RULE_TYPE
