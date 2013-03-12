package com.mall.mail;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.search.manager.utility.StringUtil;

public class MailNotifier {

	private static final Logger logger = Logger.getLogger(MailNotifier.class);

	private Message message;

	@SuppressWarnings("unused")
	private MailNotifier() {
		// do nothing...
	}

	public MailNotifier(String subject, String body, Properties prop)
			throws MessagingException {
		String localhostname = "";

		try {
			localhostname = java.net.InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			logger.error(e);
		}

		String recepient = prop.getProperty("mail.recepient");
		String cc = prop.getProperty("mail.cc");

		Session session = Session.getInstance(prop, null);
		setMessage(new MimeMessage(session));
		getMessage().setSubject(
				localhostname
						+ " "
						+ StringUtil.ifNull(subject,
								prop.getProperty("mail.subject")));
		getMessage().setRecipients(
				Message.RecipientType.TO,
				InternetAddress.parse(StringUtil.ifNull(recepient,
						"Jesrome.Jamolod@pcmall.com"), false));
		if (!StringUtil.isBlank(cc)) {
			getMessage().setRecipients(Message.RecipientType.CC,
					InternetAddress.parse(cc, false));
		}
		getMessage().setText(body);
		getMessage().setHeader("X-Mailer", "msgsend");
		getMessage().setSentDate(new Date());
	}

	public void send() throws MessagingException {
		try {
			Transport.send(getMessage());
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public Message getMessage() {
		return message;
	}

	private void setMessage(Message message) {
		this.message = message;
	}

}
