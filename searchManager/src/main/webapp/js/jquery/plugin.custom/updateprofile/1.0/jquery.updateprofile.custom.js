(function($){

			$.updateprofile = function(el, options) {
				// To avoid scope issues, use 'base' instead of 'this'
				// to reference this class from internal events and functions.
				var base = this;

				// Access to jQuery and DOM of element
				base.$el = $(el);
				base.el = el;

				// Add a reverse reference to the DOM object
				base.$el.data("updateprofile", base);

				base.options = $.extend({}, $.updateprofile.defaultOptions, options);

				// Run initializer
				base.init();
			};
			
			$.updateprofile.prototype.setId = function(id) {
				var base= this;
				var ui = base.$el;

				ui.find("div:first").prop({
					id: $.isNotBlank(id)? id: "plugin-updateprofile-" + base.options.id
				});

			};
			
			$.updateprofile.prototype.setUser = function(user) {
				var base= this;
				var ui = base.$el;

				base.user= user;

			};
			
			$.updateprofile.prototype.init = function() {
				var base = this;
				
				if(base.options.isPopup) {
					base.$el.qtip({
						id: "plugin-updateprofile-qtip",
						content: {
							text: $('<div/>'),
							title: {text: "User Setting", button: true }
						},
						position:{
							at: 'bottom center',
							my: 'top center',
							target: base.$el
						},
						style: {
							width: 'auto'
						},
						events: {
							render: function(event, api) {
								base.api = api;
								base.$el = $("div", api.elements.content);
							},

							show: function(event, api){
								base.$el.empty().append(base.getTemplate());
								base.setId();
								base.populateContents.call(base);
							}
						}
					});
				} else {
					base.$el.empty().append(base.getTemplate());
					base.setId();
					base.populateContents.call(base);
				}
			};
			
			$.updateprofile.prototype.populateContents = function() {
				var base = this;
				var ui = base.$el;
				var user = '';
				
				UserSettingServiceJS.getUser({
					callback:function(data){
						$('.proUser').html(data.username);
						$('#profull').val(data.fullName);
						$('#proemail').val(data.email);
						base.setUser(data.username);
					}
				});
				
				base.registerEventListener();
			};
			
			$.updateprofile.prototype.registerEventListener = function(){
				var base = this;
				var ui = base.$el;
				
				base.addButtonListener();
			};
			
			$.updateprofile.prototype.addButtonListener = function() {
				var base = this;
				var ui = base.$el;
							
				ui.find("#probut").off().on({
					click: function(){
							
						var profull = $.trim($('#profull').val());
						var proemail = $.trim($('#proemail').val());
						var proOld = $.trim($('#proOld').val());
						var proNew = $.trim($('#proNew').val());
						var proRe = $.trim($('#proRe').val());

						if(!validateGeneric('Fullname',profull,1))
							return;
						else if(!validateEmail('Email',proemail,1))
							return;
						else if(!validatePassword('Old password',proOld))
							return;
						else if(!$.isBlank(proNew) || !$.isBlank(proRe)){
							if(!validatePassword('New password',proNew, 8))
								return;
							else if(!validatePassword('Re-type password',proRe, 8))
								return;
							else if(proNew != proRe){
								jAlert('New and re-type passwords do not match.',"User Setting");
								return;
							}
						}
						
						UserSettingServiceJS.updateUser(base.user,profull,proemail,proOld,proNew,{
							callback:function(data){
								if(data.status == '200'){
									jAlert(data.message,"User Setting");
									$('#proOld').val('');
									$('#proNew').val('');
									$('#proRe').val('');
								}else{
									jAlert(data.message,"User Setting");
								}	
							}		
						});
					}});
			};
			
			$.updateprofile.prototype.getTemplate = function() {
				var base = this;
				var template = '';
				
				template += '<div id="home" class="txtAL padL0 marT0">';
				template += '<h2 class="txtAL marT10 padL10 borderB">Profile</h2>';
				template += '<table class="fsize12 marT20 marL20">';
				template += '<tr>';
				template +=	'<td><img src="'+ GLOBAL_contextPath + '/images/uploadImage.jpg" class="border marR10" /></td>';
				template += '<td>';
				template += '<label class="floatL w70">Username :</label><label class="w135 padL5 fbold proUser"></label><div class="clearB"></div>';
				template += '<label class="floatL w70 marT5">Fullname :</label><label class="w135 padL5 fbold floatL marT5"><input type="text" id="profull" class="w135"/></label>';
				template += '</td>';
				template += '</tr>';
				template += '<tr>';
				template += '<td>Email :</td>'
				template +=	'<td><input type="text" class="w210 " id="proemail"/></td>';
				template += '</tr>';
				template += '<tr>';
				template += '<td></td>';
				template += '<td class="padT5"></td>';
				template += '</tr>';
				template += '</table>';
					
				template += '<table class="fsize12 marT10 marL20">';
				template += '<tr class="borderT">';
				template += '<td colspan="2"><h2 class="padT5"> Change Password </h2></td>';
				template += '</tr>';
				template += '<tr>';
				template += '<td width="130px">Old Password</td>';
				template += '<td><input type="password" id="proOld" class="w150"/></td>';
				template += '</tr>';
				template += '<tr>';
				template +=	'<td>New Password</td>';
				template +=	'<td><input type="password" id="proNew" class="w150"/></td>';
				template +=	'</tr>';
				template +=	'<tr>';
				template +=	'<td>Re-Type Password</td>';
				template +=	'<td><input type="password" id="proRe" class="w150"/></td>';
				template +=	'</tr>';
				template +=	'<tr>';
				template +=	'<td colspan="2" class="txtAR padT10">';
				template +=	'<a id="probut" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold">Update</div></a>';
				template += '</td>';
				template +=	'</tr>';
				template += '</table>';
				template += '</div>';
				
				return template;
			};
			
			$.updateprofile.defaultOptions = {
					id: 1,
					isPopup: true,
			};
			

			$.fn.updateprofile = function(options) {
				if (this.length) {
					return this.each(function() {
						(new $.updateprofile(this, options));
					});
				};
			};
	
})(jQuery);