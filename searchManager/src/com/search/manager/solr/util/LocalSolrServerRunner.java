package com.search.manager.solr.util;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

public class LocalSolrServerRunner {

	private HttpSolrServer httpSolrServer;
	private String coreName;
	private String solrUrl;
	private int maxRetries;
	private int connectionTimeout;
	private int soTimeout;
	private int defaultMaxConnectionsPerHost;
	private int maxTotalConnections;
	private boolean followRedirects = false;
	private boolean allowCompression = true;
	private Timer timer = null;

	private LocalSolrServerRunner() {
		// do nothing...
	}

	public boolean initLocalSolrServerRunner() {
		try {
			if (timer != null) {
				timer.cancel();
			}

			if (!solrUrl.endsWith("/")) {
				solrUrl += "/";
			}
			httpSolrServer = new HttpSolrServer(solrUrl + coreName);
			// Default to 0. > 1 not recommended.
			httpSolrServer.setMaxRetries(maxRetries);
			httpSolrServer.setConnectionTimeout(connectionTimeout);
			// This is desirable for queries, but probably not for indexing.
			httpSolrServer.setSoTimeout(soTimeout); // socket read timeout
			httpSolrServer
					.setDefaultMaxConnectionsPerHost(defaultMaxConnectionsPerHost);
			httpSolrServer.setMaxTotalConnections(maxTotalConnections);
			// Default to false.
			httpSolrServer.setFollowRedirects(followRedirects);
			// allowCompression defaults to false.
			// Server side must support gzip or deflate for this to have any
			// effect.
			httpSolrServer.setAllowCompression(allowCompression);

			timer = new Timer(true);
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if (httpSolrServer.getHttpClient() != null) {
						httpSolrServer.getHttpClient().getConnectionManager()
								.closeExpiredConnections();
					}
				}
			}, 5000);

		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public UpdateResponse addDocs(List<SolrInputDocument> solrDocs)
			throws SolrServerException, IOException {
		return httpSolrServer.add(solrDocs);
	}

	public UpdateResponse addDoc(SolrInputDocument doc)
			throws SolrServerException, IOException {
		return httpSolrServer.add(doc);
	}

	public UpdateResponse commit() throws SolrServerException, IOException {
		return httpSolrServer.commit();
	}

	public UpdateResponse softCommit() throws SolrServerException, IOException {
		return httpSolrServer.commit(false, false, true);
	}

	public UpdateResponse rollback() throws SolrServerException, IOException {
		return httpSolrServer.rollback();
	}

	public UpdateResponse optimize() throws SolrServerException, IOException {
		return httpSolrServer.optimize();
	}

	public UpdateResponse deleteById(String id) throws SolrServerException,
			IOException {
		return httpSolrServer.deleteById(id);
	}

	public UpdateResponse deleteByQuery(String query)
			throws SolrServerException, IOException {
		return httpSolrServer.deleteByQuery(query);
	}

	public QueryResponse query(SolrQuery solrQuery) throws SolrServerException,
			IOException {
		return httpSolrServer.query(solrQuery);
	}

	public int ping() {
		SolrPingResponse solrPingResponse;
		try {
			solrPingResponse = httpSolrServer.ping();
			return solrPingResponse.getStatus();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 1;
	}

	public void shutdown() {
		httpSolrServer.shutdown();
	}

	public SolrServer getSolrServer() {
		return httpSolrServer;
	}

	public void setSolrServer(HttpSolrServer solrServer) {
		this.httpSolrServer = solrServer;
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

	public HttpSolrServer getHttpSolrServer() {
		return httpSolrServer;
	}

	public void setHttpSolrServer(HttpSolrServer httpSolrServer) {
		this.httpSolrServer = httpSolrServer;
	}

	public int getMaxRetries() {
		return maxRetries;
	}

	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getSoTimeout() {
		return soTimeout;
	}

	public void setSoTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
	}

	public int getDefaultMaxConnectionsPerHost() {
		return defaultMaxConnectionsPerHost;
	}

	public void setDefaultMaxConnectionsPerHost(int defaultMaxConnectionsPerHost) {
		this.defaultMaxConnectionsPerHost = defaultMaxConnectionsPerHost;
	}

	public int getMaxTotalConnections() {
		return maxTotalConnections;
	}

	public void setMaxTotalConnections(int maxTotalConnections) {
		this.maxTotalConnections = maxTotalConnections;
	}

	public boolean isFollowRedirects() {
		return followRedirects;
	}

	public void setFollowRedirects(boolean followRedirects) {
		this.followRedirects = followRedirects;
	}

	public boolean isAllowCompression() {
		return allowCompression;
	}

	public void setAllowCompression(boolean allowCompression) {
		this.allowCompression = allowCompression;
	}

}