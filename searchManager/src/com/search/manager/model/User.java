package com.search.manager.model;

import org.apache.commons.lang.BooleanUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.joda.time.DateTime;

import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.jodatime.JodaPatternType;

@DataTransferObject(converter = BeanConverter.class)
public class User extends ModelBean {

	private static final long serialVersionUID = 1L;

	private String username;
	private String fullName;
	private String password;
	private String email;
	private String groupId;
	private Boolean accountNonLocked;
	private Boolean credentialsNonExpired;
	private Boolean accountNonExpired;
	private DateTime lastAccessDateTime;
	private String ip;
	private DateTime thruDate;
	private Integer successiveFailedLogin;
	private String storeId;
	private String dateTimeZoneId;
	private String permissionId; 


	//	private String username;
	//	private String fullName;
	//	private String password;
	//	private String email;
	//	private String groupId;				private String roleId;			private String type; -> change to Role class
	//	private Boolean accountNonLocked;		private String status;
	//	private Boolean credentialsNonExpired;
	//	private Boolean accountNonExpired;
	//	private Date lastAccessDate;			private String lastAccess;
	//	private String ip;
	//	private String createdBy;
	//	private String lastModifiedBy;
	//	private Date createdDate;			private String dateStarted;
	//	private Date lastModifiedDate;
	//	private Date thruDate;				private String expired;
	//	private Integer successiveFailedLogin;		private boolean locked;
	//	private String storeId;



	public User() {
	}

	public User(String username, String fullName, String password, String email, String groupId, boolean accountNonLocked,
			boolean credentialsNonExpired, DateTime lastAccessDateTime, String ip, String createdBy, String lastModifiedBy,
			DateTime createdDateTime, DateTime lastModifiedDateTime, DateTime thruDate, String storeId, String dateTimeZoneId) {
		super();
		this.username = username;
		this.fullName = fullName;
		this.password = password;
		this.email = email;
		this.groupId = groupId;
		this.accountNonLocked = accountNonLocked;
		this.credentialsNonExpired = credentialsNonExpired;
		this.lastAccessDateTime = lastAccessDateTime;
		this.ip = ip;
		this.createdBy = createdBy;
		this.lastModifiedBy = lastModifiedBy;
		super.createdDateTime = createdDateTime;
		super.lastModifiedDateTime = lastModifiedDateTime;
		setThruDate(thruDate);
		this.storeId = storeId;
		this.dateTimeZoneId = dateTimeZoneId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public void setAccountNonExpired(Boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}
	
	public boolean getIsAccountNonExpired(){
		return BooleanUtils.toBoolean(isAccountNonExpired());
	}

	public Boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(Boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}
	
	public boolean getIsAccountNonLocked(){
		return BooleanUtils.toBoolean(isAccountNonLocked());
	}

	public Boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public Boolean isEnabled() {
		return accountNonLocked;
	}

	public void setEnabled(Boolean enabled) {
		this.accountNonLocked = enabled;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	
	public DateTime getThruDate() {
		return thruDate;
	}
	
	public String getFormattedThruDate() {
		return JodaDateTimeUtil.formatFromStorePattern(getThruDate(), JodaPatternType.DATE);
	}

	public void setThruDate(DateTime thruDate) {
		this.thruDate = thruDate;
		// TODO: expired is also used for expired password
		if (BooleanUtils.isNotTrue(accountNonExpired)) {
			this.accountNonExpired = this.thruDate!=null? this.thruDate.isAfterNow(): false;			
		}
	}

	public Integer getSuccessiveFailedLogin() {
		return successiveFailedLogin;
	}

	public void setSuccessiveFailedLogin(Integer successiveFailedLogin) {
		this.successiveFailedLogin = successiveFailedLogin;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	
	public String getDateTimeZoneId() {
		return dateTimeZoneId;
	}

	public void setDateTimeZoneId(String dateTimeZoneId) {
		this.dateTimeZoneId = dateTimeZoneId;
	}

	public String getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(String permissionId) {
		this.permissionId = permissionId;
	}
	
	public DateTime getLastAccessDateTime() {
		return lastAccessDateTime;
	}

	public void setLastAccessDateTime(DateTime lastAccessDateTime) {
		this.lastAccessDateTime = lastAccessDateTime;
	}

	public String getFormattedLastAccessDateTime() {
		return JodaDateTimeUtil.formatFromStorePattern(getLastAccessDateTime(), JodaPatternType.DATE_TIME);
	}
}