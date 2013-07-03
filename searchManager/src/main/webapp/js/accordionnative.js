function show_more_info(id) {
    $("#plusicon_" + id).click(function () {
		$("#more_info_" + id).slideDown('fast', function () {});
		$("#plusicon_" + id).css("display", "none");
		$("#minusicon_" + id).css("display", "block");
	});
    $("#minusicon_" + id).click(function () {
    	$("#more_info_" + id).slideUp('fast', function () {});
    	$("#plusicon_" + id).css("display", "block");
    	$("#minusicon_" + id).css("display", "none");
    });
}

/*
 function show_more_info(id,action,image_id) {
    if(action=='show'){
		$("#more_info_" + id).slideDown('fast');
		$("#plusicon_" + id).css("display", "none");
		$("#minusicon_" + id).css("display", "block");
		
		//lazy load image
		var imgObj=$('img#'+image_id);
		if($.trim(imgObj.attr('src')).indexOf('spacer2.png')>0){
			//try to load image
			imgObj.attr('src',imgObj.attr('to_load'));
		}
	}else{
    	$("#more_info_" + id).slideUp('fast');
    	$("#plusicon_" + id).css("display", "block");
    	$("#minusicon_" + id).css("display", "none");
 	}
}
 */