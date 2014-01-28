package com.search.manager.core.service.sp;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.core.dao.CommentDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.Comment;
import com.search.manager.core.model.Store;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.CommentService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;

@Service("commentServiceSp")
public class CommentServiceSpImpl implements CommentService {

    @Autowired
    @Qualifier("commentDaoSp")
    private CommentDao commentDao;

    @Override
    public Comment add(Comment model) throws CoreServiceException {
        // TODO validation here...
        // TODO add spring transaction...
        // Validate required fields.

        try {
            return commentDao.add(model);
        } catch (CoreDaoException e) {
            throw new CoreServiceException(e);
        }
    }

    @Override
    public List<Comment> add(Collection<Comment> models) throws CoreServiceException {
        // TODO validation here...
        // TODO add spring transaction...
        // Validate required fields.

        try {
            return (List<Comment>) commentDao.add(models);
        } catch (CoreDaoException e) {
            throw new CoreServiceException(e);
        }
    }

    @Override
    public Comment update(Comment model) throws CoreServiceException {
        // TODO validation here...
        // TODO add spring transaction...
        // Validate required fields.

        try {
            return commentDao.update(model);
        } catch (CoreDaoException e) {
            throw new CoreServiceException(e);
        }
    }

    @Override
    public List<Comment> update(Collection<Comment> models) throws CoreServiceException {
        // TODO validation here...
        // TODO add spring transaction...
        // Validate required fields.

        try {
            return (List<Comment>) commentDao.update(models);
        } catch (CoreDaoException e) {
            throw new CoreServiceException(e);
        }
    }

    @Override
    public boolean delete(Comment model) throws CoreServiceException {
        // TODO validation here...
        // TODO add spring transaction...
        // Validate required fields.

        try {
            return commentDao.delete(model);
        } catch (CoreDaoException e) {
            throw new CoreServiceException(e);
        }
    }

    @Override
    public Map<Comment, Boolean> delete(Collection<Comment> models) throws CoreServiceException {
        // TODO validation here...
        // TODO add spring transaction...
        // Validate required fields.

        try {
            return commentDao.delete(models);
        } catch (CoreDaoException e) {
            throw new CoreServiceException(e);
        }
    }

    @Override
    public SearchResult<Comment> search(Search search) throws CoreServiceException {
        try {
            // TODO validation here...
            return commentDao.search(search);
        } catch (CoreDaoException e) {
            throw new CoreServiceException(e);
        }
    }

    @Override
    public SearchResult<Comment> search(Comment model) throws CoreServiceException {
        if (model != null) {
            try {
                return commentDao.search(model);
            } catch (CoreDaoException e) {
                throw new CoreServiceException(e);
            }
        }
        return null;
    }

    @Override
    public SearchResult<Comment> search(Comment model, int pageNumber, int maxRowCount) throws CoreServiceException {
        if (model != null) {
            try {
                return commentDao.search(model, pageNumber, maxRowCount);
            } catch (CoreDaoException e) {
                throw new CoreServiceException(e);
            }
        }
        return null;
    }

    @Override
    public Comment searchById(String storeId, String id) throws CoreServiceException {
        Comment comment = new Comment();
        comment.setStore(new Store(storeId));
        comment.setReferenceId(id);

        SearchResult<Comment> searchResult = search(comment);

        if (searchResult.getTotalCount() > 0) {
            return searchResult.getResult().get(0);
        }
        return null;
    }

    @Override
    public Map<String, Integer> addRuleStatusComment(RuleStatusEntity ruleStatus, String store, String username,
            String pComment, String... ruleStatusId) throws CoreServiceException {
        Map<String, Integer> resultMap = new HashMap<String, Integer>();
        String formatString = "%s";
        if (ruleStatus != null) {
            switch (ruleStatus) {
                case APPROVED:
                    formatString = "[APPROVED] %s";
                    break;
                case REJECTED:
                    formatString = "[REJECTED] %s";
                    break;
                case PUBLISHED:
                    formatString = "[PUBLISHED] %s";
                    break;
                case UNPUBLISHED:
                    formatString = "[UNPUBLISHED] %s";
                    break;
                case PENDING:
                    formatString = "[REQUEST] %s";
                    break;
                case IMPORTED:
                    formatString = "[IMPORTED] %s";
                    break;
                case EXPORTED:
                    formatString = "[EXPORTED] %s";
                    break;
                default:
                    break;
            }
        }

        Comment comment = new Comment();
        comment.setRuleTypeId(RuleEntity.RULE_STATUS.getCode());
        comment.setUsername(username);
        comment.setComment(String.format(formatString, pComment));
        comment.setStore(new Store(store));
        for (String rsId : ruleStatusId) {
            comment.setReferenceId(rsId);
            int result = -1;
            try {
                if (add(comment) != null) {
                    result = 1;
                }
            } catch (CoreServiceException e) {
            }

            resultMap.put(rsId, result);
        }

        return resultMap;
    }

}
