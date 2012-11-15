package com.search.manager.report.model.xml;

import java.util.List;

import com.search.manager.model.Product;

public interface ProductDetailsAware {
	public List<? extends RuleItemXml> getItem();
	public List<Product> getProducts();
	public void setProducts(List<Product> products);
}