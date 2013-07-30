package com.search.manager.schema.model.bf;

import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.schema.RelevancyConfig;
import com.search.manager.schema.SchemaException;
import com.search.manager.schema.model.BoostFactor;
import com.search.manager.schema.model.Field;
import com.search.manager.schema.model.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DataTransferObject(converter = BeanConverter.class)
public class BoostFunctionModel implements FunctionModelComponent {

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger logger =
            LoggerFactory.getLogger(BoostFunctionModel.class);
    private FunctionQuery functionQuery;
    private BoostFactor boostFactor;

    @Override
    public boolean validate() throws SchemaException {
        // check query
        functionQuery.validate();
        boostFactor.validate();
        return true;
    }

    public static BoostFunctionModel toModel(Schema schema, String bf, boolean validate) throws SchemaException {

        BoostFunctionModel boost = new BoostFunctionModel();

        if (StringUtils.isEmpty(bf)) {
            return boost;
        }

        bf = StringUtils.remove(bf, " ");
        Matcher m = Pattern.compile("(\\w*\\(.*\\))\\^([\\d\\.]*)").matcher(bf);
        if (!m.matches() || m.groupCount() != 2) {
            throw new SchemaException("Invalid format.");
        }

        boost.boostFactor = new BoostFactor(m.group(2));

        StringTokenizer tokenizer = new StringTokenizer(m.group(1), "(),", true);
        Stack<FunctionQuery> stack = new Stack<FunctionQuery>();

        while (tokenizer.hasMoreTokens()) {
            String str = tokenizer.nextToken();
            if (")".equals(str)) {
                // additional checking. more ")" than "("
                if (stack.pop() == null) {
                    throw new IllegalArgumentException("Mismatched parentheses");
                }
                continue;
            } else if (",".equals(str)) {
                continue;
            }

            String operator = null;
            if (tokenizer.hasMoreTokens()) {
                operator = tokenizer.nextToken();
            }

            if ("(".equals(operator)) {
                Function func = RelevancyConfig.getInstance().getFunction(str);
                if (func == null) {
                    throw new SchemaException("Unknown function: " + str);
                }
                FunctionQuery fq = new FunctionQuery(func);
                if (stack.size() > 0) {
                    stack.peek().addArgument(fq);
                } else {
                    boost.functionQuery = fq;
                }
                stack.push(fq);
//				logger.debug((str + " is a function"));
            } else {
                if (CharUtils.isAsciiAlpha(str.charAt(0))) {
                    if (DateConstant.isValidConstant(str)) {
                        stack.peek().addArgument(new DateConstant(str));
//						logger.debug(str + " is a date constant");
                    } else {
                        Field field = schema.getField(str);
                        if (field == null) {
                            throw new IllegalArgumentException("Unknown field: " + str);
                        }
                        stack.peek().addArgument(field);
//						logger.debug(str + " is a field");
                    }
                } else {
                    Matcher dateMatcher = Pattern.compile("[0-9]{4}-(0[1-9]|1[0-9]|2[0-9]|3[01])-(0[1-9]|1[012])(\\s)?T([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]Z").matcher(str);
                    if (dateMatcher.matches()) { // Literal date: YYYY-MM-DD Thh:mm:ssZ
                        stack.peek().addArgument(new DateConstant(str));
                    } else {
                        stack.peek().addArgument(new NumericConstant(str));
                    }
//					logger.debug(str + " is a constant");
                }
                if (")".equals(operator)) {
                    // additional checking. more ")" than "("
                    if (stack.pop() == null) {
                        throw new IllegalArgumentException("Mismatched parentheses");
                    }
                }
            }
        }

        if (!stack.isEmpty()) {
            throw new IllegalArgumentException("Mismatched parentheses");
        }

        if (validate) {
            boost.validate();
        }
        return boost;
    }

    @Override
    public String toString() {
        return functionQuery.toString() + "^" + boostFactor.toString();
    }
}
