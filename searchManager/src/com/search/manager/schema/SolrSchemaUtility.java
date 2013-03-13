package com.search.manager.schema;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.search.manager.schema.analyzer.model.AnalyzerComponent;
import com.search.manager.schema.model.Analyzer;
import com.search.manager.schema.model.Analyzer.Type;
import com.search.manager.schema.model.Field;
import com.search.manager.schema.model.FieldType;
import com.search.manager.schema.model.Schema;
import com.search.manager.schema.model.VerifiableModel;
import com.search.manager.schema.model.bf.BoostFunctionModel;
import com.search.manager.schema.model.mm.MinimumToMatchModel;
import com.search.manager.schema.model.qf.QueryFieldsModel;
import com.search.ws.ConfigManager;

public class SolrSchemaUtility {

	private final static Logger logger = Logger.getLogger(SolrSchemaUtility.class);
	
	public static Schema getSchema(String serverName, String storeId) {
		String core = ConfigManager.getInstance().getStoreParameter(storeId, "core");
		return getSchema(ConfigManager.getInstance().getServerParameter(serverName, "url").replace("(core)", core) 
				+ "admin/file/?file=schema.xml");
	}
		
	public static Schema getSchema(String url) {
		Schema schema = null;
		
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		HttpResponse response = null;
		
		try {
			response = client.execute(post);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new Exception("SOLR Server Config call failed: " + response.getStatusLine());
			}
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser saxParser = spf.newSAXParser();
			final Stack<String> paramStack = new Stack<String>();
			final Map<String, FieldType> fieldTypes = new LinkedHashMap<String, FieldType>();
			final Map<String, Field> fields = new LinkedHashMap<String, Field>();
			saxParser.parse(response.getEntity().getContent(), new DefaultHandler() {
				
				FieldType currFieldType = null;
				Analyzer currentAnalyzer = null;

				@Override
				public void endElement(String uri, String localName, String qName) throws SAXException {
					paramStack.pop();
				}

				@Override
				public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
					// TODO: add checking of hierarchy of anscestors in stack
					if (StringUtils.equals(qName, "fieldType")) {
						currFieldType = new FieldType();
						for (int i = 0, size = attributes.getLength(); i < size; i++) {
							String name = attributes.getQName(i);
							String value = attributes.getValue(i);
							if (StringUtils.equals("name", name)) {
								currFieldType.setName(value);
							}
							else if (StringUtils.equals("class", name)) {
								currFieldType.setClassName(value);
							}
							else {
								currFieldType.getAttributes().add(new BasicNameValuePair(name, value));
							}
						}
						
						FieldType fieldType = fieldTypes.get(currFieldType.getName());
						if (fieldType == null) {
							fieldTypes.put(currFieldType.getName(), currFieldType);
						}
						else {
							fieldType.setAttributes(currFieldType.getAttributes());
						}
					}
					else if (StringUtils.equals(qName, "analyzer")) {
						if (currFieldType != null) {
							currentAnalyzer = new Analyzer();
							currFieldType.getAnalyzers().add(currentAnalyzer);
							for (int i = 0, size = attributes.getLength(); i < size; i++) {
								String name = attributes.getQName(i);
								String value = attributes.getValue(i);
								if (StringUtils.equals("type", name)) {
									if (StringUtils.equals(value, "index")) {
										currentAnalyzer.setType(Type.INDEX);
									}
									else if (StringUtils.equals(value, "query")) {
										currentAnalyzer.setType(Type.QUERY);
									}
								}
							}
							if (currentAnalyzer.getType() == null) {
								currentAnalyzer.setType(Type.BOTH);
							}
						}
					}
					else if (StringUtils.equals(qName, "tokenizer")) {
						if (currentAnalyzer != null) {
							AnalyzerComponent tokenizer = new AnalyzerComponent();
							currentAnalyzer.setTokenizer(tokenizer);
							for (int i = 0, size = attributes.getLength(); i < size; i++) {
								String name = attributes.getQName(i);
								String value = attributes.getValue(i);
								if (StringUtils.equals("class", name)) {
									tokenizer.setClassName(value);
								}
								else {
									tokenizer.getAttributes().add(new BasicNameValuePair(name, value));
								}
							}
						}
					}
					else if (StringUtils.equals(qName, "filter")) {
						if (currentAnalyzer != null) {
							AnalyzerComponent filter = new AnalyzerComponent();
							currentAnalyzer.getFilters().add(filter);
							for (int i = 0, size = attributes.getLength(); i < size; i++) {
								String name = attributes.getQName(i);
								String value = attributes.getValue(i);
								if (StringUtils.equals("class", name)) {
									filter.setClassName(value);
								}
								else {
									filter.getAttributes().add(new BasicNameValuePair(name, value));
								}
							}
						}
					}
					else if (StringUtils.equals(qName, "field") || StringUtils.equals(qName, "dynamicField")) {
						Field field = new Field();
						if (StringUtils.equals(qName, "dynamicField")) {
							field.setDynamic(true);
						}
						for (int i = 0, size = attributes.getLength(); i < size; i++) {
							String name = attributes.getQName(i);
							String value = attributes.getValue(i);
							if (StringUtils.equals("name", name)) {
								field.setName(value);
							}
							else if (StringUtils.equals("type", name)) {
								FieldType fieldType = fieldTypes.get(value);
								if (fieldType == null) {
									fieldType = new FieldType();
									fieldType.setName(value);
									fieldTypes.put(value, fieldType);
								}
								field.setFieldType(fieldType);
							}
							else if (StringUtils.equals("indexed", name)) {
								try { field.setIndexed(BooleanUtils.toBoolean(value)); } catch (Exception e) { }
							}
							else if (StringUtils.equals("ignored", name)) {
								try { field.setIgnored(BooleanUtils.toBoolean(value)); } catch (Exception e) { }
							}
							else if (StringUtils.equals("stored", name)) {
								try { field.setStored(BooleanUtils.toBoolean(value)); } catch (Exception e) { }
							}
							else if (StringUtils.equals("multiValued", name)) {
								try { field.setMultiValued(BooleanUtils.toBoolean(value)); } catch (Exception e) { }
							}
							else {
								field.getAttributes().add(new BasicNameValuePair(name, value));
							}
						}
						
						if (!field.isIgnored()) {
							Field field2 = fields.get(field.getName());
							if (field2 == null) {
								fields.put(field.getName(), field);
							}
							else {
								field2.setName(field.getName());
								field2.setFieldType(field.getFieldType());
								field2.setIndexed(field.isIndexed());
								field2.setStored(field.isStored());
								field2.setMultiValued(field.isMultiValued());
								field2.setAttributes(field.getAttributes());
							}
						}
					}
					else if (StringUtils.equals(qName, "copyField")) {
						String source = null;
						String dest = null;
						for (int i = 0, size = attributes.getLength(); i < size; i++) {
							String name = attributes.getQName(i);
							String value = attributes.getValue(i);
							if (StringUtils.equals("source", name)) {
								source = value;
							}
							else if (StringUtils.equals("dest", name)) {
								dest = value;
							}
						}
						if (StringUtils.isNotBlank(source) && StringUtils.isNotBlank(dest)) {
							Field sourceField = fields.get(source);
							Field destField = fields.get(dest);
							
							if (sourceField == null) {
								sourceField = new Field();
							}
							if (destField == null) {
								destField = new Field();
							}
							sourceField.addCopyField(destField);
						}
					}
					paramStack.add(qName);
				}

				// close the stream
				@Override
				public void endDocument() throws SAXException {
					paramStack.clear();
				}
			});
			fields.put("GenericUser_Keywords", new Field("GenericUser_Keywords", fieldTypes.get("text"), true, true));
			schema = new Schema();
			schema.setFields(fields);
		} catch (Exception e) {
			logger.error("Invalid schema file: " + url);
		} finally {
			if (response != null) {  EntityUtils.consumeQuietly(post.getEntity()); }
			post.releaseConnection();
			client.getConnectionManager().shutdown();
		}
		
		return schema;
	}
	
	/**
	 * Used to validate Solr's bq parameter
	 * @param schema
	 * @param boost
	 * @return
	 */
	public static boolean validateBoostQuery(Schema schema, String boostQuery) throws SchemaException {
		// TODO: implement [-]<field>:<value>(<value/s>)<boost>
		// alternative: send to solr. it would throw an exception if it is invalid
		return false;
	}
	
	/**
	 * Used to validate Solr's qf and pf parameters
	 * @param schema
	 * @param boost
	 * @return
	 */
	public static boolean validateQueryFields(Schema schema, String queryFields) throws SchemaException {
		QueryFieldsModel.toModel(schema, queryFields, true);
		return true;
	}
	
	/**
	 * Used to validate Solr's mm parameter
	 * @param string
	 * @return
	 */
	public static boolean validateMinToMatch(String mm) throws SchemaException {
		MinimumToMatchModel.toModel(mm, true);
		return true;
	}
	
	/**
	 * Used to validate Solr's ps and qs params.
	 * @param string
	 * @return
	 */
	public static boolean validateSlop(String slop) throws SchemaException {
		if (StringUtils.isEmpty(slop)) {
			throw new SchemaException("Empty slop value.");
		}
		try {
			Integer.parseInt(slop);
		} catch (Exception e) {
			throw new SchemaException("Invalid value for slop: " + slop);
		}
		return true;
	}
	
	/**
	 * Used to validate Solr's bf param.
	 * @param string
	 * @return
	 */
	public static boolean validateBoostFunction(Schema schema, String boostFunction) throws SchemaException {
		BoostFunctionModel.toModel(schema, boostFunction, true);
		return true;
	}

	/**
	 * Used to validate Solr's tie param.
	 * @param string
	 * @return
	 */
	public static boolean validateTie(String tie) throws SchemaException {
		if (StringUtils.isNotEmpty(tie)) {
			try {
				Float.parseFloat(tie);
			} catch (Exception e) {
				throw new SchemaException("Invalid value for tie: " + tie);
			}
		}
		return true;
	}
		
	public static void main(String[] args)  {
		
    	ConfigManager.getInstance("/home/solr/conf/solr.xml");
		RelevancyConfig.getInstance("/home/solr/conf/relevancy.xml");
		
//		Schema schema = SolrSchemaUtility.getSchema("http://afs-pl-schpd01.afservice.org:8080/solr14/macmall/admin/file/?file=schema.xml");
//		Schema schema = SolrSchemaUtility.getSchema("http://afs-pl-schmstr02.afservice.org:8080/solr4/macmall/admin/file/?file=schema.xml");
		Schema schema = SolrSchemaUtility.getSchema("afs-pl-schpd02", "pcmall");

		for (Field field: schema.getFields()) {
			logger.info("Field name: " + field.getName());
			logger.info("\tindexed: " + field.isIndexed());
			logger.info("\tstored: " + field.isStored());
			logger.info("\tmultiValued: " + field.isMultiValued());
			if (field.isDynamic()) {
				logger.info("\tdynamic: " + field.isDynamic());
			}
			for (NameValuePair attribute: field.getAttributes()) {
				logger.info("\t\t\tAttribute " + attribute.getName() + ": " + attribute.getValue());
			}
			FieldType fieldType = field.getFieldType();
			logger.info("\ttype: " + fieldType.getName());
			for (Analyzer analyzer: fieldType.getAnalyzers()) {
				logger.info("\t\t" + analyzer.getType());
				if (analyzer.getTokenizer() != null) {
					logger.info("\t\t\ttokenizer: " + analyzer.getTokenizer().getClassName());
					for (NameValuePair attribute: analyzer.getTokenizer().getAttributes()) {
						logger.info("\t\t\t\tAttribute " + attribute.getName() + ": " + attribute.getValue());
					}
				}
				for (AnalyzerComponent filter: analyzer.getFilters()) {
					logger.info("\t\t\tFilter: " + filter.getClassName());
					for (NameValuePair attribute: filter.getAttributes()) {
						logger.info("\t\t\t\tAttribute " + attribute.getName() + ": " + attribute.getValue());
					}
				}
			}
			for (Field copyField: field.getCopyFields()) {
				logger.info("\tcopy field: " + copyField.getName());
			}
		}
		
//		String[] mms = {
//				"2",
//				"-1",
//				"5%",
//				"-10%",
//				"1<5",
//				"1<5%",
//				"1%<5",
//				"3<67% 5<50",
//				"3<67% -5<50",
//				"3<67% 5",
//				"3<67% 5%",
//				"2<5 3<67 5%<50",
//				"2<5 3<67% 5<50",
//				"2<5 3<67 2<50"
//		};
//
//
//		for (String mm: mms) {
//			try {
//				VerifiableModel model = MinimumToMatchModel.toModel(mm, false);
//				if (model.validate()) {
//					logger.debug(model + " is valid");
//				}
//			} catch (Exception e) {
//				logger.debug(mm + " is invalid: " + e.getMessage());
//			}
//		}
		
//		schema = SolrSchemaUtility.getSchema("search", "pcmall");
//		for (Field field: schema.getFields()) {
//			logger.debug(field.getName() + " " + field.getGenericType());
//		}
//
//		for (Field field: schema.getNumericFields()) {
//			logger.debug(field.getName() + " " + field.getGenericType());
//		}
//
//
//
		String[] qfs = {
			"GenericUser_Keywords^2",
//			"Manufacturer^10.0",
//			"Manufacturer^10.",
//			"Manufacturer^.0",
//			"Manufacturer0",
//			"Manufacturer^10 ManufacturerIndex^5.2",
//			"Manufacturer^10.0 ManufacturerIndex^5.2",
//			"Manufacturer^10.0 ManufacturerIndex^5.2 Manufacturer^1",
//			"Manufacturer^10.0 ManufacturerIndex^-2 Manufacturer^1",
//			"Manufacturer^10.0 ManufacturerIndex^2Manufacturer^1",
		};

		for (String string: qfs) {
			try {
				VerifiableModel model = QueryFieldsModel.toModel(schema, string, false);
				try {
					if (model.validate()) {
						logger.debug(model + " is valid");
					}
				} catch (Exception e) {
					logger.debug(model + " is invalid: " + e.getMessage());
				}
			} catch (Exception e) {
				logger.debug(string + " is invalid: " + e.getMessage());
			}
		}

		logger.debug("***************************************************");
//
//
//
//
		String[] bfs =
		{
				/*"sum(linear(eCOST_PopularityScale,1.2,0),map(NextDayUnits,1,999999999,8),map(SecondDayUnits,1,999999999,8.0))^10.0",
				"sum(linear(PcMall_PopularityScale,1.2,0),map(NextDayUnits,1,999999999,8),map(SecondDayUnits,1,999999999,8.0))^10.0",
				"sum(linear(PcMall_PopularityScale,PcMall_PopularityScale,0),map(NextDayUnits,1,999999999,8),map(SecondDayUnits,1,999999999,8.0))^10.0",
				"linear(PcMall_PopularityScale,1,0)^2",
				"ms(NOW)^2.0",
				"ms(NOW,2000-01-01T00:00:00Z)^2.0",
				"ms(NOW,2000-01-01T00:00:00ABZZ)^2.0",
				"ms(NOW,2000-01-01T00:65:61Z)^2.0",
//				"sum(linear(PcMall_PopularityScale,PcMall_PopularityScale,0),map(NextDayUnits,1,999999999,8),map(SecondDayUnits,1,999999999,8.0)^10.0",*/
				"sum(linear(PcMall_PopularityScale,1,0),0)^2",
//				"ms(NOW/HOUR)^2.0",
//				"ms(NOW/2HOURS)^2.0",
//				"ms(NOW/DAY+6MONTHS-3DAYS)^2.0"
		};

		for (String bf: bfs) {
			try {
				BoostFunctionModel model = BoostFunctionModel.toModel(schema, bf, false);
				if (model.validate()) {
					logger.debug(model + " is valid.");

				}
			} catch (Exception e) {
				logger.debug(bf + " is invalid: " + e.getMessage());
			}
		}
		
	}
	
}
