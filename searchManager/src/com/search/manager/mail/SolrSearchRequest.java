package com.search.manager.mail;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

import javax.xml.parsers.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;

import com.search.manager.model.TopKeyword;

public class SolrSearchRequest implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(SolrSearchRequest.class);

    private static final int MAX_TRIES = 10;

    private BlockingQueue<TopKeyword> stats;

    private String solrUrl;

    public SolrSearchRequest(BlockingQueue<TopKeyword> stats, String solrUrl) {
        this.stats = stats;
        this.solrUrl = solrUrl;
    }

    public void run() {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(solrUrl);
        HttpResponse response = null;
        DocumentBuilder builder = null;

        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return;
        }

        while (!stats.isEmpty()) {
            TopKeyword keywordStats = stats.poll();

            if (keywordStats == null) {
                break;
            }

            try {
                log.info("processing keyword {}", keywordStats.getKeyword());

                keywordStats.incrementTries();
                post.setEntity(new UrlEncodedFormEntity(Arrays.asList(new BasicNameValuePair("q", keywordStats
                        .getKeyword())), "UTF-8"));
                response = client.execute(post);

                Document doc = builder.parse(response.getEntity().getContent());

                int count = Integer.parseInt(doc.getElementsByTagName("result").item(0).getAttributes()
                        .getNamedItem("numFound").getNodeValue());
                String edp = "";

                if (count != 0) {
                    NodeList docNodes = doc.getElementsByTagName("doc").item(0).getChildNodes();
                    for (int k = 0, kSize = docNodes.getLength(); k < kSize; k++) {
                        Node kNode = docNodes.item(k);
                        if (kNode.getNodeName().equalsIgnoreCase("int")
                                && kNode.getAttributes().getNamedItem("name").getNodeValue().equalsIgnoreCase("DPNo")) {
                            edp = kNode.getTextContent();
                            break;
                        }
                    }
                }

                keywordStats.setSku(edp);
                keywordStats.setResultCount(count);

                log.info("Solr stats successfully retrieved: count: {}, keyword: {}, results: {},  edpt: {}",
                        keywordStats.toStringArray());
            } catch (Exception e) {
                log.error("Exception occured during solr search.", e);

                try {
                    if (keywordStats.continueProcessing(MAX_TRIES)) {
                        log.info("Putting back keyword {} to queue for reprocessing.", keywordStats.getKeyword());
                        stats.put(keywordStats);
                    } else {
                        log.info("Maximum tries for solr request keyword '{}' was reached.", keywordStats.getKeyword());
                        keywordStats.stopProcessing();
                    }
                } catch (InterruptedException ex) {
                    log.error("Unable to enqueue keyword " + keywordStats.getKeyword(), ex);
                }
            } finally {
                EntityUtils.consumeQuietly(response.getEntity());
            }
        }

        client.getConnectionManager().shutdown();
        log.info("Search thread destroyed.");
    }
}
