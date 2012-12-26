jQuery.extend(
		(function($){
			return {
				getWeekNumber: function(date){
				    d = new Date(date);
				    d.setHours(0,0,0);
				    d.setDate(d.getDate() + 4 - (d.getDay()||7));

				    var yearStart = new Date(d.getFullYear(),0,1);
				    var weekNo = Math.ceil(( ( (d - yearStart) / 86400000) + 1)/7);

				    return [d.getFullYear(), weekNo];
				},

				getFirstDayOfWeek: function(date) {
					d = new Date(date);
					d.setHours(0,0,0);
					d.setDate(d.getDate() - (d.getDay() || 7) + 1);
					
					return d;
				},

				getLastDayOfWeek: function(date) {
					d = new Date(date);
					d.setHours(0,0,0);
					d.setDate(d.getDate() - (d.getDay() || 7) + 7);
					
					return d;
				}
			};
		}(jQuery))  
);  