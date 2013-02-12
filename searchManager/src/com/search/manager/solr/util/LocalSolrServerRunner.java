package com.search.manager.solr.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;

public class LocalSolrServerRunner {

	private SolrServer solrServer;
	private String coreName;
	private String solrUrl;

	@SuppressWarnings("unused")
	private LocalSolrServerRunner() {
		// do nothing...
	}

	public LocalSolrServerRunner(String solrHome, File solrConfigFile)
			throws SolrServerException {
		try {
			File f = new File(solrConfigFile, "solr.xml");
			CoreContainer coreContainer = new CoreContainer(solrHome, f);
			solrServer = new EmbeddedSolrServer(coreContainer, coreName);
		} catch (Exception e) {
			throw new SolrServerException(e);
		}
	}

	public LocalSolrServerRunner(String solrUrl, String coreName)
			throws SolrServerException {
		this.solrUrl = solrUrl;
		this.coreName = coreName;

		if (!solrUrl.endsWith("/")) {
			solrUrl += "/";
		}
		solrServer = new HttpSolrServer(solrUrl + coreName);
	}

	public UpdateResponse addDocs(List<SolrInputDocument> solrDocs)
			throws SolrServerException, IOException {
		return solrServer.add(solrDocs);
	}

	public UpdateResponse addDoc(SolrInputDocument doc)
			throws SolrServerException, IOException {
		return solrServer.add(doc);
	}

	public UpdateResponse commit() throws SolrServerException, IOException {
		return solrServer.commit();
	}

	public UpdateResponse softCommit() throws SolrServerException, IOException {
		return solrServer.commit(false, false, true);
	}

	public UpdateResponse rollback() throws SolrServerException, IOException {
		return solrServer.rollback();
	}

	public UpdateResponse optimize() throws SolrServerException, IOException {
		return solrServer.optimize();
	}

	public UpdateResponse deleteById(String id) throws SolrServerException,
			IOException {
		return solrServer.deleteById(id);
	}

	public UpdateResponse deleteByQuery(String query)
			throws SolrServerException, IOException {
		return solrServer.deleteByQuery(query);
	}

	public QueryResponse query(SolrQuery solrQuery) throws SolrServerException,
			IOException {
		return solrServer.query(solrQuery);
	}

	public int ping() {
		SolrPingResponse solrPingResponse;
		try {
			solrPingResponse = solrServer.ping();
			return solrPingResponse.getStatus();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 1;
	}

	public void shutdown() {
		solrServer.shutdown();
	}

	public SolrServer getSolrServer() {
		return solrServer;
	}

	public void setSolrServer(SolrServer solrServer) {
		this.solrServer = solrServer;
	}

	public String getCoreName() {
		return coreName;
	}

	public void setCoreName(String coreName) {
		this.coreName = coreName;
	}

	public String getSolrUrl() {
		return solrUrl;
	}

	public void setSolrUrl(String solrUrl) {
		this.solrUrl = solrUrl;
	}

}
