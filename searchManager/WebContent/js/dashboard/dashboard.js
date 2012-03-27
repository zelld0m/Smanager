(function ($) {
	$(document).ready(function(){

		var defaultSearchText = "Enter keyword";
		
		$("#search").find('input').bind('keydown', function(e) {
		      if (e.which == 13) {
		        var value = $.trim($(this).val());
		        if (value) {
		        	$("#search #keyword").val(value);
		        	$("#search form").submit();
		        }
		      }
		    }).bind('blur', function(event) {
	   			if ($.trim($(this).val()).length == 0) $(this).val(defaultSearchText);
	   		})
	   		.bind('focus', function(event) {
	   			$(this).val("");
		    });
		
		$("#searchbutton").bind('click', function(e) {
			 var value = $.trim($("#search").find('input').val());
	    	 if (value){
	    		 $("#search #keyword").val(value);
	    		 $("#search form").submit();
	    	 }
		    }); 	
	});
})(jQuery);