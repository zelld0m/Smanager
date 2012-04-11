(function($){
	var pageSize = 10;
	var curPage = 1;
	var keyword = "";
	var action = "";
	var entity = "";
	var refId = "";
	var startDate = null;
	var endDate = null;
	var username = "";
	var totalSize;

	var auditTrail = AuditServiceJS.getAuditTrail(username, action, entity, keyword, refId, startDate, endDate, curPage, pageSize, {
		callback: function(data){
			totalSize = data.totalSize;
			var audits = data.list;
			$("#resultsBody").find("tr:gt(0)").remove();

			for (var i = 0; i < audits.length; i++) {
				audit = audits[i];
				$('#resultsBody').append('<tr><td class=\"txtAC\">' + $.format.date(audit.date, "MM-dd-yyyy HH:mm") + '</td><td class=\"txtAC\">' + audit.referenceId + '</td><td class=\"txtAC\">' + audit.username + '</td>' +
						'<td class=\"txtAC\">' + audit.entity + '</td><td class=\"txtAC\">' + audit.operation + '</td><td class=\"txtAC\">' + audit.keyword + '</td><td class=\"txtAC\">' + audit.details + '</td></tr>');
			}

		},
		errorHandler: function(message){ alert(message); }
	});		
	
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
				var option = this.id == "startDate" ? "minDate" : "maxDate",
						instance = $(this).data("datepicker"),
						date = $.datepicker.parseDate(
								instance.settings.dateFormat ||
								$.datepicker._defaults.dateFormat,
								selectedDate, instance.settings);
				dates.not(this).datepicker("option", option, date);
			}
		});
		
		$("#goBtn").click(function() {
			username = $("#userList option:selected").val();
			action = $("#actionList option:selected").val();
			entity = $("#typeList option:selected").val();
			keyword = $("#keyword").val();
			refId = $("#refList option:selected").val();
			if ($("#startDate").val().length > 0) {
				startDate = $("#startDate").val();
			}
			if ($("#endDate").val().length > 0) {
				endDate = $("#endDate").val();
			}
			AuditServiceJS.getAuditTrail(username, action, entity, keyword, refId, startDate, endDate, curPage, pageSize, {
				callback: function(data){
					totalSize = data.totalSize;
					var audits = data.list;
					$("#resultsBody").find("tr:gt(0)").remove();

					for (var i = 0; i < audits.length; i++) {
						audit = audits[i];
						$('#resultsBody').append('<tr><td class=\"txtAC\">' + $.format.date(audit.date, "MM-dd-yyyy HH:mm") + '</td><td class=\"txtAC\">' + audit.referenceId + '</td><td class=\"txtAC\">' + audit.username + '</td>' +
								'<td class=\"txtAC\">' + audit.entity + '</td><td class=\"txtAC\">' + audit.operation + '</td><td class=\"txtAC\">' + audit.keyword + '</td><td class=\"txtAC\">' + audit.details + '</td></tr>');
					}

				},
				errorHandler: function(message){ alert(message); }
			});		

		});

	});	
})(jQuery);	