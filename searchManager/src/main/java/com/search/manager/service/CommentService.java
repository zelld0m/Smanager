package com.search.manager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.Comment;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.utility.DateAndTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service(value = "commentService")
@RemoteProxy(
        name = "CommentServiceJS",
        creator = SpringCreator.class,
        creatorParams =
        @Param(name = "beanName", value = "commentService"))
public class CommentService {

    private static final Logger logger =
            LoggerFactory.getLogger(CommentService.class);
    @Autowired
    private DaoService daoService;
    @Autowired
    private DateAndTimeUtils dateAndTimeUtils;
    @Autowired
    private UtilityService utilityService;
    
    public DaoService getDaoService() {
        return daoService;
    }

    public void setDaoService(DaoService daoService) {
        this.daoService = daoService;
    }

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
                        date = dateAndTimeUtils.formatDateTimeUsingConfig(utilityService.getStoreId(), new Date(Long.parseLong(date)));
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
                    } catch (Exception e) {
                    }
                    // comment
                    start = end + 1;
                    String text = strComment.substring(start, start + commentSize);
                    comment.setComment(text);
                    commentList.add(0, comment);
                    // point to start of next comment
                    start += commentSize + 1;
                }
            } catch (Exception e) {
                logger.error("Failed during getComments()", e);
            }
        }
        RecordSet<Comment> comments = new RecordSet<Comment>(commentList, commentList.size());
        return comments;
    }

    @RemoteMethod
    public int addComment(String ruleType, String pComment, String[] ruleId) {
        int result = 0;
        try {
            for (String rsId : ruleId) {
                Comment comment = new Comment(new Store(utilityService.getStoreId()), rsId, RuleEntity.getId(ruleType), pComment, utilityService.getUsername());
                result = daoService.addComment(comment);
            }
        } catch (DaoException e) {
            logger.error("Failed during addComment()", e);
        }
        return result;
    }

    @RemoteMethod
    public int addRuleItemComment(String ruleType, String memberId, String pComment) {
        int result = 0;
        try {
            Comment comment = new Comment(new Store(utilityService.getStoreId()), memberId, RuleEntity.getId(ruleType), pComment, utilityService.getUsername());
            result = daoService.addComment(comment);
        } catch (DaoException e) {
            logger.error("Failed during addRuleItemComment()", e);
        }
        return result;
    }

    @RemoteMethod
    public RecordSet<Comment> getComment(String ruleType, String ruleId, int page, int itemsPerPage) {
        RecordSet<Comment> rSet = null;
        try {
            Comment comment = new Comment();
            comment.setReferenceId(ruleId);
            comment.setRuleTypeId(RuleEntity.getId(ruleType));
            rSet = daoService.getComment(new SearchCriteria<Comment>(comment, null, null, page, itemsPerPage));
        } catch (DaoException e) {
            logger.error("Failed during getComment()", e);
        }
        return rSet;
    }

    @RemoteMethod
    public int removeComment(Integer commentId) {
        int result = -1;
        try {
            result = daoService.removeComment(commentId);
        } catch (DaoException e) {
            logger.error("Failed during removeComment()", e);
        }
        return result;
    }
}
