isAscii = function(text){
	var asciiPrintCharRegex= /^[\x20-\x7E\x0A\x0D]*$/;
	return asciiPrintCharRegex.test(text);
};

isXSSSafe = function(text){
	//var hasNoXSSRegex= /^((?!(javascript:|<script>)).)*$/i;
	//return hasNoXSSRegex.test(text) && isAscii(text);
	return isAscii(text) && !((text.indexOf("<") >= 0) || (text.indexOf(">") >= 0));
};

isXSSSafeAllowNonAscii = function(text){
	return !((text.indexOf("<") >= 0) && (text.indexOf(">") >= 0));
};

isAllowedFileName = function(text){ //invalid characters: \/:*?"<>|
	var invalidCharsRegex= /^[^\\\/\:\*\?\"\<\>\|]*$/;
	var hasValidChars = invalidCharsRegex.test(text);
	return isXSSSafe(text) && hasValidChars && $.isNotBlank(text);
}; 

isAllowedSearchKeyword = function(text){
	var alphaNumRegex= /^[a-zA-Z0-9_&\.\:\;\\\@\*\s\-\"\'\(\)\?\/]*$/;
	return isXSSSafe(text) && alphaNumRegex.test(text);
};

isAllowedName = function(text){
	var alphaNumRegex= /^[a-zA-Z0-9_&\.\:\;\\\@\*\s\-\"\'\(\)\?\/]*$/;
	return isXSSSafe(text) && alphaNumRegex.test(text) && $.isNotBlank(text);
//	return isXSSSafe(text) && isAscii(text) && $.isNotBlank(text);
};

isDigit = function(text){
	var digitRegex= /^\d+$/;
	return digitRegex.test(text) && $.isNotBlank(text);
};

isAlphanumeric = function(text){
	var digitRegex= /^[a-zA-Z0-9]*$/;
	return digitRegex.test(text) && $.isNotBlank(text);
};

isValidUsername = function(text) {
    var digitRegex= /^[a-zA-Z0-9\-\.\_]*$/;
    return digitRegex.test(text) && $.isNotBlank(text);
};

validateEmail = function(fieldName, fieldValue, length) {
	if (!validateGeneric(fieldName, fieldValue, length)) {
		return false;
	};
	if (!$.isBlank(fieldValue)) {
		var pattern = /^[\w\\+\\-]+([\.-]?[\w\\+\\-]+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
		if (!pattern.test(fieldValue)) {
			jAlert(fieldName+" is invalid.","Validate Email");
			return false;
		};
	}
	return true;
};

validateComment = function(moduleName, comment, minLength, maxLength){
	if(minLength != undefined && minLength > 0) {
		if ($.isBlank(comment)) {
			jAlert("Please add a comment.",moduleName);
			return false;
		}
		else if (comment.length < minLength){
			jAlert("Comment should be at least " + minLength + " characters.",moduleName);
			return false;
		}
	}
	
	if (maxLength != undefined && $.isNotBlank(comment) && comment.length > maxLength){
		jAlert("Comment cannot exceed " + maxLength + " characters.",moduleName);
		return false;
	}
	
	if(!isXSSSafe(comment)){
		jAlert("Invalid comment. HTML/XSS is not allowed.",moduleName);
		return false;
	}
	
	return true;
};

validateDescription = function(moduleName, desc, minLength, maxLength){
	if(minLength != undefined && minLength > 0) {
		if ($.isBlank(desc)) {
			jAlert("Please add a description.",moduleName);
			return false;
		}
		else if (desc.length < minLength){
			jAlert("Description should be at least " + minLength + " characters.",moduleName);
			return false;
		}
	}
	
	if (maxLength != undefined && $.isNotBlank(desc) && desc.length > maxLength){
		jAlert("Description cannot exceed " + maxLength + " characters.",moduleName);
		return false;
	}
	
	if(!isXSSSafe(desc)){
		jAlert("Invalid description. HTML/XSS is not allowed.",moduleName);
		return false;
	}
	
	return true;
};

validateDate = function(fieldName, fieldValue, length, minDate) {
	if (!validateGeneric(fieldName, fieldValue, length)) {
		return false;
	};
	if(!$.isBlank(fieldValue)) {
		if(minDate != null && minDate.getTime() > new Date(fieldValue).getTime()) {
			jAlert(fieldName+" cannot be earlier than " + $.datepicker.formatDate('mm/dd/yy', minDate));
			return false;
		}
		if(!$.isDate(fieldValue)){
			jAlert(fieldName+" is an invalid date.");
			return false;
		}
		return true;
	}
};

validateUsername = function(fieldName, fieldValue, length) {
	if (!validateGeneric(fieldName, fieldValue, length)) {
		return false;
	};
	if (!$.isBlank(fieldValue)) {
		if(!isValidUsername(fieldValue)){
			jAlert(fieldName+" contains invalid value.");
			return false;
		}
	}
	
	return true;
};

validateFullname = function(fieldName, fieldValue, length) {
	if (!validateGeneric(fieldName, fieldValue, length)) {
		return false;
	};
	if (!$.isBlank(fieldValue)) {
		if(!isAllowedName(fieldValue)){
			jAlert(fieldName + " contains invalid value.", fieldName);
			return false;
		}
	}
	
	var validRegex= /^[a-zA-Z0-9\s\.]*$/;
	
	if (!validRegex.test(fieldValue)) {
		jAlert("Invalid characters detected in " + fieldName + ".\nAcceptable characters are alphanumeric characters. ", fieldName);
		return false;
	}
	
	return true;
};

validatePassword = function(fieldName, fieldValue, length) {
	if (!validateGeneric(fieldName, fieldValue, length)) {
		return false;
	};
	if (!$.isBlank(fieldValue)) {
		var passwordRegex= /^[a-zA-Z0-9_.?*+^$[\]\\|-]*$/;
		if (!$.isBlank(fieldValue)) {
			if(!passwordRegex.test(fieldValue)) {
				jAlert("Invalid characters detected in " + fieldName + ".\nAcceptable characters are alphanumeric characters " +
						"and the special characters *+^$[]\|-_?.");
				return false;
			}
		}
	}
	return true;
};

validateField = function(fieldName, fieldValue, minLength, maxLength) {
	fieldName = $.capitalize(fieldName);
	if (!validateGeneric(fieldName, fieldValue, minLength, maxLength)) {
		return false;
	};
	if (!$.isBlank(fieldValue)) {
		if(!isAllowedName(fieldValue)){
			jAlert(fieldName+" contains invalid value.");
			return false;
		}
	}
	return true;
};

validateGeneric = function(fieldName, fieldValue, minLength, maxLength) {
	fieldName = $.capitalize(fieldName);
	if(minLength != undefined && minLength > 0) {
		if ($.isBlank(fieldValue)) {
			jAlert(fieldName+" cannot be empty.", fieldName);
			return false;
		}
		if (fieldValue.length < minLength){
			jAlert(fieldName + " should be at least " + minLength + " characters.", fieldName);
			return false;
		}
	}
	if (maxLength != undefined && $.isNotBlank(fieldValue) && fieldValue.length > maxLength){
		jAlert(fieldName + " cannot exceed " + maxLength + " characters.", fieldName);
		return false;
	}
	if(!isAscii(fieldValue)) {
		jAlert(fieldName+" contains non-ASCII characters.", fieldName);		
		return false;
	}
	if(!isXSSSafe(fieldValue)){
		jAlert(fieldName+" contains XSS.", fieldName);
		return false;
	}
	return true;
};

validateAlphanumeric = function(fieldName, fieldValue) {
	if(!isAlphanumeric(fieldValue)){
		jAlert(fieldName+" should be alphanumeric.");
		return false;
	}
	return true;
};

validateCatCode = function(fieldName, fieldValue) {
	var digitRegex= /^[a-zA-Z0-9\*]*$/;
	
	var isValid = digitRegex.test(fieldValue) && $.isNotBlank(fieldValue);
	
	if(!isValid){
		jAlert(fieldName+" is not valid.");
		return false;
	}
	return true;
};

validateSearchKeyword = function(fieldName, fieldValue, required) {
	fieldName = $.capitalize(fieldName);

	if (!validateGeneric(fieldName, fieldValue, required ? 1 : 0, 100)) {
		return false;
	};
	if (!$.isBlank(fieldValue)) {
		if(!isAllowedSearchKeyword(fieldValue)){
			jAlert(fieldName+" contains invalid value.");
			return false;
		}
	}
	return true;
};

validateInteger = function(fieldName, fieldValue, min, max) {
	var regex = /^[\-\+]{0,1}[0-9]{1,}[\.]{0,1}[0]{0,}$/;
	var value = $.trim(fieldValue);
	var valid = validateIntegerValue(fieldValue);

	if (!valid) {
		jAlert(fieldName + " is not a valid value.", fieldName);
		return false;
	}
	
	if ((min || min === 0) && min > parseInt(value)) {
		jAlert("Minimum value allowed for " + fieldName + " is " + min + ".", fieldName);
		return false;
	}
	
	if ((max || max === 0) && max < parseInt(value)) {
		jAlert("Maximum value allowed for " + fieldName + " is " + max + ".", fieldName);
		return false;
	}
	
	return true;
};

validateIntegerValue = function(fieldValue) {
	var regex = /^[\-\+]{0,1}[0-9]{1,}[\.]{0,1}[0]{0,}$/;
	var value = $.trim(fieldValue);
	
	return regex.test(value) && $.isNotBlank(value);
}
