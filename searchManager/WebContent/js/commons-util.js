

function checkIfParsableToInt(str){
	str=$.trim(str);
	var isParsable=true;
	if(str!=''){
		if(isNaN(str)){
			alert('Non numeric string is an invalid input');
			isParsable=false;
		}
	}else{
		alert('Empty string is an invalid input');
		isParsable=false;
	}
	return isParsable;
}

Number.prototype.formatMoney = function(c, d, t){
	var n = this, c = isNaN(c = Math.abs(c)) ? 2 : c, d = d == undefined ? "," : d, t = t == undefined ? "." : t, s = n < 0 ? "-" : "", i = parseInt(n = Math.abs(+n || 0).toFixed(c)) + "", j = (j = i.length) > 3 ? j % 3 : 0;
	   return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
};

function isNumeric(input) {
    var number = /^\-{0,1}(?:[0-9]+){0,1}(?:\[0-9]+){0,1}$/i;
    var regex = RegExp(number);
    return regex.test(input) && input.length>0;
}

function checkIfCanBeParsed(str){
	str=$.trim(str);
	var isParsable=true;
	if(str!=''){
		if(isNaN(str)){
			isParsable=false;
		}
	}else{
		isParsable=false;
	}
	return isParsable;
}

//todo:use this to get the css class depending of the value of the margin
function getClassName(margin){
	var cssClass='';
	//margin could be a string
	if(margin=='Unavailable'|| margin=='unavailable'){
		cssClass="errors_nowrap";
	}else{//if not, then it could be an integer
		//alert(margin+'-'+(margin<0)+'-'+(typeof margin));
		cssClass=margin<0?"errors_nowrap":"profit";
	}
	return cssClass;
}


function truncateValue(num){
	//multiply and divide by 10000 since we want two decimal places
	return Math.floor(num*100)/100;
}

function subtractFloatingPointValue(sub1,sub2){
	//use Number object to prevent inaccurate floating point subtractions, format it to 2 decimal places too
	return Number(sub1-sub2).toFixed(2);
}

//formula for margin is (price-cost)/price * 100
function calculateMargin(s,s2){
	//return (s-s2)/s2*100;
	//use Math.Floor for proper formatting of numbers, without using toFixed which just rounds off the number and is not what we wanted to 
	//do since it has to match the margin found in the db
	return (s-s2)/s*100;
}

function textBlurEvent(obj){
	var str=$.trim(obj.val());
	if(str!=''&&!isNumeric(str)){
		alert('Non numeric string is an invalid input');
		str = str.replace(/[^\d]/g, ''); 
		obj.val(str);
	}
}

function removeInvalidInFloat(float){
	return float.replace(/,/g, '');
}

function format_decimal(id){
	var obj=$(id);
	var val=$.trim(obj.val());
	if(checkIfParsableToInt(val)){
		obj.val(parseFloat(val).formatMoney(2,'.',','));
	}else{
		val = val.replace(/[^\d.]/g, ''); 
		//it should be parsable to int now, so format it, assuming that it is not empty
		obj.val(val!=''?parseFloat(val).formatMoney(2,'.',','):val);
	}
}

function format_decimal(id){
	var obj=$(id);
	var val=$.trim(obj.val());
	if(checkIfParsableToInt(val)){
		obj.val(parseFloat(val).formatMoney(2,'.',','));
	}else{
		val = val.replace(/[^\d.]/g, ''); 
		//it should be parsable to int now, so format it, assuming that it is not empty
		obj.val(val!=''?parseFloat(val).formatMoney(2,'.',','):val);
	}
}

function format_decimal_no_comma(id){
	var obj=$(id);
	var val=$.trim(obj.val());
	if(checkIfParsableToInt(val)){
		obj.val(parseFloat(val).formatMoney(2,'.',''));
	}else{
		val = val.replace(/[^\d.]/g, ''); 
		//it should be parsable to int now, so format it, assuming that it is not empty
		obj.val(val!=''?parseFloat(val).formatMoney(2,'.',''):val);
	}
}

function openWindow(url){
	var params='width=950, height=500, scrollbars=1, resizable=1, menubar=1, status=1,location=1,toolbar=1';
	window.open(url,'eCost_Web_Manager',params);
}

function refreshTooltipDetails(){
	$('td.tooltip').qtip({
		style: { classes: 'ui-tooltip-plain ui-tooltip-shadow' },
		content: {
			title: { text: function(api) {
					return $(this).attr('alt').toUpperCase();
				} 
			},
			text: function(api) {
				if($(this).attr('title') === 'name') {
					return $(this).attr('titlefull');
				} //else if($(this).attr('alt') === 'Auto-Renew Status') {
				//	return "S = Scheduled, U = Unscheduled";
				//}
				else if($(this).text() === '') {									
					return 'Unavailable';
				} else {
					return $(this).attr('title');
				}						
			}
		},
		position: {
			my: 'bottom center',
			at: 'top center'
		}
	});
}

function checkInArray(arr,val){
	var inArr=false;
	for(var i=0;i<arr.length;i++){
		if( val==$.trim(arr[i])){
			inArr=true;
			break;
		}
	}
	return inArr;
}

function checkInArrayIndex(arr,val){
	var nIndex=-1;
	for(var i=0;i<arr.length;i++){
		if( val==$.trim(arr[i])){
			nIndex=i;
			break;
		}	
	}
	return nIndex;
}