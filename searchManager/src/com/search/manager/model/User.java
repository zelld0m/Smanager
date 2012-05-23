package com.search.manager.model;

import java.io.Serializable;
import java.util.Date;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.utility.DateAndTimeUtils;

@DataTransferObject(converter = BeanConverter.class)
public class User implements Serializable {
	
private static final long serialVersionUID = 1L;
	
	private String username;
	private String usernameLike;
	private String fullName;
	private String password;
	private String email;
	private String groupId;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private Date lastAccessDate;
	private String ip;
	private String createdBy;
	private String lastModifiedBy;
	private Date createdDate;
	private Date lastModifiedDate;
	private Date thruDate;

	public User() {
	}

	public User(String username, String fullName, String password, String email, String groupId, boolean accountNonLocked,
			boolean credentialsNonExpired, Date lastAccessDate, String ip, String createdBy, String lastModifiedBy,
			Date createdDate, Date lastModifiedDate, Date thruDate) {
		super();
		this.username = username;
		this.fullName = fullName;
		this.password = password;
		this.email = email;
		this.groupId = groupId;
		this.accountNonLocked = accountNonLocked;
		this.credentialsNonExpired = credentialsNonExpired;
		this.lastAccessDate = lastAccessDate;
		this.ip = ip;
		this.createdBy = createdBy;
		this.lastModifiedBy = lastModifiedBy;
		this.createdDate = createdDate;
		this.lastModifiedDate = lastModifiedDate;
		this.thruDate = thruDate;
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

	public boolean isAccountNonExpired() {
		return DateAndTimeUtils.compare(new Date(), thruDate) < 0;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		if (accountNonExpired) {
			this.thruDate = DateAndTimeUtils.addYearToDate(2);
		} else {
			this.thruDate = DateAndTimeUtils.getDateYesterday();
		}
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public boolean isEnabled() {
		return accountNonLocked;
	}

	public void setEnabled(boolean enabled) {
		this.accountNonLocked = enabled;
	}

	public Date getLastAccessDate() {
		return lastAccessDate;
	}

	public void setLastAccessDate(Date lastAccessDate) {
		this.lastAccessDate = lastAccessDate;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getCreatedBy() {
		return createdBy;
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

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public Date getThruDate() {
		return thruDate;
	}

	public void setThruDate(Date thruDate) {
		this.thruDate = thruDate;
	}

	public String getUsernameLike() {
		return usernameLike;
	}

	public void setUsernameLike(String usernameLike) {
		this.usernameLike = usernameLike;
	}

}
