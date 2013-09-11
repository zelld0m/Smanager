package com.search.manager.schema.model.mm;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.schema.SchemaException;
import com.search.manager.schema.model.VerifiableModel;

@DataTransferObject(converter = BeanConverter.class)
public class MinimumToMatchModel implements VerifiableModel {
	
	private static final long serialVersionUID = 1L;

	private List<MinimumToMatch> parameters = new ArrayList<MinimumToMatch>();

	public static MinimumToMatchModel toModel(String mm, boolean validate) throws SchemaException {
		
		MinimumToMatchModel model = new MinimumToMatchModel();
		for (String str : mm.split(" ")) {
			model.parameters.add((str.contains("<")) ? new ConditionalMatch(str)
				: new FixedMatch(str));
		}
		if (validate) {
			model.validate();
		}
		return model;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (parameters.size() > 0) {
			for (MinimumToMatch mm: parameters) {
				builder.append(mm).append(" ");
			}
			builder.deleteCharAt(builder.length() - 1);
		}
		return builder.toString();
	}
	
	@Override
	public boolean validate() throws SchemaException {
		if (parameters.size() == 1) {
			parameters.get(0).validate();
		}
		else {
			List<String> numbers = new ArrayList<String>();
			for (MinimumToMatch mm: parameters) {
				if(!(mm instanceof ConditionalMatch)) {
					throw new SchemaException("Invalid condition: " + mm);
				}
				mm.validate();
				String number = ((ConditionalMatch)mm).getCondition();
				if (numbers.contains(number)) {
					throw new SchemaException("Multiple declaration for condition: " + number);
				}
				numbers.add(number);
			}
		}
		return true;
	}

	public void setParameters(List<MinimumToMatch> parameters) {
		this.parameters = parameters;
	}

	public List<MinimumToMatch> getParameters() {
		return parameters;
	}
	
	public boolean getIsSingleRule(){
		return CollectionUtils.isNotEmpty(parameters) && parameters.size() == 1 && (parameters.get(0)) instanceof FixedMatch;
	}
}
