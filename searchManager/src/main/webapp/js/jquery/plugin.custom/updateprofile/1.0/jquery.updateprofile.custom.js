(function($){

	$.updateprofile = function(el, options) {
		this.ui = $(el);
		this.ui.data("updateprofile", this);
		this.options = $.extend({}, $.updateprofile.defaultOptions, options);
		this.init();
	};

	$.updateprofile.prototype.setId = function(id) {
		this.ui.find("div:first").prop({
			id: $.isNotBlank(id)? id: "plugin-updateprofile-" + this.options.id
		});
	};

	$.updateprofile.prototype.init = function() {
		var base = this;

		if(base.options.isPopup) {
			base.ui.qtip({
				id: "plugin-updateprofile-qtip",
				content: {
					text: $('<div/>')
				},
				position:{
					at: 'bottom center',
					my: 'top center',
					target: base.ui
				},
				style: {
					width: '331px'
				},
				events: {
					render: function(event, api) {
						base.api = api;
						base.ui = $("div", api.elements.content);
					},

					show: function(event, api){
						base.ui.empty().append(base.getTemplate());
						base.setId();
						base.populateContents();
					},

					hide: function() {
						return !base.unhide;
					}
				},
				hide: {
					event: 'unfocus',
					fixed: true,
					delay: 300,
					effect: function() {
						this.fadeOut(160);
					}
				}
			});
		} else {
			base.ui.empty().append(base.getTemplate());
			base.setId();
			base.populateContents();
		}
	};

	$.updateprofile.prototype.populateContents = function() {
		var base = this;

		// track components
		base.display = base.ui.find("#display");
		base.edit = base.ui.find("#edit");
		base.passwordChange = base.ui.find("#password-change");
		base.displayFullName = base.display.find("#fullname");
		base.displayEmail = base.display.find("#email");
		base.editFullName = base.edit.find("#fullname");
		base.editEmail = base.edit.find("#email");
		base.oldPassword = base.passwordChange.find("#password-old");
		base.newPassword = base.passwordChange.find("#password-new");
		base.repeatPassword = base.passwordChange.find("#password-repeat");

		UserSettingServiceJS.getUser({
			callback:function(data){
				// display data
				base.displayFullName.html(data.fullName);
				base.displayEmail.html(data.email);

				// edit data
				base.editFullName.val(data.fullName);
				base.editEmail.val(data.email);

				base.display.show();
				base.user = data.username;
			}
		});

		base.registerEventListener();
	};

	$.updateprofile.prototype.registerEventListener = function() {
		var base = this;

		base.ui.find("#updateBtn").off().on({
			click: function(){

				var fullname = $.trim(base.editFullName.val());
				var email = $.trim(base.editEmail.val());
				var oldPassword = $.trim(base.oldPassword.val());
				var newPassword = $.trim(base.newPassword.val());
				var repeatPassword = $.trim(base.repeatPassword.val());

				$.alerts.callback = function() {
					base.unhide = false;
					$.alerts.callback = null;
				};

				base.unhide = true;

				if(!validateGeneric('Full Name', fullname,1))
					return;
				else if(!validateEmail('Email', email, 1))
					return;
				else if(!validatePassword('Old Password', oldPassword))
					return;
				else if(!$.isBlank(newPassword) || !$.isBlank(repeatPassword)){
					if(!validatePassword('New Password', newPassword, 8))
						return;
					else if(!validatePassword('Re-type Password', repeatPassword, 8))
						return;
					else if(newPassword != repeatPassword){
						jAlert('New and re-type passwords do not match.',"User Setting");
						return;
					}
				}

				UserSettingServiceJS.updateUser(base.user, fullname, email, oldPassword, newPassword, {
					callback:function(data){
						if(data.status == '200'){
							jAlert(data.message,"User Setting");
							base.reset();
						}else{
							jAlert(data.message,"User Setting");
						}
					}
				});
			}});

		base.passwordChange.find("#cancelBtn").off().on({
			'click': function() { base.reset(true); }
		});

		base.display.find("a").off().on({
			'click': function() {
				base.display.hide('fast');
				base.edit.show('fast', function() {
					base.passwordChange.show('fast');
				});
			}
		});
	};

	$.updateprofile.prototype.reset = function(cancelled) {
		var base = this;

		base.oldPassword.val("");
		base.newPassword.val("");
		base.repeatPassword.val("");

		if (cancelled) {
			base.editFullName.val(base.displayFullName.html());
			base.editEmail.val(base.displayEmail.html());
		} else {
			base.displayFullName.html(base.editFullName.val());
			base.displayEmail.html(base.editEmail.val());
		}

		base.passwordChange.hide('fast', function() {
			base.edit.hide('fast');
			base.display.show('fast');
		});
	};

	$.updateprofile.prototype.getTemplate = function() {
		var template = '';

		template += '<div id="home" class="txtAL padL0 marT0">';
		template += '  <table id="profile-info" class="fsize12 marT5 marR5 marL5" style="width:260px;">';
		template += '    <tr>';
		template += '      <td><img src="/searchManager/images/uploadImage.jpg" class="border marR5"></td>';
		template += '      <td style="vertical-align: top;">';
		template += '        <div id="display" style="display:none;">';
		template += '          <div id="fullname" class="padT5" style="font-weight:bold;"></div>';
		template += '          <div id="email" class="padT3"></div>';
		template += '          <div class="padT5">';
		template += '            <a href="javascript:void(0)" style="font-style:italic;font-size:11px;text-decoration:none;">Edit</a>';
		template += '          </div>';
		template += '        </div>';
		template += '        <div id="edit" style="display:none">';
		template += '          <input type="text" id="fullname" class="w135">';
		template += '          <div class="clearB"></div>';
		template += '          <input type="text" class="w210" id="email">';
		template += '        </div>';
		template += '      </td>';
		template += '    </tr>';
		template += '  </table>';
		template += '  <table id="password-change" class="fsize12 marT5 marL5 marR5" style="display: none;">';
		template += '    <tr>';
		template += '      <td width="130px">Old Password</td>';
		template += '      <td><input type="password" id="password-old" class="w150"></td>';
		template += '    </tr>';
		template += '    <tr>';
		template += '      <td>New Password</td>';
		template += '      <td><input type="password" id="password-new" class="w150"></td>';
		template += '    </tr>';
		template += '    <tr>';
		template += '      <td>Re-Type Password</td>';
		template += '      <td><input type="password" id="password-repeat" class="w150"></td>';
		template += '    </tr>';
		template += '    <tr>';
		template += '      <td colspan="2" class="txtAR padT10">';
		template += '        <a id="cancelBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
		template += '          <div class="buttons fontBold">Cancel</div>';
		template += '        </a>';
		template += '        <a id="updateBtn" href="javascript:void(0);" class="buttons btnGray clearfix">';
		template += '          <div class="buttons fontBold">Update</div>';
		template += '        </a>';
		template += '      </td>';
		template += '    </tr>';
		template += '  </table>';
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