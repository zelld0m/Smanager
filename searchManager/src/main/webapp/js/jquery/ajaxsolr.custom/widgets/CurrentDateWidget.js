(function ($) {

	AjaxSolr.CurrentDateWidget = AjaxSolr.AbstractWidget.extend({
		init: function(){
			var self = this;
			$(self.target).html(self.getTemplate());
			var month = ((GLOBAL_currentDate.getMonth()+1) / 10) >= 1? (GLOBAL_currentDate.getMonth()+1) : "0" + (GLOBAL_currentDate.getMonth()+1);
			var day = (GLOBAL_currentDate.getDate() / 10) >= 1? (GLOBAL_currentDate.getDate()) : "0" + (GLOBAL_currentDate.getDate());
			var formattedCurrentDate = $.formatText("{0}/{1}/{2}", month, day, GLOBAL_currentDate.getYear());
			$(self.target).find("input#overrideCurrentDate").val(formattedCurrentDate).datepicker({
				defaultDate: new Date(GLOBAL_currentDate.getYear(), GLOBAL_currentDate.getMonth(), GLOBAL_currentDate.getDate()),
				changeMonth: true,
				changeYear: true,
				showOn: "both",
				dateFormat: "mm/dd/yy",
				buttonImage: GLOBAL_contextPath + "/images/icon_calendar.png",
				buttonText: "Simulate Current Date",
				buttonImageOnly: true,
				onClose: function(selectedDate){
					$.simCurrDate = new Date(selectedDate);
					self.manager.store.addByValue('simCurrDate', $.trim(selectedDate));
					if($.isNotBlank(self.manager.store.values('q'))){
						self.manager.doRequest();
					}
				}
			});
		},
		
		beforeRequest: function () {
			var self = this;
			var simCurrDate = $.trim($(self.target).find("input#overrideCurrentDate").val());
			if($.isNotBlank(simCurrDate) && $.isDate(simCurrDate)){
				self.manager.store.addByValue('simCurrDate', $.trim(simCurrDate));
			}else{
				self.manager.store.remove('simCurrDate');
			}
		},
		
		afterRequest: function () {
			var self = this;
			
			var simCurrDate = self.manager.store.values('simCurrDate');
			
			if($.isNotBlank(simCurrDate) && $.isDate(simCurrDate)){
				$(self.target).find("input#overrideCurrentDate").val(simCurrDate);
			}
		},

		getTemplate: function(){
			var self = this;
			var template = "";

			template += '<div class="floatL">';
			template += '	<label>Set Current Date: </label>';
			template += '	<input type="text" id="overrideCurrentDate">';
			template += '</div>';
	
			return template;
		}
	});
})(jQuery);