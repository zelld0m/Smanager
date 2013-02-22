package com.search.manager.schema.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.http.NameValuePair;
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
	protected boolean multiValued;
	protected boolean dynamic;
	protected boolean ignored;
	protected List<NameValuePair> attributes = new ArrayList<NameValuePair>();
	protected List<Field> copyFields = new ArrayList<Field>();
	
	public Field() {
	}
	
	public Field(String name, FieldType fieldType, boolean indexed, boolean stored){
		this.name = name;
		this.fieldType = fieldType;
		this.indexed = indexed;
		this.stored = stored;
	}
	
	public Field(String name, FieldType fieldType, boolean indexed, boolean stored, boolean multiValued, boolean dynamic, boolean ignored) {
		this.name = name;
		this.fieldType = fieldType;
		this.indexed = indexed;
		this.stored = stored;
		this.multiValued = multiValued;
		this.dynamic = dynamic;
		this.ignored = ignored;
	}
	
	public Field(String name, FieldType fieldType, boolean indexed, boolean stored, boolean multiValued, boolean dynamic, boolean ignored,
			List<NameValuePair> attributes, List<Field> copyFields) {
		this.name = name;
		this.fieldType = fieldType;
		this.indexed = indexed;
		this.stored = stored;
		this.multiValued = multiValued;
		this.dynamic = dynamic;
		this.ignored = ignored;
		if (CollectionUtils.isNotEmpty(attributes)) {
			this.attributes.addAll(attributes);
		}
		if (CollectionUtils.isNotEmpty(copyFields)) {
			this.copyFields.addAll(copyFields);
		}
	}
	
	public boolean isMultiValued() {
		return multiValued;
	}

	public void setMultiValued(boolean multiValued) {
		this.multiValued = multiValued;
	}

	public boolean isDynamic() {
		return dynamic;
	}

	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

	public boolean isIgnored() {
		return ignored;
	}

	public void setIgnored(boolean ignored) {
		this.ignored = ignored;
	}

	public List<NameValuePair> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<NameValuePair> attributes) {
		this.attributes = attributes;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFieldType(FieldType fieldType) {
		this.fieldType = fieldType;
	}

	public void setIndexed(boolean indexed) {
		this.indexed = indexed;
	}

	public void setStored(boolean stored) {
		this.stored = stored;
	}

	public void setCopyFields(List<Field> copyFields) {
		this.copyFields = copyFields;
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
	
	public List<Field> getCopyFields() {
		return copyFields;
	}
	
	public void addCopyField(Field field) {
		if (copyFields != null) {
			copyFields.add(field);
		}
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
