(function($){

	$(document).ready(function(){

		set = {			
			user : '',	
					
			update : function(){
				
				var profull = $.trim($('#profull').val());
				var proemail = $.trim($('#proemail').val());
				var proOld = $.trim($('#proOld').val());
				var proNew = $.trim($('#proNew').val());
				var proRe = $.trim($('#proRe').val());

				if(!validateGeneric('Fullname',profull,1))
					return;
				else if(!validateEmail('Email',proemail,1))
					return;
				else if(!validatePassword('Old password',proOld))
					return;
				else if(!$.isBlank(proNew) || !$.isBlank(proRe)){
					if(!validatePassword('New password',proNew,8))
						return;
					else if(!validatePassword('Re-type password',proRe,8))
						return;
					else if(proNew != proRe){
						jAlert('New and re-type passwords do not match.',"User Setting");
						return;
					}
				}
				
				UserSettingServiceJS.updateUser(set.user,profull,proemail,proOld,proNew,{
					callback:function(data){
						if(data.status == '200'){
							jAlert(data.message,"User Setting");
							$('#proOld').val('');
							$('#proNew').val('');
							$('#proRe').val('');
						}else{
							jAlert(data.message,"User Setting");
						}	
					}		
				});
			},
			getUser : function(){
				UserSettingServiceJS.getUser({
					callback:function(data){
						$('.proUser').html(data.username);
						$('#profull').val(data.fullName);
						$('#proemail').val(data.email);
						set.user = data.username;
					}		
				});
			},		
			init : function(){
				set.getUser();
				$("#probut").on({
					click: function(e){
						set.update();
					}
				});		
			}
		};

		set.init();	
	});
})(jQuery);	
