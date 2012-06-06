(function ($) {
	$(document).ready(function() { 
		$('.iphone-style').live('click', function() {
			checkboxID = '#' + $(this).attr('rel');

			if($(checkboxID)[0].checked == false) {
				$(this).animate({backgroundPosition: '0% 100%'});
				$(checkboxID)[0].checked = true;
				$(this).removeClass('off').addClass('on');
			} else {
				$(this).animate({backgroundPosition: '100% 0%'});
				$(checkboxID)[0].checked = false;
				$(this).removeClass('on').addClass('off');
			}
		});

		$('.firerift-style').live('click', function() {
			checkboxID = '#' + $(this).attr('rel');

			
			if($(checkboxID)[0].checked == false) {
				$(checkboxID)[0].checked = true;
				$(this).removeClass('off').addClass('on');
				$(this).css("background-position", "0% 100%");
			} else {
				$(checkboxID)[0].checked = false;
				$(this).removeClass('on').addClass('off');
				$(this).css("background-position", "100% 0%");
			}
		});

		$('.iphone-style-checkbox, .firerift-style-checkbox').each(function() {

			thisID		= $(this).attr('id');
			thisClass	= $(this).attr('class');
			setClass	= "firerift-style";
			
			switch(thisClass) {
			case "iphone-style-checkbox":
				setClass = "iphone-style";
				break;
			case "firerift-style-checkbox":
				setClass = "firerift-style";
				break;
			}

			$(this).addClass('hidden');
			
			if($(this)[0].checked == true)
				$(this).after('<div class="'+ setClass +' on" rel="'+ thisID +'">&nbsp;</div>');
			else
				$(this).after('<div class="'+ setClass +' off" rel="'+ thisID +'">&nbsp;</div>');
			
			var imageLabel = "check-default.png";
			
			if ($(this).hasClass("active-locked")) imageLabel = "check-active-locked.png";
			
			$('.' + setClass).css("background", "url('" + contextPath + "/js/jquery/checkbox/" + imageLabel + "') no-repeat");
			
		});
	});
})(jQuery);