package com.search.manager.mail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.User;

@Service("workflowNotificationMailService")
public class WorkflowNotificationMailService {

	private static final Logger logger = Logger
			.getLogger(WorkflowNotificationMailService.class);

	@Autowired
	private EmailSender emailSender;
	@Autowired
	private SimpleMailMessage mailDetails;
	@Autowired
	private DaoService daoService;

	public boolean sendNotification(RuleStatusEntity status, String ruleType,
			String approvedBy, List<RuleStatus> ruleStatusList, String comment) {
		SimpleMailMessage messageDetails = mailDetails;
		String templateLocation = "";
		String subject = "";
		Map<Object, Object> model = new HashMap<Object, Object>();
		boolean flag = false;

		switch (status) {
		case APPROVED:
			subject = "[SearchManager] Approval";
			templateLocation = "default-approval-approve.vm";
			break;
		case REJECTED:
			subject = "[SearchManager] Approval";
			templateLocation = "default-approval-reject.vm";
			break;
		case PUBLISHED:
			subject = "[SearchManager] Push To Prod";
			templateLocation = "default-pushtoprod-publish.vm";
			break;
		case UNPUBLISHED:
			subject = "[SearchManager] Push To Prod";
			templateLocation = "default-pushtoprod-unpublish.vm";
			break;
		default:
			return false;
		}

		messageDetails.setSubject(subject);
		model.put("comment", comment);
		
		try {
			User approvedByUser = daoService.getUser(approvedBy);
			messageDetails.setCc(approvedByUser.getEmail());
			model.put("approvedBy",
					StringUtils.trim(approvedByUser.getFullName()));
		} catch (DaoException e1) {
			logger.error("Error in getting approvedBy User information.", e1);
		}

		Map<String, List<RuleStatus>> userRuleStatus = new HashMap<String, List<RuleStatus>>();
		Map<String, User> users = new HashMap<String, User>();

		for (RuleStatus ruleStatus : ruleStatusList) {
			if (ruleStatus.getRequestBy() != null) {
				try {
					User user = daoService.getUser(ruleStatus.getRequestBy());
					if (!userRuleStatus.containsKey(user.getUsername())) {
						userRuleStatus.put(user.getUsername(),
								new ArrayList<RuleStatus>());
						users.put(user.getUsername(), user);
					}
					userRuleStatus.get(user.getUsername()).add(ruleStatus);
				} catch (DaoException e) {
					logger.error(e);
				}
			}
		}

		for (Entry<String, List<RuleStatus>> entry : userRuleStatus.entrySet()) {
			messageDetails.setTo(users.get(entry.getKey()).getEmail());
			model.put("requestBy",
					StringUtils.trim(users.get(entry.getKey()).getFullName()));

			List<RuleStatus> rStatusList = entry.getValue();
			StringBuffer content = new StringBuffer("<ul>");
			for (RuleStatus r : rStatusList) {
				content.append(String.format("<li> %s </li>",
						StringUtils.trim(r.getDescription())));
			}
			content.append("</ul>");
			model.put("ruleType", ruleType);
			model.put("rules", content);
			flag &= emailSender.send(messageDetails, templateLocation, model);
		}

		return flag;
	}
}
