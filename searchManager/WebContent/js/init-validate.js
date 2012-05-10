isXSSSafe = function(text){
	var asciiPrintCharRegex= /^[\040-\176]*$/;
	var hasNoXSSRegex= /^((?!(javascript:|<script>)).)*$/i;
	return hasNoXSSRegex.test(text) && asciiPrintCharRegex.test(text);
};

isAllowedName = function(text){
	var alphaNumRegex= /^[a-zA-Z0-9_\s-]*$/;
	return isXSSSafe(text) && alphaNumRegex.test(text) && $.isNotBlank(text);
};

isDigit = function(text){
	var digitRegex= /^\d+$/;
	return digitRegex.test(text) && $.isNotBlank(text);
};
