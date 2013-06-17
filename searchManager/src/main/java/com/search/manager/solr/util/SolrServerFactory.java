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

	private Map<String, LocalSolrServerRunner> solrServers;
	private List<String> cores;

	@SuppressWarnings("unused")
	private SolrServerFactory() {
		// do nothing...
	}

	public SolrServerFactory(List<LocalSolrServerRunner> localSolrServerRunners) {
		if (localSolrServerRunners != null) {
			solrServers = new HashMap<String, LocalSolrServerRunner>();
			cores = new ArrayList<String>();

			for (LocalSolrServerRunner localSolrServerRunner : localSolrServerRunners) {
				this.cores.add(localSolrServerRunner.getCoreName());
				if (localSolrServerRunner.initLocalSolrServerRunner()) {
					this.solrServers.put(localSolrServerRunner.getCoreName(),
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
		if (solrServers != null) {
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
		if (solrServers != null) {
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
		}
		return false;
	}

	public boolean clearConnections() {
		if (cores != null) {
			for (String core : cores) {
				if (solrServers.containsKey(core)) {
					try {
						solrServers.get(core).clearConnections();
					} catch (Exception e) {
						logger.error("Error in clearing connection for core = "
								+ solrServers.get(core) + ".");
					}
				}
			}
			return true;
		}
		return false;
	}
	
	public boolean shutdown() {
		if (cores != null) {
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
