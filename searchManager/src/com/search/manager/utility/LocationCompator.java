package com.search.manager.utility;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.Date;

public class LocationCompator implements Comparator<Object>{

	@SuppressWarnings("unchecked")
	public int compare(Object p1, Object p2) throws IllegalArgumentException {

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
				if (i1 > i2) {
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
			} else {
				return 0;
			}
	}
}
