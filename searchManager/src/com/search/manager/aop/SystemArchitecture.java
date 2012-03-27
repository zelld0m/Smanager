package com.search.manager.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class SystemArchitecture {

	@Pointcut("bean(*Service)")
	public void inServiceLayer() {}
	
	@Pointcut("bean(*DAO)")
	public void inDaoLayer() {}

}
