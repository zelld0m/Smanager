package com.search.manager.mail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.User;
import com.search.manager.service.UtilityService;
import com.search.ws.ConfigManager;
import com.search.ws.ConfigManager.PropertyFileType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service("workflowNotificationMailService")
public class WorkflowNotificationMailService {

	private static final Logger logger = LoggerFactory.getLogger(WorkflowNotificationMailService.class);

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final Pattern PATTERN = Pattern.compile(EMAIL_PATTERN);

	@Autowired
	private EmailSender emailSender;
	@Autowired
	private SimpleMailMessage mailDetails;
	@Autowired
	private DaoService daoService;

	public boolean sendNotification(RuleStatusEntity status, String ruleType,
			String approvedBy, List<RuleStatus> ruleStatusList, String comment) {

		Map<Object, Object> model = new HashMap<Object, Object>();
		SimpleMailMessage messageDetails = new SimpleMailMessage(mailDetails);
		String templateLocation = "";
		String subject = "";
		String actionBy = approvedBy;
		boolean flag = false;

		Set<String> cc = new HashSet<String>();
		Set<String> bcc = new HashSet<String>();

		cc.addAll(ConfigManager.getInstance().getPropertyList(PropertyFileType.MAIL,
				UtilityService.getStoreId(), "mail.workflow.cc"));
		bcc.addAll(ConfigManager.getInstance().getPropertyList(PropertyFileType.MAIL,
				UtilityService.getStoreId(), "mail.workflow.bcc"));

		try {
			User approvedByUser = daoService.getUser(approvedBy);
			if (approvedByUser != null) {
				cc.add(approvedByUser.getEmail());
				actionBy = approvedByUser.getFullName();
			}
		} catch (Exception e) {
			logger.error(
					"Error in getting 'approvedBy' user information. " + e, e);
			return false;
		}

		switch (status) {
		case PENDING:
			String pendingSubject = ConfigManager.getInstance()
					.getProperty(PropertyFileType.MAIL, UtilityService.getStoreId(),
							"mail.workflow.pendingSubject");
			subject = (StringUtils.isNotBlank(pendingSubject)) ? pendingSubject
					: "Search Manager: Rule Submitted For Approval";
			templateLocation = "default-submitted-for-approval.vm";
			cc.addAll(ConfigManager.getInstance().getPropertyList(PropertyFileType.MAIL,
					UtilityService.getStoreId(), "mail.workflow.pendingCc"));
			bcc.addAll(ConfigManager.getInstance().getPropertyList(PropertyFileType.MAIL,
					UtilityService.getStoreId(), "mail.workflow.pendingBcc"));

			try {
				Map<String, User> recipients = getApprover(UtilityService
						.getStoreId());

				if (recipients != null && recipients.size() > 0) {
					messageDetails.setTo(recipients.keySet().toArray(
							new String[recipients.keySet().size()]));
					if (recipients.size() > 1) {
						model.put("recipient", "All");
					} else {
						for (Map.Entry<String, User> entry : recipients
								.entrySet()) {
							model.put("recipient", StringUtils.trim(entry
									.getValue().getFullName()));
						}
					}
				} else {
					String recipient = ruleStatusList.get(0).getRequestBy();
					User user = daoService.getUser(recipient);
					messageDetails.setTo(user.getEmail());
					model.put("recipient", user.getFullName());
				}
			} catch (Exception e) {
				logger.error("Error getting 'approver' information. " + e, e);
				flag = false;
			}

			break;
		case APPROVED:
			String approvedSubject = ConfigManager.getInstance()
					.getProperty(PropertyFileType.MAIL, UtilityService.getStoreId(),
							"mail.workflow.approvedSubject");
			subject = (StringUtils.isNotBlank(approvedSubject)) ? approvedSubject
					: "Search Manager: Approved Rule(s)";
			templateLocation = "default-approval-approve.vm";
			cc.addAll(ConfigManager.getInstance().getPropertyList(PropertyFileType.MAIL, 
					UtilityService.getStoreId(), "mail.workflow.approvedCc"));
			bcc.addAll(ConfigManager.getInstance().getPropertyList(PropertyFileType.MAIL, 
					UtilityService.getStoreId(), "mail.workflow.approvedBcc"));
			break;
		case REJECTED:
			String rejectedSubject = ConfigManager.getInstance()
					.getProperty(PropertyFileType.MAIL, UtilityService.getStoreId(),
							"mail.workflow.rejectedSubject");
			subject = (StringUtils.isNotBlank(rejectedSubject)) ? rejectedSubject
					: "Search Manager: Rejected Rule(s)";
			templateLocation = "default-approval-reject.vm";
			cc.addAll(ConfigManager.getInstance().getPropertyList(PropertyFileType.MAIL, 
					UtilityService.getStoreId(), "mail.workflow.rejectedCc"));
			bcc.addAll(ConfigManager.getInstance().getPropertyList(PropertyFileType.MAIL, 
					UtilityService.getStoreId(), "mail.workflow.rejectedBcc"));
			break;
		case PUBLISHED:
			String publishedSubject = ConfigManager.getInstance()
					.getProperty(PropertyFileType.MAIL, UtilityService.getStoreId(),
							"mail.workflow.publishedSubject");
			subject = (StringUtils.isNotBlank(publishedSubject)) ? publishedSubject
					: "Search Manager: Published Rule(s)";
			templateLocation = "default-pushtoprod-publish.vm";
			cc.addAll(ConfigManager.getInstance().getPropertyList(PropertyFileType.MAIL, 
					UtilityService.getStoreId(), "mail.workflow.publishedCc"));
			bcc.addAll(ConfigManager.getInstance().getPropertyList(PropertyFileType.MAIL, 
					UtilityService.getStoreId(), "mail.workflow.publishedBcc"));
			break;
		case UNPUBLISHED:
			String unpublishedSubject = ConfigManager.getInstance()
					.getProperty(PropertyFileType.MAIL, UtilityService.getStoreId(),
							"mail.workflow.unpublishedSubject");

			subject = (StringUtils.isNotBlank(unpublishedSubject)) ? unpublishedSubject
					: "Search Manager: Un-published Rule(s)";
			templateLocation = "default-pushtoprod-unpublish.vm";
			cc.addAll(ConfigManager.getInstance().getPropertyList(PropertyFileType.MAIL, 
					UtilityService.getStoreId(), "mail.workflow.unpublishedCc"));
			bcc.addAll(ConfigManager.getInstance()
					.getPropertyList(PropertyFileType.MAIL, UtilityService.getStoreId(),
							"mail.workflow.unpublishedBcc"));
			break;
		default:
			return false;
		}

		// set Subject
		messageDetails.setSubject(subject);
		// set Cc
		validateEmail(cc);
		if (cc.size() > 0) {
			messageDetails.setCc(cc.toArray(new String[cc.size()]));
		}
		// set Bcc
		validateEmail(bcc);
		if (bcc.size() > 0) {
			messageDetails.setBcc(bcc.toArray(new String[bcc.size()]));
		}

		model.put("store", UtilityService.getStoreName());
		RuleEntity ruleEntity = RuleEntity.find(ruleType);
		model.put("ruleType", ruleEntity != null ? ruleEntity.getValues()
				.get(0) : ruleType);

		model.put("actionBy", StringUtils.trim(actionBy));

		DateTimeZone defTZ = DateTimeZone.forID(ConfigManager.getInstance()
				.getStoreParameter(UtilityService.getStoreId(),
						"default-timezone"));
		DateTime inNewTZ = DateTime.now().withZone(defTZ);
		String dateFormatted = "";

		try {
			String pattern = ConfigManager.getInstance().getStoreParameter(
					UtilityService.getStoreId(), "datetime-format");
			DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
			dateFormatted = formatter.print(inNewTZ);
		} catch (Exception e) {
			dateFormatted = inNewTZ.toString("MM/dd/yyyy hh:mm aa");
		}

		model.put("actionDate", dateFormatted + " " + inNewTZ.getZone().getID());
		model.put("comment", StringUtils.trim(comment));

		Map<String, List<RuleStatus>> userRuleStatus = new HashMap<String, List<RuleStatus>>();
		Map<String, User> users = new HashMap<String, User>();

		try {
			for (RuleStatus ruleStatus : ruleStatusList) {
				String recipientUsername = (status
						.equals(RuleStatusEntity.UNPUBLISHED)) ? ruleStatus
						.getPublishedBy() : ruleStatus.getRequestBy();

				if (StringUtils.isNotBlank(recipientUsername)) {
					recipientUsername = StringUtils.trim(recipientUsername);
					if (!users.containsKey(recipientUsername)) {
						User user = daoService.getUser(recipientUsername);
						if (user != null) {
							userRuleStatus.put(
									StringUtils.trim(user.getUsername()),
									new ArrayList<RuleStatus>());
							users.put(StringUtils.trim(user.getUsername()),
									user);
							userRuleStatus.get(recipientUsername).add(
									ruleStatus);
						}
					} else {
						userRuleStatus.get(recipientUsername).add(ruleStatus);
					}
				}
			}

			for (Entry<String, List<RuleStatus>> entry : userRuleStatus
					.entrySet()) {
				if (!status.equals(RuleStatusEntity.PENDING)) {
					// set To status != PENDING
					messageDetails.setTo(users.get(entry.getKey()).getEmail());
					model.put("recipient", StringUtils.trim(users.get(
							entry.getKey()).getFullName()));
				}

				List<RuleStatus> rStatusList = entry.getValue();
				StringBuffer content = new StringBuffer("<ul>");
				for (RuleStatus r : rStatusList) {
					content.append(String.format("<li> %s </li>",
							StringUtils.trim(r.getDescription())));
				}
				content.append("</ul>");
				model.put("rules", content);
				flag &= emailSender.send(messageDetails, templateLocation,
						model);
			}
		} catch (DaoException e) {
			logger.error("Error at WorkflowNotificationMailService.sendNotification()", e);
			flag = false;
		}

		return flag;
	}

	private void validateEmail(Collection<String> col) {
		CollectionUtils.filter(col, new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				Matcher matcher = PATTERN.matcher((String) object);
				return matcher.matches();
			}
		});
	}

	private Map<String, User> getApprover(String storeId) throws DaoException {
		Map<String, User> userMap = new HashMap<String, User>();

		User userFilter = new User();
		userFilter.setStoreId(storeId);
		userFilter.setGroupId((StringUtils.isNotBlank(ConfigManager
				.getInstance().getProperty(PropertyFileType.MAIL, UtilityService.getStoreId(),
						"mail.workflow.approver.group"))) ? ConfigManager
				.getInstance().getProperty(PropertyFileType.MAIL, UtilityService.getStoreId(),
						"mail.workflow.approver.group") : "APPROVER");
		userFilter.setAccountNonExpired(true);
		userFilter.setAccountNonLocked(true);
		userFilter.setCredentialsNonExpired(true);
		userFilter.setEnabled(true);

		SearchCriteria<User> searchCriteria = new SearchCriteria<User>(
				userFilter);
		RecordSet<User> users = daoService.getUsers(searchCriteria,
				MatchType.LIKE_NAME);

		if (users.getTotalSize() > 0) {
			for (User user : users.getList()) {
				userMap.put(user.getEmail(), user);
			}
			return userMap;
		}

		return null;
	}
}
