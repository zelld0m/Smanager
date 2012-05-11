isAscii = function(text){
	var asciiPrintCharRegex= /^[\x20-\x7E\x0A\x0D]*$/;
	return asciiPrintCharRegex.test(text);
};

isXSSSafe = function(text){
	//var hasNoXSSRegex= /^((?!(javascript:|<script>)).)*$/i;
	//return hasNoXSSRegex.test(text) && isAscii(text);
	return isAscii(text) && !((text.indexOf("<") >= 0) && (text.indexOf(">") >= 0));
};

isAllowedName = function(text){
	var alphaNumRegex= /^[a-zA-Z0-9_\s-]*$/;
	return isXSSSafe(text) && alphaNumRegex.test(text) && $.isNotBlank(text);
};

isDigit = function(text){
	var digitRegex= /^\d+$/;
	return digitRegex.test(text) && $.isNotBlank(text);
};
