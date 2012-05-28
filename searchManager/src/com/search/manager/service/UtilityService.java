package com.search.manager.service;

import java.util.Date;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.search.manager.authentication.dao.UserDetailsImpl;
import com.search.manager.utility.PropsUtils;

@Service(value = "utilityService")
@RemoteProxy(
		name = "UtilityServiceJS",
		creator = SpringCreator.class,
		creatorParams = @Param(name = "beanName", value = "utilityService")
)
public class UtilityService {
	
	private static final String MACMALL = "MacMall";
	private static final String PCMALL = "PCMall";
	private static final String ECOST = "eCost";
	private static final String SBN = "SBN";
	
	@RemoteMethod
	public static String getUsername(){
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if (principal==null || !(principal instanceof UserDetailsImpl)) return "";

		return ((UserDetailsImpl) principal).getUsername();
	}

	@RemoteMethod
	public static String getServerName(){
		return "search";
	}

	@RemoteMethod
	public static String getStoreName(){
		Object principal = null;
		try {
			principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		} catch (NullPointerException npe) {
			npe.printStackTrace();
		}
		
		
		if (principal==null || !(principal instanceof UserDetailsImpl)) return "";

		return ((UserDetailsImpl) principal).getStoreId();
	}

	@RemoteMethod
	public static String getStoreLabel(){
		String label = null;
		if (MACMALL.toLowerCase().equals(getStoreName())) {
			label = MACMALL;
		} else if (PCMALL.toLowerCase().equals(getStoreName())) {
			label = PCMALL;
		} else if (ECOST.toLowerCase().equals(getStoreName())) {
			label = ECOST;
		} else if (SBN.toLowerCase().equals(getStoreName())) {
			label = SBN;
		}
		return label;
	}
	
	@RemoteMethod
	public static String getStoreLogo(){
		return new StringBuilder("/images/logo").append(getStoreLabel()).append(".png").toString();
	}
	
	@RemoteMethod
	public static String getSolrConfig(){
		JSONObject json = new JSONObject();
		json.put("solrUrl", PropsUtils.getValue("browsejssolrurl"));
		json.put("isFmGui", PropsUtils.getValue("isFmSolrGui").equals("1")?true:false);
		return json.toString();
	}

	public static String formatComment(String comment) {
		if (StringUtils.isNotBlank(comment)) {
			StringBuilder commentBuilder = new StringBuilder();
			commentBuilder.append(new Date().getTime())
			.append("|")
			.append(UtilityService.getUsername())
			.append("|")
			.append(comment.length())
			.append("|")
			.append(comment)
			.append("|");
			return commentBuilder.toString();
		}
		return null;
	}
}
