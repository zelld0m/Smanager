package com.search.manager.schema;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.apache.solr.core.SolrConfig;
import org.apache.solr.schema.IndexSchema;

import com.search.manager.schema.model.Field;
import com.search.manager.schema.model.Schema;
import com.search.manager.schema.model.VerifiableModel;
import com.search.manager.schema.model.bf.BoostFunctionModel;
import com.search.manager.schema.model.mm.MinimumToMatchModel;
import com.search.manager.schema.model.qf.QueryFieldsModel;
import com.search.ws.ConfigManager;

public class SolrSchemaUtility {

	private final static Logger logger = Logger.getLogger(SolrSchemaUtility.class);
	
	public static Schema getSchema(String serverName, String storeName) {
		try {
			HttpClient client = new DefaultHttpClient();
			String url = ConfigManager.getInstance().getServerParameter(serverName, "url")
							.replace("(store)", storeName);
			logger.debug("url is: " + url);
			HttpGet get = new HttpGet(url + "/admin/file/?file=solrconfig.xml");
			HttpResponse response = client.execute(get);
			SolrConfig config = new SolrConfig("config", response.getEntity().getContent());
	
			get = new HttpGet(url + "/admin/file/?file=schema.xml");
			response = client.execute(get);
			Schema schema = new Schema(new IndexSchema(config, "schema", response.getEntity().getContent()));
			return schema;
		} catch (Exception e) {
			logger.error("Cannot create schema object", e);
		}
		return null;
	};
	
	/**
	 * Used to validate Solr's bq parameter
	 * @param schema
	 * @param boost
	 * @return
	 */
	public static boolean validateBoostQuery(IndexSchema schema, String boostQuery) throws SchemaException {
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
		
		String[] mms = {
				"2",
				"-1",
				"5%",
				"-10%",
				"1<5",
				"1<5%",
				"1%<5",
				"3<67% 5<50",
				"3<67% -5<50",
				"3<67% 5",
				"3<67% 5%",
				"2<5 3<67 5%<50",
				"2<5 3<67% 5<50",
				"2<5 3<67 2<50"
		};


		for (String mm: mms) {
			try {
				VerifiableModel model = MinimumToMatchModel.toModel(mm, false);
				if (model.validate()) {
					logger.debug(model + " is valid");
				}
			} catch (Exception e) {
				logger.debug(mm + " is invalid: " + e.getMessage());
			}
		}
		
    	ConfigManager.getInstance("/home/solr/conf/solr.xml");
		RelevancyConfig.getInstance("/home/solr/conf/relevancy.xml");
		Schema schema = SolrSchemaUtility.getSchema("search", "pcmall");
		for (Field field: schema.getFields()) {
			logger.debug(field.getName() + " " + field.getGenericType());
		}

		for (Field field: schema.getNumericFields()) {
			logger.debug(field.getName() + " " + field.getGenericType());
		}



		String[] qfs = {
			"Manufacturer^10.0",
			"Manufacturer^10.",
			"Manufacturer^.0",
			"Manufacturer0",
			"Manufacturer^10 ManufacturerIndex^5.2",
			"Manufacturer^10.0 ManufacturerIndex^5.2",
			"Manufacturer^10.0 ManufacturerIndex^5.2 Manufacturer^1",
			"Manufacturer^10.0 ManufacturerIndex^-2 Manufacturer^1",
			"Manufacturer^10.0 ManufacturerIndex^2Manufacturer^1",
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
				"sum(linear(PcMall_PopularityScale,PcMall_PopularityScale,0),map(NextDayUnits,1,999999999,8),map(SecondDayUnits,1,999999999,8.0)^10.0",*/
				
				"ms(NOW/HOUR)^2.0",
				"ms(NOW/2HOURS)^2.0",
				"ms(NOW/DAY+6MONTHS-3DAYS)^2.0"
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
