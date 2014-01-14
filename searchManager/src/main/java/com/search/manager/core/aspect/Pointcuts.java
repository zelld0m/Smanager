package com.search.manager.core.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class Pointcuts {

	// All Methods
	@Pointcut("execution(* *.*(..))")
	public void allMethods() {
	}

	// Getter Methods
	@Pointcut("execution(* *.get*(..))")
	public void getterMethods() {
	}

	// Setter Methods
	@Pointcut("execution(* *.set*(..))")
	public void setterMethods() {
	}

	// All Core Methods
	@Pointcut("execution(* *.*(..)) && within(com.search.manager.core..*)")
	public void allCoreMethods() {
	}

}
