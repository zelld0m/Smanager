package com.search.manager.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;

import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.component.SearchHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class QueryValidation {

	private CoreContainer container;

	@Before
	public void init() throws FileNotFoundException  {
		container = new CoreContainer("/home/solr/solrworks/solr4", new File("/home/solr/solrworks/solr4/solr.xml"));
	}

	@Test
	public void testLoading() {
		Collection<String> names = container.getCoreNames();
		Assert.assertNotNull(names);
		Assert.assertEquals(1, names.size());
		
		SolrCore core = container.getCore("ecost");
		
		Assert.assertNotNull(core);
		SearchHandler handler = (SearchHandler) core.getRequestHandler("select");
		
		Assert.assertNotNull(handler);
		
	}
}
