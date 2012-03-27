package com.search.manager.schema.model.bf;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.schema.model.VerifiableModel;

@DataTransferObject(converter = BeanConverter.class)
public interface FunctionModelComponent extends VerifiableModel {
}
