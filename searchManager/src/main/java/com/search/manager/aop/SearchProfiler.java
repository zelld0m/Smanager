package com.search.manager.aop;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SearchProfiler {
	
	private static final Logger logger = Logger.getLogger(SearchProfiler.class);
	
    @Pointcut("execution(* com.search.manager.service.*.*(..))")
    public void businessMethods() { }

    @Around("businessMethods()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
            long start = System.currentTimeMillis();
            logger.info(pjp.getTarget().getClass() + " " + pjp.getSignature().getName() + " ");
            for (Object obj: pjp.getArgs()) {
            	logger.info("param: " + obj);
            }
            logger.debug("Going to call the method.");
            Object output = pjp.proceed();
            logger.debug("Method execution completed.");
            long elapsedTime = System.currentTimeMillis() - start;
            logger.debug("Method execution time: " + elapsedTime + " milliseconds.");
            return output;
    }
    
//	@Pointcut("execution(* com.search.manager.service.*.*(..))")
//    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
//            long start = System.currentTimeMillis();
//            logger.debug("Going to call the method.");
//            Object output = pjp.proceed();
//            logger.debug("Method execution completed.");
//            long elapsedTime = System.currentTimeMillis() - start;
//            logger.debug("Method execution time: " + elapsedTime + " milliseconds.");
//            return output;
//    }

}
