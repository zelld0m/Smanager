(function($){
	var Login = {
		init: function(){
			var self= this;
			$("#signinBtn").off().on({
				click: function(e){
					e.preventDefault();
					var username=$("#j_username").val();
					var password=$("#j_password").val();
					if($.isBlank(username) || $.isBlank(password)){
						jAlert("Username and password are required.","Search Manager");
					}else
						$("#f").submit();
						
				}
			});

		}
	};
$(document).ready(function() {
	Login.init();
});	

})(jQuery);	