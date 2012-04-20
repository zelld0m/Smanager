package com.search.manager.model;

import java.io.Serializable;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class Comment implements Serializable {
	
private static final long serialVersionUID = 1L;
	
	private String username;
	private String date;
	private String comment;
	private String commentId;
	private String referenceId;
	
	public Comment() {
	}

	public Comment(String commentId, String referenceId, String comment, String username, String date) {
		super();
		this.username = username;
		this.date = date;
		this.comment = comment;
		this.commentId = commentId;
		this.referenceId = referenceId;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getUsername() {
		return username;
	}

}
