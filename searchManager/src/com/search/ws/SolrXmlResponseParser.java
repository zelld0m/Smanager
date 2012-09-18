package com.search.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.utility.SolrRequestDispatcher;

public class SolrXmlResponseParser implements SolrResponseParser {

	// TODO: create a threadpool for this?

	// Belongs to initial Solr XML response which will be used to generate the HTTP Response
	private Document mainDoc = null;
	private Node resultNode = null;
	private Node explainNode = null;
	private Node qtimeNode = null;
	private Node placeHolderNode = null;

	private String requestPath;
	private int startRow;
	private int requestedRows;
	private String changedKeyword;

	private List<ElevateResult> elevatedList = null;
	private List<String> expiredElevatedEDPs = null;
	private List<Map<String,String>> activeRules;
	private List<ElevateResult> forceAddedList = null;

	@Override
	public void setActiveRules(List<Map<String,String>> activeRules) throws SearchException {
		this.activeRules = activeRules;
	}

	@Override
	public void setElevatedItems(List<ElevateResult> list) throws SearchException {
		elevatedList= list;
	}

	@Override
	public void setExpiredElevatedEDPs(List<String> list) throws SearchException {
		expiredElevatedEDPs = list;
	}

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

	private List<Node> sortElevateList(Document document, Map<String, Node> nodeMap) throws SearchException{
		Node node;
		ArrayList<Node> sortedElevateList = new ArrayList<Node>();
		for (ElevateResult result: elevatedList) {
			node = nodeMap.get(result.getEdp());
			if (node != null) {
				Node elevateNode = document.createElement(SolrConstants.TAG_ELEVATE);
				elevateNode.appendChild(document.createTextNode(String.valueOf(result.getLocation())));
				node.appendChild(elevateNode);
				sortedElevateList.add(node);
			}
		}
		return sortedElevateList;
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

	@Override
	public int getElevatedItems(List<NameValuePair> requestParams, int reqRows) throws SearchException {
		int addedRecords = 0;
		try {
			Map<String, Node> explainMap = new HashMap<String, Node>();
			Map<String, Node> nodeMap = new LinkedHashMap<String, Node>();
			Document elevateDoc = null;
			int size = startRow + requestedRows;
			
			BasicNameValuePair kwNvp = null;
			if (forceAddedList.size() > 0) {
				for (NameValuePair nameValuePair : requestParams) {
					if (SolrConstants.SOLR_PARAM_KEYWORD.equals(nameValuePair.getName())) {
						kwNvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_KEYWORD,nameValuePair.getValue());
						break;
					} 
				}
			}
			
			int currItem = 0;			
			for (ElevateResult elevateResult : elevatedList) {
				BasicNameValuePair nvp = null;
				BasicNameValuePair excludeEDPNVP = null;
				BasicNameValuePair excludeFacetNVP = null;
				StringBuilder elevateValues = new StringBuilder();
				StringBuilder elevateFacetValues = new StringBuilder();
				currItem++;
				if (elevateResult.getElevateEntity() == MemberTypeEntity.PART_NUMBER) {
					nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, "EDP:" + elevateResult.getEdp());
				} else {
					nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, elevateResult.getCondition().getConditionForSolr());
					generateElevateList(elevateValues, elevateFacetValues, elevatedList, currItem);
					if (elevateValues.length() > 0) {
						excludeEDPNVP = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, "-" + elevateValues.toString());
						requestParams.add(excludeEDPNVP);
					}				
					if (elevateFacetValues.length() > 0) {
						excludeFacetNVP = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, "-" + elevateFacetValues.toString());
						requestParams.add(excludeFacetNVP);
					}				
				}
				if (elevateResult.isForceAdd() && kwNvp!=null) {
					requestParams.remove(kwNvp);
				}				
				requestParams.add(nvp);
				HttpResponse solrResponse = SolrRequestDispatcher.dispatchRequest(requestPath, requestParams);
				requestParams.remove(nvp);
				
				if (elevateResult.isForceAdd()  && kwNvp!=null) {
					requestParams.add(kwNvp);
				}
				if (elevateValues.length() > 0) {
					requestParams.remove(excludeEDPNVP);
				}				
				if (elevateFacetValues.length() > 0) {
					requestParams.remove(excludeFacetNVP);
				}		
				DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				elevateDoc  = docBuilder.parse(solrResponse.getEntity().getContent());
				// <result> tag that is parent node for all the <doc> tags
				Node tmpResultNode = locateElementNode(locateElementNode(elevateDoc, SolrConstants.TAG_RESPONSE),
						SolrConstants.TAG_RESULT, SolrConstants.ATTR_NAME_VALUE_RESPONSE);
	
				NodeList children = elevateDoc.getElementsByTagName(SolrConstants.TAG_DOC);
	
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
								nodeMap.put(edp, docNode);
								explainMap.put(edp, locateElementNode(locateElementNode(locateElementNode(elevateDoc, SolrConstants.TAG_RESPONSE),
												SolrConstants.TAG_LIST, SolrConstants.ATTR_NAME_VALUE_DEBUG),
												SolrConstants.TAG_LIST, SolrConstants.ATTR_NAME_VALUE_EXPLAIN));
								break;
							}
						}
					}
					if (nodeMap.size() >= size) {
						break;
					}
				}
				if (nodeMap.size() >= size) {
					break;
				}
			}

			int ctr = 0;
			for (Map.Entry<String, Node> entry : nodeMap.entrySet()) {
				if (ctr++ >= startRow) {
		        	resultNode.insertBefore(mainDoc.importNode(entry.getValue(), true), placeHolderNode);
					if (explainNode != null) {
						explainNode.appendChild(mainDoc.importNode(locateElementNode(explainMap.get(entry.getKey()), SolrConstants.TAG_STR,
								locateElementNode(entry.getValue(), SolrConstants.TAG_INT, SolrConstants.ATTR_NAME_VALUE_EDP).getTextContent()), true));
					}
					if (++addedRecords >= reqRows) {
						break;
					}
				}
	        }
		} catch (Exception e) {
			throw new SearchException("Error occured while trying to get elevated items" ,e);
		}
		return addedRecords;
	}

	private void generateElevateList(StringBuilder elevateValues, StringBuilder elevateFacetValues, Collection<ElevateResult> elevateList, int currItem) {
		boolean edpFlag = false;
		boolean facetFlag = false;
		int i = 1;
		if (!(elevateList == null || elevateList.isEmpty())) {
			for (ElevateResult elevate: elevateList) {
				if (++i > currItem) {
					break;
				}
				if (elevate.getElevateEntity().equals(MemberTypeEntity.PART_NUMBER)) {
					if (!edpFlag) {
						elevateValues.append("EDP:(");
						edpFlag = true;
					}
					elevateValues.append(" ").append(elevate.getEdp());
				} else {
					if (!facetFlag) {
						elevateFacetValues.insert(0, "(");
						facetFlag = true;
					} else {
						elevateFacetValues.append(" OR ");
					}
					elevateFacetValues.append("(").append(elevate.getCondition().getConditionForSolr()).append(")");
				}
			}
			if (edpFlag) {
				elevateValues.append(")");
			}
			if (facetFlag) {
				elevateFacetValues.append(")");
			}
		}
	}

	@Override
	public int getElevatedItems(List<NameValuePair> requestParams) throws SearchException {
		int addedRecords = 0;
		try {
			StringBuilder elevatedEdps = new StringBuilder();
			generateEdpElevateList(elevatedEdps, elevatedList);
			generateEdpElevateList(elevatedEdps, forceAddedList);
			if (elevatedEdps.length() > 0) {
				elevatedEdps.append(")");
			}
			for (NameValuePair nameValuePair : requestParams) {
				if (SolrConstants.SOLR_PARAM_KEYWORD.equals(nameValuePair.getName())) {
					requestParams.remove(new BasicNameValuePair(SolrConstants.SOLR_PARAM_KEYWORD,nameValuePair.getValue()));
					break;
				}
			}
			requestParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY,elevatedEdps.toString()));
			HttpResponse solrResponse = SolrRequestDispatcher.dispatchRequest(requestPath, requestParams);
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document elevateDoc = docBuilder.parse(solrResponse.getEntity().getContent());
			// <result> tag that is parent node for all the <doc> tags
			Node tmpResultNode = locateElementNode(locateElementNode(elevateDoc, SolrConstants.TAG_RESPONSE),
					SolrConstants.TAG_RESULT, SolrConstants.ATTR_NAME_VALUE_RESPONSE);

			NodeList children = elevateDoc.getElementsByTagName(SolrConstants.TAG_DOC);
			Map<String, Node> elevateDocuments = new HashMap<String, Node>();

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
							elevateDocuments.put(edp, docNode);
							break;
						}
					}
				}
			}

			// sort the edps
			List<Node> nodeList = sortElevateList(elevateDoc, elevateDocuments);
			//<lst name="debug"> <lst name="explain"> <str name="6230888">
			Node elevateExplainNode = locateElementNode(locateElementNode(locateElementNode(elevateDoc, SolrConstants.TAG_RESPONSE),
					SolrConstants.TAG_LIST, SolrConstants.ATTR_NAME_VALUE_DEBUG),
					SolrConstants.TAG_LIST, SolrConstants.ATTR_NAME_VALUE_EXPLAIN);

			// TODO: need to test if elevated list always show up before non-elevated list. Had to fix for JSON, but for XML so far so good
			for (int i = startRow, size = startRow + requestedRows, resultSize = nodeList.size(); i < size && i < resultSize; i++) {
				// insert the elevate results to the document node
				addedRecords++;
				resultNode.insertBefore(mainDoc.importNode(nodeList.get(i), true), placeHolderNode);
				if (explainNode != null) {
					explainNode.appendChild(mainDoc.importNode(locateElementNode(elevateExplainNode, SolrConstants.TAG_STR,
							locateElementNode(nodeList.get(i), SolrConstants.TAG_INT, SolrConstants.ATTR_NAME_VALUE_EDP).getTextContent()), true));
				}
			}
		} catch (Exception e) {
			throw new SearchException("Error occured while trying to get elevated items" ,e);
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
								Node expiredNode = elevateDoc.createElement(SolrConstants.TAG_EXPIRED);
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

	private int getForceAddCount(List<NameValuePair> requestParams) throws Exception {
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		HttpResponse solrResponse = SolrRequestDispatcher.dispatchRequest(requestPath, requestParams);
		Document doc = docBuilder.parse(solrResponse.getEntity().getContent());
		Node resultNode2 = locateElementNode(locateElementNode(doc, SolrConstants.TAG_RESPONSE),
				SolrConstants.TAG_RESULT, SolrConstants.ATTR_NAME_VALUE_RESPONSE);
		return Integer.parseInt(resultNode2.getAttributes().getNamedItem(SolrConstants.ATTR_NUM_FOUND).getNodeValue());
	}
	@Override
	public int getForceAddTemplateCounts(List<NameValuePair> requestParams) throws SearchException {
		int result = -1;
		try {
			int forceAddCount = 0;
			requestParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_KEYWORD, ""));
			requestParams.add(new BasicNameValuePair("q.alt", "*:*"));
			requestParams.add(new BasicNameValuePair("defType", "dismax"));
			StringBuffer edpBuffer = new StringBuffer("EDP:(");
			for (ElevateResult e : forceAddedList) {
				if (MemberTypeEntity.PART_NUMBER == e.getElevateEntity()) {
					edpBuffer.append(e.getEdp()).append(" ");
				}
			}			
			edpBuffer.append(")");
			
			if (edpBuffer.length() > 8) {
				BasicNameValuePair edpNvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, edpBuffer.toString());
				requestParams.add(edpNvp);
				forceAddCount = getForceAddCount(requestParams);
				result += forceAddCount;
				requestParams.remove(edpNvp);
			}
			for (ElevateResult e : forceAddedList) {
				StringBuffer buffer = new StringBuffer("");
				if (MemberTypeEntity.FACET == e.getElevateEntity()) {
					buffer.append(e.getCondition().getConditionForSolr());
				} else {
					continue;
				}
				BasicNameValuePair facetNvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, buffer.toString());
				requestParams.add(facetNvp);
				forceAddCount = getForceAddCount(requestParams);
				result += forceAddCount;
				requestParams.remove(facetNvp);
			}
		} catch (Exception e) {
			throw new SearchException("Error occured while trying to get template counts" ,e);
		}
		return result;
	}

	@Override
	public boolean generateServletResponse(HttpServletResponse response, long totalTime) throws SearchException {
		boolean success = false;
		try {
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

	@Override
	public void setSolrUrl(String solrUrl) throws SearchException {
		requestPath= solrUrl;
	}

	@Override
	public void setSolrQueryParameters(HashMap<String, List<NameValuePair>> paramMap) throws SearchException {
	}

	@Override
	public void setRequestRows(int startRow, int requestedRows) throws SearchException {
		this.startRow = startRow;
		this.requestedRows = requestedRows;
	}

	@Override
	public void setChangeKeyword(String changedKeyword) throws SearchException {
		this.changedKeyword = changedKeyword;
	}

	public List<ElevateResult> getForceAddedList() {
		return forceAddedList;
	}

	public void setForceAddedList(List<ElevateResult> forceAddedList) {
		this.forceAddedList = forceAddedList;
	}

	private static void generateEdpElevateList(StringBuilder elevateValues, Collection<ElevateResult> elevateList) {
		if (!(elevateList == null || elevateList.isEmpty())) {
			for (ElevateResult elevate: elevateList) {
				if (elevate.getElevateEntity().equals(MemberTypeEntity.PART_NUMBER)) {
					if (elevateValues.length() == 0) {
						elevateValues.append("EDP:(");
					}
					elevateValues.append(" ").append(elevate.getEdp());
				} 
			}
		}
	}

	// TODO: implement
	private List<DemoteResult> demotedList = null;
	private List<String> expiredDemotedEDPs = null;
	
	@Override
	public void setDemotedItems(List<DemoteResult> list) throws SearchException {
		demotedList = list;
	}
	
	@Override
	public void setExpiredDemotedEDPs(List<String> list) throws SearchException {
		expiredDemotedEDPs = list;
	}

	@Override
	public int getDemotedItems(List<NameValuePair> requestParams) throws SearchException {
		int addedRecords = 0;
		return addedRecords;
	}

	@Override
	public int getDemotedItems(List<NameValuePair> requestParams, int reqRows) throws SearchException {
		int addedRecords = 0;
		return addedRecords;
	}

}
