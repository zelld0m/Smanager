package com.search.ws;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.enums.SortType;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.FacetEntry;
import com.search.manager.model.SearchResult;

public class SolrXmlResponseParser extends SolrResponseParser {

	// TODO: create a threadpool for this?
	private static final Logger logger =
			LoggerFactory.getLogger(SolrXmlResponseParser.class);
	
	// Belongs to initial Solr XML response which will be used to generate the HTTP Response
	private Document mainDoc = null;
	private Node resultNode = null;
	private Node explainNode = null;
	private Node qtimeNode = null;
	private Node responseHeaderParamsNode = null;
	private Node placeHolderNode = null;
	private Node facetFieldsNode = null;
	private List<Node> demotedEntries = new ArrayList<Node>();
	private List<Node> elevatedEntries = new ArrayList<Node>();
	private Node spellcheckNode = null;
	private List<Node> spellCheckParams = new ArrayList<Node>();
	
	private static Node locateElementNode(Node startingNode, String nodeName) throws SearchException {
		return locateElementNode(startingNode, nodeName, null);
	}

	private static Node locateElementNode(Node startingNode, String nodeName, String attributeNameValue) throws SearchException {
		Node currentNode = null;
		Node atributeNode = null;

		if (startingNode == null) {
			return null;
		}

		NodeList children = startingNode.getChildNodes();
		for (int i = 0, size = children.getLength(); i < size; i++) {
			currentNode = children.item(i);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE && nodeName.equalsIgnoreCase(currentNode.getNodeName())) {
				if (attributeNameValue == null) {
					return currentNode;
				} else {
					atributeNode = currentNode.getAttributes().getNamedItem(SolrConstants.ATTR_NAME);
					if (atributeNode != null && attributeNameValue.equalsIgnoreCase(atributeNode.getNodeValue())) {
						return currentNode;
					}
				}
			}
		}
		return null;
	}

	@Override
	public int getCount(List<NameValuePair> requestParams) throws SearchException {
		int numFound = -1;
		HttpClient client = null;
		HttpPost post = null;
		HttpResponse solrResponse = null;
		InputStream in = null;

		try {
			client = new DefaultHttpClient();
			post = new HttpPost(requestPath);
			post.setEntity(new UrlEncodedFormEntity(requestParams, "UTF-8"));
			post.addHeader("Connection", "close");
			if (logger.isDebugEnabled()) {
				logger.debug("URL: {} Parameter: {}", post.getURI(), requestParams);
			}
			solrResponse = client.execute(post);
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			in = solrResponse.getEntity().getContent();
			Document elevateDoc = docBuilder.parse(in);
			// locate the result node and get the numFound attribute
			numFound = Integer.parseInt(elevateDoc.getElementsByTagName(SolrConstants.TAG_RESULT).item(0)
					.getAttributes().getNamedItem(SolrConstants.ATTR_NUM_FOUND).getNodeValue());
		} catch (Exception e) {
			String error = "Error occured while trying to get number of items";
			logSolrError(post, error, e);
			throw new SearchException(error, e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
			}
			if (post != null) {
				if (solrResponse != null) {
					EntityUtils.consumeQuietly(solrResponse.getEntity());
				}
				post.releaseConnection();
			}
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		}
		return numFound;
	}

	private void tagSearchResult(Document parentDocument, Node resultNode, SearchResult result) {
		Node tagNode = null;
		if (result instanceof ElevateResult) {
			tagNode = parentDocument.createElement(SolrConstants.TAG_ELEVATE);
			tagNode.appendChild(parentDocument.createTextNode(String.valueOf(((ElevateResult) result).getLocation())));
		} else if (result instanceof DemoteResult) {
			tagNode = parentDocument.createElement(SolrConstants.TAG_DEMOTE);
			tagNode.appendChild(parentDocument.createTextNode(String.valueOf(((DemoteResult) result).getLocation())));
		}
		if (tagNode != null) {
			resultNode.appendChild(tagNode);
		}
	}

	@Override
	protected int getFacet(List<NameValuePair> requestParams, SearchResult facet) throws SearchException {
		int addedRecords = 0;
		HttpClient client = null;
		HttpPost post = null;
		InputStream in = null;
		HttpResponse solrResponse = null;

		try {
			client = new DefaultHttpClient();
			post = new HttpPost(requestPath);
			post.setEntity(new UrlEncodedFormEntity(requestParams, "UTF-8"));
			post.addHeader("Connection", "close");
			if (logger.isDebugEnabled()) {
				logger.debug("URL: " + post.getURI());
				logger.debug("Parameter: " + requestParams);
			}
			solrResponse = client.execute(post);
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			in = solrResponse.getEntity().getContent();
			Document currentDoc = docBuilder.parse(in);

			// <result> tag that is parent node for all the <doc> tags
			Node tmpResultNode = locateElementNode(locateElementNode(currentDoc, SolrConstants.TAG_RESPONSE),
					SolrConstants.TAG_RESULT, SolrConstants.ATTR_NAME_VALUE_RESPONSE);

			Node currentExplainNode = locateElementNode(locateElementNode(locateElementNode(currentDoc, SolrConstants.TAG_RESPONSE),
					SolrConstants.TAG_LIST, SolrConstants.ATTR_NAME_VALUE_DEBUG),
					SolrConstants.TAG_LIST, SolrConstants.ATTR_NAME_VALUE_EXPLAIN);

			NodeList children = currentDoc.getElementsByTagName(SolrConstants.TAG_DOC);

			for (int j = 0, length = children.getLength(); j < length; j++) {
				Node docNode = children.item(j);
				if (docNode.getParentNode() == tmpResultNode) {
					// get the EDPs
					NodeList docNodes = docNode.getChildNodes();
					for (int k = 0, kSize = docNodes.getLength(); k < kSize; k++) {
						Node kNode = docNodes.item(k);
						if (kNode.getNodeName().equalsIgnoreCase(SolrConstants.TAG_INT)
								&& kNode.getAttributes().getNamedItem(SolrConstants.ATTR_NAME).getNodeValue()
								.equalsIgnoreCase(SolrConstants.ATTR_NAME_VALUE_EDP)) {
							String edp = kNode.getTextContent();
							tagSearchResult(currentDoc, docNode, facet);
							if (!includeEDP) {
								docNode.removeChild(kNode);
							}
							if (facet instanceof ElevateResult) {
								elevatedEntries.add(mainDoc.importNode(docNode, true));
							} else if (facet instanceof DemoteResult) {
								demotedEntries.add(mainDoc.importNode(docNode, true));
							}
							addedRecords++;
							if (explainNode != null) {
								explainNode.appendChild(mainDoc.importNode(locateElementNode(currentExplainNode, SolrConstants.TAG_STR, edp), true));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			String error = "Error occured while trying to get items";
			logSolrError(post, error, e);
			throw new SearchException(error, e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
			}
			if (post != null) {
				if (solrResponse != null) {
					EntityUtils.consumeQuietly(solrResponse.getEntity());
				}
				post.releaseConnection();
			}
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		}
		return addedRecords;
	}

	@Override
	protected int getEdps(List<NameValuePair> requestParams, List<? extends SearchResult> edpList, int startRow, int requestedRows) throws SearchException {
		int addedRecords = 0;
		HttpClient client = null;
		HttpPost post = null;
		HttpResponse solrResponse = null;
		InputStream in = null;

		try {
			client = new DefaultHttpClient();
			post = new HttpPost(requestPath);
			post.setEntity(new UrlEncodedFormEntity(requestParams, "UTF-8"));
			post.addHeader("Connection", "close");
			if (logger.isDebugEnabled()) {
				logger.debug("URL: " + post.getURI());
				logger.debug("Parameter: " + requestParams);
			}
			solrResponse = client.execute(post);
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			in = solrResponse.getEntity().getContent();
			Document currentDocument = docBuilder.parse(in);
			// <result> tag that is parent node for all the <doc> tags
			Node tmpResultNode = locateElementNode(locateElementNode(currentDocument, SolrConstants.TAG_RESPONSE),
					SolrConstants.TAG_RESULT, SolrConstants.ATTR_NAME_VALUE_RESPONSE);

			NodeList children = currentDocument.getElementsByTagName(SolrConstants.TAG_DOC);
			Map<String, Node> resultDocuments = new HashMap<String, Node>();

			for (int j = 0, length = children.getLength(); j < length; j++) {
				Node docNode = children.item(j);
				if (docNode.getParentNode() == tmpResultNode) {
					// get the EDPs
					NodeList docNodes = docNode.getChildNodes();
					for (int k = 0, kSize = docNodes.getLength(); k < kSize; k++) {
						Node kNode = docNodes.item(k);
						if (kNode.getNodeName().equalsIgnoreCase(SolrConstants.TAG_INT)
								&& kNode.getAttributes().getNamedItem(SolrConstants.ATTR_NAME).getNodeValue()
								.equalsIgnoreCase(SolrConstants.ATTR_NAME_VALUE_EDP)) {
							String edp = kNode.getTextContent();
							if (!includeEDP) {
								docNode.removeChild(kNode);
							}
							resultDocuments.put(edp, docNode);
							break;
						}
					}
				}
			}

			Node currentExplainNode = locateElementNode(locateElementNode(locateElementNode(currentDocument, SolrConstants.TAG_RESPONSE),
					SolrConstants.TAG_LIST, SolrConstants.ATTR_NAME_VALUE_DEBUG),
					SolrConstants.TAG_LIST, SolrConstants.ATTR_NAME_VALUE_EXPLAIN);

			// sort the edps
			int currRow = 0;
			for (SearchResult result : edpList) {
				String edp = result.getEdp();
				Node node = resultDocuments.get(edp);
				if (node != null) {
					if (currRow++ < startRow) {
						continue;
					}
					if (addedRecords + 1 > requestedRows) {
						break;
					}
					addedRecords++;
					tagSearchResult(currentDocument, node, result);
					if (result instanceof ElevateResult) {
						elevatedEntries.add(mainDoc.importNode(node, true));
					} else if (result instanceof DemoteResult) {
						demotedEntries.add(mainDoc.importNode(node, true));
					}
					if (explainNode != null) {
						explainNode.appendChild(mainDoc.importNode(locateElementNode(currentExplainNode, SolrConstants.TAG_STR, edp), true));
					}
				}
			}
		} catch (Exception e) {
			String error = "Error occured while trying to get items";
			logSolrError(post, error, e);
			throw new SearchException(error, e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
			}
			if (post != null) {
				if (solrResponse != null) {
					EntityUtils.consumeQuietly(solrResponse.getEntity());
				}
				post.releaseConnection();
			}
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		}
		return addedRecords;
	}

	@Override
	public int getNonElevatedItems(List<NameValuePair> requestParams) throws SearchException {
		int addedRecords = 0;
		HttpClient client = null;
		HttpPost post = null;
		HttpResponse solrResponse = null;
		InputStream in = null;

		try {
			client = new DefaultHttpClient();
			post = new HttpPost(requestPath);
			post.setEntity(new UrlEncodedFormEntity(requestParams, "UTF-8"));
			post.addHeader("Connection", "close");
			if (logger.isDebugEnabled()) {
				logger.debug("URL: " + post.getURI());
				logger.debug("Parameter: " + requestParams);
			}
			solrResponse = client.execute(post);
			in = solrResponse.getEntity().getContent();
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document elevateDoc = docBuilder.parse(in);
			// locate the result node and reference it <result name="response" maxScore="23.015398" start="0" numFound="360207">
			// results will be added here
			NodeList children = elevateDoc.getChildNodes().item(0).getChildNodes();
			Node resultElevateNode = locateElementNode(locateElementNode(locateElementNode(
					elevateDoc, SolrConstants.TAG_RESPONSE),
					SolrConstants.TAG_LIST, SolrConstants.ATTR_NAME_VALUE_DEBUG),
					SolrConstants.TAG_LIST, SolrConstants.ATTR_NAME_VALUE_EXPLAIN);

			for (int i = 0, size = children.getLength(); i < size; i++) {
				Node resulNode = children.item(i);
				// grab the result node (for the scoring) 	<result name="response" maxScore="23.015398" start="0" numFound="5">
				if (resulNode.getNodeName().equalsIgnoreCase(SolrConstants.TAG_RESULT)) {
					// grab all the doc nodes			    <doc>
					children = resulNode.getChildNodes();
					for (int j = 0, length = children.getLength(); j < length; j++) {
						addedRecords++;
						Node docNode = children.item(j);
						if (docNode.getNodeName().equalsIgnoreCase(SolrConstants.TAG_DOC)) {
							Node edpNode = locateElementNode(docNode, SolrConstants.TAG_INT, SolrConstants.ATTR_NAME_VALUE_EDP);
							String edp = edpNode.getTextContent();
							if (expiredElevatedEDPs.contains(edp)) {
								Node expiredNode = elevateDoc.createElement(SolrConstants.TAG_ELEVATE_EXPIRED);
								docNode.appendChild(expiredNode);
							}
							if (expiredDemotedEDPs.contains(edp)) {
								Node expiredNode = elevateDoc.createElement(SolrConstants.TAG_DEMOTE_EXPIRED);
								docNode.appendChild(expiredNode);
							}
							if (!includeEDP) {
								docNode.removeChild(edpNode);
							}
							resultNode.appendChild(mainDoc.importNode(docNode, true));
							if (explainNode != null) {
								explainNode.appendChild(mainDoc.importNode(locateElementNode(resultElevateNode, SolrConstants.TAG_STR, edp), true));
							}
						}
					}
					break;
				}
			}
		} catch (Exception e) {
			String error = "Error occured while trying to get Non-Elevated items";
			logSolrError(post, error, e);
			throw new SearchException(error, e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
			}
			if (post != null) {
				if (solrResponse != null) {
					EntityUtils.consumeQuietly(solrResponse.getEntity());
				}
				post.releaseConnection();
			}
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		}
		return addedRecords;
	}

	@Override
	public int getTemplateCounts(List<NameValuePair> requestParams) throws SearchException {
		int numFound = -1;
		HttpClient client = null;
		HttpPost post = null;
		HttpResponse solrResponse = null;
		InputStream in = null;

		try {
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			client = new DefaultHttpClient();
			post = new HttpPost(requestPath);
			post.setEntity(new UrlEncodedFormEntity(requestParams, "UTF-8"));
			post.addHeader("Connection", "close");
			if (logger.isDebugEnabled()) {
				logger.debug("URL: " + post.getURI());
				logger.debug("Parameter: " + requestParams);
			}
			solrResponse = client.execute(post);
			in = solrResponse.getEntity().getContent();
			mainDoc = docBuilder.parse(in);
			// locate the result node and reference it <result name="response" maxScore="23.015398" start="0" numFound="360207">
			// results will be added here
			resultNode = locateElementNode(locateElementNode(mainDoc, SolrConstants.TAG_RESPONSE),
					SolrConstants.TAG_RESULT, SolrConstants.ATTR_NAME_VALUE_RESPONSE);
			explainNode = locateElementNode(locateElementNode(locateElementNode(mainDoc, SolrConstants.TAG_RESPONSE),
					SolrConstants.TAG_LIST, SolrConstants.ATTR_NAME_VALUE_DEBUG),
					SolrConstants.TAG_LIST, SolrConstants.ATTR_NAME_VALUE_EXPLAIN);
			facetFieldsNode = locateElementNode(locateElementNode(locateElementNode(mainDoc, SolrConstants.TAG_RESPONSE),
					SolrConstants.TAG_LIST, SolrConstants.TAG_FACET_COUNTS), SolrConstants.TAG_LIST, SolrConstants.TAG_FACET_FIELDS);

			// insert a temp node, that will separate elevated items from non-elevated items
			placeHolderNode = mainDoc.createElement("tmp-node");
			resultNode.appendChild(placeHolderNode);

			resultNode.getAttributes().getNamedItem(SolrConstants.SOLR_PARAM_START).setNodeValue(String.valueOf(startRow));
			if (resultNode == null) {
				throw new RuntimeException("Solr returned malformed response");
			}
			// number of records
			numFound = Integer.parseInt(resultNode.getAttributes().getNamedItem(SolrConstants.ATTR_NUM_FOUND).getNodeValue());
			// put back rows in header
			Node responseHeaderNode = locateElementNode(locateElementNode(
					mainDoc, SolrConstants.TAG_RESPONSE),
					SolrConstants.TAG_LIST, SolrConstants.ATTR_NAME_VALUE_RESPONSE_HEADER);

			if (redirectRule != null) {
				Node redirectNode = null;
				Node origKeywordNode = mainDoc.createElement(SolrConstants.TAG_REDIRECT_ORIGINAL_KEYWORD);
				Node replacementKeywordNode = mainDoc.createElement(SolrConstants.TAG_REDIRECT_REPLACEMENT_KEYWORD);
				
				if (redirectRule.isRedirectChangeKeyword()) {
					redirectNode = mainDoc.createElement(SolrConstants.TAG_REDIRECT);
					Node replacementTypeNode = mainDoc.createElement(SolrConstants.TAG_REDIRECT_REPLACEMENT_TYPE);
					Node customTextNode = mainDoc.createElement(SolrConstants.TAG_REDIRECT_CUSTOM_TEXT);	
					
					if (redirectRule.getReplaceKeywordMessageType() != null) {
						replacementTypeNode.appendChild(mainDoc.createTextNode(redirectRule.getReplaceKeywordMessageType() + ""));
						redirectNode.appendChild(replacementTypeNode);
					}
					if (StringUtils.isNotBlank(redirectRule.getReplaceKeywordMessageCustomText())) {
						customTextNode.appendChild(mainDoc.createTextNode(redirectRule.getReplaceKeywordMessageCustomText()));
						redirectNode.appendChild(customTextNode);
					}
				} else if (redirectRule.isRedirectToPage()) {
					redirectNode = mainDoc.createElement(SolrConstants.TAG_REDIRECT_DIRECT_HIT);
					Node redirectUrlNode = mainDoc.createElement(SolrConstants.TAG_REDIRECT_REDIRECT_URL);
					
					if (StringUtils.isNotBlank(redirectRule.getRedirectUrl())) {
						redirectUrlNode.appendChild(mainDoc.createTextNode(redirectRule.getRedirectUrl()));
						redirectNode.appendChild(redirectUrlNode);
					}
				} else if (redirectRule.isRedirectFilter()) { // not used
					redirectNode = mainDoc.createElement(SolrConstants.TAG_REDIRECT_FILTER);
					Node redirectCondition = mainDoc.createElement(SolrConstants.TAG_REDIRECT_CONDITION);

					if (StringUtils.isNotBlank(redirectRule.getCondition())) {
						redirectCondition.appendChild(mainDoc.createTextNode(redirectRule.getCondition()));
						redirectNode.appendChild(redirectCondition);
					}
				}
				
				if(redirectNode != null) {
					if (StringUtils.isNotBlank(originalKeyword)) {
						origKeywordNode.appendChild(mainDoc.createTextNode(originalKeyword));
						redirectNode.appendChild(origKeywordNode);
					}
					if (StringUtils.isNotBlank(redirectRule.getChangeKeyword())) {
						replacementKeywordNode.appendChild(mainDoc.createTextNode(redirectRule.getChangeKeyword()));
						redirectNode.appendChild(replacementKeywordNode);
					}
					responseHeaderNode.appendChild(redirectNode);
				}
			}

			if (activeRules != null) {
				Node activeRuleNode = mainDoc.createElement(SolrConstants.TAG_SEARCH_RULES);
				for (Map<String, String> rule : activeRules) {
					Node ruleNode = mainDoc.createElement(SolrConstants.TAG_RULE);
					for (String key : rule.keySet()) {
						Node ruleParamNode = mainDoc.createElement(key);
						if (StringUtils.isNotBlank(rule.get(key))) {
							ruleParamNode.appendChild(mainDoc.createTextNode(rule.get(key)));
						}
						ruleNode.appendChild(ruleParamNode);
					}
					activeRuleNode.appendChild(ruleNode);
				}
				responseHeaderNode.appendChild(activeRuleNode);
			}

			responseHeaderParamsNode = locateElementNode(responseHeaderNode,
					SolrConstants.TAG_LIST, SolrConstants.ATTR_NAME_VALUE_PARAMS);
			locateElementNode(responseHeaderParamsNode, SolrConstants.TAG_STR, SolrConstants.SOLR_PARAM_ROWS)
			.setTextContent(String.valueOf(requestedRows));
			locateElementNode(responseHeaderParamsNode, SolrConstants.TAG_STR, SolrConstants.SOLR_PARAM_START)
			.setTextContent(String.valueOf(startRow));
			qtimeNode = locateElementNode(responseHeaderNode, SolrConstants.TAG_INT, SolrConstants.ATTR_NAME_VALUE_QTIME);
		} catch (Exception e) {
			String error = "Error occured while trying to get template counts";
			logSolrError(post, error, e);
			throw new SearchException(error, e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
			}
			if (post != null) {
				if (solrResponse != null) {
					EntityUtils.consumeQuietly(solrResponse.getEntity());
				}
				post.releaseConnection();
			}
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		}
		return numFound;
	}

	@Override
	public boolean generateServletResponse(HttpServletResponse response, long totalTime) throws SearchException {
		boolean success = false;
		try {
			addBanners();
			addElevatedEntries();
			addDemotedEntries();
			addSpellcheckEntries();
			applyDefaultFacetSorting();
			applyFacetSort();
			resultNode.removeChild(placeHolderNode);
			qtimeNode.setTextContent(String.valueOf(totalTime));
			response.setContentType("text/xml;charset=UTF-8");
			DOMSource source = new DOMSource(mainDoc);
			TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(response.getOutputStream()));
			success = true;
		} catch (Exception e) {
			throw new SearchException("Error occured while trying to send Http response", e);
		}
		return success;
	}

	private void addBanners() {
		Node bannersNode = mainDoc.createElement(SolrConstants.TAG_BANNERS);
		try {
			if (CollectionUtils.isNotEmpty(bannerList)) {
				for (BannerRuleItem rule : bannerList) {
					Node ruleNode = mainDoc.createElement(SolrConstants.TAG_BANNER_RULE);

					Node memberId = mainDoc.createElement(SolrConstants.TAG_BANNER_MEMBER_ID);
					memberId.appendChild(mainDoc.createTextNode(rule.getMemberId() + ""));
					ruleNode.appendChild(memberId);

					Node size = mainDoc.createElement(SolrConstants.TAG_BANNER_SIZE);
					size.appendChild(mainDoc.createTextNode(rule.getImagePath().getSize() + ""));
					ruleNode.appendChild(size);

					Node newWindow = mainDoc.createElement(SolrConstants.TAG_BANNER_OPEN_NEW_WINDOW);
					newWindow.appendChild(mainDoc.createTextNode(rule.getOpenNewWindow() + ""));
					ruleNode.appendChild(newWindow);

					Node imageAlt = mainDoc.createElement(SolrConstants.TAG_BANNER_IMAGE_ALT);
					imageAlt.appendChild(mainDoc.createTextNode(rule.getImageAlt()));
					ruleNode.appendChild(imageAlt);

					Node linkPath = mainDoc.createElement(SolrConstants.TAG_BANNER_LINK_PATH);
					linkPath.appendChild(mainDoc.createTextNode(rule.getLinkPath()));
					ruleNode.appendChild(linkPath);

					Node imagePath = mainDoc.createElement(SolrConstants.TAG_BANNER_IMAGE_PATH);
					imagePath.appendChild(mainDoc.createTextNode(rule.getImagePath().getPath()));
					ruleNode.appendChild(imagePath);

					bannersNode.appendChild(ruleNode);
				}
			}
			locateElementNode(mainDoc, SolrConstants.TAG_RESPONSE).appendChild(bannersNode);
		} catch (Exception e) {
			logger.error("Error occured during banner creation. ", e);
		}
	}

	private void addElevatedEntries() {
		for (Node node : elevatedEntries) {
			resultNode.insertBefore(node, placeHolderNode);
		}
	}

	private void addDemotedEntries() {
		for (Node node : demotedEntries) {
			resultNode.appendChild(node);
		}
	}

	private void addSpellcheckEntries() throws SearchException {
		int count = 0;

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();
			List<String> suggestedKeywords = new ArrayList<String>();
			String[] ruleSuggestions = spellRule == null ? new String[0] : spellRule.getSuggestions();

			// create spellcheck element
			Element spellcheck = createLstElement(doc, SolrConstants.ATTR_NAME_VALUE_SPELLCHECK);
			doc.appendChild(spellcheck);

			// create suggestions element
			Element suggestions = createLstElement(doc, SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_SUGGESTIONS);
			spellcheck.appendChild(suggestions);

			// create suggestion element for original keyword
			Element origKeyword = createLstElement(doc, originalKeyword);
			Element suggestionArray = createElement(doc, SolrConstants.TAG_ARR, SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_SUGGESTION);
			Element origStartOffset = createElement(doc, SolrConstants.TAG_INT,
					SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_START_OFFSET, "0");
			Element origEndOffset = createElement(doc, SolrConstants.TAG_INT,
					SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_END_OFFSET, String.valueOf(originalKeyword.length()));

			for (String suggestion : ruleSuggestions) {
				suggestionArray.appendChild(createUnnamedElement(doc, SolrConstants.TAG_STR, suggestion));
				suggestedKeywords.add(suggestion);
				if (++count >= maxSuggestCount) {
					break;
				}
			}

			if (count < maxSuggestCount && spellcheckNode != null) {
				// retrieve solr spellcheck results
				Node solrSuggestions = locateElementNode(spellcheckNode, SolrConstants.TAG_LIST,
						SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_SUGGESTIONS);

				// prioritize collation suggestion before per term suggestion
				Node collation = solrSuggestions != null ? locateElementNode(solrSuggestions,
						SolrConstants.TAG_STR, SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_COLLATION) : null;
				String collationString = collation == null ? null : collation.getTextContent();

				if (collationString != null && !suggestedKeywords.contains(collationString)) {
					Element e = createUnnamedElement(doc, SolrConstants.TAG_STR, collationString);
					suggestionArray.appendChild(e);
					suggestedKeywords.add(collationString);
					count++;
				}

				Node solrSuggestion = solrSuggestions != null ? solrSuggestions.getFirstChild() : null;

				while (solrSuggestion != null && count < maxSuggestCount) {
					String name = solrSuggestion.getAttributes().getNamedItem(SolrConstants.ATTR_NAME)
							.getTextContent();

					if (SolrConstants.TAG_STR.equals(solrSuggestion.getNodeName())
							&& SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_COLLATION.equals(name)) {
						solrSuggestion = solrSuggestion.getNextSibling();
						continue;
					}

					Node solrkw = locateElementNode(solrSuggestion, SolrConstants.TAG_ARR,
							SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_SUGGESTION);
					NodeList children = solrkw.getChildNodes();

					for (int i = 0; i < children.getLength(); i++) {
						Node n = children.item(i);
						String s = n.getTextContent();

						if (!suggestedKeywords.contains(s)) {
							suggestionArray.appendChild(doc.importNode(n, true));
							suggestedKeywords.add(s);
							count++;
						}

						if (count >= maxSuggestCount) {
							break;
						}
					}

					solrSuggestion = solrSuggestion.getNextSibling();
				}
			}

			NodeList childNodes = suggestionArray.getChildNodes();
			if (childNodes.getLength() > 0) {
				origKeyword.appendChild(createElement(doc, SolrConstants.TAG_INT,
						SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_NUMFOUND,
						String.valueOf(childNodes.getLength())));
				origKeyword.appendChild(origStartOffset);
				origKeyword.appendChild(origEndOffset);
				origKeyword.appendChild(suggestionArray);

				suggestions.appendChild(origKeyword);
			}
			locateElementNode(mainDoc, SolrConstants.TAG_RESPONSE)
			.appendChild(mainDoc.importNode(spellcheck, true));

		} catch (ParserConfigurationException pce) {
			count = 0;
			logger.error("Error occured during spelling document creation. Reverting to solr results.", pce);
		}

		for (Node node : spellCheckParams) {
			responseHeaderParamsNode.appendChild(mainDoc.importNode(node, true));
		}
	}
	
	private void applyDefaultFacetSorting() {
		if (facetFieldsNode == null || popularFacetMap == null || popularFacetMap.size() == 0) {
			return;
		}
		
		boolean isFacetTemplate = false;
		
		NodeList children = facetFieldsNode.getChildNodes();
		for (int i = 0, size = children.getLength(); i < size; i++) {
			Node currentNode = children.item(i);
			
			for(String key : popularFacetMap.keySet()) {
				
				if (isCNETImplementation && key.endsWith("FacetTemplate")) {
					isFacetTemplate = true;
				}
				
				if (currentNode.getNodeType() == Node.ELEMENT_NODE && StringUtils.equals(key,
						currentNode.getAttributes().getNamedItem(SolrConstants.ATTR_NAME).getNodeValue())) {
					List<FacetEntry> entries = new ArrayList<FacetEntry>();
					NodeList facetFieldValues = currentNode.getChildNodes();

					Map<String, Node> nodeMap = new HashMap<String, Node>();
					for (int j = 0; j < facetFieldValues.getLength(); j++) {
						Node facetFieldValue = facetFieldValues.item(j);
						if (facetFieldValue!=null){
							NamedNodeMap attribs = facetFieldValue.getAttributes();
							if(attribs!=null){
								String name = attribs.getNamedItem(SolrConstants.ATTR_NAME).getNodeValue();
								String count = facetFieldValue.getTextContent();
								entries.add(new FacetEntry(name, Long.parseLong(count)));
								nodeMap.put(name, facetFieldValue);
							}
						}
					}

					// clear everything
					Node tmpNode = currentNode.getFirstChild();
					while (tmpNode != null) {
						currentNode.removeChild(tmpNode);
						tmpNode = currentNode.getFirstChild();
					}

					// sort
					if (isFacetTemplate) {
						FacetEntry.sortFacetTemplateEntries(entries, SortType.DEFAULT_ORDER, popularFacetMap.get(key), !"desc".equals(defaultSortOrder));
					} else {
						FacetEntry.sortEntries(entries, SortType.DEFAULT_ORDER, popularFacetMap.get(key), !"desc".equals(defaultSortOrder));
					}

					// insert everything in sorted order
					for (FacetEntry entry : entries) {
						currentNode.appendChild(mainDoc.importNode(nodeMap.get(entry.getLabel()), true));
					}
					break;
				}
			}
			
		}
		
	}

	private void applyFacetSort() {
		if (facetSortRule == null || facetFieldsNode == null) {
			return;
		}
		
		for (String key : facetSortRule.getItems().keySet()) {

			boolean isFacetTemplate = false;
			SortType sortType = facetSortRule.getGroupSortType().get(key);
			if (sortType == null) {
				sortType = facetSortRule.getSortType();
			}
			List<String> elevatedValues = facetSortRule.getItems().get(key);
//			if (StringUtils.equals("Category", key) && configManager.isMemberOf("PCM", facetSortRule.getStoreId())) {
//			key = configManager.getStoreParameter(facetSortRule.getStoreId(), SolrConstants.SOLR_PARAM_FACET_TEMPLATE);
			if (StringUtils.equals("Category", key) && isCNETImplementation) {
				key = facetTemplate;
				isFacetTemplate = true;
			}

			facetFieldsNode.getChildNodes();
			NodeList children = facetFieldsNode.getChildNodes();
			for (int i = 0, size = children.getLength(); i < size; i++) {
				Node currentNode = children.item(i);
				if (currentNode.getNodeType() == Node.ELEMENT_NODE && StringUtils.equals(key,
						currentNode.getAttributes().getNamedItem(SolrConstants.ATTR_NAME).getNodeValue())) {

					List<FacetEntry> entries = new ArrayList<FacetEntry>();
					NodeList facetFieldValues = currentNode.getChildNodes();

					Map<String, Node> nodeMap = new HashMap<String, Node>();
					for (int j = 0; j < facetFieldValues.getLength(); j++) {
						Node facetFieldValue = facetFieldValues.item(j);
						if (facetFieldValue!=null){
							NamedNodeMap attribs = facetFieldValue.getAttributes();
							if(attribs!=null){
								String name = attribs.getNamedItem(SolrConstants.ATTR_NAME).getNodeValue();
								String count = facetFieldValue.getTextContent();
								entries.add(new FacetEntry(name, Long.parseLong(count)));
								nodeMap.put(name, facetFieldValue);
							}
						}
					}

					// clear everything
					Node tmpNode = currentNode.getFirstChild();
					while (tmpNode != null) {
						currentNode.removeChild(tmpNode);
						tmpNode = currentNode.getFirstChild();
					}

					// sort
					if (isFacetTemplate) {
						FacetEntry.sortFacetTemplateEntries(entries, sortType, elevatedValues, false);
					} else {
						FacetEntry.sortEntries(entries, sortType, elevatedValues, false);
					}

					// insert everything in sorted order
					for (FacetEntry entry : entries) {
						currentNode.appendChild(mainDoc.importNode(nodeMap.get(entry.getLabel()), true));
					}
					break;
				}
			}
		}
	}

	@Override
	public String getCommonTemplateName(String templateNameField, List<NameValuePair> requestParams) throws SearchException {
		String templateName = "";
		HttpPost post = null;
		HttpClient client = null;
		HttpResponse solrResponse = null;
		InputStream in = null;

		try {
			client = new DefaultHttpClient();
			post = new HttpPost(requestPath);
			post.setEntity(new UrlEncodedFormEntity(requestParams, "UTF-8"));
			post.addHeader("Connection", "close");
			if (logger.isDebugEnabled()) {
				logger.debug("URL: " + post.getURI());
				logger.debug("Parameter: " + requestParams);
			}
			solrResponse = client.execute(post);
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			in = solrResponse.getEntity().getContent();
			Document document = docBuilder.parse(in);
			NodeList templateNames = locateElementNode(locateElementNode(locateElementNode(locateElementNode(document, SolrConstants.TAG_RESPONSE),
					SolrConstants.TAG_LIST, SolrConstants.TAG_FACET_COUNTS), SolrConstants.TAG_LIST, SolrConstants.TAG_FACET_FIELDS),
					SolrConstants.TAG_LIST, templateNameField).getChildNodes();
			if (templateNames.getLength() == 1) {
				templateName = templateNames.item(0).getAttributes().getNamedItem(SolrConstants.ATTR_NAME).getNodeValue();
			}
		} catch (Exception e) {
			String error = "Error occured while trying to get common template name";
			logSolrError(post, error, e);
			throw new SearchException(error, e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
			}
			if (post != null) {
				if (solrResponse != null) {
					EntityUtils.consumeQuietly(solrResponse.getEntity());
				}
				post.releaseConnection();
			}
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		}
		return templateName;
	}

	@Override
	public void getSpellingSuggestion(List<NameValuePair> requestParams) throws SearchException {
		HttpClient client = null;
		HttpPost post = null;
		InputStream in = null;
		HttpResponse solrResponse = null;

		try {
			client = new DefaultHttpClient();
			post = new HttpPost(getSpellCheckRequestPath());
			post.setEntity(new UrlEncodedFormEntity(requestParams, "UTF-8"));
			post.addHeader("Connection", "close");
			if (logger.isDebugEnabled()) {
				logger.debug("URL: " + post.getURI());
				logger.debug("Parameter: " + requestParams);
			}
			solrResponse = client.execute(post);
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			in = solrResponse.getEntity().getContent();
			Document currentDoc = docBuilder.parse(in);

			Node responseNode = locateElementNode(currentDoc, SolrConstants.TAG_RESPONSE);
			spellcheckNode = locateElementNode(responseNode, SolrConstants.TAG_LIST,
					SolrConstants.ATTR_NAME_VALUE_SPELLCHECK);

			NodeList paramNodes = locateElementNode(
					locateElementNode(responseNode, SolrConstants.TAG_LIST,
							SolrConstants.ATTR_NAME_VALUE_RESPONSE_HEADER), SolrConstants.TAG_LIST,
							SolrConstants.ATTR_NAME_VALUE_PARAMS).getChildNodes();
			for (int i = 0, size = paramNodes.getLength(); i < size; i++) {
				Node kNode = paramNodes.item(i);
				if (kNode.getNodeType() == Node.ELEMENT_NODE
						&& kNode.getNodeName().equalsIgnoreCase(SolrConstants.TAG_STR)
						&& kNode.getAttributes().getNamedItem(SolrConstants.ATTR_NAME).getNodeValue()
						.startsWith(SolrConstants.ATTR_NAME_VALUE_SPELLCHECK)) {
					spellCheckParams.add(kNode);
				}
			}

		} catch (Exception e) {
			String error = "Error occured while trying to get spelling suggestion";
			logSolrError(post, error, e);
			throw new SearchException(error, e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
			}
			if (post != null) {
				if (solrResponse != null) {
					EntityUtils.consumeQuietly(solrResponse.getEntity());
				}
				post.releaseConnection();
			}
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		}
	}
	
	private Element createLstElement(Document doc, String nameValue) {
		Element el = doc.createElement(SolrConstants.TAG_LIST);
		el.setAttribute(SolrConstants.ATTR_NAME, nameValue);
		return el;
	}

	private Element createElement(Document doc, String tag, String name, String value) {
		Element el = doc.createElement(tag);
		el.setAttribute(SolrConstants.ATTR_NAME, name);
		el.setTextContent(value);
		return el;
	}

	private Element createUnnamedElement(Document doc, String tag, String value) {
		Element el = doc.createElement(tag);
		el.setTextContent(value);
		return el;
	}

	private Element createElement(Document doc, String tag, String name) {
		Element el = doc.createElement(tag);
		el.setAttribute(SolrConstants.ATTR_NAME, name);
		return el;
	}
}
