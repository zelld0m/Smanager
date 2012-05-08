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

	var getAuditTrail = function(curPage) {
		username = $("#userList option:selected").val();
		action = $("#actionList option:selected").val();
		entity = $("#typeList option:selected").val();
		keyword = $("#keyword").val();
		refId = $("#refList option:selected").val();
		startDate = $.trim($("#startDate").val());
		endDate = $.trim($("#endDate").val());
		AuditServiceJS.getAuditTrail(username, action, entity, keyword, refId, startDate, endDate, curPage, pageSize, {
			callback: function(data){
				totalSize = data.totalSize;
				var audits = data.list;
				$("#resultsBody").find("tr:gt(0)").remove();

				for (var i = 0; i < audits.length; i++) {
					audit = audits[i];
					$('#resultsBody').append('<tr><td class=\"txtAC\">' + $.format.date(audit.date, "MM-dd-yyyy HH:mm") + '</td><td class=\"txtAC\"><p class="breakWord w100">' + audit.referenceId + '</p></td><td class=\"txtAC\">' + audit.username + '</td>' +
							'<td class=\"txtAC\"><p class="breakWord w80">' + audit.entity + '</p></td><td class=\"txtAC\"><p class="breakWord w90">' + audit.operation + '</p></td><td class=\"txtAC\">' + audit.keyword + '</td><td><p class="breakWord w135">' + audit.details + '</p></td></tr>');
				}

				$("#resultsBody > tr:even").addClass("alt");

				addFieldValuesPaging(curPage, totalSize);
			},
		});		

	};

	var addFieldValuesPaging = function(curPage, totalItem){
		if(totalItem==0){
			$("div#resultsTopPaging, div#resultsBottomPaging").empty();
		}else{
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

		AuditServiceJS.getDropdownValues({
			callback: function(data){ 
				$.each(data, function(key, element) {
					if ("USER_NAME" == element.name) {
						$("#userList").append($("<option>", { value : element.value }).text(element.value)); 
					} else if ("ACTION" == element.name) {
						$("#actionList").append($("<option>", { value : element.value }).text(element.value)); 
					} else if ("ENTITY" == element.name) {
						$("#typeList").append($("<option>", { value : element.value }).text(element.value)); 
					} else if ("REFERENCE" == element.name) {
						$("#refList").append($("<option>", { value : element.value }).text(element.value)); 
					} 
				});
			},
			errorHandler: function(message){ alert(message); }
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
				alert("Please provide a valid date range");
			}else{
				getAuditTrail(1);
			}
		});

		$("#resetBtn").click(function() {
			$("#userList").val("option:first");
			$("#actionList").val("option:first");
			$("#typeList").val("option:first");
			$("#keyword").val("");
			$("#refList").val("option:first");
			$("#startDate").val("");
			$("#endDate").val("");
		});

		getAuditTrail(1);

	});	
})(jQuery);	