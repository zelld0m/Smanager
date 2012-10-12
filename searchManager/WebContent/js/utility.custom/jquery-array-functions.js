/** 
 * This plugin adds additional array functions to the 
 * jQuery global object. 
 */  
jQuery.extend(
		(function($){  
			return {  
				/** 
				 * This function returns random members of 
				 * the array provided, up to the number in 
				 * the count provided. 
				 */  
				randArray: function(arr, count) {  
					var randomNumber = function(min, max) {  
						return Math.floor(Math.random() * (max - min + 1)) + min;  
					},  
					rand_arr = [],  
					rand, i;  
					count = (count && (count > arr.length ? arr.length : count)) || 1;  

					for(i = 0; i < count; i++) {  
						rand = randomNumber(0, arr.length - 1);  
						rand_arr[i] = arr[rand];  
						arr.splice(rand, 1);  
					}  

					return rand_arr;  
				},  
				
				/** 
				 * This function removes duplicate members 
				 * of the array provided. 
				 */  
				uniqueArray: function(arr) {  
					var unique_arr = [];  

					return $.grep(arr, function(elem, index) {  
						unique_arr.push(elem);  
						return $.inArray(arr[index], unique_arr) === index;  
					});  
				},  
				
				/** 
				 * This function returns the keys of an array 
				 * or an object provided. 
				 */  
				keysArray: function(arrOrObj) {  
					var type, i, len, retArr = [];  

					// The easy way  
					if (typeof Object.keys === 'function') {  
						return Object.keys(arrOrObj);  
					}  
					// The slightly-less-easy way  
					else {  
						type = Object.prototype.toString.call(arrOrObj);  
						if (type === '[object Array]') {  
							for (i = 0, len = arrOrObj.length; i < len; i++) {  
								retArr.push(String(i));  
							}  
						}  
						else if (type === '[object Object]') {  
							for (i in arrOrObj) {  
								if (arrOrObj.hasOwnProperty(i)) {  
									retArr.push(String(i));  
								}  
							}  
						}  
						return retArr;  
					}  
				},

				/**
				 * Check if an element exists in array using a comparer function
				 * comparer : function(currentElement)
				 */
				objectInArray: function(arrObject, comparer) { 
					for(var i=0; i < arrObject.length; i++) { 
						if(comparer(arrObject[i])) return true; 
					}
					return false; 
				}, 
				
				/**
				 * Adds an element to the array if it does not already exist using a comparer
				 * function
				 */				
				pushIfNotExist : function(arrObject, element, comparer) { 
					if (!$.objectInArray(arrObject, comparer)) {
						arrObject.push(element);
						return true;
					}
					return false;
				}
			};  
		}(jQuery))  
);  