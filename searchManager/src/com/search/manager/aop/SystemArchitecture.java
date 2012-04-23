package com.search.manager.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component("systemArchitecture")
public class SystemArchitecture {

	@Pointcut("bean(*Service)")
	public void inServiceLayer() {}
	
	@Pointcut("bean(*DAO)")
	public void inDaoLayer() {}

}