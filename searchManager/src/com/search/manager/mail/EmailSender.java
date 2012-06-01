package com.search.manager.mail;

import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

@Component("emailSender")
public class EmailSender{
	
	private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);

	private final VelocityEngine velocityEngine;
	private final JavaMailSender mailSender;

	@Autowired
	public EmailSender(VelocityEngine velocityEngine, 
			JavaMailSender mailSender) {
		this.velocityEngine = velocityEngine;
		this.mailSender = mailSender;
	}

	/**
	 * Sends e-mail using Velocity template for the body and 
	 * the properties passed in as Velocity variables. 
	 * 
	 * @param messageDetails		The e-mail message to be sent, except for the body.
	 * @param templateLocation		Template to be used
	 * @param hTemplateVariables	Variables to use when processing the template.
	 */
	public void send(final SimpleMailMessage messageDetails,
					 final String templateLocation,
					 final Map<Object, Object> hTemplateVariables) {
		
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			
			public void prepare(MimeMessage mimeMessage) throws Exception {
				
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
				message.setTo(messageDetails.getTo());
				message.setFrom(messageDetails.getFrom());
				message.setCc(messageDetails.getCc());
				message.setBcc(messageDetails.getBcc());
				message.setSubject(messageDetails.getSubject());
			
				String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateLocation, hTemplateVariables);

				logger.info("body={}", body);

				message.setText(body, true);
			}
		};

		try {
			mailSender.send(preparator);
			logger.info("Sent e-mail to '{}'.", messageDetails.getTo());
		} catch (MailException e) {
			logger.error("catched MailException {}", e);
		} catch (Exception e) {
			logger.error("catched MailException {}", e);
		}
	}
}