package com.search.manager.schema.model.bq;

import com.search.manager.schema.SchemaException;
import com.search.manager.schema.model.BoostFactor;
import com.search.manager.schema.model.VerifiableModel;

public class BoostQuery implements VerifiableModel {

	private static final long serialVersionUID = 1L;

	private Expression<SubQuery, SubQuery> expression;
	private BoostFactor boost;
	
	public BoostQuery() {
	}
	
	public BoostQuery(Expression<SubQuery, SubQuery> expression, BoostFactor boost) {
		this.expression = expression;
		this.boost = boost;
	}
	
	@Override
	public boolean validate() throws SchemaException {
		// TODO Auto-generated method stub
		return false;
	}

	public void setBoost(BoostFactor boost) {
		this.boost = boost;
	}

	public BoostFactor getBoost() {
		return boost;
	}

	public void setExpression(Expression<SubQuery, SubQuery> expression) {
		this.expression = expression;
	}

	public Expression<SubQuery, SubQuery> getExpression() {
		return expression;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(expression == null ? "" : expression.toString()).append("^").append(boost == null ? "" : boost.toString());
		return builder.toString();
	}
}
