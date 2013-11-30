package com.search.manager.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.search.manager.authentication.dao.internal.UserDetailsImpl;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.jodatime.JodaPatternType;
import com.search.manager.mail.AccessNotificationMailService;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RoleModel;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.User;
import com.search.manager.schema.MessagesConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service(value = "securityService")
@RemoteProxy(
        name = "SecurityServiceJS",
        creator = SpringCreator.class,
        creatorParams =
        @Param(name = "beanName", value = "securityService"))
public class SecurityService {

    private static final Logger logger =
            LoggerFactory.getLogger(SecurityService.class);
    private static final String RESPONSE_STATUS_OK = "200";
    private static final String RESPONSE_STATUS_FAILED = "0";
    @Autowired
    private DaoService daoService;
    @Autowired
    private AccessNotificationMailService mailService;

    @RemoteMethod
    public RecordSet<User> getUserList(String roleId, String page, String search, String memberSince, String status, String expired) {
        User user = new User();
        user.setGroupId(StringUtils.trimToNull(roleId));
        user.setStoreId(UtilityService.getStoreId());
        user.setFullName(StringUtils.trimToNull(search));

        if (StringUtils.isNotEmpty(status)) {
            user.setAccountNonLocked(!StringUtils.equalsIgnoreCase("YES", status));
        }
        if (StringUtils.isNotEmpty(expired)) {
            user.setAccountNonExpired(!StringUtils.equalsIgnoreCase("YES", expired));
        }

        SearchCriteria<User> searchCriteria = new SearchCriteria<User>(user, null, null, Integer.parseInt(page), 10);
        searchCriteria.setEndDate(JodaDateTimeUtil.toDateTimeFromStorePattern(memberSince, JodaPatternType.DATE));
        RecordSet<User> users = getUsers(searchCriteria, MatchType.LIKE_NAME);
        for (User u : users.getList()) {
            // clear the password before returning
            u.setPassword(null);
        }
        return users;
    }

    @RemoteMethod
    public JSONObject deleteUser(String username) {
        JSONObject json = new JSONObject();
        username = StringUtils.trim(username);
        int result = -1;
        try {
            User user = new User();
            user.setUsername(username);
            user.setLastModifiedBy(UtilityService.getUsername());
            user.setStoreId(UtilityService.getStoreId());
            result = daoService.removeUser(user);
            if (result > -1) {
                json.put("status", RESPONSE_STATUS_OK);
                json.put("message", MessagesConfig.getInstance().getMessage("common.deleted", username));
                return json;
            }
        } catch (DaoException e) {
            logger.error("Failed during deleteUser()", e);
        }
        json.put("status", RESPONSE_STATUS_FAILED);
        json.put("message", MessagesConfig.getInstance().getMessage("common.not.deleted", username));
        return json;
    }

    @RemoteMethod
    public JSONObject resetPassword(String roleId, String username, String password) {

        JSONObject json = new JSONObject();
        roleId = StringUtils.trim(roleId);
        username = StringUtils.trim(username);
        int result = -1;

        try {
            User user = new User();
            user.setGroupId(roleId);
            user.setUsername(username);

            SearchCriteria<User> searchCriteria = new SearchCriteria<User>(user, null, null, null, 1);
            RecordSet<User> record = getUsers(searchCriteria, MatchType.MATCH_ID);

            if (record != null && record.getTotalSize() > 0) {
                user.setEmail(record.getList().get(0).getEmail());
                user.setFullName(record.getList().get(0).getFullName());
                user.setLastModifiedBy(UtilityService.getUsername());
                user.setStoreId(UtilityService.getStoreId());
                if (StringUtils.isNotBlank(password)) {
                    user.setPassword(UtilityService.getPasswordHash(password));
                }
                result = daoService.resetPassword(user);
            }

            if (result > -1) {
                user.setPassword(password);
                mailService.sendResetPassword(user);
                json.put("status", RESPONSE_STATUS_OK);
                json.put("message", MessagesConfig.getInstance().getMessage("password.updated", username));
                return json;
            }
        } catch (Exception e) {
            logger.error("Failed during resetPassword()", e);
        }

        json.put("status", RESPONSE_STATUS_FAILED);
        json.put("message", MessagesConfig.getInstance().getMessage("password.not.updated", username));
        return json;
    }

    @RemoteMethod
    public JSONObject addUser(String roleId, String rolename, String username, String fullname, String password, String expire, String locked, String email, String timezoneId) {
        JSONObject json = new JSONObject();
        String storeId = UtilityService.getStoreId();

        int result = -1;
        try {
            //check if username already exist
            username = StringUtils.trim(username);
            User user = daoService.getUser(username);

            if (user != null) {
                json.put("status", RESPONSE_STATUS_FAILED);
                json.put("message", MessagesConfig.getInstance().getMessage("username.exist"));
                return json;
            }

            user = new User();
            user.setFullName(fullname);
            user.setUsername(username);
            user.setEmail(email);
            user.setGroupId(roleId);
            user.setStoreId(storeId);
            user.setTimezoneId(timezoneId);

            if (StringUtils.isNotEmpty(locked)) {
                user.setAccountNonLocked(!"true".equalsIgnoreCase(locked));
            }

            user.setThruDate(JodaDateTimeUtil.toDateTimeFromStorePattern(storeId, expire, JodaPatternType.DATE));
            user.setPassword(UtilityService.getPasswordHash(password));
            user.setCreatedBy(UtilityService.getUsername());
            result = daoService.addUser(user);

            if (result > -1) {
                user.setPassword(password);
                mailService.sendAddUser(user);
                json.put("status", RESPONSE_STATUS_OK);
                json.put("message", MessagesConfig.getInstance().getMessage("common.added", username));
                return json;
            }
        } catch (DaoException e) {
            logger.error("Failed during addComment()", e);
        }

        json.put("status", RESPONSE_STATUS_FAILED);
        json.put("message", MessagesConfig.getInstance().getMessage("common.not.added", username));

        return json;
    }

    @RemoteMethod
    public RecordSet<RoleModel> getRoleList() {
        List<RoleModel> roleList = new ArrayList<RoleModel>();
        try {
            List<String> gpList = daoService.getGroups();
            int cnt = 0;

            for (String gp : gpList) {
                RoleModel role = new RoleModel();
                role.setId(gp);
                role.setRolename(gp);
                if (cnt == 0) {
                    role.setDefault(true); // make default
                }
                roleList.add(role);
            }
        } catch (DaoException e) {
            logger.error("Error in SecurityService.getRoleList " + e, e);
        }
        return new RecordSet<RoleModel>(roleList, roleList.size());
    }
    
    @RemoteMethod
    public RecordSet<RuleStatusEntity> getRuleEntityList() {
        List<RuleStatusEntity> statusLIst = Arrays.asList(RuleStatusEntity.APPROVAL_STATUS);
        
        return new RecordSet<RuleStatusEntity>(statusLIst, statusLIst.size());
    }

    @RemoteMethod
    public RoleModel getRole(String id) {
        List<RoleModel> list = new ArrayList<RoleModel>();
        try {
            List<String> gpList = daoService.getGroups();
            for (String gp : gpList) {
                RoleModel role = new RoleModel();
                role.setId(gp);
                role.setRolename(gp);
                if ((gp).equalsIgnoreCase(id)) {
                    role.setDefault(true); // get default
                    return role;
                }
                list.add(role);
            }
        } catch (Exception e) {
            logger.error("Error in SecurityService.getRole " + e, e);
        }

        if (list.size() > 0) {
            return list.get(0);
        }

        return new RoleModel();
    }

    private RecordSet<User> getUsers(SearchCriteria<User> searchCriteria, MatchType matchTypeUser) {
        try {
            return daoService.getUsers(searchCriteria, matchTypeUser);
        } catch (DaoException e) {
            logger.error("Error in SecurityService.getUsers " + e, e);
        }
        return new RecordSet<User>(null, 0);
    }

    @RemoteMethod
    public JSONObject updateUser(String roleId, String username, String expire, String locked, String email, String timezoneId) {
        JSONObject json = new JSONObject();
        username = StringUtils.trim(username);
        String storeId = UtilityService.getStoreId();

        int result = -1;

        try {
            User user = new User();
            user.setUsername(username);
            user.setLastModifiedBy(UtilityService.getUsername());
            SearchCriteria<User> searchCriteria = new SearchCriteria<User>(user, null, null, null, 1);
            RecordSet<User> record = getUsers(searchCriteria, MatchType.MATCH_ID);

            if (record != null && record.getTotalSize() > 0) {
                user.setGroupId(roleId);
                if (StringUtils.isNotBlank(expire)) {
                    DateTime newExpiryDate = JodaDateTimeUtil.toDateTimeFromStorePattern(storeId, expire, JodaPatternType.DATE);
                    user.setThruDate(newExpiryDate);
                    user.setAccountNonExpired(newExpiryDate.isAfter(DateTime.now()));
                }
                user.setStoreId(UtilityService.getStoreId());
                if (StringUtils.isNotEmpty(locked)) {
                    user.setAccountNonLocked(!"true".equalsIgnoreCase(locked));
                }
                user.setEmail(email);
                user.setTimezoneId(timezoneId);
                user.setLastModifiedBy(UtilityService.getUsername());
                result = daoService.updateUser(user);
            }

            if (result > -1) {

                //Reload authentication if modified user is the current logged in user
                UserDetailsImpl userDetailsImpl = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

                if (userDetailsImpl != null && userDetailsImpl.getUsername().equalsIgnoreCase(username)) {
                    userDetailsImpl.setDateTimeZoneId(timezoneId);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsImpl, userDetailsImpl.getPassword(), userDetailsImpl.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

                json.put("status", RESPONSE_STATUS_OK);
                json.put("message", MessagesConfig.getInstance().getMessage("common.updated", username));
                return json;
            }
        } catch (Exception e) {
            logger.error("Failed during updateUser()", e);
        }

        json.put("status", RESPONSE_STATUS_FAILED);
        json.put("message", MessagesConfig.getInstance().getMessage("common.not.updated", username));
        return json;
    }
}