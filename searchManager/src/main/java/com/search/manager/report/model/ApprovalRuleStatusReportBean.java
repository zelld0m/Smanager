package com.search.manager.report.model;

import com.search.manager.model.Comment;
import com.search.manager.model.RuleStatus;
import com.search.manager.report.annotation.ReportField;

public class ApprovalRuleStatusReportBean extends ReportBean<RuleStatus> {

	public ApprovalRuleStatusReportBean(RuleStatus model) {
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

	@ReportField(label="Update Status", size=20, sortOrder=3)
	public String getUpdateStatus() {
		return model.getUpdateStatus();
	}
	
	@ReportField(label="Comment", size=20, sortOrder=4)
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