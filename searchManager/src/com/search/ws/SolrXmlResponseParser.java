package com.search.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.SearchResult;
import com.search.manager.utility.SolrRequestDispatcher;

public class SolrXmlResponseParser extends SolrResponseParser {

	// TODO: create a threadpool for this?

	// Belongs to initial Solr XML response which will be used to generate the HTTP Response
	private Document mainDoc = null;
	private Node resultNode = null;
	private Node explainNode = null;
	private Node qtimeNode = null;
	private Node placeHolderNode = null;

	private List<Node> demotedEntries = new ArrayList<Node>();
	private List<Node> elevatedEntries = new ArrayList<Node>();

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
				}
				else {
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
		try {
			HttpResponse solrResponse = SolrRequestDispatcher.dispatchRequest(requestPath, requestParams);
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document elevateDoc = docBuilder.parse(solrResponse.getEntity().getContent());
			// locate the result node and get the numFound attribute
			numFound = Integer.parseInt(elevateDoc.getElementsByTagName(SolrConstants.TAG_RESULT).item(0)
					.getAttributes().getNamedItem(SolrConstants.ATTR_NUM_FOUND).getNodeValue());
		} catch (Exception e) {
			throw new SearchException("Error occured while trying to get number of items" ,e);
		}
		return numFound;
	}

	private void tagSearchResult(Document parentDocument, Node resultNode, SearchResult result) {
		Node tagNode = null;
		if (result instanceof ElevateResult) {
			tagNode = parentDocument.createElement(SolrConstants.TAG_ELEVATE);
			tagNode.appendChild(parentDocument.createTextNode(String.valueOf(((ElevateResult)result).getLocation())));
		}
		else if (result instanceof DemoteResult) {
			tagNode = parentDocument.createElement(SolrConstants.TAG_DEMOTE);
			tagNode.appendChild(parentDocument.createTextNode(String.valueOf(((DemoteResult)result).getLocation())));
		}
		if (tagNode != null) {
			resultNode.appendChild(tagNode);			
		}
	}
	
	@Override
	protected int getFacet(List<NameValuePair> requestParams, SearchResult facet) throws SearchException {
		int addedRecords = 0;
		try {
			HttpResponse solrResponse = SolrRequestDispatcher.dispatchRequest(requestPath, requestParams);
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document currentDoc  = docBuilder.parse(solrResponse.getEntity().getContent());
			
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
						if (kNode.getNodeName().equalsIgnoreCase(SolrConstants.TAG_INT) &&
								kNode.getAttributes().getNamedItem(SolrConstants.ATTR_NAME).getNodeValue()
								.equalsIgnoreCase(SolrConstants.ATTR_NAME_VALUE_EDP)) {
							String edp = kNode.getTextContent();
							tagSearchResult(currentDoc, docNode, facet);
							if (facet instanceof ElevateResult) {
								elevatedEntries.add(mainDoc.importNode(docNode, true));
							}
							else if (facet instanceof DemoteResult) {
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
			throw new SearchException("Error occured while trying to get items" ,e);
		}
		return addedRecords;
	}

	@Override
	protected int getEdps(List<NameValuePair> requestParams, List<? extends SearchResult> edpList, int startRow, int requestedRows) throws SearchException {
		int addedRecords = 0;
		try {
			HttpResponse solrResponse = SolrRequestDispatcher.dispatchRequest(requestPath, requestParams);
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document currentDocument = docBuilder.parse(solrResponse.getEntity().getContent());
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
						if (kNode.getNodeName().equalsIgnoreCase(SolrConstants.TAG_INT) &&
								kNode.getAttributes().getNamedItem(SolrConstants.ATTR_NAME).getNodeValue()
								.equalsIgnoreCase(SolrConstants.ATTR_NAME_VALUE_EDP)) {
							String edp = kNode.getTextContent();
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
			for (SearchResult result: edpList) {
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
					}
					else if (result instanceof DemoteResult) {
						demotedEntries.add(mainDoc.importNode(node, true));
					}
					if (explainNode != null) {
						explainNode.appendChild(mainDoc.importNode(locateElementNode(currentExplainNode, SolrConstants.TAG_STR, edp), true));
					}
				}
			}
		} catch (Exception e) {
			throw new SearchException("Error occured while trying to get items" ,e);
		}
		return addedRecords;
	}
	
	@Override
	public int getNonElevatedItems(List<NameValuePair> requestParams) throws SearchException {
		int addedRecords = 0;
		try {
			HttpResponse solrResponse = SolrRequestDispatcher.dispatchRequest(requestPath, requestParams);
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document elevateDoc = docBuilder.parse(solrResponse.getEntity().getContent());
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
							String edp = locateElementNode(docNode, SolrConstants.TAG_INT, SolrConstants.ATTR_NAME_VALUE_EDP).getTextContent();
							if (expiredElevatedEDPs.contains(edp)) {
								Node expiredNode = elevateDoc.createElement(SolrConstants.TAG_ELEVATE_EXPIRED);
								docNode.appendChild(expiredNode);
							}
							if (expiredDemotedEDPs.contains(edp)) {
								Node expiredNode = elevateDoc.createElement(SolrConstants.TAG_DEMOTE_EXPIRED);
								docNode.appendChild(expiredNode);
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
			throw new SearchException("Error occured while trying to get Non-Elevated items" ,e);
		}
		return addedRecords;
	}

	@Override
	public int getTemplateCounts(List<NameValuePair> requestParams) throws SearchException {
		int numFound = -1;
		try {
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			HttpResponse solrResponse = SolrRequestDispatcher.dispatchRequest(requestPath, requestParams);
			mainDoc = docBuilder.parse(solrResponse.getEntity().getContent());
			// locate the result node and reference it <result name="response" maxScore="23.015398" start="0" numFound="360207">
			// results will be added here
			resultNode = locateElementNode(locateElementNode(mainDoc, SolrConstants.TAG_RESPONSE),
					SolrConstants.TAG_RESULT, SolrConstants.ATTR_NAME_VALUE_RESPONSE);
			explainNode = locateElementNode(locateElementNode(locateElementNode(mainDoc, SolrConstants.TAG_RESPONSE),
					SolrConstants.TAG_LIST, SolrConstants.ATTR_NAME_VALUE_DEBUG),
					SolrConstants.TAG_LIST, SolrConstants.ATTR_NAME_VALUE_EXPLAIN);

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

			if (StringUtils.isNotBlank(changedKeyword)) {
				Node redirectNode = mainDoc.createElement(SolrConstants.TAG_REDIRECT);
				redirectNode.appendChild(mainDoc.createTextNode(changedKeyword));				
				responseHeaderNode.appendChild(redirectNode);
			}
			
			if (activeRules != null) {
				Node activeRuleNode = mainDoc.createElement(SolrConstants.TAG_SEARCH_RULES);
				for (Map<String, String> rule: activeRules) {
					Node ruleNode = mainDoc.createElement(SolrConstants.TAG_RULE);
					for (String key: rule.keySet()) {
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
			
			Node responseHeaderParamsNode = locateElementNode(responseHeaderNode,
					SolrConstants.TAG_LIST, SolrConstants.ATTR_NAME_VALUE_PARAMS);
			locateElementNode(responseHeaderParamsNode,SolrConstants.TAG_STR, SolrConstants.SOLR_PARAM_ROWS)
					.setTextContent(String.valueOf(requestedRows));
			locateElementNode(responseHeaderParamsNode,SolrConstants.TAG_STR, SolrConstants.SOLR_PARAM_START)
					.setTextContent(String.valueOf(startRow));
			qtimeNode = locateElementNode(responseHeaderNode,SolrConstants.TAG_INT, SolrConstants.ATTR_NAME_VALUE_QTIME);
		} catch (Exception e) {
			throw new SearchException("Error occured while trying to get template counts" ,e);
		}
		return numFound;
	}

	@Override
	public boolean generateServletResponse(HttpServletResponse response, long totalTime) throws SearchException {
		boolean success = false;
		try {
			addElevatedEntries();
			addDemotedEntries();
			resultNode.removeChild(placeHolderNode);
			qtimeNode.setTextContent(String.valueOf(totalTime));
			response.setContentType("text/xml;charset=UTF-8");
			DOMSource source = new DOMSource(mainDoc);
			TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(response.getOutputStream()));
			success = true;
		} catch (Exception e) {
			throw new SearchException("Error occured while trying to send Http response" ,e);
		}
		return success;
	}

	private void addElevatedEntries() {
		for (Node node: elevatedEntries) {
        	resultNode.insertBefore(node, placeHolderNode);
		}
	}

	private void addDemotedEntries() {
		for (Node node: demotedEntries) {
			resultNode.appendChild(node);
		}
	}

	@Override
	public String getCommonTemplateName(String templateNameField, List<NameValuePair> requestParams) throws SearchException {
		String templateName = "";
		try {
			HttpResponse solrResponse = SolrRequestDispatcher.dispatchRequest(requestPath, requestParams);
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = docBuilder.parse(solrResponse.getEntity().getContent());
			Node node0 = locateElementNode(document, SolrConstants.TAG_RESPONSE);
			Node node1 = locateElementNode(node0, SolrConstants.TAG_LIST, SolrConstants.TAG_FACET_COUNTS);
			Node node2 = locateElementNode(node1, SolrConstants.TAG_LIST, SolrConstants.TAG_FACET_FIELDS);
			Node node3 = locateElementNode(node2, SolrConstants.TAG_LIST, templateNameField);
			NodeList templateNames = node3.getChildNodes();
			if (templateNames.getLength() == 1) {
				templateName = templateNames.item(0).getAttributes().getNamedItem(SolrConstants.ATTR_NAME).getNodeValue();
			}
		} catch (Exception e) {
			throw new SearchException("Error occured while trying to get common template name" ,e);
		}
		return templateName;
	}
	
}
