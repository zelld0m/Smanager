
// When the page has loaded focus the element marked with tab index of 1.
$(document).ready(function(){
	$('input[tabindex="1"]').focus();
});

function substring_text(text) {
	var dotdot = "";		
	
	if(text.length > 22) dotdot = '...';
	document.write(text.substring(0,25) + dotdot);
}

function warnIfDeletesChecked() {
	alert("warnIfDeletesChecked");
}

function markAllRowsForDelete() {
	alert("markAllRowsForDelete");
}

ddsmoothmenu.init({
	mainmenuid: "smoothmenu1", //menu DIV id
	orientation: 'h', //Horizontal or vertical menu: Set to "h" or "v"
	classname: 'ddsmoothmenu', //class added to menu's outer DIV
	//customtheme: ["#1c5a80", "#18374a"],
	contentsource: "markup" //"markup" or ["container_id", "path_to_menu_file"]
})