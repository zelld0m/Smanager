package com.search.manager.solr.util;

import java.util.ArrayList;
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

	private SolrServerFactory() {
		// do nothing...
	}

	public SolrServerFactory(List<LocalSolrServerRunner> localSolrServerRunners) {
		if (localSolrServerRunners != null) {
			instance = new SolrServerFactory();
			solrServers = new HashMap<String, LocalSolrServerRunner>();
			cores = new ArrayList<String>();

			for (LocalSolrServerRunner localSolrServerRunner : localSolrServerRunners) {
				cores.add(localSolrServerRunner.getCoreName());
				if (localSolrServerRunner.initLocalSolrServerRunner()) {
					solrServers.put(localSolrServerRunner.getCoreName(),
							localSolrServerRunner);
				} else {
					logger.error("Unable to initialize localSolrServerRunner for "
							+ localSolrServerRunner.getCoreName() + ".");
				}
			}
		}
	}

	public LocalSolrServerRunner getCoreInstance(String core)
			throws SolrServerException {
		if (instance != null) {
			LocalSolrServerRunner server = solrServers.get(core);
			if (server != null) {
				return server;
			}
			throw new SolrServerException("Core not found exception. " + core);
		}
		throw new SolrServerException("SolrServerFactory not initialized. ");
	}

	public boolean resetSolrServers() {
		if (cores != null) {
			for (String core : cores) {
				LocalSolrServerRunner localSolrServerRunner = solrServers
						.get(core);
				if (localSolrServerRunner.initLocalSolrServerRunner()) {
					solrServers.put(core, localSolrServerRunner);
				} else {
					logger.error("Unable to reset localSolrServerRunner for "
							+ core + ".");
				}
				return true;
			}
		}
		return false;
	}

	public boolean resetSolrServer(String core) {
		if (instance != null) {
			if (solrServers.containsKey(core)) {
				LocalSolrServerRunner localSolrServerRunner = solrServers
						.get(core);
				if (localSolrServerRunner.initLocalSolrServerRunner()) {
					solrServers.put(core, localSolrServerRunner);
				} else {
					logger.error("Unable to reset localSolrServerRunner for "
							+ core + ".");
					return false;
				}

			}
			return false;
		}

		return false;
	}

	public boolean shutdown() {
		if (instance != null) {
			for (String core : cores) {
				if (solrServers.containsKey(core)) {
					try {
						logger.info("Shutting down core = "
								+ solrServers.get(core) + ".");
						solrServers.get(core).shutdown();
					} catch (Exception e) {
						logger.error("Error in shutting down core = "
								+ solrServers.get(core) + ".");
					}
				}
			}
			return true;
		}

		return false;
	}

	public List<String> getCores() {
		return cores;
	}

	public void setCores(List<String> cores) {
		this.cores = cores;
	}

}
