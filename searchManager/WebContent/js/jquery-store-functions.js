/** 
 * This plugin adds additional store related functions to the 
 * jQuery global object. 
 */  
jQuery.extend(
		(function($){  
			return {  
				processTemplate: function(htmlTemplate){
					var htmlTemplate = htmlTemplate.replace("%%store%%", "MacMall");
					return htmlTemplate;
				}
			};  
		}(jQuery))  
);  