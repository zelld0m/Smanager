package com.search.manager.authentication.dao.internal;

import java.util.Collection;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.joda.time.DateTime;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.search.manager.jodatime.jaxbadapter.DateTimeAdapter;
import com.search.manager.model.User;

@DataTransferObject(converter = BeanConverter.class)
public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = -2502976220060750855L;
    private Collection<GrantedAuthority> authorities;
    private String password;
    private String username;
    private String fullName;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private DateTime loggedInTime;
    private String currentPage;
    private String storeId;
    private String dateTimeZoneId;

    public UserDetailsImpl() {
    }

    public UserDetailsImpl(Collection<GrantedAuthority> authorities, String password, String username, String fullName, boolean accountNonExpired,
            boolean accountNonLocked, boolean credentialsNonExpired, boolean enabled) {
        this.authorities = authorities;
        this.password = password;
        this.username = username;
        this.fullName = fullName;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
    }

    public UserDetailsImpl(User user) {
        this.password = user.getPassword();
        this.username = user.getUsername();
        this.fullName = user.getFullName();
        this.accountNonExpired = user.isAccountNonExpired();
        this.accountNonLocked = user.isAccountNonLocked();
        this.credentialsNonExpired = user.isCredentialsNonExpired();
        this.enabled = user.isEnabled();
        this.storeId = user.getStoreId();
        this.dateTimeZoneId = user.getTimezoneId();
    }

    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getFullName() {
        return fullName;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public void setAuthorities(Collection<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setLoggedInTime(DateTime loggedintime) {
        this.loggedInTime = loggedintime;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    public DateTime getLoggedIntime() {
        return loggedInTime;
    }

    public String getCurrentPage() {
        return currentPage;
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
}