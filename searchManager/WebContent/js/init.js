/** Global DWR Handler */
dwr.engine.setTextHtmlHandler(function() {
	window.alert("Your session has expired, please login again.");
	document.location.href = document.location.href;
});

dwr.engine.setErrorHandler(function(msg, exc) {
	var errMessage = "Error Message: " + msg;
	var errInfo = "Error Details: " + dwr.util.toDescriptiveString(exc, 3);
	//alert(errMessage + '\n' + errInfo);
});

dwr.engine.setWarningHandler(function(msg, wrn) {
	var wrnMessage = "Warning Message: " + msg;
	var wrnInfo = "Warning Details: " + dwr.util.toDescriptiveString(wrn, 3);
	//alert(wrnMessage + '\n' + wrnInfo);
});

resetInputFields = function(selector){
	$(selector).filter("input,textarea").val("");
};

getRuleNameSubTextStatus = function(ruleStatus){
	if (ruleStatus==null) 
		return "Unknown Status";

	if (ruleStatus!=null && $.isBlank(ruleStatus["approvalStatus"])) 
		return "Setup a Rule";

	switch (ruleStatus["approvalStatus"]){
	case "REJECTED": return "Action Required";
	case "PENDING": return "Awaiting Approval";
	case "APPROVED": return "Ready For Production";
	}	
};

showActionResponse = function(code, action, param){
	switch(code){
	case -1: jAlert("Error encountered while processing " + action + " request for " + param, "Error Encountered"); break;
	case  0: jAlert("Failed " + action + " request for " + param, "Failed Request"); break;
	default: jAlert("Successful " + action + " request for " + param, "Successful Request"); break;
	}
};

showActionResponseFromMap = function(code, action, param, additionalFailMessage){
	var message = "";
	var title = "";
	
	if (code["PASSED"].length > 0) {
		message += "Successful " + action + " request for " + code["PASSED"] + ".";
		title = "Successful Request";
	}
	if (message !== "") {
		message += "\n\n";
	}
	if (code["FAILED"].length > 0) {
		message += "Failed " + action + " request for " + code["FAILED"]+ ".";
		title = "Failed Request";
		if (additionalFailMessage) {
			message += "\n" + additionalFailMessage;
		}
	}
	jAlert(message,title); 
};

/** Style for HTML upload tag */
showMessage = function(selector, msg){
	$(selector).qtip({
		id: "hover-custom",
		content: {
			text: $('<div/>')
		},
		position: {
			at: 'right center',
			my: 'left center',
			target: $(selector)
		},
		show:{
			solo: false,
			ready: true
		},
		hide: 'unfocus, mouseout',
		style:{width:'auto'},
		events: {
			show: function(event, api){
				var $content = $("div", api.elements.content);
				$content.html(msg);
			},
			hide: function(event, api){
				api.destroy();
			}
		}
	});
};

getLockedRuleHTMLTemplate = function(){
	var template = '';

	template += '<div id="ruleIsLocked" class="w180">';
	template += '	<div class="w180 alert">';
	template += '		You are not allowed to perform this action because you do not have the required permission or rule is temporarily locked.';
	template += '	</div>';
	template += '</div>';

	return $(template).html();
};


getLastModifiedHTMLTemplate = function(user, date){
	var template = '';
	
	template += '<div>';
	template += '	<div>Modified by <strong>' + user + '</strong><br/>';
	template += '	on ' + date;
	template += '	</div>';
	template += '</div>';

	return $(template).html();
};

showLastModified = function(e){
	showMessage(e.target, getLastModifiedHTMLTemplate(e.data.user, e.data.date));
};

/** Style for HTML upload tag */
showHoverInfo = function(e){
	if(e.data.locked){
		showMessage(e.target, getLockedRuleHTMLTemplate());
	}
};

/** Style for HTML upload tag */
var W3CDOM = (document.createElement && document.getElementsByTagName);

function initFileUploads() {
	if (!W3CDOM) return;
	var fakeFileUpload = document.createElement('div');
	fakeFileUpload.className = 'fakefile';
	fakeFileUpload.appendChild(document.createElement('input'));
	var image = document.createElement('img');
	image.src='../images/img_uploadfile.jpg';
	fakeFileUpload.appendChild(image);
	var x = document.getElementsByTagName('input');
	for (var i=0;i<x.length;i++) {
		if (x[i].type != 'file') continue;
		if (x[i].parentNode.className != 'fileinputs') continue;
		x[i].className = 'file hidden';
		var clone = fakeFileUpload.cloneNode(true);
		x[i].parentNode.appendChild(clone);
		x[i].relatedElement = clone.getElementsByTagName('input')[0];
		x[i].onchange = x[i].onmouseout = function () {
			this.relatedElement.value = this.value;
		};
	}
}

/** Global initialization of jQuery */
(function($){
	$(document).ready(function() {

		var load = false;
		window.onfocus = function(){
			if (load) {
				load = false;
				var serverSelected = $.trim($.cookie(COOKIE_SERVER_SELECTED));
				if (serverSelected !== $("#select-server option:selected").text()) {
					$("#select-server").triggerHandler("change", {reload: true});				
				}
			}
		};

		window.onblur = function() {
			load = true;
		};

		var useTinyMCE = function(){
			$('textarea.tinymce').tinymce({
				// Location of TinyMCE script
				script_url : '../js/jquery/tinymce-3.5b3/tiny_mce/tiny_mce.js',

				// General options
				mode : "textareas",
				theme : "advanced",
				plugins : "autolink,lists,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template,advlist",

				// Theme options
				theme_advanced_buttons1 : "bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,styleselect,formatselect,fontselect,fontsizeselect",
				theme_advanced_buttons2 : "cut,copy,paste,pastetext,pasteword,|,search,replace,|,bullist,numlist,|,outdent,indent,blockquote,|,undo,redo,|,link,unlink,anchor,image,cleanup,help,code,|,insertdate,inserttime,preview,|,forecolor,backcolor",
				theme_advanced_buttons3 : "tablecontrols,|,hr,removeformat,visualaid,|,sub,sup,|,charmap,emotions,iespell,media,advhr,|,print,|,ltr,rtl,|,fullscreen",
				theme_advanced_buttons4 : "insertlayer,moveforward,movebackward,absolute,|,styleprops,|,cite,abbr,acronym,del,ins,attribs,|,visualchars,nonbreaking,template,pagebreak",
				theme_advanced_toolbar_location : "bottom",
				theme_advanced_toolbar_align : "left",
				theme_advanced_statusbar_location : "top",
				theme_advanced_resizing : false,

				// Skin options
				skin : "o2k7",
				skin_variant : "silver",

				// Example content CSS (should be your site CSS)
				content_css : "css/content.css",

				// Drop lists for link/image/media/template dialogs
				template_external_list_url : "lists/template_list.js",
				external_link_list_url : "lists/link_list.js",
				external_image_list_url : "lists/image_list.js",
				media_external_list_url : "lists/media_list.js",

				// Replace values for the template plugin
				template_replace_values : {
					username : "Some User",
					staffid : "991234"
				}
			});
		};

		var useTabs = function(){
			$(".tabs").each(function(idx, el) {
				if($(this).attr("id")!=="redirect-type"){
					var tabid = "ui-tab-" + $(this).attr("id").toLowerCase();
					if (tabid == undefined || tabid == null || tabid == "") {
						$(this).tabs({
							cookie: {
								expires: 30
							}
						});
					}
					else {
						$(this).tabs({
							cookie: {
								expires: 30,
								name: tabid
							}
						});
					}
				}
			});
		};

		var COOKIE_SERVER_SELECTION = "server.selection";
		var COOKIE_SERVER_SELECTED = "server.selected";

		var getServerList = function(){

			var serverSelection = $.trim($.cookie(COOKIE_SERVER_SELECTION));

			if($.isNotBlank(serverSelection)){
				$("#select-server option").remove();
				parseData = JSON.parse($.trim($.cookie(COOKIE_SERVER_SELECTION)));
				for (key in parseData){
					$("#select-server").append($("<option>", { value : key }).text(key));							
				}
				setSelectedServer();				
			}else{
				$("#select-server option").remove();
				UtilityServiceJS.getServerListForSelectedStore(true, {
					callback:function(data){
						$.cookie(COOKIE_SERVER_SELECTION, JSON.stringify(data) ,{path: GLOBAL_contextPath});
						for (key in data){
							$("#select-server").append($("<option>", { value : key }).text(key));							
						}
						setSelectedServer();
					}
				});

			}

			$("#select-server").off().on({
				change: function(event, data){
					var reload;
					if (data != undefined) {
						reload = data["reload"];
					}
					if (reload == undefined) {
						$.cookie(COOKIE_SERVER_SELECTED, $("#select-server option:selected").val() ,{path:GLOBAL_contextPath});
						UtilityServiceJS.setServerName($("#select-server option:selected").text(), {
							callback:function(){

							}
						});						
					}
					else if (reload == true) {
						setSelectedServer();
					}
				}
			});
		};

		var setSelectedServer = function() {
			var serverSelected = $.trim($.cookie(COOKIE_SERVER_SELECTED));
			if ($.isBlank(serverSelected)) {
				UtilityServiceJS.getServerName({
					callback:function(serverName){
						$.cookie(COOKIE_SERVER_SELECTED, serverName ,{path:GLOBAL_contextPath});
						$("#select-server option[value='" + serverName + "']").attr("selected", "selected");
					}
				});
			}
			else {
				$("#select-server option[value='" + serverSelected + "']").attr("selected", "selected");				
			}
		};

		var COOKIE_NAME_DOCK = "dock.active";

		var refreshDock = function(){
			var dockActive = $.trim($.cookie(COOKIE_NAME_DOCK));

			$('div#dockItem > div').hide();
			$("ul#dockIcon > li > a").removeClass("active");

			if ($.isNotBlank(dockActive)){
				$('ul#dockIcon > li#' + dockActive + " > a").addClass("active");
				$('div#dockItem > div#dock' + dockActive).show();
			}
		};

		$("ul#dockIcon > li > a").on("click",function(evt){
			$this = $(evt.target);
			var cookieVal = $this.parent("li").attr("id");

			if($this.hasClass("active")) cookieVal = "";

			$.cookie(COOKIE_NAME_DOCK, cookieVal ,{expires: 1});
			refreshDock();

		});

		$("#onlineList").auditpanel({
			fieldName: "username",
			headerText : "Online",
			page: 1,
			pageSize: 5,
			type: "online",
			itemDataCallback: function(base, page){
				DAOCacheServiceJS.getAllLoggedInUser({
					callback: function(data){
						base.populateList(data);
						base.addPaging(page, data.totalSize);
					},
					preHook: function(){ base.prepareList(); }
				});
			},
			itemOptionCallback: function(base, id, name, model){
				var selector = '#item' + $.escapeQuotes($.formatAsId(id)); 
				base.$el.find(selector + ' img.avatar').attr("src", GLOBAL_contextPath + "/images/noAvatar.jpg");
				base.$el.find(selector + ' .user').html(model["username"]);
				base.$el.find(selector + ' .duration').html(model["elapsedTime"]);
				base.$el.find(selector + ' .page').html(model["currentPage"]);
			}
		});

		useTabs();
		useTinyMCE();
		getServerList();
		refreshDock();
	});
})(jQuery);
