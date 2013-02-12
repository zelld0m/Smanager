package com.search.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.sp.DAOValidation;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.solr.service.SolrService;

/**
 * Servlet implementation class ForceSolrServlet
 */
@Component
public class ForceSolrServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Autowired
	private SolrService solrService;

	@Override
	public void init() {
		WebApplicationContextUtils
				.getWebApplicationContext(super.getServletContext())
				.getAutowireCapableBeanFactory().autowireBean(this);
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ForceSolrServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String strStore = request.getParameter("store");
		String action = request.getParameter("action");
		String rule = request.getParameter("rule");
		String keyword = request.getParameter("keyword");
		String id = request.getParameter("id");
		String name = request.getParameter("name");
		Store store = null;
		String msg = "";
		long timeStart = System.currentTimeMillis();

		out.println("START...");

		if (StringUtils.isNotEmpty(strStore)) {
			store = new Store(strStore);
			try {
				DAOValidation.checkStoreId(store);
			} catch (DaoException e) {
				store = null;
			}
		}

		if (store != null && StringUtils.isNotEmpty(action)
				&& StringUtils.isNotEmpty(rule)) {
			if (action.equalsIgnoreCase("loadByStore")) {
				out.println("Calling method: loadBy(Store)");
				msg = loadByStore(rule, store);
			} else if (action.equalsIgnoreCase("loadByStoreKeyword")) {
				out.println("Calling method: loadByStoreKeyword(StoreKeyword)");
				try {
					StoreKeyword storeKeyword = new StoreKeyword(strStore,
							keyword);
					DAOValidation.checkStoreKeywordPK(storeKeyword);
					msg = loadByStoreKeyword(rule, storeKeyword);
				} catch (DaoException e) {
					msg = "Invalid paramter!";
					msg += "\nError: Invalid 'store' or 'keyword' parameter.";
				}
			} else if (action.equalsIgnoreCase("loadByName")) {
				out.println("Calling method: loadByName(Store, RuleName)");
				if (StringUtils.isNotEmpty(name)) {
					msg = loadByName(rule, store, name);
				} else {
					msg = "Invalid paramter!";
					msg += "\nError: You need to specify 'name=' parameter.";
				}
			} else if (action.equalsIgnoreCase("loadById")) {
				out.println("Calling method: loadById(Store, RuleId)");
				if (StringUtils.isNotEmpty(id)) {
					msg = loadById(rule, store, id);
				} else {
					msg = "Invalid paramter!";
					msg += "\nError: You need to specify 'id=' parameter.";
				}
			} else if (action.equalsIgnoreCase("resetByStore")) {
				out.println("Calling method: resetByStore(Store)");
				msg = resetByStore(rule, store);
			} else if (action.equalsIgnoreCase("resetByStoreKeyword")) {
				out.println("Calling method: reset(StoreKeyword)");
				try {
					StoreKeyword storeKeyword = new StoreKeyword(strStore,
							keyword);
					DAOValidation.checkStoreKeywordPK(storeKeyword);
					msg = resetByStoreKeyword(rule, storeKeyword);
				} catch (DaoException e) {
					msg = "Invalid paramter!";
					msg += "\nError: Invalid 'store' or 'keyword' parameter.";
				}
			} else if (action.equalsIgnoreCase("resetById")) {
				out.println("Calling method: resetById(Store, RuleId)");
				if (StringUtils.isNotEmpty(id)) {
					msg = resetById(rule, store, id);
				} else {
					msg = "Invalid paramter!";
					msg += "\nError: You need to specify 'id=' parameter.";
				}
			} else if (action.equalsIgnoreCase("resetByName")) {
				out.println("Calling method: resetByName(Store, RuleName)");
				if (StringUtils.isNotEmpty(name)) {
					msg = resetByName(rule, store, name);
				} else {
					msg = "Invalid paramter!";
					msg += "\nError: You need to specify 'name=' parameter.";
				}
			} else if (action.equalsIgnoreCase("deleteByStore")) {
				out.println("Calling method: deleteByStore(Store)");
				msg = deleteByStore(rule, store);
			} else if (action.equalsIgnoreCase("deleteByStoreKeyword")) {
				out.println("Calling method: delete(StoreKeyword)");
				try {
					StoreKeyword storeKeyword = new StoreKeyword(strStore,
							keyword);
					DAOValidation.checkStoreKeywordPK(storeKeyword);
					msg = deleteByStoreKeyword(rule, storeKeyword);
				} catch (DaoException e) {
					msg = "Invalid paramter!";
				}
			} else if (action.equalsIgnoreCase("deleteById")) {
				out.println("Calling method: deleteById(Store, RuleId)");
				if (StringUtils.isNotEmpty(id)) {
					msg = deleteById(rule, store, id);
				} else {
					msg = "Invalid paramter!";
					msg += "\nError: You need to specify 'id=' parameter.";
				}
			} else if (action.equalsIgnoreCase("deleteByName")) {
				out.println("Calling method: deleteByName(Store, RuleName)");
				if (StringUtils.isNotEmpty(name)) {
					msg = deleteByName(rule, store, name);
				} else {
					msg = "Invalid paramter!";
					msg += "\nError: You need to specify 'name=' parameter.";
				}
			} else if (action.equalsIgnoreCase("commit")) {
				out.println("Calling method: commit()");
				msg = commit(rule, store);
			} else {
				msg = "Invalid paramter!";
				if (StringUtils.isNotEmpty(action)) {
					msg += "\nError: Invalid 'action=' value.";
				} else {
					msg += "\nError: You need to specify 'action=' parameter.";
				}
			}

			long elapsedTimeMillis = System.currentTimeMillis() - timeStart;

			out.println(msg);
			out.println("Done in: " + (elapsedTimeMillis / (1000F)) + " secs./"
					+ (elapsedTimeMillis / (60 * 1000F)) + " mins.");
		} else {
			out.println("Invalid paramter!");
		}

		out.println("\nEND");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/* LOAD */
	private String loadByStore(String rule, Store store) {
		String msg = "";
		String status = "";

		if (rule.equalsIgnoreCase("demote")) {
			try {
				status = "" + solrService.loadDemoteRules(store);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("elevate")) {
			try {
				status = "" + solrService.loadElevateRules(store);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("exclude")) {
			try {
				status = "" + solrService.loadExcludeRules(store);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("facetsort")) {
			try {
				status = "" + solrService.loadFacetSortRules(store);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("redirect")) {
			try {
				status = "" + solrService.loadRedirectRules(store);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("relevancy")) {
			try {
				status = "" + solrService.loadRelevancyRules(store);
			} catch (Exception e) {
				status = "error";
			}
		}

		if (status.equals("error")) {
			msg = "Error loading " + rule.toUpperCase() + " rules for store["
					+ store.getStoreId() + "].";
		} else if (status.equals("true")) {
			msg = rule.toUpperCase() + " rules successfully loaded: store["
					+ store.getStoreId() + "].";
		} else {
			msg = "Unable to load " + rule.toUpperCase() + " rules for store["
					+ store.getStoreId() + "].";
		}

		return msg;
	}

	private String loadByStoreKeyword(String rule, StoreKeyword storeKeyword) {
		String msg = "";
		String status = "";

		if (rule.equalsIgnoreCase("demote")) {
			try {
				status = "" + solrService.loadDemoteRules(storeKeyword);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("elevate")) {
			try {
				status = "" + solrService.loadElevateRules(storeKeyword);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("exclude")) {
			try {
				status = "" + solrService.loadExcludeRules(storeKeyword);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("facetsort")) {
			try {
				status = "unimplemented";
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("redirect")) {
			try {
				status = "unimplemented";
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("relevancy")) {
			try {
				status = "unimplemented";
			} catch (Exception e) {
				status = "error";
			}
		}

		if (status.equals("error")) {
			msg = "Error loading " + rule.toUpperCase() + " rules for store["
					+ storeKeyword.getStoreId() + "] keyword["
					+ storeKeyword.getKeywordId() + "].";
		} else if (status.equals("unimplemented")) {
			msg = "Unimplemented method for " + rule.toUpperCase() + " rule.";
		} else if (status.equals("true")) {
			msg = rule.toUpperCase() + " rules successfully loaded: store["
					+ storeKeyword.getStoreId() + "] keyword["
					+ storeKeyword.getKeywordId() + "].";
		} else {
			msg = "Unable to load " + rule.toUpperCase() + " rules for store["
					+ storeKeyword.getStoreId() + "] keyword["
					+ storeKeyword.getKeywordId() + "].";
		}

		return msg;
	}

	private String loadByName(String rule, Store store, String name) {
		String msg = "";
		String status = "";

		if (rule.equalsIgnoreCase("demote")) {
			try {
				status = "unimplemented";
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("elevate")) {
			try {
				status = "unimplemented";
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("exclude")) {
			try {
				status = "unimplemented";
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("facetsort")) {
			try {
				status = "" + solrService.loadFacetSortRuleByName(store, name);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("redirect")) {
			try {
				status = "" + solrService.loadRedirectRuleByName(store, name);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("relevancy")) {
			try {
				status = "" + solrService.loadRelevancyRuleByName(store, name);
			} catch (Exception e) {
				status = "error";
			}
		}

		if (status.equals("error")) {
			msg = "Error loading " + rule.toUpperCase() + " rule for store["
					+ store.getStoreId() + "] name[" + name + "].";
		} else if (status.equals("unimplemented")) {
			msg = "Unimplemented method for " + rule.toUpperCase() + " rule.";
		} else if (status.equals("true")) {
			msg = rule.toUpperCase() + " rule successfully loaded: store["
					+ store.getStoreId() + "] name[" + name + "].";
		} else {
			msg = "Unable to load " + rule.toUpperCase() + " rule for store["
					+ store.getStoreId() + "] name[" + name + "].";
			;
		}

		return msg;
	}

	private String loadById(String rule, Store store, String id) {
		String msg = "";
		String status = "";

		if (rule.equalsIgnoreCase("demote")) {
			try {
				status = "unimplemented";
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("elevate")) {
			try {
				status = "unimplemented";
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("exclude")) {
			try {
				status = "unimplemented";
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("facetsort")) {
			try {
				status = "" + solrService.loadFacetSortRuleById(store, id);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("redirect")) {
			try {
				status = "" + solrService.loadRedirectRuleById(store, id);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("relevancy")) {
			try {
				status = "" + solrService.loadRelevancyRuleById(store, id);
			} catch (Exception e) {
				status = "error";
			}
		}

		if (status.equals("error")) {
			msg = "Error loading " + rule.toUpperCase() + " rule for store["
					+ store.getStoreId() + "] id[" + id + "].";
		} else if (status.equals("unimplemented")) {
			msg = "Unimplemented method for " + rule.toUpperCase() + " rule.";
		} else if (status.equals("true")) {
			msg = rule.toUpperCase() + " rule successfully loaded: store["
					+ store.getStoreId() + "] id[" + id + "].";
		} else {
			msg = "Unable to load " + rule.toUpperCase() + " rule for store["
					+ store.getStoreId() + "] id[" + id + "].";
		}

		return msg;
	}

	/* RESET */
	private String resetByStore(String rule, Store store) {
		String msg = "";
		String status = "";

		if (rule.equalsIgnoreCase("demote")) {
			try {
				status = "" + solrService.resetDemoteRules(store);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("elevate")) {
			try {
				status = "" + solrService.resetElevateRules(store);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("exclude")) {
			try {
				status = "" + solrService.resetExcludeRules(store);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("facetsort")) {
			try {
				status = "" + solrService.resetFacetSortRules(store);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("redirect")) {
			try {
				status = "" + solrService.resetRedirectRules(store);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("relevancy")) {
			try {
				status = "" + solrService.resetRelevancyRules(store);
			} catch (Exception e) {
				status = "error";
			}
		}

		if (status.equals("error")) {
			msg = "Error resetting " + rule.toUpperCase() + " rules for store["
					+ store.getStoreId().toUpperCase() + "].";
		} else if (status.equals("true")) {
			msg = rule.toUpperCase() + " rules successfully reset: store["
					+ store.getStoreId().toUpperCase() + "].";
		} else {
			msg = "Unable to reset " + rule.toUpperCase() + " rule for store["
					+ store.getStoreId().toUpperCase() + "].";
		}

		return msg;
	}

	private String resetByStoreKeyword(String rule, StoreKeyword storeKeyword) {
		String msg = "";
		String status = "";

		if (rule.equalsIgnoreCase("demote")) {
			try {
				status = "" + solrService.resetDemoteRules(storeKeyword);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("elevate")) {
			try {
				status = "" + solrService.resetElevateRules(storeKeyword);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("exclude")) {
			try {
				status = "" + solrService.resetExcludeRules(storeKeyword);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("facetsort")) {
			try {
				status = "unimplemented";
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("redirect")) {
			try {
				status = "unimplemented";
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("relevancy")) {
			try {
				status = "";
			} catch (Exception e) {
				status = "error";
			}
		}

		if (status.equals("error")) {
			msg = "Error resetting " + rule.toUpperCase() + " rules for store["
					+ storeKeyword.getStoreId() + "] keyword["
					+ storeKeyword.getKeywordId() + "].";
		} else if (status.equals("unimplemented")) {
			msg = "Unimplemented method for " + rule.toUpperCase() + " rule.";
		} else if (status.equals("true")) {
			msg = rule.toUpperCase() + " rules successfully reset: store["
					+ storeKeyword.getStoreId() + "] keyword["
					+ storeKeyword.getKeywordId() + "].";
		} else {
			msg = "Unable to reset " + rule.toUpperCase() + " rules for store["
					+ storeKeyword.getStoreId() + "] keyword["
					+ storeKeyword.getKeywordId() + "].";
		}

		return msg;
	}

	private String resetByName(String rule, Store store, String name) {
		String msg = "";
		String status = "";

		if (rule.equalsIgnoreCase("demote")) {
			try {
				status = "unimplemented";
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("elevate")) {
			try {
				status = "unimplemented";
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("exclude")) {
			try {
				status = "unimplemented";
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("facetsort")) {
			try {
				status = "" + solrService.resetFacetSortRuleByName(store, name);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("redirect")) {
			try {
				status = "" + solrService.resetRedirectRuleByName(store, name);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("relevancy")) {
			try {
				status = "" + solrService.resetRelevancyRuleByName(store, name);
			} catch (Exception e) {
				status = "error";
			}
		}

		if (status.equals("error")) {
			msg = "Error resetting " + rule.toUpperCase() + " rule for store["
					+ store.getStoreId() + "] name[" + name + "].";
		} else if (status.equals("unimplemented")) {
			msg = "Unimplemented method for " + rule.toUpperCase() + " rule.";
		} else if (status.equals("true")) {
			msg = rule.toUpperCase() + " rule successfully reset: store["
					+ store.getStoreId() + "] name[" + name + "].";
		} else {
			msg = "Unable to reset " + rule.toUpperCase() + " rule for store["
					+ store.getStoreId() + "] name[" + name + "].";
		}

		return msg;
	}

	private String resetById(String rule, Store store, String id) {
		String msg = "";
		String status = "";

		if (rule.equalsIgnoreCase("demote")) {
			try {
				status = "unimplemented";
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("elevate")) {
			try {
				status = "unimplemented";
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("exclude")) {
			try {
				status = "";
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("facetsort")) {
			try {
				status = "" + solrService.resetFacetSortRuleById(store, id);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("redirect")) {
			try {
				status = "" + solrService.resetRedirectRuleById(store, id);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("relevancy")) {
			try {
				status = "" + solrService.resetRelevancyRuleById(store, id);
			} catch (Exception e) {
				status = "error";
			}
		}

		if (status.equals("error")) {
			msg = "Error resetting " + rule.toUpperCase() + " rule for store["
					+ store.getStoreId() + "] id[" + id + "].";
		} else if (status.equals("unimplemented")) {
			msg = "Unimplemented method for " + rule.toUpperCase() + " rule.";
		} else if (status.equals("true")) {
			msg = rule.toUpperCase() + " rule successfully reset: store["
					+ store.getStoreId() + "] id[" + id + "].";
		} else {
			msg = "Unable to reset " + rule.toUpperCase() + " rule for store["
					+ store.getStoreId() + "] id[" + id + "].";
		}

		return msg;
	}

	/* DELETE */
	private String deleteByStore(String rule, Store store) {
		String msg = "";
		String status = "";

		if (rule.equalsIgnoreCase("demote")) {
			try {
				status = "" + solrService.deleteDemoteRules(store);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("elevate")) {
			try {
				status = "" + solrService.deleteElevateRules(store);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("exclude")) {
			try {
				status = "" + solrService.deleteExcludeRules(store);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("facetsort")) {
			try {
				status = "" + solrService.deleteFacetSortRules(store);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("redirect")) {
			try {
				status = "" + solrService.deleteRedirectRules(store);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("relevancy")) {
			try {
				status = "" + solrService.deleteRelevancyRules(store);
			} catch (Exception e) {
				status = "error";
			}
		}

		if (status.equals("error")) {
			msg = "Error deleting " + rule.toUpperCase() + " rule for store["
					+ store.getStoreId().toUpperCase() + "].";
		} else if (status.equals("true")) {
			msg = rule.toUpperCase() + " rule successfully deleted: store["
					+ store.getStoreId().toUpperCase() + "].";
		} else {
			msg = "Unable to delete " + rule.toUpperCase() + " rule: store["
					+ store.getStoreId().toUpperCase() + "].";
		}

		return msg;
	}

	private String deleteByStoreKeyword(String rule, StoreKeyword storeKeyword) {
		String msg = "";
		String status = "";

		if (rule.equalsIgnoreCase("demote")) {
			try {
				status = "" + solrService.deleteDemoteRules(storeKeyword);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("elevate")) {
			try {
				status = "" + solrService.deleteElevateRules(storeKeyword);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("exclude")) {
			try {
				status = "" + solrService.deleteExcludeRules(storeKeyword);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("facetsort")) {
			try {
				status = "unimplemented";
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("redirect")) {
			try {
				status = "unimplemented";
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("relevancy")) {
			try {
				status = "";
			} catch (Exception e) {
				status = "error";
			}
		}

		if (status.equals("error")) {
			msg = "Error deleting " + rule.toUpperCase() + " rules for store["
					+ storeKeyword.getStoreId() + "] keyword["
					+ storeKeyword.getKeywordId() + "].";
		} else if (status.equals("unimplemented")) {
			msg = "Unimplemented method for " + rule.toUpperCase() + " rule.";
		} else if (status.equals("true")) {
			msg = rule.toUpperCase() + " rules successfully deleted: store["
					+ storeKeyword.getStoreId() + "] keyword["
					+ storeKeyword.getKeywordId() + "].";
		} else {
			msg = "Unable to delete " + rule.toUpperCase()
					+ " rules for store[" + storeKeyword.getStoreId()
					+ "] keyword[" + storeKeyword.getKeywordId() + "].";
		}

		return msg;
	}

	private String deleteById(String rule, Store store, String id) {
		String msg = "";
		String status = "";

		if (rule.equalsIgnoreCase("demote")) {
			try {
				status = "unimplemented";
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("elevate")) {
			try {
				status = "unimplemented";
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("exclude")) {
			try {
				status = "unimplemented";
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("facetsort")) {
			try {
				status = "" + solrService.deleteFacetSortRuleById(store, id);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("redirect")) {
			try {
				status = "" + solrService.deleteRedirectRuleById(store, id);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("relevancy")) {
			try {
				status = "" + solrService.deleteRelevancyRuleById(store, id);
			} catch (Exception e) {
				status = "error";
			}
		}

		if (status.equals("error")) {
			msg = "Error deleting " + rule.toUpperCase() + " rule for store["
					+ store.getStoreId() + "] id[" + id + "].";
		} else if (status.equals("unimplemented")) {
			msg = "Unimplemented method for " + rule.toUpperCase() + " rule.";
		} else if (status.equals("true")) {
			msg = rule.toUpperCase() + " rule successfully deleted: store["
					+ store.getStoreId() + "] id[" + id + "].";
		} else {
			msg = "Unable to delete " + rule.toUpperCase() + " rule for store["
					+ store.getStoreId() + "] id[" + id + "].";
		}

		return msg;
	}

	private String deleteByName(String rule, Store store, String name) {
		String msg = "";
		String status = "";

		if (rule.equalsIgnoreCase("demote")) {
			try {
				status = "unimplemented";
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("elevate")) {
			try {
				status = "unimplemented";
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("exclude")) {
			try {
				status = "unimplemented";
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("facetsort")) {
			try {
				status = ""
						+ solrService.deleteFacetSortRuleByName(store, name);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("redirect")) {
			try {
				status = "" + solrService.deleteRedirectRuleByName(store, name);
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("relevancy")) {
			try {
				status = ""
						+ solrService.deleteRelevancyRuleByName(store, name);
			} catch (Exception e) {
				status = "error";
			}
		}

		if (status.equals("error")) {
			msg = "Error deleting " + rule.toUpperCase() + " rule for store["
					+ store.getStoreId() + "] name[" + name + "].";
		} else if (status.equals("unimplemented")) {
			msg = "Unimplemented method for " + rule.toUpperCase() + " rule.";
		} else if (status.equals("true")) {
			msg = rule.toUpperCase() + " rule successfully deleted: store["
					+ store.getStoreId() + "] name[" + name + "].";
		} else {
			msg = "Unable to delete " + rule.toUpperCase() + " rule for store["
					+ store.getStoreId() + "] name[" + name + "].";
		}

		return msg;
	}

	/* COMMIT */
	private String commit(String rule, Store store) {
		String msg = "";
		String status = "";

		if (rule.equalsIgnoreCase("demote")) {
			try {
				status = "" + solrService.commitDemoteRule();
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("elevate")) {
			try {
				status = "" + solrService.commitElevateRule();
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("exclude")) {
			try {
				status = "" + solrService.commitExcludeRule();
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("facetsort")) {
			try {
				status = "" + solrService.commitFacetSortRule();
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("redirect")) {
			try {
				status = "" + solrService.commitRedirectRule();
			} catch (Exception e) {
				status = "error";
			}
		} else if (rule.equalsIgnoreCase("relevancy")) {
			try {
				status = "" + solrService.commitRelevancyRule();
			} catch (Exception e) {
				status = "error";
			}
		}

		if (status.equals("error")) {
			msg = "Error commiting " + rule.toUpperCase() + " rules for store["
					+ store.getStoreId() + "].";
		} else if (status.equals("true")) {
			msg = rule.toUpperCase() + " rules successfully commited: store["
					+ store.getStoreId() + "].";
		} else {
			msg = "Unable to commit " + rule.toUpperCase()
					+ " rules for store[" + store.getStoreId() + "].";
		}

		return msg;
	}

}
