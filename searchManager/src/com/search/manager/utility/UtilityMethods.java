package com.search.manager.utility;

import java.util.List;
import java.util.UUID;

public class UtilityMethods {

	
	public static String getUniqueId(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public static String getUniqueTimeId(){
		return Long.toString(System.nanoTime());
	}
	
	public static String checkAction(String action){
		return action==null||(action=action.trim()).isEmpty()?"load":action;
	}
}
