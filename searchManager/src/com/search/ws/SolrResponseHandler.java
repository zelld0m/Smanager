package com.search.ws;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Stack;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SolrResponseHandler extends DefaultHandler {

	private Writer out;
	private File file;

	private static Logger logger = Logger.getLogger(SolrResponseHandler.class);
		
	private Stack<NameValuePair> paramStack = new Stack<NameValuePair>();
	
	public SolrResponseHandler(String tmpFile) {
		file = new File(tmpFile);
	}
	
	public void startDocument() throws SAXException {
		try {
			logger.info("Start parsing xml document!");
			out = new FileWriter(file);
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (getQueryTime) {
			queryTime = new String(ch, start, length);
			getQueryTime = false;
		}
		try {
			if (currentEntity != CurrentEntity.RESPONSE) {
				out.write(ch, start, length);
			}
		} catch (IOException ioe) {
			cleanUp();
			throw new SAXException(ioe);
		}
	}

	public enum CurrentEntity { RESPONSE_HEADER, RESPONSE, OTHERS };
	
	private CurrentEntity currentEntity;
	
	private boolean getQueryTime;
	
	private String queryTime;
	private String numFound;
	private String maxScore;
		
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		try {
			if (currentEntity != CurrentEntity.RESPONSE) {
				out.write("</");
				out.write(paramStack.pop().getName());
				out.write(">");
			}
		} catch (IOException ioe) {
			cleanUp();
			throw new SAXException(ioe);
		}	
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException {
		try {
			String attrName = attributes.getValue(SolrConstants.ATTR_NAME);
			logger.info("qName: " + qName +"  Attribute Name: " + attrName);
			
			if (qName.equals(SolrConstants.TAG_RESULT)) {
				// Skip result tag
				return;
			}
			else if (qName.equals(SolrConstants.TAG_LIST) && attrName.equals(SolrConstants.ATTR_NAME_VALUE_RESPONSE_HEADER)) {
				logger.debug("Inside response header node");
				currentEntity = CurrentEntity.RESPONSE_HEADER;				
			}
			else if (qName.equals(SolrConstants.TAG_RESULT) && attrName.equals(SolrConstants.ATTR_NAME_VALUE_RESPONSE)) {
				logger.debug("Inside response node");
				currentEntity = CurrentEntity.RESPONSE;
			}
			else {
				logger.debug("Inside response header node");
				currentEntity = CurrentEntity.OTHERS;
			}
			paramStack.add(new BasicNameValuePair(qName, attrName));
			
			if (currentEntity != CurrentEntity.RESPONSE) {
				out.write("<");
				out.write(qName);
				for (int i = 0, size = attributes.getLength(); i < size; i++) {
					out.write(" ");
					out.write(attributes.getQName(i));
					out.write("=\"");
					out.write(attributes.getValue(i));
					out.write("\"");
				}
				out.write(">");
			}
			else {
				// RESPONSE HEADER
				for (int i = 0, size = attributes.getLength(); i < size; i++) {
					logger.debug("ATTRIBUTES: " + attributes.getQName(i) + ": " + attributes.getValue(i));
					if (attributes.getQName(i).equals(SolrConstants.ATTR_NUM_FOUND)) {
						numFound = attributes.getValue(i);
					}
					else if (attributes.getQName(i).equals(SolrConstants.ATTR_MAX_SCORE)) {
						maxScore = attributes.getValue(i);
					}
				}
			}
		} catch (IOException ioe) {
			cleanUp();
			throw new SAXException(ioe);
		}	
	}

	// close the stream
	@Override
	public void endDocument() throws SAXException {
		cleanUp();
	}
	
	public void cleanUp() {
		try {
			paramStack.clear();
			logger.debug("Parsing XML document done. Output file: " + file.getAbsolutePath());
			if (out != null) {
				out.close();
			}
		} catch (IOException ioe) {
			logger.debug("Error closing file: " + file.getAbsolutePath(), ioe);
		}		
	}

	public String getQueryTime() {
		return queryTime;
	}

	public String getMaxScore() {
		return maxScore;
	}

	public String getNumFound() {
		return numFound;
	}
	
	
}
