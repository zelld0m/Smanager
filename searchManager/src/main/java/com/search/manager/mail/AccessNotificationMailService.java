package com.search.manager.mail;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import com.search.manager.model.User;

@Service("accessNotificationMailService")
public class AccessNotificationMailService {

	@Autowired private EmailSender emailSender;
	@Autowired private SimpleMailMessage mailDetails;
	
	private final static String APP_NAME = "Search Manager";
	private final static String APP_LINK = "";
	
	public void sendAddUser(User user){
		SimpleMailMessage messageDetails = new SimpleMailMessage(mailDetails);
		String templateLocation = "default-adduser.vm";
		String subject = "[SearchManager] Welcome to Search Manager";
		Map<Object,Object> model = new HashMap<Object,Object>();
		
		messageDetails.setTo(user.getEmail());
		messageDetails.setSubject(subject);
		
		model.put("fullName", user.getFullName());
		model.put("username", user.getUsername());
		model.put("password", user.getPassword());
		model.put("app-name", APP_NAME);
		model.put("app-link", APP_LINK);
		
		emailSender.send(messageDetails, templateLocation, model);
	}
	
	public void sendResetPassword(User user){
		SimpleMailMessage messageDetails = new SimpleMailMessage(mailDetails);
		String templateLocation = "default-resetpassword.vm";
		String subject = "[SearchManager] Your password was reset";
		Map<Object,Object> model = new HashMap<Object,Object>();
		
		messageDetails.setTo(user.getEmail());
		messageDetails.setSubject(subject);
		
		model.put("fullName", user.getFullName());
		model.put("username", user.getUsername());
		model.put("password", user.getPassword());
		model.put("app-name", APP_NAME);
		model.put("app-link", APP_LINK);
		
		emailSender.send(messageDetails, templateLocation, model);
	}
}