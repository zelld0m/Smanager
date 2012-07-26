import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;


public class TopKeywordsUtility {
	
	
    final ConcurrentHashMap<String, KeyValuePair> mapZero = new ConcurrentHashMap<String,KeyValuePair>();
	
	private static class ZeroResults implements Runnable {

		HttpClient client = new DefaultHttpClient();
		HttpPost post = null;
		HttpResponse solrResponse = null;
	    DocumentBuilder builder = null;
        Document doc = null;
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        List<String> zeroResultsList = new ArrayList<String>();
        List<String> forReprocess = new ArrayList<String>();
        BlockingQueue<String> toProcess = new ArrayBlockingQueue<String>(500);
        boolean running = false;
        
        public ZeroResults (String solrURL) {
			post = new HttpPost(solrURL);
			try {
				builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		}
		
        public boolean addKeywordToProcess(String keyword) {
        	try {
        		return toProcess.add(keyword);
        	} catch (IllegalStateException ie) {
        		// no capacity at the present
        		return false;
        	}
        }
		
		@Override
		public void run() {
			int retry = 0;
			running = true;
			while (!toProcess.isEmpty()) {
				String keyword = toProcess.peek();
			    parameters.clear();
			    try {
					parameters.add(new BasicNameValuePair("q", URLDecoder.decode(keyword,"UTF-8")));
				    post.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));
				    solrResponse = client.execute(post);
				    doc = builder.parse(solrResponse.getEntity().getContent());
				    int count = 0;
				    	count =  Integer.parseInt(doc.getElementsByTagName("result").item(0)
								.getAttributes().getNamedItem("numFound").getNodeValue());
				    if(count == 0){
				    	zeroResultsList.add(keyword);
				    }
					toProcess.poll();
//System.out.println(keyword + " -> " + count);
				} catch (Exception e) {
//System.err.println(keyword + " -> " + e.getMessage());
					try {
//System.out.println(Thread.currentThread().getId() + "*************retry " + retry++ + " fail" );	        			
	        			Thread.sleep(5000);
	        		} catch (Exception ex) {
	        		}
	        		
	        		if (retry > 10) {
	        			retry = 0;
	        			toProcess.poll();
	        			forReprocess.add(keyword);
	        		}
					client = new DefaultHttpClient();
					continue;
				}
			}
			running = false;
		}
	}
	
	public static class KeyValuePair {
		
		String key;
		int value;
		
		public KeyValuePair(String key, int value) {
			this.key = key;
			this.value = value;
		}

	}
	
	public static boolean sendMail(String subject,String body,Properties prop, String attachment) throws MessagingException {
		boolean sent = false;
		Session session = Session.getInstance(prop, null);
		Message message = new MimeMessage(session);
		message.setSubject(subject);
		message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(prop.getProperty("mail.recipient")));
		message.setHeader("X-Mailer","msgsend");
		message.setSentDate(new Date());
		
		

		if (attachment != null) {
			MimeBodyPart mbp1 = new MimeBodyPart();
			mbp1.setText(body);
			
			MimeBodyPart mbp2 = new MimeBodyPart();
			// attach the file to the message
			FileDataSource fds = new FileDataSource(attachment);
			mbp2.setDataHandler(new DataHandler(fds));
			mbp2.setFileName(fds.getName());
			
			// create the Multipart and add its parts to it
			Multipart mp = new MimeMultipart();
			mp.addBodyPart(mbp1);
			mp.addBodyPart(mbp2);
			message.setContent(mp);
		}
		else {
			message.setText(body);			
		}
	      
		try{
			Transport.send(message);
			sent = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return sent;
	}

	public static void main(String[] args) {

		StringBuilder log = new StringBuilder();
		Properties properties = new Properties();
		String store = "";
		String strDate = "";
		String generatedFile = null;
		String generatedZeroFile = null;
		boolean generated = false;
		boolean generatedZero = false;
		boolean toGenerate = false;
		
		// assuming we have the files 
		try {
			String computerName = InetAddress.getLocalHost().getHostName();

			SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1); // date yesterday
			Date dateYesterday = cal.getTime();
			cal.add(Calendar.DATE, -6);
			Date dateLastWeek = cal.getTime();
			strDate = df.format(dateYesterday);
			SimpleDateFormat df2 = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss");
			log.append("[").append(computerName).append("]").append(df2.format(new Date())).append(" - Starting keywords retrieval\n");
			if (args.length < 1 || args[0] == null) {
				log.append("ERROR: no config file specified!\n");
				System.out.println("Please provide location of the properties file");
				return;
			}
			
			String configLocation = args[0];
			log.append("Config file: ").append(configLocation).append("\n");

		    properties.load(new FileInputStream(configLocation));
		    //String[0]"/home/solr/utilities/topkeywords/macmall.properties"));
		    
			String destFolder = properties.getProperty("destHome");
			store = properties.getProperty("store");
			String[] servers = properties.getProperty("remoteServers").split(",");
			String solrURL = properties.getProperty("solrURL");
			String user = properties.getProperty("remoteUser");
			String[] file = properties.getProperty("remoteFile").split(",");
			log.append("Store: ").append(store).append("\n");
			log.append("Servers: ");
			for (String server: servers) {
				log.append(server).append(" "); 
			}
			log.append("\n");
			log.append("User: ").append(user).append("\n");
			log.append("File: ").append(file).append("\n");
			
			File tmpInFolder = new File(destFolder + store);
			File outFolder = new File(destFolder + store + "/" + strDate);
			if (!outFolder.exists()) { // create directory structure if not present
				outFolder.mkdirs();
			}
			
			log.append("Input folder: ").append(tmpInFolder).append("\n");
			log.append("Output folder: ").append(outFolder).append("\n");

			HashMap<String, KeyValuePair> map = new HashMap<String,KeyValuePair>();
			int x=0;
			for (String server: servers) {
				//scp -p solr@afs-pl-schpd07.afservice.org:/home/solr/utility/keywords/MacMallbtorschprod03_topKeywords.csv /home/solr/utilities/topkeywords/macmall/macmall_afs-pl-schpd07_topkeywords.csv
				String outputFile = tmpInFolder + "/" + store+ "_" + server + "_topkeywords.csv";
// TODO: uncomment after testing				
//				String command = "scp -p " + user + "@" + server + ":" + file[x] + " " + outputFile;
//				x++;
//				Process p = Runtime.getRuntime().exec(command);
//				if (p.waitFor() != 0) {
//					log.append("Problem with scp: " + command);
//				}

				File f = new File(outputFile);
				// send out error if file not exists and date is more than one week from current date
				if (f.exists()) {
					log.append("Processing file: ").append(f.getAbsolutePath()).append("\n");
					Long diff = f.lastModified() - dateLastWeek.getTime() ;
					if (diff < 0) {
						log.append("File is older than a week: ").append(df2.format(new Date(f.lastModified()))).append(" Skipping file.\n");
						f.delete();
						continue;
					}
					BufferedReader reader = null;
					toGenerate = true;
					try {
						reader = new BufferedReader(new FileReader(f));
						String line = reader.readLine();
						if (line != null) {
							do {
								String[] entries = line.split(",", 2);
								try {
									int count = Integer.parseInt(entries[0]);
									String key = entries[1];
									if (map.containsKey(key)) {
										map.get(key).value += count;
									}
									else {
										map.put(key, new KeyValuePair(key,count));										
									}
								} catch (Exception e) {
									log.append("ERROR Something went wrong while processing line: ").append(line)
									   .append(". Error is ").append(e.getMessage()).append("\n");									
								}
								line = reader.readLine();
							} while (line != null);
						}
					}
					catch (Exception e) {
						log.append("ERROR Something went wrong while processing file. Error is ").append(e.getMessage()).append("\n");
					}
					finally {
						try {
							if (reader != null) {
								reader.close();
							}
						} catch (Exception e) {
						}
						if (f.exists()) {
							// move processed file
							File destFile = new File(outFolder.getAbsoluteFile() + "/" + f.getName());
							if (destFile.exists()) {
								destFile.delete();
							}
// TODO: uncomment after test							
//							f.renameTo(destFile);
						}
					}
				}
				else {
					log.append("WARNING File not found: ").append(f.getAbsolutePath()).append("\n");
					continue;
				}
				
			}
			
	        ArrayList<String> keywordCopy = new ArrayList<String>(map.keySet());

	        // create resource pool. TODO: make this configurable in config file
	        int poolSize = 100;
	        ZeroResults[] zeroResultsPool = new ZeroResults[poolSize];
	        for (int n = 0; n < poolSize; n++) {
	        	zeroResultsPool[n] = new ZeroResults(solrURL);
	        }
	        
	        // feed initial data
	        int i = 0;
	        while (!keywordCopy.isEmpty()) {
	        	String keyword = keywordCopy.remove(0);
	        	if (!zeroResultsPool[i].addKeywordToProcess(keyword)) {
	        		keywordCopy.add(keyword);
	        		break;
	        	}
	        	if (++i >= poolSize) {
	        		i = 0;
	        	}
	        }
	        
	        // start the process
	        for (int n = 0; n < poolSize; n++) {
	        	new Thread(zeroResultsPool[n]).start();
	        }
	        
	        // feed the rest of the data
	        i = 0;
	        while (!keywordCopy.isEmpty()) {
	        	String keyword = keywordCopy.remove(0);
	        	if (!zeroResultsPool[i].addKeywordToProcess(keyword)) {
	        		keywordCopy.add(keyword);
	        	}
	        	if (++i >= poolSize) {
	        		i = 0;
	        	}
	        }
	        
	        i = 0;
	        while (true) {
	        	if (zeroResultsPool[i].running) {
	        		i = 0;
	        		// wait for 10 secs
	        		try {
	        			Thread.sleep(10000);
	        		} catch (Exception e) {
	        		}
	        		continue;
	        	}
	        	else {
	        		// restart runnable if more words to process
	        		if (!zeroResultsPool[i].toProcess.isEmpty()) {
	        			new Thread(zeroResultsPool[i]).start();
	        			i = 0;
	        			continue;
	        		}
	        	}
	        	i++;
	        	if (i >= poolSize) {
	        		break;
	        	}
	        }

	        List<KeyValuePair> valuesZero = new ArrayList<KeyValuePair>();
	        List<String> forReprocess = new ArrayList<String>();
			for (int n = 0; n < poolSize; n++) {
				for (String key: zeroResultsPool[n].zeroResultsList) {
					valuesZero.add(map.get(key));					
				}
				forReprocess.addAll(zeroResultsPool[n].forReprocess);
			}
			
			if (!forReprocess.isEmpty()) {
				log.append("Please reprocess the following keywords to check for zero results:\n");
				for (String key: forReprocess) {
					log.append(key + "\n");
				}
			}
			
			Collections.sort(valuesZero, new Comparator<KeyValuePair>() {
				@Override
				public int compare(KeyValuePair arg0, KeyValuePair arg1) {
					int result = arg1.value - arg0.value;
					if (result == 0) {
						result = arg0.key.toLowerCase().compareTo(arg1.key.toLowerCase());
						if (result == 0) {
							result = arg0.key.compareTo(arg1.key);
						}
					}
					return result;
				}
			});
			
			if (valuesZero.size() > 0) {			
				BufferedWriter writer = null;
				generatedZeroFile = tmpInFolder.getAbsolutePath() + "/" + store + "_zero_report" + strDate +".csv";
				File outFile = new File(generatedZeroFile);
				try {
					outFile.createNewFile();
					writer = new BufferedWriter(new FileWriter(outFile));
					for (KeyValuePair kvp: valuesZero) {
						writer.write(String.valueOf(kvp.value));
						writer.write(",");
						writer.write(URLDecoder.decode(kvp.key, "UTF-8"));
						writer.write("\n");
					}
					generatedZero = true;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (writer != null) {
						writer.close();
					}
				}
				log.append("Generated zero results summary file: ").append(outFile.getAbsolutePath()).append("\n");
			}
			
			// sort
			List<KeyValuePair> values = new ArrayList<KeyValuePair>(map.values());
			Collections.sort(values, new Comparator<KeyValuePair>() {
				@Override
				public int compare(KeyValuePair arg0, KeyValuePair arg1) {
					int result = arg1.value - arg0.value;
					if (result == 0) {
						result = arg0.key.toLowerCase().compareTo(arg1.key.toLowerCase());
						if (result == 0) {
							result = arg0.key.compareTo(arg1.key);
						}
					}
					return result;
				}
			});
			
			
			
			if (toGenerate) {
				BufferedWriter writer = null;
				generatedFile = tmpInFolder.getAbsolutePath() + "/" + store + "_summary_" + strDate +".csv";
				File outFile = new File(generatedFile);
				try {
					outFile.createNewFile();
					writer = new BufferedWriter(new FileWriter(outFile));
					for (KeyValuePair kvp: values) {
						writer.write(String.valueOf(kvp.value));
						writer.write(",");
						writer.write(URLDecoder.decode(kvp.key, "UTF-8"));
						writer.write("\n");
					}
					generated = true;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (writer != null) {
						writer.close();
					}
				}
				log.append("Generated summary file: ").append(outFile.getAbsolutePath()).append("\n");
				
			}
			
		} catch (Exception e) {
			// send email report
			e.printStackTrace();
		}
		finally {
			if (!generated) {
				generatedFile = null;
				log.append("WARNING Output file was not generated!");
			}
			if (!generatedZero) {
				generatedZeroFile = null;
				log.append("No zero search result found. Zero report was not generated!");
			}
			try {
				if (sendMail(store + " " + strDate + " report ", log.toString(), properties, generatedFile)) {
					System.out.println(new Date() + ": Sent email notification.");
				}
				else {
					System.out.println(new Date() + ": Failed to send email notification.");
				}
				if (sendMail(store + " " + strDate + " zero_report ", log.toString(), properties, generatedZeroFile)) {
					System.out.println(new Date() + ": Sent email notification.");
				}
				else {
					System.out.println(new Date() + ": Failed to send email notification.");
				}
			} catch (MessagingException e) {
				System.out.println(new Date() + ": Failed to send email notification.");
			}
			
			System.out.println(log.toString());
		}
		
	}
}
