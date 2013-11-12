package com.search.manager.core.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component("loggingAspect")
public class LoggingAspect {

	private Logger getLogger(JoinPoint jp) {
		return LoggerFactory.getLogger(jp.getTarget().getClass());
	}
	
	@Before("Pointcuts.allCoreMethods()")
	public void logBefore(JoinPoint joinPoint) {
		Logger logger = this.getLogger(joinPoint);
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();

		logger.info("BEGIN: " + className + "." + methodName + "()");
	}

	@AfterReturning(pointcut = "Pointcuts.allCoreMethods()", returning = "result")
	public void logAfterReturning(JoinPoint joinPoint, Object result) {
		Logger logger = this.getLogger(joinPoint);
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();

		if (result != null) {
			logger.info("RESULT: " + result.getClass().getSimpleName() + "="
					+ result);
		} else {
			logger.info("RESULT: " + "NULL");
		}

		logger.info("END: " + className + "." + methodName + "()");
	}

	@AfterThrowing(pointcut = "Pointcuts.allCoreMethods()", throwing = "e")
	public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
		Logger logger = this.getLogger(joinPoint);
		String className = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();

		if (e != null) {
			logger.error("ERROR:" + className + "." + methodName + "():", e);
		}
	}
	
}
