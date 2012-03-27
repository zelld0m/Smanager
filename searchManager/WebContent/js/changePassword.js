function checkPasswordDetails(){
	var pass=$.trim($('input#password').val());
	var new_pass1=$.trim($('input#newPassword1').val());
	var new_pass2=$.trim($('input#newPassword2').val());
	if(pass!=''&&new_pass1!=''&&new_pass2!=''){
		return true;
	}else{
		alert('Fill up all required fields before clicking the button');
		return false;
	}
}