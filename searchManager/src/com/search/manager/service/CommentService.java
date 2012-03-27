package com.search.manager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;

import com.search.manager.model.Comment;
import com.search.manager.model.RecordSet;
import com.search.manager.utility.DateAndTimeUtils;

@RemoteProxy(
		name = "CommentServiceJS",
	    creator = SpringCreator.class,
	    creatorParams = @Param(name = "beanName", value = "commentService")
	)
public class CommentService {

	private static final Logger logger = Logger.getLogger(CommentService.class);

	public CommentService() {
	}
	
	@RemoteMethod
	public RecordSet<Comment> parseComment(String strComment) {
		List<Comment> commentList = new ArrayList<Comment>();
		if (StringUtils.isNotBlank(strComment)) {
			try {
				logger.info(String.format("parseComment %s ", strComment));
				int start = 0;
				int end = 0;
				while (start < strComment.length()) {
					Comment comment = new Comment();
					// date
					end = strComment.indexOf("|", start);
					String date = strComment.substring(start, end);
					if (StringUtils.isNotBlank(date)) {
						date = DateAndTimeUtils.formatDateTimeUsingConfig(UtilityService.getStoreName(), new Date(Long.parseLong(date)));
					}
					comment.setDate(date);
					// user
					start = end + 1;
					end = strComment.indexOf("|", start);
					String username = strComment.substring(start, end);
					comment.setUsername(username);
					// comment size
					int commentSize = 0;
					start = end + 1;
					end = strComment.indexOf("|", start);
					try {
						commentSize = Integer.parseInt(strComment.substring(start, end));
					}
					catch (Exception e) { }
					// comment
					start = end + 1;
					String text = strComment.substring(start, start + commentSize);
					comment.setComment(text);
					commentList.add(0, comment);
					// point to start of next comment
					start += commentSize + 1;
				}
			} catch (Exception e) {
				logger.error("Failed during getComments()",e);
			}
		}
		RecordSet<Comment> comments = new RecordSet<Comment>(commentList, commentList.size());
		return comments;
	}

}
