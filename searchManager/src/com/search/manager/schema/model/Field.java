package com.search.manager.schema.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.schema.SchemaException;
import com.search.manager.schema.model.bf.FunctionModelComponent;
import com.search.manager.schema.model.qf.QueryFieldModelComponent;

@DataTransferObject(converter = BeanConverter.class)
public class Field implements Serializable, FunctionModelComponent, QueryFieldModelComponent {
	
	private static final long serialVersionUID = 1L;
		
	protected String name;
	protected FieldType fieldType;
	protected boolean indexed;
	protected boolean stored;
	protected List<Field> relatedFields = new ArrayList<Field>();
	
	public Field(String name, FieldType fieldType, boolean indexed, boolean stored) {
		this.name = name;
		this.fieldType = fieldType;
		this.indexed = indexed;
		this.stored = stored;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isIndexed() {
		return indexed;
	}
	
	public boolean isStored() {
		return stored;
	}
	
	public boolean isCaseSensitive() {
		return (fieldType == null) ? true : fieldType.isCaseSensitive();
	}
	
	public List<Field> getRelatedFields() {
		return relatedFields;
	}
	
	public void addRelatedField(Field field) {
		relatedFields.add(field);
	}
	
	public FieldType getFieldType() {
		return fieldType;
	}
	
	public GenericType getGenericType() {
		return fieldType.getGenericType();
	}

	@Override
	public boolean validate() throws SchemaException {
		if (fieldType == null) {
			throw new SchemaException("Unknown field: " + name);
		}
		return true;
	}
	
	@Override
	public String toString() {
		return name;
	}


}
