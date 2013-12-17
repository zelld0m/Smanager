(function($){

	var UserProfile = {
			showProfile: function(){
				$("#username").updateprofile({
					id:1,
					isPopup: true
				});
			},
			
			addUsernameListener: function(){
				var base = this;
				$("#username").off().on({
					click: base.showProfile()
				});
			},
			
			init: function(){
				var base = this;
				base.addUsernameListener();
			}
	};

	$(document).ready(function(){
		UserProfile.init();
	});
	
})(jQuery);