$(document).ready(function(){
	/* The following code is executed once the DOM is loaded */

	$('.sponsorFlip').bind("click",function(){

		// $(this) point to the clicked .sponsorFlip element (caching it in elem for speed):

		var elem = $(this);

		// data('flipped') is a flag we set when we flip the element:

		if(elem.data('flipped'))
		{
			// If the element has already been flipped, use the revertFlip method
			// defined by the plug-in to revert to the default state automatically:

			elem.revertFlip();

			// Unsetting the flag:
			elem.data('flipped',false);
		}
		else
		{
			// Using the flip method defined by the plugin:

			elem.flip({
				direction:'lr',
				speed: 350,
				onBefore: function(){
					// Insert the contents of the .sponsorData div (hidden
					// from view with display:none) into the clicked
					// .sponsorFlip div before the flipping animation starts:

					elem.html(elem.siblings('.sponsorData').html());
				}
			});

			// Setting the flag:
			elem.data('flipped',true);
		}
	});

	
	$('.iphone-style').live('click', function() {
		
		checkboxID		= '#' + $(this).attr('rel');

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
	
		checkboxID		= '#' + $(this).attr('rel');

		if($(checkboxID)[0].checked == false) {
		
			$(checkboxID)[0].checked = true;
			$(this).removeClass('off').addClass('on');
			
		} else {
			
			$(checkboxID)[0].checked = false;
			$(this).removeClass('on').addClass('off');
			
		}
	});
	
	$('.iphone-style-checkbox, .firerift-style-checkbox').each(function() {
		
		thisID		= $(this).attr('id');
		thisClass	= $(this).attr('class');

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
	});
});