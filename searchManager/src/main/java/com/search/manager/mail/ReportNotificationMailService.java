package com.search.manager.mail;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service("reportNotificationMailService")
public class ReportNotificationMailService {

	@Autowired private EmailSender emailSender;
	@Autowired private SimpleMailMessage mailDetails;

	public boolean sendTopKeyword(File file, String filename, String[] recipients,ByteArrayInputStream bias, String contentType){
		SimpleMailMessage messageDetails = new SimpleMailMessage(mailDetails);
		String templateLocation = "default-topkeyword.vm";
		String subject = "[SearchManager] Top Keyword";
		Map<Object,Object> model = new HashMap<Object,Object>();
		Map<String, InputStreamSource> attachments = new HashMap<String, InputStreamSource>();

		messageDetails.setTo(recipients);
		messageDetails.setSubject(subject);

		attachments.put(filename, new AttachmentSource(file));

		return emailSender.send(messageDetails, templateLocation, model, attachments,bias,contentType);
	}

    public boolean sendTopKeyword(InputStream is, String filename, String[] recipients,ByteArrayInputStream bias, String contentType){
        SimpleMailMessage messageDetails = new SimpleMailMessage(mailDetails);
        String templateLocation = "default-topkeyword.vm";
        String subject = "[SearchManager] Top Keyword";
        Map<Object,Object> model = new HashMap<Object,Object>();
        Map<String, InputStreamSource> attachments = new HashMap<String, InputStreamSource>();

        messageDetails.setTo(recipients);
        messageDetails.setSubject(subject);

        attachments.put(filename, new AttachmentSource(is));

        return emailSender.send(messageDetails, templateLocation, model, attachments,bias,contentType);
    }

	public boolean sendZeroResult(File file, String filename, String[] recipients,ByteArrayInputStream bias, String contentType){
		SimpleMailMessage messageDetails = new SimpleMailMessage(mailDetails);
		String templateLocation = "default-zeroresult.vm";
		String subject = "[SearchManager] Zero Result";
		Map<Object,Object> model = new HashMap<Object,Object>();
		Map<String, InputStreamSource> attachments = new HashMap<String, InputStreamSource>();

		messageDetails.setTo(recipients);
		messageDetails.setSubject(subject);

		attachments.put(filename, new AttachmentSource(file));

		return emailSender.send(messageDetails, templateLocation, model, attachments,bias,contentType);
	}
}