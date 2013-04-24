(function($) {
	$(document).ready(function(){

		$.alerts = {
				_show: function(opt, title, content, callback){
					var callbackDone = false;
					$("<div>").prop({
						id: "jd-custom"
					}).html(content).dialog({
						title: title,
						height: "auto",
						modal: true,
						draggable: true,
						resizable: false,
						stack: false, 
						zIndex: 15000,
						create: function(event, ui){
							switch(opt){
							case "alert": 
								$(this).dialog(
										"option", 
										"buttons", 
										[ 
										 {	
											 text: "Ok", 
											 click: function() { 
												 callbackDone = true;
												 $( this ).dialog("close"); 
											 } 
										 }
										 ]);
								break;
							case "confirm":
								$(this).dialog(
										"option", 
										"buttons", 
										[ 
										 {	
											 text: "Cancel", 
											 click: function() { 
												 callbackDone = true;
												 if(callback) callback(false);
												 $(this).dialog("close"); 
											 } 
										 },
										 {	
											 text: "OK", 
											 click: function() {
												 callbackDone = true;
												 if(callback) callback(true);
												 $(this).dialog("close"); 
											 } 
										 }
										 ]);
								break;
							}					
						},
						close: function(event, ui){
							if(callback && !callbackDone) callback(false);
							$(this).dialog("destroy");
							$(this).remove();
						}	          
					});
				}
		};

		// Shortcut functions
		jAlert = function(message, title, callback) {
			$.alerts._show("alert", title, message, callback);
		};

		jConfirm = function(message, title, callback) {
			$.alerts._show("confirm", title, message, callback);
		};
	});

})(jQuery);
