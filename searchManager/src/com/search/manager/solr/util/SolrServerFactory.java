package com.search.manager.solr.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;

public class SolrServerFactory {

	private static final Logger logger = Logger
			.getLogger(SolrServerFactory.class);

	private static SolrServerFactory instance = null;
	private static Map<String, LocalSolrServerRunner> solrServers;
	private List<String> cores;
	private String solrUrl;

	private SolrServerFactory() {
		// do nothing...
	}

	public static SolrServerFactory getInstance(String solrUrl, List<String> cores) {
		if (instance == null) {
			instance = new SolrServerFactory();
			instance.solrUrl = solrUrl;
			instance.cores = cores;
			initCores(solrUrl, cores);
		}

		return instance;
	}

	private static void initCores(String solrUrl, List<String> cores) {
		if (solrServers == null) {
			solrServers = new HashMap<String, LocalSolrServerRunner>();
			for (String core : cores) {
				try {
					LocalSolrServerRunner server = new LocalSolrServerRunner(
							solrUrl, core);
					if (!solrServers.containsKey(core)) {
						solrServers.put(core, server);
						logger.info("Core instance = " + core + "[" + server
								+ "]");
					} else {
						logger.info("Duplicate core = " + core);
					}
				} catch (SolrServerException e) {
					logger.error("Error in creating solrServer for " + core, e);
				}
			}
		}
	}

	public LocalSolrServerRunner getCoreInstance(String core)
			throws SolrServerException {
		if (instance == null) {
			getInstance(solrUrl, cores);
		}

		LocalSolrServerRunner server = solrServers.get(core);
		if (server != null) {
			if (1 == server.ping()) {
				try {
					server = new LocalSolrServerRunner(server.getSolrUrl(),
							core);
					solrServers.put(core, server);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return server;
		}
		logger.debug("Core not found. " + core);
		throw new SolrServerException("Core not found exception. " + core);
	}

	public boolean shutdown() {
		if (instance != null) {
			for (String core : cores) {
				if (solrServers.containsKey(core)) {
					try {
						logger.info("Shutting down Core = "
								+ solrServers.get(core));
						solrServers.get(core).shutdown();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return true;
		}

		return false;
	}

	public String getSolrUrl() {
		return solrUrl;
	}

	public void setSolrUrl(String solrUrl) {
		this.solrUrl = solrUrl;
	}

	public List<String> getCores() {
		return cores;
	}

	public void setCores(List<String> cores) {
		this.cores = cores;
	}

}
