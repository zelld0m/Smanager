(function($){
	var pageSize = 10;
	var curPage = 1;
	var username = "";
	var action = "";
	var entity = "";
	var keyword = "";
	var refId = "";
	var	startDate = "";
	var	endDate = "";
	var totalSize = 0;
	var filter = "";
	var getAuditTrail = function(curPage) {
		username = $("#userList option:selected").val();
		action = $("#actionList option:selected").val();
		entity = $("#typeList option:selected").val();
		keyword = $("#keyword").val();
		refId = $("#refList option:selected").val();
		startDate = $.trim($("#startDate").val());
		endDate = $.trim($("#endDate").val());
		
		if (!$.isBlank(keyword) && !isAllowedName(keyword)) {
			jAlert("Keyword is an invalid value!","Audit");
			return;
		} else if(($.isNotBlank(startDate) && !$.isDate(startDate)) || ($.isNotBlank(endDate) && !$.isDate(endDate))){
			jAlert("Please provide a valid date range!","Audit");
			return;
		} else if ($.isNotBlank(startDate) && $.isDate(startDate) && $.isNotBlank(endDate) && $.isDate(endDate) && (new Date(startDate).getTime() > new Date(endDate).getTime())) {
			jAlert("End date cannot be earlier than start date!","Audit");
			return;
		}
		AuditServiceJS.getAuditTrail(username, action, entity, keyword, refId, startDate, endDate, curPage, pageSize, {
			callback: function(data){
				totalSize = data.totalSize;
				var audits = data.list;
				$("#resultsBody").find("tr:gt(0)").remove();

				
				if (audits.length > 0) {
					for (var i = 0; i < audits.length; i++) {
						audit = audits[i];
						$('#resultsBody').append('<tr><td class=\"txtAC\">' + $.format.date(audit.date, "MM-dd-yyyy HH:mm") + '</td><td class=\"txtAC\"><p class="breakWord w100">' + audit.referenceId + '</p></td><td class=\"txtAC\">' + audit.username + '</td>' +
								'<td class=\"txtAC\"><p class="breakWord w80">' + audit.entity + '</p></td><td class=\"txtAC\"><p class="breakWord w90">' + audit.operation + '</p></td><td class=\"txtAC\">' + $.trimToEmpty(audit.keyword) + '</td><td><p class="breakWord w135">' + audit.details + '</p></td></tr>');
					}
					
					$("#resultsBody > tr:even").addClass("alt");
				}
				else {
					$('#resultsBody').append('<tr><td colspan=7 class="txtAC fsize12" >No matching records found.</td></tr>');
				}
				addFieldValuesPaging(curPage, totalSize);					
			}
		});		

	};
	

	
	var addFieldValuesPaging = function(curPage, totalItem){
		if(totalItem==0){
			$("div#resultsTopPaging, div#resultsBottomPaging").empty();
			$("#exportBtn").hide();
		}else{
			$("#exportBtn").show();
			$("#resultsTopPaging, #resultsBottomPaging").paginate({
				currentPage: curPage, 
				pageSize: pageSize,
				totalItem: totalItem,
				callbackText: function(itemStart, itemEnd, itemTotal){
					return "Displaying " + itemStart + "-" + itemEnd + " of " + itemTotal + " Items";
				},
				pageLinkCallback: function(e){ getAuditTrail(e.data.page); },
				nextLinkCallback: function(e){ getAuditTrail(e.data.page+1);},
				prevLinkCallback: function(e){ getAuditTrail(e.data.page-1);},
				firstLinkCallback: function(e){getAuditTrail(1);},
				lastLinkCallback: function(e){ getAuditTrail(e.data.totalPages);}
			});

			$("#exportBtn").download({
				headerText:"Download Audit",
				requestCallback:function(e){
					var params = new Array();
					var url = document.location.pathname + "/xls";
					var urlParams = "";
					var count = 0;

					params["filename"] = e.data.filename;
					params["type"] = e.data.type;
					params["username"] = username;
					params["operation"] = action;
					params["entity"] = entity;
					params["keyword"] = keyword;
					params["referenceId"] = refId;
					params["startDate"] = startDate;
					params["endDate"] = endDate;
					params["totalSize"] = totalSize;
					params["clientTimezone"] = +new Date();

					for(var key in params){
						if (count>0) urlParams +='&';
						urlParams += (key + '=' + params[key]);
						count++;
					};

					document.location.href = url + '?' + urlParams;
				}
			});

		}
		
	};
	
	
	
	$(document).ready(function() { 
		$("#typeList").off().on({
			change: function(e){
				entity = $("#typeList option:selected").val();
				filter = entity;
				if(entity != "") {
					$("#actionList").empty();
					AuditServiceJS.getDropdownValues(2,filter.split(","),{
						callback: function(data){
							$("#actionList").append($("<option>", { value : "" }).text("-- Select Action --"));
							for (var i = 0; i < data.length; i++) {
								$("#actionList").append($("<option>", { value : data[i]}).text(data[i])); 
							};
						},
						postHook: function(){$("#actionList").removeAttr("disabled");}
						,
						errorHandler: function(message){ jAlert(message,"Audit"); }
					});	
				}else{
					$("#refList").val(""); 							
					$("#actionList").val(""); 							
					$("#actionList").attr("disabled","disabled"); 							
					$("#refList").attr("disabled","disabled"); 						
				}
			}
		});
		
		$("#actionList").off().on({
			change:	function(e){
				action = $("#actionList option:selected").val();
				
				filter = filter + "," + action;
				if(action != "") {
					$("#refList").empty();
					AuditServiceJS.getDropdownValues(4,filter.split(","),{
					callback: function(data){ 
						$("#refList").append($("<option>", { value : "" }).text("-- Select Ref ID --"));
						for (var i = 0; i < data.length; i++) {
								$("#refList").append($("<option>", { value : data[i] }).text(data[i])); 
						};
					},
					postHook: function(){$("#refList").removeAttr("disabled");}
					,
					errorHandler: function(message){ jAlert(message,"Audit"); }
					});	
				}	
				else{
					$("#refList").val("");
					$("#refList").attr("disabled","disabled");
				}
			}
		});
		AuditServiceJS.getDropdownValues(1,filter.split(","),{
			callback: function(data){ 
				for (var i = 0; i < data.length; i++) {
						$("#userList").append($("<option>", { value : data[i] }).text(data[i])); 
				};
			},
			errorHandler: function(message){ jAlert(message,"Audit"); }
		});		
	

		AuditServiceJS.getDropdownValues(3,filter.split(","),{
			callback: function(data){ 
				for (var i = 0; i < data.length; i++) {
						$("#typeList").append($("<option>", { value : data[i] }).text(data[i])); 
				};
			},
			errorHandler: function(message){ jAlert(message,"Audit"); }
		});		

	
		var dates = $("#startDate, #endDate").datepicker({
			defaultDate: "+1w",
			showOn: "both",
			buttonImage: "../images/icon_calendar.png",
			buttonImageOnly: true,
			onSelect: function(selectedDate) {
				var option = (this.id === "startDate") ? "minDate" : "maxDate",
						instance = $(this).data("datepicker"),
						date = $.datepicker.parseDate(
								instance.settings.dateFormat ||
								$.datepicker._defaults.dateFormat,
								selectedDate, instance.settings);
				dates.not(this).datepicker("option", option, date);
			}
		});

		$("#goBtn").click(function() {
			var strDate = $.trim($("#startDate").val());
			var endDate = $.trim($("#endDate").val());

			if(($.isNotBlank(strDate) && !$.isDate(strDate)) || ($.isNotBlank(endDate) && !$.isDate(endDate))){
				jAlert("Please provide a valid date range");
			}else{
				getAuditTrail(1);
			}
		});

		$("#resetBtn").click(function() {
			$("#keyword").val("");
			$("#keyword").val("");
			$("#startDate").val("");
			$("#endDate").val("");
			
			$('#userList').prop("selectedIndex", 0);
			$('#refrole').prop("selectedIndex", 0);
			$('#actionList').prop("selectedIndex", 0);
			$('#typeList').prop("selectedIndex", 0);
			$('#refList').prop("selectedIndex", 0);
			
			getAuditTrail(1);
		});

		getAuditTrail(1);

	});	
})(jQuery);	
