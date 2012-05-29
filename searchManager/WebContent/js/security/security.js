(function($){

	$(document).ready(function(){

		sec = {		
			cursrc : '',
			curmem : '',
			curstat : '',
			curexp : '',
			curid : '',
			curname : '',
			curtot : '0',
			dateMinDate : -2,
			dateMaxDate : '+1Y',
			expadd : '',
			expsh : '',
			key:'abcdefghijklmnopqrstuvwxyz123456789',
			
			genpass : function(){
				var temp='';
				for (var i=0;i<6;i++){
					temp+=sec.key.charAt(Math.floor(Math.random()*sec.key.length));
				}
				return temp;
			},
			updateUser : function(e,api,user){
				var shexp = sec.expsh;
				var shemail = $.trim(e.find('#shemail').val());
				var shlck = e.find('#shlck').is(':checked');

				if(!sec.validField('Email',shemail,true))
					return;
				else if(!sec.validField('Expired',shexp,false,true))
					return;
				
				SecurityServiceJS.updateUser(sec.curid,user,shexp,shlck,shemail,{
					callback:function(data){
						if(data.status == '200'){
							alert(data.message);
							sec.getUserList(sec.curid,sec.curname,1,sec.cursrc,sec.curmem,sec.curstat,sec.curexp);
							api.destroy();
						}else{
							alert(data.message);
						}
					}		
	          	});		
			},	
			isEmail : function(s) {
				var pattern=/^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\.([a-zA-Z])+([a-zA-Z])+/;
				if(!pattern.test(s))
					return true;
				else
					return false;
			},
			validField : function(f,fv,e,d){
				
				if($.isBlank(fv)){
					alert(f+' cannot be empty.');
					return false;
				}else if(!e && !d && !isAllowedName(fv)){
					alert(f+" contains invalid value.");
					return false;
				}else if(!isAscii(fv)) {
					alert(f+" contains non-ASCII characters.");		
					return false;
				}else if(!isXSSSafe(fv)){
					alert(f+" contains XSS.");
					return false;
				}else if(e && sec.isEmail(fv)){
					alert(f+" is invalid.");
					return false;
				}else if(d && !$.isDate(fv)){
					alert(f+" is invalid date.");
					return false;
				}

				return true;
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
			},	
			addUser : function(e,api){
				var aduser = $.trim(e.find('#aduser').val());
				var adfull = $.trim(e.find('#adfull').val());
				var adexp = sec.expadd;
				var ademail = $.trim(e.find('#ademail').val());			
				var adlck = e.find('#adlck').is(':checked');
				
				if(e.find('#adgen').is(':checked'))
					e.find('#adpass').val(sec.genpass());
				
				var adpass = $.trim(e.find('#adpass').val());
						
				if(!sec.validField('Username',aduser))
					return;
				else if(!sec.validField('Fullname',adfull))
					return;
				else if(!sec.validField('Email',ademail,true))
					return;
				else if(!sec.validField('Password',adpass))
					return;
				else if(!sec.validField('Expired',adexp,false,true))
					return;
				
				SecurityServiceJS.addUser(sec.curid,sec.curname,aduser,adfull,adpass,adexp,adlck,ademail,{
					callback:function(data){
						if(data.status == '200'){
							alert(data.message);
							sec.getUserList(sec.curid,sec.curname,1,sec.cursrc,sec.curmem,sec.curstat,sec.curexp);
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
								buttonImage: "../images/icon_calendarwithBG.png",
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
				SecurityServiceJS.resetPassword(data.type,data.id,e.find('#shpass').val(),{
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
				var data = e.data;
				sec.expsh = data.thruDate;
				
				$(this).qtip({
					content: {
						text: $('<div/>'),
						title: { text: data.name, button: true
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
							contentHolder.find(".shuser").html(data.name);
							contentHolder.find(".shfname").html(data.fullname);
							contentHolder.find(".shlacss").html(data.lastaccess);
							contentHolder.find(".ship").html(data.ip);
							contentHolder.find("#shemail").val(data.email);	
							contentHolder.find("#shlck").attr('checked', data.locked);	
							
							contentHolder.find("#resetBtn").on({
								click: function(e){	
									sec.resetPass(contentHolder,data,api);
								}
							});

							contentHolder.find("#shexp").attr("id", "shexp_1");	
							contentHolder.find("#shexp_1").val(data.thruDate);
							
							contentHolder.find("#shexp_1").datepicker({
								showOn: "both",
								minDate: sec.dateMinDate,
								maxDate: sec.dateMaxDate,
								buttonText: "Expiration Date",
								buttonImage: "../images/icon_calendar.png",
								buttonImageOnly: true,
								disabled: false,
								onSelect: function(dateText, inst) {			
									sec.expsh = contentHolder.find("#shexp_1").val();
								}
							});	
							
							contentHolder.find("#shsv").on({
								click: function(e){	
									sec.updateUser(contentHolder,api,data.name);
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
				$('#refsrc').val('');
				$('#refmem').val('');
				sec.getExpList();
				sec.getStatList();
			},
			getExpList : function(){
				SecurityServiceJS.getExpList({
					callback:function(data){
						var list = data.list;
						if (list.length>0){
							$('#refexp').html('');
							var content = '<option>Select Expired</option>';
							for(var i=0; i<list.length; i++){							
								content += '<option id="'+list[i].name+'">'+list[i].value+'</option>';
							}	
							$('#refexp').html(content);
						}
					}			
				});
			},
			getStatList : function(){
				SecurityServiceJS.getStatList({
					callback:function(data){
						var list = data.list;
						if (list.length>0){
							$('#refstat').html('');
							var content = '<option>Select Status</option>';
							for(var i=0; i<list.length; i++){							
								content += '<option id="'+list[i].name+'">'+list[i].value+'</option>';
							}	
							$('#refstat').html(content);
						}
					}			
				});
			},
			filter : function(){
				sec.cursrc = $('#refsrc').val();
				sec.curmem =  $('#refmem').val();
				sec.curstat = ($('#refstat').val() == 'Select Status')?'':$('#refstat').val();
				sec.curexp = ($('#refexp').val() == 'Select Expired')?'':$('#refexp').val();
				sec.getUserList(sec.curid,sec.curname,1,sec.cursrc,sec.curmem,sec.curstat,sec.curexp);
			},		
			showPaging : function(page,id,name,total){
				$("#sortablePagingTop, #sortablePagingBottom").paginate({
					currentPage:page, 
					pageSize:10,
					totalItem:total,
					callbackText: function(itemStart, itemEnd, itemTotal){
						return 'Displaying ' + itemStart + ' to ' + itemEnd + ' of ' + itemTotal + " "+name;
					}
					,
					pageLinkCallback: function(e){ sec.getUserList(id,name,e.data.page,sec.cursrc,sec.curmem,sec.curstat,sec.curexp); },
					nextLinkCallback: function(e){ sec.getUserList(id,name,e.data.page + 1,sec.cursrc,sec.curmem,sec.curstat,sec.curexp); },
					prevLinkCallback: function(e){ sec.getUserList(id,name,e.data.page - 1,sec.cursrc,sec.curmem,sec.curstat,sec.curexp); }
				});
			},
			getRole : function(e){
				var id = '';
				
				if(e != null)
					id = e.data.id;

				SecurityServiceJS.getRole(id,{
					callback:function(data){
						if (data.id != null || data.id != ''){
							$('.rolH').html('');				
							var content = '<span id="titleText">User list for</span><span id="titleHeader" class="fLblue fnormal" style="padding-left: 8px">'+data.rolename+'</span>';
							$('.rolH').append(content);
							sec.clrFil();
							sec.curid = data.id;
							sec.curname	= data.rolename;
							sec.getUserList(data.id,data.rolename,1,sec.cursrc,sec.curmem,sec.curstat,sec.curexp);
						}
					}			
				});
			},
			getRoleList : function(){
				SecurityServiceJS.getRoleList({
					callback:function(data){
						var list = data.list;
						if (list.length>0){
							var content = '';
							$('.rolUl').html('');
							
							for(var i=0; i<list.length; i++){
								if(i%2 > 0)
									content = '<li class="alt"><a href="javascript:void(0);" id="role'+list[i].id+'">'+list[i].rolename+'</a></li>';
								else
									content = '<li><a href="javascript:void(0);" id="role'+list[i].id+'">'+list[i].rolename+'</a></li>';
								$('.rolUl').append(content);	
								sec.setRoleValues(list[i]);
							}								
						}
					}		
				});
			},
			setRoleValues : function(data){
				$('#role' + data.id).on({
					click: sec.getRole
				}, {id:data.id, name:data.rolename});
			},
			setUserValues : function(data){
				$('#user' + data.id).on({
					click: sec.showUser
				}, {id:data.id, type:data.type, name:data.username,fullname:data.fullname,lastaccess:data.lastAccess,ip:data.ip,email:data.email,locked:data.locked,thruDate:data.thruDate});
				
				$('#del' + data.id).on({
					click: sec.delUser
				}, {id:data.id, type:data.type, name:data.username});
			},
			getUserList : function(id,name,pg,src,mem,stat,exp){
				SecurityServiceJS.getUserList(id,pg,src,mem,stat,exp,{
					callback:function(data){
						var list = data.list;
						var content = '';
						$('.conTr').remove();
						if (list.length>0){	
							$('.conTr1').show();
							for(var i=0; i<list.length; i++){
								content = '<tr class="conTr"><td class="txtAC"><a href="javascript:void(0);" id="del'+list[i].id+'"><img src="../images/icon_del.png"></a></td><td><a href="javascript:void(0);" id="user'+list[i].id+'">'+list[i].username+'</a></td><td class="txtAC">'+list[i].status+'</td><td class="txtAC">'+list[i].expired+'</td><td class="txtAC">'+list[i].dateStarted+'</td><td class="txtAC">'+list[i].lastAccess+'</td></tr>';
								$('.conTable').append(content);
								sec.setUserValues(list[i]);
							}					
							sec.showPaging(pg,id,name,data.totalSize);
						}else{	
							$('.conTr1').hide();
							content = '<tr class="conTr"><td>No record found.</td></tr>';
							$('.conTable').append(content);
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
					
				$("#addUserBtn").on({
					click: sec.showAdd
				});

				$('#refmem').datepicker({
					showOn: "both",
					buttonText: "Member Date",
					buttonImage: "../images/icon_calendar.png",
					buttonImageOnly: true,
					disabled: false
				});	
				
				sec.getStatList();
				sec.getExpList();
				sec.getRoleList();
				sec.getRole(null);
			},
			delUser : function(e){
				var data = e.data;
		        if (confirm("Are you sure you want to delete this user ?")){                  
		          	SecurityServiceJS.deleteUser(data.id,{
						callback:function(data){
							if(data.status == '200'){
								alert(data.message);
								sec.getUserList(sec.curid,sec.curname,1,sec.cursrc,sec.curmem,sec.curstat,sec.curexp);
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
