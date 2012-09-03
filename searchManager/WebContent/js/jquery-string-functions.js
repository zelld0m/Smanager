/** 
 * This plugin adds additional string functions to the 
 * jQuery global object. 
 */  
jQuery.extend(
		(function($){  
			return {  

				setDefaultIfEmpty: function(obj, defaultValue){
					return (obj) ? obj[0] : defaultValue;
				},
				
				defaultIfBlank: function(obj, defaultValue){
					return $.isNotBlank(obj) ? obj : defaultValue;
				},
				
				compareBlank: function(str1, str2){
					return ($.isBlank(str1) && $.isBlank(str2)) ? true : (str1 === str2);
				},
				
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
					return $.isBlank(prefix) ? false: str.indexOf(prefix) === 0;
				},

				endsWith: function(str, suffix) {
					return $.isBlank(suffix) ? false: str.match(suffix+"$")==suffix;  
				},

				escapeQuotes: function(id) {
					return id.replace(/\"/g, "\\\"").replace(/\'/g, "\\\'");
				},

				formatAsId: function(id) {
					return "_".concat(("" + id).
							replace(/\s/g,"_").
							replace(/&amp;/g,"_").
							replace(/\'/g,"_").
							replace(/\"/g,"_").
							replace(/\./g,"_").
							replace(/\&/g,"_").
							replace(/\,/g,"_").
							replace(/\//g,"_")
					);
				},
				
				isDate: function(text, format){
				    var isValid = true;
				    var dateFormat = $.isNotBlank(format)?format: "mm/dd/yy";
				    
				    try{
				        $.datepicker.parseDate(dateFormat, text, null);
				    }
				    catch(error){
				        isValid = false;
				    }

				    return isValid;
				},
				
				trimToEmpty: function(obj){
					if ($.isBlank(obj)) {
						return "";
					}
					return obj;
				}
			};  
		}(jQuery))  
);  
