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

				if(!set.validField('Fullname',profull))
					return;
				else if(!set.validField('Email',proemail,true))
					return;
				else if(!set.validField('Old password',proOld))
					return;
				else if(!$.isBlank(proNew) || !$.isBlank(proRe)){
					if(!set.validField('New password',proNew))
						return;
					else if(!set.validField('Re-type password',proRe))
						return;
					else if(proNew != proRe){
						alert('New and re-type passwords are not match.');
						return;
					}
				}
				
				if(proOld == 'secret')
					proOld = '';

				UserSettingServiceJS.updateUser(set.user,profull,proemail,proOld,proNew,{
					callback:function(data){
						if(data.status == '200'){
							alert(data.message);
							$('#proOld').val('secret');
							$('#proNew').val('');
							$('#proRe').val('');
						}else{
							alert(data.message);
						}	
					}		
				});
			},
			getUser : function(){
				UserSettingServiceJS.getUser({
					callback:function(data){
						$('.proUser').html(data.username);
						$('#profull').val(data.fullname);
						$('#proemail').val(data.email);
						set.user = data.username;
					}		
				});
			},		
			isEmail : function(s) {
				var pattern=/^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\.([a-zA-Z])+([a-zA-Z])+/;
				if(!pattern.test(s))
					return true;
				else
					return false;
			},
			validField : function(f,fv,e,d){
				
				if($.isBlank(fv)){
					alert(f+' cannot be empty.');
					return false;
				}else if(!e && !d && !isAllowedName(fv)){
					alert(f+" contains invalid value.");
					return false;
				}else if(!isAscii(fv)) {
					alert(f+" contains non-ASCII characters.");		
					return false;
				}else if(!isXSSSafe(fv)){
					alert(f+" contains XSS.");
					return false;
				}else if(e && set.isEmail(fv)){
					alert(f+" is invalid.");
					return false;
				}else if(d && !$.isDate(fv)){
					alert(f+" is invalid date.");
					return false;
				}

				return true;
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
