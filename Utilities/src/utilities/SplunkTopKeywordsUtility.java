package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

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

import org.apache.commons.lang.StringUtils;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;


public class SplunkTopKeywordsUtility {
	
			
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
			cal.add(Calendar.DATE, -1); // date yesterday  - for PCMALL only
			Date dateYesterday = cal.getTime();
			cal.add(Calendar.DATE, -6); // date 1 week ago
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

		    String destTopKeywordsFolder = properties.getProperty("destHome")+"topkeywords/";
			String destZeroResultsFolder = properties.getProperty("destHome")+"zeroresults/";
			String srcSplunkReportFolder = properties.getProperty("destHome")+"splunk/";
			
			int reportRange = 0;
			try {
				reportRange = Integer.parseInt(properties.getProperty("defaultReportDateRange"));
				if (reportRange < 0) {
					reportRange = 0;
				}
			} catch (Exception e) {
			}

			store = properties.getProperty("store");
			String solrURL = properties.getProperty("solrURL");
			log.append("Store: ").append(store).append("\n");
			log.append("Solr URL: ").append(solrURL).append("\n");
			log.append("\n");
			log.append("Report from: ").append(new SimpleDateFormat("MMM dd, yyyy").format(dateLastWeek)).append(" to ")
			   .append(new SimpleDateFormat("MMM dd, yyyy").format(dateYesterday)).append("\n");
			
			
			File tmpInFolder = new File(srcSplunkReportFolder + store);
			File outFolder = new File(destTopKeywordsFolder + store);
			if (!outFolder.exists()) { // create directory structure if not present
				outFolder.mkdirs();
			}
			
			log.append("Input folder: ").append(tmpInFolder).append("\n");
			log.append("Output folder: ").append(outFolder).append("\n");

			HashMap<String, KeyValuePair> map = new HashMap<String,KeyValuePair>();

			ArrayList<String> filenames = new ArrayList<String>();
			
			cal.setTime(dateYesterday);
			for (int i=0; i<7; i++) {
				String filename = String.format("%04d%02d%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));
				filenames.add(filename);
				System.out.println(filename);
				cal.add(Calendar.DATE, -1);
			}
			
			
			for (String filename: filenames) {
				String outputFile = tmpInFolder + "/" + filename.substring(0, 6) + "/" + filename + ".csv";

				File f = new File(outputFile);
				// send out error if file not exists and date is more than one week from current date
				if (f.exists()) {
					log.append("Processing file: ").append(f.getAbsolutePath()).append("\n");
					CSVReader reader = null;
					toGenerate = true;
					try {
//						reader = new CSVReader(new FileReader(f));
						reader = new CSVReader(new FileReader(f), ',', '\"', '\0', 0, true);
						reader.readNext(); // discard first line (column headers)
						String[] line = reader.readNext();
						if (line != null) {
							do {
//if (map.size() > 10) break;						
								try {
									int count = Integer.parseInt(line[1]);
									String key = line[0];
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
								line = reader.readNext();
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
					}
				}
				else {
					log.append("WARNING File not found: ").append(f.getAbsolutePath()).append("\n");
					continue;
				}
			}
			
//			List<KeyValuePair> kvpList = new ArrayList<KeyValuePair>(map.values());
//			Collections.sort(kvpList, new Comparator<KeyValuePair>() {
//				@Override
//				public int compare(KeyValuePair arg0, KeyValuePair arg1) {
//					int result = arg1.value - arg0.value;
//					String key0 = arg0.key;
//					try {
//						key0 = URLDecoder.decode(arg0.key, "UTF-8");
//					} catch (UnsupportedEncodingException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					String key1 = arg1.key;
//					try {
//						key1 = URLDecoder.decode(arg1.key, "UTF-8");
//					} catch (UnsupportedEncodingException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					if (result == 0) {
//						result = key0.toLowerCase().compareTo(key1.toLowerCase());
//						if (result == 0) {
//							result = key0.compareTo(key1);
//						}
//					}
//					return result;
//				}
//			});
//			
//			for (KeyValuePair kvp: kvpList) {
//				System.out.println(String.format("%5s %s", kvp.value, URLDecoder.decode(kvp.key, "UTF-8")));
//			}
//			
//			if (true) {
//				return;
//			}
			
			TopKeywords topKeywords = new TopKeywords(solrURL);
			List<String[]> valuesZero = topKeywords.processKeywords(map, solrURL);
			
			for (String[] line: valuesZero) {
				try {
					line[0] = line[0];
				} catch (Exception e) {
				}
			}
			
			
			if (!topKeywords.forReprocess.isEmpty()) {
				log.append("Please reprocess the following keywords to check for top keywords:\n");
				for (String key: topKeywords.forReprocess) {
					log.append(key + "\n");
				}
			}
			
			Collections.sort(valuesZero, new Comparator<String[]>() {
				@Override
				public int compare(String[] arg0, String[] arg1) {
					int val0 = 0, val1 = 0;
					try {
						val0 = Integer.parseInt(arg0[3]);
					} catch (Exception e) {
					}
					try {
						val1 = Integer.parseInt(arg1[3]);
					} catch (Exception e) {
					}
					int result = val1 - val0;
					if (result == 0) {
						result = arg0[0].toLowerCase().compareTo(arg1[0].toLowerCase());
						if (result == 0) {
							result = arg0[0].compareTo(arg1[0]);
						}
					}
					return result;
				}
			});
			
			if (valuesZero.size() > 0) {			
				CSVWriter writer = null;
				generatedZeroFile = destZeroResultsFolder + "/" + store + "/" + store + "_zero_report_" + strDate +"-splunk.csv";
				File outFile = new File(generatedZeroFile);
				try {
					outFile.createNewFile();
					writer = new CSVWriter(new FileWriter(outFile));
					for (String[] line: valuesZero) {
						if (StringUtils.isNotBlank(line[2]) && StringUtils.equals("0", line[2])) {
							writer.writeNext(new String[]{line[3], line[0]});							
						}
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

			
			if (toGenerate) {
				CSVWriter writer = null;
				generatedFile = destTopKeywordsFolder + "/" + store + "/" + store + "_summary_" + strDate +"-splunk.csv";
				File outFile = new File(generatedFile);
				try {
					outFile.createNewFile();
					writer = new CSVWriter(new FileWriter(outFile));
					for (String[] line: valuesZero) {
						writer.writeNext(new String[]{line[3], line[0], line[2], line[1]});							
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
//			if (!generated) {
//				generatedFile = null;
//				log.append("WARNING Output file was not generated!");
//			}
//			if (!generatedZero) {
//				generatedZeroFile = null;
//				log.append("No zero search result found. Zero report was not generated!");
//			}
//			try {
//				if (sendMail(store + " " + strDate + " report ", log.toString(), properties, generatedFile)) {
//					System.out.println(new Date() + ": Sent email notification.");
//				}
//				else {
//					System.out.println(new Date() + ": Failed to send email notification.");
//				}
//				if (sendMail(store + " " + strDate + " zero_report ", log.toString(), properties, generatedZeroFile)) {
//					System.out.println(new Date() + ": Sent email notification.");
//				}
//				else {
//					System.out.println(new Date() + ": Failed to send email notification.");
//				}
//			} catch (MessagingException e) {
//				System.out.println(new Date() + ": Failed to send email notification.");
//			}
			
			System.out.println(log.toString());
		}
		
	}
}
