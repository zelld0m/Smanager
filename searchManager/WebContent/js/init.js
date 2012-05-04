/** Global DWR Handler */
dwr.engine.setTextHtmlHandler(function() {
	window.alert("Your session has expired, please login again.");
	document.location.href = document.location.href;
});

dwr.engine.setErrorHandler(function(msg, exc) {
	var errMessage = "Error Message: " + msg;
	var errInfo = "Error Details: " + dwr.util.toDescriptiveString(exc, 3);
	alert(errMessage + '\n' + errInfo);
});

dwr.engine.setWarningHandler(function(msg, wrn) {
	var wrnMessage = "Warning Message: " + msg;
	var wrnInfo = "Warning Details: " + dwr.util.toDescriptiveString(wrn, 3);
	alert(wrnMessage + '\n' + wrnInfo);
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
	case -1: alert("Error encountered while processing " + action + " request for " + param); break;
	case  0: alert("Failed " + action + " request for " + param); break;
	default: alert("Successful " + action + " request for " + param); break;
	}
};

showDeploymentStatusBar = function(moduleName, ruleStatus){
	$("span#status").html("");
	$("span#statusMode").html("");
	$("span#statusDate").html("");

	$("#submitForApproval").hide();

	if(ruleStatus!=null){
		$("#submitForApproval").show();

		$("div#statusHolder").hide();
		if($.isNotBlank(ruleStatus["approvalStatus"])){
			$("div#statusHolder").show();
			$("span#status").html(getRuleNameSubTextStatus(ruleStatus));
		}

		$("div#publishHolder").hide();
		if($.isNotBlank(ruleStatus["lastModifiedDate"])){
			$("div#publishHolder").show();
			$("span#statusDate").html(ruleStatus["lastModifiedDate"].toUTCString());
		}

		$("a#submitForApprovalBtn").show();
		if(ruleStatus["locked"]){
			$("span#statusMode").append("[Read-Only]");
			$("a#submitForApprovalBtn").hide();
		}
		
		$("div#commentHolder span#commentIcon").on({
			click: showAuditList
		}, {type:moduleName, ruleId:ruleStatus["ruleStatusId"], ruleType:"Rule Status" });

	}
};

/** Style for HTML upload tag */
showMessage = function(selector, msg){
	$(selector).qtip({
		id: "hover-custom",
		content: {
			text: $('<div/>')
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

/** Style for HTML upload tag */
showHoverInfo = function(e){
	if(e.data.locked){
		$(this).qtip({
			id: "hover-locked",
			content: {
				text: $('<div/>')
			},
			position:{
				at: 'right center',
				my: 'left center'
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
					$content.html($("#ruleIsLocked").html());
				},
				hide: function(event, api){
					api.destroy();
				}
			}
		});
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
			$(".tabs").each(function() {
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
			});
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
				base.$el.find(selector + ' img.avatar').attr("src","../images/noAvatar.jpg");
				base.$el.find(selector + ' .user').html(model["username"]);
				base.$el.find(selector + ' .duration').html(model["elapsedTime"]);
				base.$el.find(selector + ' .page').html(model["currentPage"]);
			}
		});

		useTabs();
		useTinyMCE();
		refreshDock();
	});
})(jQuery);