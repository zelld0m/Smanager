package com.search.manager.utility;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiplePropertyComparator implements Comparator<Object> {

    private static final Logger logger =
            LoggerFactory.getLogger(MultiplePropertyComparator.class);
    
    private String[] property;
    private Comparator<Object> comparator;

    public MultiplePropertyComparator(String... property) {
        this(null, property);
    }

    public MultiplePropertyComparator(Comparator<Object> comparator, String... property) {
        this.property = property;
        this.comparator = comparator;
    }

    @SuppressWarnings("unchecked")
    public int compare(Object o1, Object o2) throws IllegalArgumentException {
        int result = 0;
        for (int i = 0; i < property.length; i++) {
            Object p1 = BeanUtil.getProperty(property[i], o1);
            //System.out.println("p1: "+p1.getClass());
            Object p2 = BeanUtil.getProperty(property[i], o2);
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
                    logger.info(String.format("i1: %i i2: %i"));
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
                        return ((Comparable<Object>) p1).compareTo(p2);
                    }

                } else if (p2 instanceof Comparable) {
                    return ((Comparable<Object>) p2).compareTo(p1);
                } else {
                    return 0;
                }
            } else {
                result = comparator.compare(p1, p2);
            }
            if (result != 0) {
                return result;
            }
        }
        return result;
    }
}
