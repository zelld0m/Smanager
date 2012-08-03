package com.search.manager.mail;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service("reportNotificationMailService")
public class ReportNotificationMailService {

	@Autowired private EmailSender emailSender;
	@Autowired private SimpleMailMessage mailDetails;
	
	public void sendTopKeyword(File file, String filename, String[] recipients){
		SimpleMailMessage messageDetails = mailDetails;
		String templateLocation = "default-topkeyword.vm";
		String subject = "[SearchManager] Top Keyword";
		Map<Object,Object> model = new HashMap<Object,Object>();
		Map<String, File> fileMap = new HashMap<String, File>();

		messageDetails.setTo(recipients);
		messageDetails.setSubject(subject);

		fileMap.put(filename, file);

		emailSender.send(messageDetails, templateLocation, model, fileMap);
	}
	
	public void sendZeroResult(File file, String filename, String[] recipients){
		SimpleMailMessage messageDetails = mailDetails;
		String templateLocation = "default-zeroresult.vm";
		String subject = "[SearchManager] Zero Result";
		Map<Object,Object> model = new HashMap<Object,Object>();
		Map<String, File> fileMap = new HashMap<String, File>();

		messageDetails.setTo(recipients);
		messageDetails.setSubject(subject);

		fileMap.put(filename, file);

		emailSender.send(messageDetails, templateLocation, model, fileMap);
	}
}