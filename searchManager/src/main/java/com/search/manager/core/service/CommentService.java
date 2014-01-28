package com.search.manager.core.service;

import java.util.Map;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.Comment;
import com.search.manager.enums.RuleStatusEntity;

public interface CommentService extends GenericService<Comment> {

    Map<String, Integer> addRuleStatusComment(RuleStatusEntity ruleStatus, String store, String username,
            String pComment, String... ruleStatusId) throws CoreServiceException;

    // Add CommentService specific method here...

}
