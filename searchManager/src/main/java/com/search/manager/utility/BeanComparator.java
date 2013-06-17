package com.search.manager.utility;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.Date;

public class BeanComparator implements Comparator<Object> {

	private String property;
	private Comparator<Object> comparator;

	public BeanComparator(String property) {
		this(property, null);
	}

	public BeanComparator(String property, Comparator<Object> comparator) {
		this.property = property;
		this.comparator = comparator;
	}

	public int compare(Object o1, Object o2) throws IllegalArgumentException {

		Object p1 = BeanUtil.getProperty(property, o1);
		Object p2 = BeanUtil.getProperty(property, o2);
		if (comparator == null) {
			if (p1 instanceof Date) {
				Date i1 = (Date) p1;
				Date i2 = (Date) p2;
				if (i1.getTime() > i2.getTime()) {
					return 1;
				} else if (i1.getTime() > i2.getTime()) {
					return -1;
				} else {
					return 0;
				}
			} else if (p1 instanceof BigInteger) {
				BigInteger i1 = (BigInteger) p1;
				BigInteger i2 = (BigInteger) p2;
				if (i1.intValue() > i2.intValue()) {
					return 1;
				} else if (i1.intValue() < i2.intValue()) {
					return -1;
				} else {
					return 0;
				}
			} else if (p1 instanceof Integer) {
				Integer i1 = (Integer) p1;
				Integer i2 = (Integer) p2;
				
				if(i1 == 0 || i2 == 0){
					return 0;
				}else if (i1 > i2) {
					return 1;
				} else if (i1 < i2) {
					return -1;
				} else {
					return 0;
				}
			} else if (p1 instanceof Long) {
				Long i1 = (Long) p1;
				Long i2 = (Long) p2;
				if (i1 > i2) {
					return 1;
				} else if (i1 < i2) {
					return -1;
				} else {
					return 0;
				}
			} else if (p1 instanceof String) {
				String s1 = String.valueOf(p1);
				String s2 = String.valueOf(p2);
				return s1.compareTo(s2);
			} else if (p1 instanceof Comparable) {
				try {
					Integer i1 = Integer.parseInt(p1.toString());
					Integer i2 = Integer.parseInt(p2.toString());
					if (i1 > i2) {
						return 1;
					} else if (i1 < i2) {
						return -1;
					} else {
						return 0;
					}
				} catch (Exception e) {
					return 0;
				}

			} else if (p2 instanceof Comparable) {
				try {
					Integer i1 = Integer.parseInt(p1.toString());
					Integer i2 = Integer.parseInt(p2.toString());
					if (i1 < i2) {
						return 1;
					} else if (i1 > i2) {
						return -1;
					} else {
						return 0;
					}
				} catch (Exception e) {
					return 0;
				}
				//return ((Comparable<Object>) p2).compareTo(p1);
			} else {
				return 0;
			}
		} else {
			return comparator.compare(p1, p2);
		}
	}
}
