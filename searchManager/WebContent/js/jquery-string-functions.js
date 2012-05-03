/** 
 * This plugin adds additional string functions to the 
 * jQuery global object. 
 */  
jQuery.extend(
		(function($){  
			return {  

				isBlank: function(obj){
					return(!obj || $.trim(obj) === "");
				},

				isNotBlank: function(obj){
					return !($.isBlank(obj));
				},

				addSlashes: function (str) {
					str=str.replace(/\\/g,'\\\\');
					str=str.replace(/\'/g,'\\\'');
					str=str.replace(/\"/g,'\\"');
					str=str.replace(/\0/g,'\\0');
					str=str.replace(/\ /g,'\\\ ');
					return str;
				},

				stripSlashes: function (str) {
					str=str.replace(/\\'/g,'\'');
					str=str.replace(/\\"/g,'"');
					str=str.replace(/\\0/g,'\0');
					str=str.replace(/\\\\/g,'\\');
					str=str.replace(/\\ /g,' ');
					return str;
				},

				startsWith: function(str, prefix) {
					return str.indexOf(prefix) === 0;
				},

				endsWith: function(str, suffix) {
					return str.match(suffix+"$")==suffix;
				},

				escapeQuotes: function(id) {
					return id.replace(/\"/g, "\\\"").replace(/\'/g, "\\\'");
				},

				formatAsId: function(id) {
					return "_".concat(("" + id).replace(/ /g,"_").toLowerCase());
				},
				
				isDate: function(format, text){
				    var isValid = true;

				    try{
				        $.datepicker.parseDate(format, text, null);
				    }
				    catch(error){
				    	alert(error);
				        isValid = false;
				    }

				    return isValid;
				},
				
				isXSSSafe: function(text){
					var asciiPrintCharRegex= /^[\040-\0176]*$/;
					var hasNoXSSRegex= /^((?!(javascript:|<script>)).)*$/;
					return hasNoXSSRegex.test(text) && asciiPrintCharRegex.test(text);
				},
				
				isAllowedName: function(text){
					var alphaNumRegex= /^[a-zA-Z0-9_\s\-]*$/;
					return $.isXSSSafe(text) && alphaNumRegex.test(text) && $.isNotBlank(text);
				}
			};  
		}(jQuery))  
);  