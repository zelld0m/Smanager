package utilities;

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


public class TopKeywordsUtility {
	
			
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
		    
			String destFolder = properties.getProperty("destHome")+"topkeywords/";
			String destFolderZero = properties.getProperty("destHome")+"zeroresults/";
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
									if (key != null && key.contains("!dismax")) {
										// ignore
										line = reader.readLine();
										continue;										
									}
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
			
			ZeroResults zero = new ZeroResults(solrURL);
			List<KeyValuePair> valuesZero = zero.processKeywords(map, solrURL);
			
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
				generatedZeroFile = destFolderZero + store + "/" + store + "_zero_report" + strDate +".csv";
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
