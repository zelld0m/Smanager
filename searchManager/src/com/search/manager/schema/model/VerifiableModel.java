package com.search.manager.schema.model;

import java.io.Serializable;

import com.search.manager.schema.SchemaException;

public interface VerifiableModel extends Serializable {
	public boolean validate() throws SchemaException;
}
