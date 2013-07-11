/** 
 * This plugin adds additional string functions to the 
 * jQuery global object. 
 */  
jQuery.extend(
		(function($){  
			return {  
				
				formatText: function() {
					var s = arguments[0];
					for (var i = 0; i < arguments.length - 1; i++) {       
						var reg = new RegExp("\\{" + i + "\\}", "gm");             
						s = s.replace(reg, arguments[i + 1]);
					}

					return s;
				},
				
				isWildcardField : function(fieldName, fieldList){
					if(!$.isEmptyObject(fieldList) && $.isNotBlank(fieldName)){
						var patt = new RegExp("("+fieldList.join("|")+")");
						return patt.test(fieldName);
					}
					return false;
				},
				
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
					return(!obj || $.trim(obj) === "" || obj == null);
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
					return $.isBlank(suffix) ? false: str.indexOf(suffix)>=0 && str.substring(str.length-suffix.length)===suffix;  
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
							replace(/\//g,"_").
							replace(/\(/g,"_").
							replace(/\)/g,"_")
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
				},
				
				capitalize: function(obj) {
					return obj.charAt(0).toUpperCase() + obj.slice(1);
				},
				
				toDecimalFormat: function(vValue) {
					if(isNaN(parseFloat(vValue))) return vValue;
					aDigits = vValue.toFixed(2).split(".");
					aDigits[0] = aDigits[0].split("").reverse().join("").replace(/(\d{3})(?=\d)/g, "$1,").split("").reverse().join("");
					return aDigits.join(".");
				},
				
				toCurrencyFormat: function(sSymbol, vValue) {
					if(isNaN(parseFloat(vValue))) return vValue;
					return sSymbol + $.toDecimalFormat(vValue);
				},
				
				toPercentFormat: function(sSymbol, vValue) {
					if(isNaN(parseFloat(vValue))) return vValue;
					return  $.toDecimalFormat(vValue) + sSymbol;
				},

				compressWhitespaces: function(text) {
					if (text) {
						return text.replace(/[\s]{2,}/g, " ");
					}

					return text;
				},
				
				toLowerCase: function(text) {
					return text && text.toLowerCase();
				},
				
				iequals: function(text1, text2) {
					return $.toLowerCase(text1) === $.toLowerCase(text2);
				},
				
				isValidURL: function(text){
					return /^((https?|s?ftp):\/\/)?(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i.test(text);
				}
			};  
		}(jQuery))  
);  