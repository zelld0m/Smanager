package com.search.manager.report.model;

import com.search.manager.model.Comment;
import com.search.manager.model.RuleStatus;
import com.search.manager.report.annotation.ReportField;

public class PublishRuleStatusReportBean extends ReportBean<RuleStatus> {

	public PublishRuleStatusReportBean(RuleStatus model) {
		super(model);
	}

	@ReportField(label="Rule Id", size=20, sortOrder=1)
	public String getId() {
		return model.getRuleRefId();
	}

	@ReportField(label="Rule Name", size=20, sortOrder=2)
	public String getName() {
		return model.getDescription();
	}

	@ReportField(label="Approval Status", size=20, sortOrder=3)
	public String getApprovalStatus() {
		return model.getApprovalStatus();
	}
	
	@ReportField(label="Production Status", size=20, sortOrder=3)
	public String getProductionStatus() {
		return model.getPublishedStatus();
	}
	
	@ReportField(label="Last Published Date", size=20, sortOrder=5)
	public String getLastPublishedDate() {
		return model.getFormattedLastPublishedDateTime();
	}
	
	@ReportField(label="Comment", size=20, sortOrder=6)
	public String getComment() {
		StringBuilder comments = new StringBuilder();
		if (model.getCommentList() != null && model.getCommentList().size() > 0) {
			for (Comment comment : model.getCommentList()) {
				comments.append("\n").append(comment.getUsername()).append(" : ").append(comment.getCreatedDate()).append(" : " ).append(comment.getComment());
			}
			comments.deleteCharAt(0);
		}
		return comments.toString();			
	}	
}