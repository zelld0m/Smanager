(function($){
	$(document).ready(function(){
		/*
		 * Common dialogue() function that creates our dialogue qTip.
		 * We'll use this method to create both our prompt and confirm dialogues
		 * as they share very similar styles, but with varying content and titles.
		 */
		Dialogue = function(content, title) {
			/* 
			 * Since the dialogue isn't really a tooltip as such, we'll use a dummy
			 * out-of-DOM element as our target instead of an actual element like document.body
			 */
			$('<div />').qtip({
					content: {
						text: content,
						title: title
					},
					position: {
						my: 'center', 
						at: 'center', // Center it...
						target: $(window) // ... in the window
					},
					show: {
						ready: true, // Show it straight away
						modal: {
							on: true, // Make it modal (darken the rest of the page)...
							blur: false // ... but don't close the tooltip when clicked
						},
						solo: true
					},
					hide: false, // We'll hide it maunally so disable hide events
					style: {
						classes: 'ui-tooltip-wiki ui-tooltip-light ui-tooltip-shadow'
					}, 
					events: {
						// Hide the tooltip when any buttons in the dialogue are clicked
						render: function(event, api) {
							$('button', api.elements.content).click(api.hide);
						},
						// Destroy the tooltip once it's hidden as we no longer need it!
						hide: function(event, api) { api.destroy(); }
					}
					});
		};

		// Our Alert method
		Alert = function (content, title){
			// Content will consist of the message and an ok button
			var message = $('<div/>', { text: message }),
			ok = $('<button />', { text: 'Ok', 'class': 'full' });
			Dialogue( message.add(ok), title);
		};
		
		// Added search functionality to list
		SearchableList = function(content){
			
			zebraRows = function(selector, className){  
				$(selector).removeClass(className).addClass(className);  
			};

			addRecordCount = function(selector, target){ 
				var count = $(selector).length;  
				if (count==0){
					$(target).html("No Records Found!");
				}
				else{	
					$(target).html(count);
					$(target).append(' ');
					$(target).append(count == 1 ? "Record" : "Records");
				}
			};
			
			content.find('input#searchField').val('');
			content.find('.resultTable tr').removeClass('visible').show();
			content.find('.resultTable tr td').removeClass('odd').show();
			addRecordCount(content.find('.resultTable tr'), content.find('.searchCount'));

			zebraRows(content.find('.resultTable tbody tr:odd td'), 'odd');

			content.find('.resultTable tbody tr').hover(function(){  
				$(this).find('td').addClass('hovered');  
			}, function(){  
				$(this).find('td').removeClass('hovered');  
			}); 

			content.find('.resultTable tbody tr').addClass('visible');  

			content.find('input#searchField').keyup(function(event) {  
				//if esc is pressed or nothing is entered  

				if (event.keyCode == 27 || $(this).val() == '') {  
					//if esc is pressed we want to clear the value of search box  
					$(this).val('');  

					//we want each row to be visible because if nothing  
					//is entered then all rows are matched.  
					content.find('.resultTable tbody tr').removeClass('visible').show().addClass('visible');  
				}  

				//if there is text, lets filter  
				else {  
					query = $.trim($(this).val()); //trim white space  
					query = query.replace(/ /gi, '|'); //add OR for regex query  

					content.find('.resultTable tbody tr').each(function() {  
						($(this).text().search(new RegExp(query, "i")) < 0) ? $(this).hide().removeClass('visible') : $(this).show().addClass('visible');  
					}); 
				}  

				//reapply zebra rows  
				content.find('.resultTable tr.visible td').removeClass('odd');  
				zebraRows(content.find('.resultTable tr[class=visible]:odd td'), 'odd');
				addRecordCount(content.find('.resultTable tr.visible'), content.find('.searchCount'));
			});
		};
	});
})(jQuery);