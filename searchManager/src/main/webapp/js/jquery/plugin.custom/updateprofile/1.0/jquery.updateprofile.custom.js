(function($){

			$.updateprofile = function(el, options) {
				this.$el = $(el);
				this.el = el;
				this.$el.data("updateprofile", this);
				this.options = $.extend({}, $.updateprofile.defaultOptions, options);
				this.init();
			};

			$.updateprofile.prototype.setId = function(id) {
				var ui = this.$el;

				ui.find("div:first").prop({
					id: $.isNotBlank(id)? id: "plugin-updateprofile-" + this.options.id
				});

			};

			$.updateprofile.prototype.setUser = function(user) {
				this.user= user;
			};

			$.updateprofile.prototype.init = function() {
				var base = this;

				if(base.options.isPopup) {
					base.$el.qtip({
						id: "plugin-updateprofile-qtip",
						content: {
							text: $('<div/>')
						},
						position:{
							at: 'bottom center',
							my: 'top center',
							target: base.$el
						},
						style: {
							width: '331px'
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
					base.$el.empty().append(base.getTemplate());
					base.setId();
					base.populateContents.call(base);
				}
			};

			$.updateprofile.prototype.populateContents = function() {
				var base = this;
				var ui = base.$el;

				UserSettingServiceJS.getUser({
					callback:function(data){
						// display data
						ui.find("#display #fullname").html(data.fullName);
						ui.find("#display #email").html(data.email);

						// edit data
						ui.find("#edit #fullname").val(data.fullName);
						ui.find("#edit #email").val(data.email);

						ui.find("#display").show();
						base.setUser(data.username);
					}
				});

				base.registerEventListener();
			};

			$.updateprofile.prototype.registerEventListener = function() {
				var base = this;
				var ui = base.$el;

				ui.find("#updateBtn").off().on({
					click: function(){

						var profull = $.trim(ui.find('#edit #fullname').val());
						var proemail = $.trim(ui.find('#edit #email').val());
						var proOld = $.trim(ui.find('#password-change #password-old').val());
						var proNew = $.trim(ui.find('#password-change #password-new').val());
						var proRe = $.trim(ui.find('#password-change #password-repeat').val());

						if(!validateGeneric('Full Name', profull,1))
							return;
						else if(!validateEmail('Email', proemail, 1))
							return;
						else if(!validatePassword('Old Password', proOld))
							return;
						else if(!$.isBlank(proNew) || !$.isBlank(proRe)){
							if(!validatePassword('New Password', proNew, 8))
								return;
							else if(!validatePassword('Re-type Password', proRe, 8))
								return;
							else if(proNew != proRe){
								jAlert('New and re-type passwords do not match.',"User Setting");
								return;
							}
						}

						UserSettingServiceJS.updateUser(base.user, profull, proemail, proOld, proNew, {
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

				ui.find("#cancelBtn").off().on({
					'click': function() { base.reset(true); }
				});

				ui.find("#display a").off().on({
					'click': function() {
						ui.find("#display").hide('fast');
						ui.find("#edit").show('fast', function() {
							ui.find("#password-change").show('fast');
						});
					}
				});
			};

			$.updateprofile.prototype.reset = function(cancelled) {
				var ui = this.$el;

				ui.find("#password-change #password-old").val("");
				ui.find("#password-change #password-new").val("");
				ui.find("#password-change #password-repeat").val("");

				if (cancelled) {
					ui.find("#edit #fullname").val(ui.find("#display #fullname").html());
					ui.find("#edit #email").val(ui.find("#display #email").html());
				} else {
					ui.find("#display #fullname").html(ui.find("#edit #fullname").val());
					ui.find("#display #email").html(ui.find("#edit #email").val());
				}
				

                ui.find("#password-change").hide('fast', function() {
                    ui.find("#edit").hide('fast');
                    ui.find("#display").show('fast');
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