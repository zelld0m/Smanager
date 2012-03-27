(function ($) {
	
	$(document).ready(function() {
		$("#mG1").microgallery({
			size		: 'large',	/*small,medium,large*/
			menu		: true,
	        mode    	: 'thumbs',
			cycle		: true,
			autoplay	: true,
			autoplayTime: 3000});
		
		
		$("#mG2").microgallery({
			size		: 'large',	/*small,medium,large*/
			menu		: true,
			mode    	: 'thumbs',
			cycle		: true,
			autoplay	: true,
			autoplayTime: 3000});
		
		$("#mG3").microgallery({
			size		: 'large',	/*small,medium,large*/
			menu		: true,
			mode    	: 'thumbs',
			cycle		: true,
			autoplay	: true,
			autoplayTime: 3000});
		
		$("#mG4").microgallery({
			size		: 'large',	/*small,medium,large*/
			menu		: true,
			mode    	: 'thumbs',
			cycle		: true,
			autoplay	: true,
			autoplayTime: 3000});
		
		
	    $("#startdatepicker").datepicker({
		    showOn: "both",
		    buttonImage: "../../images/icon_calendarwithBG.png",
		    buttonImageOnly: true
	    });
	    
	    $("#enddatepicker").datepicker({
		    showOn: "both",
		    buttonImage: "../../images/icon_calendarwithBG.png",
		    buttonImageOnly: true
	    });
	    
	    $("#startdatepicker2").datepicker({
		    showOn: "both",
		    buttonImage: "../images/icon_calendarwithBG.png",
		    buttonImageOnly: true
	    });
	    
	    $("#enddatepicker2").datepicker({
		    showOn: "both",
		    buttonImage: "../images/icon_calendarwithBG.png",
		    buttonImageOnly: true
	    });
	});
	
})(jQuery);