(function($){

	$(document).ready(function(){

		sec = {		
				cursrc : '',
				curmem : '',
				currole : '',
				curstat : '',
				curexp : '',
				curid:'',
				curname : '',
				curtot : '0',
				roleList : null,
				dateMinDate : 0,
				dateMaxDate : '+1Y',
				expadd : '',
				expsh : '',
				key:'abcdefghijklmnopqrstuvwxyz123456789',
				src: 'Enter Name/Username',

				genpass : function(){
					var temp='';
					for (var i=0;i<8;i++){
						temp+=sec.key.charAt(Math.floor(Math.random()*sec.key.length));
					}
					return temp;
				},

				updateUser : function(e,api,user){
					var shexp = $.trim(e.find('#shexp_1').val());
					var shemail = $.trim(e.find('#shemail').val());
					var shrole = $.trim(e.find('#shrole').val());
					var shlck = e.find('div[rel="shlck"]').hasClass('off');

					minDate = new Date();
					//ignore time of current date 
					minDate.setHours(0,0,0,0);
					
					if(!validateEmail('Email',shemail,1))
						return;
					else if(!validateDate('Validity Date',shexp,1,minDate))
						return;

					SecurityServiceJS.updateUser(shrole,user,shexp,shlck,shemail,{
						callback:function(data){
							if(data.status == '200'){
								alert(data.message);
								sec.getUserList(sec.currole,sec.curname,1,sec.cursrc,sec.curmem,sec.curstat,sec.curexp);
								api.destroy();
							}else{
								alert(data.message);
							}
						}		
					});
				},
				
				clrUser : function(e){
					e.find('#aduser').val('');
					e.find('#adfull').val('');
					e.find('#ademail').val('');
					e.find('#adexp_1').val('');
					sec.expadd = '';
					e.find('#adpass').val('');
					e.find('#adlck').attr('checked', false);
					e.find('#adgen').attr('checked', false);
					e.find('#adpass').removeProp('readonly');
				},	

				addUser : function(e,api){
					var aduser = $.trim(e.find('#aduser').val());
					var adfull = $.trim(e.find('#adfull').val());
					var ademail = $.trim(e.find('#ademail').val());			
					var adrole = $.trim(e.find('#adrole').val());			
					var adexp = $.trim(e.find('#adexp_1').val());
					var adlck = e.find('div[rel="adlck"]').hasClass('off');

					var adpass = $.trim(e.find('#adpass').val());

					minDate = new Date();
					//ignore time of current date 
					minDate.setHours(0,0,0,0);
					
					if(!validateUsername('Username',aduser, 4))
						return;
					else if(!validateField('Fullname',adfull, 1))
						return;
					else if(!validateEmail('Email',ademail, 1))
						return;
					else if(!validatePassword('Password',adpass, 8))
						return;
					else if(!validateDate('Validity Date',adexp,1,minDate))
						return;

					SecurityServiceJS.addUser(adrole,sec.curname,aduser,adfull,adpass,adexp,adlck,ademail,{
						callback:function(data){
							if(data.status == '200'){
								alert(data.message);
								sec.getUserList(sec.currole,sec.curname,1,sec.cursrc,sec.curmem,sec.curstat,sec.curexp);
								api.destroy();
							}else{
								alert(data.message);
							}
						}					
					});		
				},	

				showAdd : function(e){		
					sec.adexp = '';
					$(this).qtip({
						content: {
							text: $('<div/>'),
							title: { text: 'Add User', button: true
							}
						},
						position:{
							at: 'top left',
							my: 'bottom left'
						},
						show:{
							solo: true,
							ready: true
						},
						style: {
							width: 'auto'
						},
						events: { 
							show: function(event, api){
								var contentHolder = $("div", api.elements.content);
								contentHolder.html($("#addUserInfoTemplate").html());

								for (var i=0; i < roleList.list.length; i++){
									contentHolder.find("#adrole").append($("<option>", { value : roleList.list[i]["id"]}).text(roleList.list[i]["rolename"]));
								}

								contentHolder.find('div[rel="adlck"]').removeClass("on").removeClass("off").addClass("on").css("background-position", "0% 100%");
								
								contentHolder.find("#adgen").on({
									click: function(e){	
										if(contentHolder.find('#adgen').is(':checked')){
											contentHolder.find('#adpass').prop("readonly","readonly");
											contentHolder.find('#adpass').val(sec.genpass());
										}else{
											contentHolder.find('#adpass').removeProp("readonly");
											contentHolder.find('#adpass').val("");
										}
									}
								});

								contentHolder.find("#adaddBtn").on({
									click: function(e){	
										sec.addUser(contentHolder,api);
									}
								});

								contentHolder.find("#adclrBtn").on({
									click: function(e){	
										sec.clrUser(contentHolder);
									}
								});

								contentHolder.find("#adexp").attr("id", "adexp_1");		
								contentHolder.find("#adexp_1").datepicker({
									showOn: "both",
									minDate: sec.dateMinDate,
									maxDate: sec.dateMaxDate,
									buttonText: "Expiration Date",
									buttonImage: "../images/icon_calendar.png",
									buttonImageOnly: true,
									disabled: false,
									onSelect: function(dateText, inst) {			
										sec.expadd = contentHolder.find("#adexp_1").val();
									}
								});	
							},
							hide:function(evt, api){
								api.destroy();
							}
						}
					});
				},

				resetPass : function(e,data,api){
					if($.isBlank($.trim(e.find('#shpass').val())))
						e.find('#shpass').val(sec.genpass());
					
					if(!validatePassword('Password',e.find('#shpass').val(), 8))
						return;

					SecurityServiceJS.resetPassword(data.groupId,data.username,e.find('#shpass').val(),{
						callback:function(data){
							if(data.status == '200'){
								alert(data.message);
								api.destroy();
							}else{
								alert(data.message);
							}
						}			
					});	
				},	

				showUser : function(e){
					var data = e.data.user;
					sec.expsh = data.thruDate;

					$(this).qtip({
						content: {
							text: $('<div/>'),
							title: { text: "Profile of " + data.fullName, button: true
							}
						},
						position:{
							at: 'top center',
							my: 'bottom center'
						},
						show:{
							solo: true,
							ready: true
						},
						style: {
							width: 'auto'
						},
						events: { 
							show: function(event, api){
								var contentHolder = $("div", api.elements.content);
								contentHolder.html($("#userInfoTemplate").html());
								contentHolder.find(".shuser").html(data.username);
								contentHolder.find(".shfname").html(data.fullName);
								contentHolder.find(".shlacss").html(data.lastAccessDate==null? '': data.lastAccessDate.toUTCString());
								contentHolder.find(".ship").html(data.ip);
								contentHolder.find("#shemail").val(data.email);	

								for (var i=0; i < roleList.list.length; i++){
									$option = $("<option>", { value : roleList.list[i]["id"]}).text(roleList.list[i]["rolename"]);
									
									if (roleList.list[i]["id"] == data.groupId) $option.prop("selected","selected");
										
									contentHolder.find("#shrole").append($option);
								}

								contentHolder.find('div[rel="shlck"]').removeClass("on").removeClass("off");
							
								if (data.isAccountNonLocked){
									contentHolder.find('div[rel="shlck"]').addClass("on").css("background-position", "0% 100%");
								}else{
									contentHolder.find('div[rel="shlck"]').addClass("off").css("background-position", "100% 0%");
								}

								contentHolder.find("#view-profile").tabs({
									cookie: {
										expires: 1,
										path: GLOBAL_contextPath,
										name: $(this).prop("id")
									}
								});

								contentHolder.find("#resetBtn").on({
									click: function(e){	
										sec.resetPass(contentHolder,data,api);
									}
								});

								contentHolder.find("#shexp").attr("id", "shexp_1");
								
								var formattedThruDate = $.isNotBlank(data.thruDate)? $.datepicker.formatDate('mm/dd/yy', data.thruDate):data.thruDate;
								
								contentHolder.find("#shexp_1").val(formattedThruDate);

								contentHolder.find("#shexp_1").datepicker({
									showOn: "both",
									minDate: sec.dateMinDate,
									maxDate: sec.dateMaxDate,
									buttonText: "Expiration Date",
									buttonImage: "../images/icon_calendar.png",
									buttonImageOnly: true,
									disabled: false
								});	

								contentHolder.find("#shsv").on({
									click: function(e){	
										sec.updateUser(contentHolder,api,data.username);
									}
								});
							},
							hide:function(evt, api){
								api.destroy();
							}
						}
					});
				},

				clrFil : function(){
					sec.curstat = '';
					sec.curexp = '';
					sec.cursrc = '';
					sec.curmem = '';
					$('#refsrc').val(sec.src);
					$('#refmem').val('');

					$('#refrole').prop("selectedIndex", 0);
					$('#refstat').prop("selectedIndex", 0);
					$('#refexp').prop("selectedIndex", 0);
				},

				filter : function(){
					sec.cursrc = $('#refsrc').val() !== sec.src ? $.trim($('#refsrc').val()) : "";
					sec.curmem =  $('#refmem').val();
					sec.currole =  $('#refrole').val();
					sec.curstat = $('#refstat').val();
					sec.curexp = $('#refexp').val();

					var validformat=/^\d{2}\/\d{2}\/\d{4}$/;
					if(!isXSSSafe(sec.cursrc))
						alert("Invalid keyword. HTML/XSS is not allowed.");
					else if($.isNotBlank(sec.curmem) && !validformat.test(sec.curmem))
						alert("Invalid date. (Use MM/DD/YYYY format)");
					else
						sec.getUserList(sec.currole,sec.curname,1,sec.cursrc,sec.curmem,sec.curstat,sec.curexp);
				},	

				showPaging : function(page,id,name,total){
					$("#sortablePagingTop, #sortablePagingBottom").paginate({
						currentPage:page, 
						pageSize:10,
						totalItem:total,
						callbackText: function(itemStart, itemEnd, itemTotal){
							return 'Displaying ' + itemStart + ' to ' + itemEnd + ' of ' + itemTotal + " " + name;
						},
						pageLinkCallback: function(e){ sec.getUserList(id,name,e.data.page,sec.cursrc,sec.curmem,sec.curstat,sec.curexp); },
						nextLinkCallback: function(e){ sec.getUserList(id,name,e.data.page + 1,sec.cursrc,sec.curmem,sec.curstat,sec.curexp); },
						prevLinkCallback: function(e){ sec.getUserList(id,name,e.data.page - 1,sec.cursrc,sec.curmem,sec.curstat,sec.curexp); }
					});
				},

				getRoleList : function(){
					SecurityServiceJS.getRoleList({
						callback:function(data){
							roleList = data;
							var list = data.list;

							if (list.length>0){
								for(var i=0; i<list.length; i++){
									$("select#refrole").append($("<option>", { value : list[i]["id"]}).text(list[i]["rolename"])); 
								}
							}
						}		
					});
				},

				setUserValues : function(data){
					$('#user' + $.formatAsId(data.username)).on({
						click: sec.showUser
					}, {user:data});

					$('#del' + $.formatAsId(data.username)).on({
						click: sec.delUser
					}, {user:data});
				},

				getUserList : function(id,name,pg,src,mem,stat,exp){
					SecurityServiceJS.getUserList(id,pg,src,mem,stat,exp,{
						callback:function(data){
							var list = data.list;
							var content = '';
							$('.conTr').remove();

							$('tr.conTableItem').filter('tr:not(#conTr1Pattern)').remove();

							if (list.length>0){	
								$table = $('table.conTable');
								$('.conTable tr#nomatch').remove();
								for(var i=0; i<list.length; i++){
									$tr = $table.find('tr#conTr1Pattern').clone();
									$tr.prop("id", $.formatAsId(list[i].username)).show();
									$tr.find("td#delIcon > a").prop("id", "del"+$.formatAsId(list[i].username));
									$tr.find("td#userInfo > span#username > a").prop("id", "user"+$.formatAsId(list[i].username)).html(list[i].username);
									$tr.find("td#userInfo > span#fullName").html(list[i].fullName);
									$tr.find("td#userInfo > span#email").html(list[i].email);
									$tr.find("td#role > span").html(list[i].groupId);

									$tr.find("td#memberSince > span").html(list[i].createdDate==null? "" : list[i].formattedCreatedDate);
									$tr.find("td#status > span").html(list[i].isAccountNonLocked==true? "Active" : "Locked");
									$tr.find("td#validity > span").html(list[i].thruDate==null? "" : list[i].thruDate.toDateString());

									$tr.find("td#lastAccess > span#dateAccess").html(list[i].lastAccessDate==null? "" : list[i].lastAccessDate.toUTCString());
									$tr.find("td#lastAccess > span#ipAccess").html(list[i].ip);
									if (i%2!=0) $tr.addClass("alt"); 
									$table.append($tr);
									sec.setUserValues(list[i]);
								}					

								sec.showPaging(pg,id,name,data.totalSize);
							}else{	
								$empty = '<tr class="conTableItem"><td colspan="7" class="txtAC">No matching records found</td></tr>';
								$('.conTable').append($empty);
								
								$('#sortablePagingTop').hide();
								$('#sortablePagingBottom').hide();			
							}		
						},
						preHook:function(){ 
							$('#preloader').show();
						},
						postHook:function(){ 
							$('#preloader').hide();
						}			
					});
				},

				init : function(){

					$("#refFilBtn").on({
						click: function(e){
							sec.filter();
						}
					});	

					$("#clrFilBtn").on({
						click: function(e){
							sec.clrFil();
							sec.getUserList(sec.currole,sec.curname,1,sec.cursrc,sec.curmem,sec.curstat,sec.curexp);
						}
					});		

					$("#addUserBtn").on({
						click: sec.showAdd
					});

					$('#refmem').datepicker({
						showOn: "both",
						buttonText: "Member Since",
						buttonImage: "../images/icon_calendar.png",
						buttonImageOnly: true,
						disabled: false
					});	
					
					$('#refsrc').val(sec.src);
					$('#refsrc').on({
						blur: function(e){
							if ($.trim($(e.target).val()).length == 0) 
								$(e.target).val(sec.src);
							},
						focus: function(e){
							if ($.trim($(e.target).val()) == sec.src)
								$(e.target).val('');
							}
					});
					
					sec.getRoleList();
					sec.getUserList(sec.currole,sec.curname,1,sec.cursrc,sec.curmem,sec.curstat,sec.curexp);
				},

				delUser : function(e){
					var data = e.data.user;
					if (confirm("Are you sure you want to delete this user ?")){                  
						SecurityServiceJS.deleteUser(data.username,{
							callback:function(data){
								if(data.status == '200'){
									alert(data.message);
									sec.getUserList(sec.currole,sec.curname,1,sec.cursrc,sec.curmem,sec.curstat,sec.curexp);
								}else{
									alert(data.message);
								}
							}		
						});	
					}  
				}
		};

		sec.init();	
	});
})(jQuery);	
