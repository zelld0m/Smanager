package com.search.manager.utility;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortUtil {
	
	/**
     * Sorts a given list by a comparator in ascending order.
     * 
     * @param listToSort List that will be sorted.
     * @param comparator Comparator to be use.
     */
	@SuppressWarnings("unchecked")
	public static void sort(List listToSort, Comparator comparator) {
		Collections.sort(listToSort, comparator);
	}
	
	/**
     * Sorts a given list by a bean property in ascending order.
     * 
     * @param listToSort List that will be sorted.
     * @param property   Property of bean to be sorted.
     */
	@SuppressWarnings("unchecked")
	public static void sort(List listToSort, String property) {
		Comparator propertyComparator = new BeanComparator(property);
		Collections.sort(listToSort, propertyComparator);
	}
	
	/**
     * Sorts a given list by natural ordering in ascending order.
     * 
     * @param listToSort List that will be sorted
     */
	@SuppressWarnings("unchecked")
	public static void sort(List listToSort) {
		Collections.sort(listToSort);
	}
	
	/**
     * Sorts a given list by an array of bean properties in ascending order. First value in the array
     * of bean property will be sorted first.
     * 
     * @param listToSort List that will be sorted
     * @param property   Array of bean to be sorted.
     */
	@SuppressWarnings("unchecked")
	public static void sort(List listToSort, String... property) {
		Comparator propertyComparator = new MultiplePropertyComparator(property);
		Collections.sort(listToSort, propertyComparator);
	}
	
	/**
     * Sorts a given list by a comparator in descending order.
     * 
     * @param listToSort List that will be sorted.
     * @param comparator Comparator to be use.
     */
	@SuppressWarnings("unchecked")
	public static void sortreverse(List listToSort, Comparator comparator) {
		Collections.sort(listToSort, comparator);
		Collections.reverse(listToSort);
	}
	
	/**
     * Sorts a given list by a bean property in descending order.
     * 
     * @param listToSort List that will be sorted.
     * @param property   Property of bean to be sorted.
     */
	@SuppressWarnings("unchecked")
	public static void sortreverse(List listToSort, String property) {
		Comparator propertyComparator = new BeanComparator(property);
		Collections.sort(listToSort, propertyComparator);
		Collections.reverse(listToSort);
	}
	
	/**
     * Sorts a given list by natural ordering in descending order.
     * 
     * @param listToSort List that will be sorted
     */
	@SuppressWarnings("unchecked")
	public static void sortreverse(List listToSort) {
		Collections.sort(listToSort);
		Collections.reverse(listToSort);
	}
	
	/**
     * Sorts a given list by an array of bean properties in descending order. First value in the array
     * of bean property will be sorted first.
     * 
     * @param listToSort List that will be sorted
     * @param property   Array of bean to be sorted.
     */
	@SuppressWarnings("unchecked")
	public static void sortreverse(List listToSort, String... property) {
		Comparator propertyComparator = new MultiplePropertyComparator(property);
		Collections.sort(listToSort, propertyComparator);
		Collections.reverse(listToSort);
	}
}
