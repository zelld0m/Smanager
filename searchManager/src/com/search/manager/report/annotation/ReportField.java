package com.search.manager.report.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.poi.ss.usermodel.CellStyle;

@Retention(RetentionPolicy.RUNTIME)
@Target(value=ElementType.METHOD)
public @interface ReportField {
	/* Column header or tag field */
	String label();
	/* Approximate number of visible characters. Valid range is from 1-255 */
	int size();
	/* Lower value means higher priority */
	int sortOrder();
	/* Alignment */
	short alignment() default CellStyle.ALIGN_CENTER;
	/* Indicates wrapping of text */
	boolean wrapText() default false;
}
