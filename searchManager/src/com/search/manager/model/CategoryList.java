package com.search.manager.model;

import java.io.Serializable;
import java.util.List;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class CategoryList implements Serializable {

	private static final long serialVersionUID = 2023800856514363459L;
	private List<Category> categories;
	private List<String> manufacturers;
	
	public CategoryList(){
	}

	public CategoryList(List<Category> categories, List<String> manufacturers) {
		this.categories = categories;
		this.manufacturers = manufacturers;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public List<String> getManufacturers() {
		return manufacturers;
	}

}