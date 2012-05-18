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
	
			clrUser : function(e){
				e.find('#aduser').val('');
				e.find('#adfull').val('');
				e.find('#adaccs').val('');
				e.find('#adip').val('');
				e.find('#adpass').val('');
				e.find('#adexp').attr('checked', false);
				e.find('#adlck').attr('checked', false);
			},	
			// todo validate ssl
			addUser : function(e){
				var aduser = $.trim(e.find('#aduser').val());
				var adfull = $.trim(e.find('#adfull').val());
				var adaccs = $.trim(e.find('#adaccs').val());
				var adip = $.trim(e.find('#adip').val());
				var adpass = $.trim(e.find('#adpass').val());
				var adexp = e.find('#adexp').is(':checked');
				var adlck = e.find('#adlck').is(':checked');
				
				if($.isBlank(aduser)){
					alert('Username cannot be empty.');
					return;
				}else if($.isBlank(adfull)){
					alert('Fullname cannot be empty.');
					return;
				}else if($.isBlank(adaccs)){
					alert('Last Access cannot be empty.');
					return;
				}else if($.isBlank(adip)){
					alert('IP address cannot be empty.');
					return;
				}else if($.isBlank(adpass)){
					alert('Password cannot be empty.');
					return;
				}
				
				SecurityServiceJS.addUser(sec.curid,sec.curname,aduser,adfull,adaccs,adip,adpass,adexp,adlck,{
					callback:function(data){
						if(data.status == '200'){
							alert(data.message);
							sec.getUserList(sec.curid,sec.curname,1,null,sec.cursrc,sec.curmem,sec.curstat,sec.curexp);
						}else{
							alert(data.message);
						}
					},
					preHook:function(){ 
					
					},
					postHook:function(){ 
					
					}			
	          	});		
			},	
			showAdd : function(e){					
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
									sec.addUser(contentHolder);
								}
							});
							
							contentHolder.find("#adclrBtn").on({
								click: function(e){	
									sec.clrUser(contentHolder);
								}
							});
						},
						hide:function(evt, api){
							api.destroy();
						}
					}
				});
			},
			
			// todo validate ssl
			resetPass : function(e,data){
				if (!$.isBlank($.trim(e.find('#shpass').val()))){
					SecurityServiceJS.resetPassword(data.type,data.id,data.name,e.find('#shlck').is(':checked'),e.find('#shexp').is(':checked'),$.trim(e.find('#shpass').val()),{
						callback:function(data){
							if(data.status == '200'){
								alert(data.message);
							}else{
								alert(data.message);
							}
						},
						preHook:function(){ 
						
						},
						postHook:function(){ 
						
						}			
		          	});	
				}else
					alert('Please enter new password.');
			},	
			showUser : function(e){
				var data = e.data;
				
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
						
							contentHolder.find("#resetBtn").on({
								click: function(e){	
									sec.resetPass(contentHolder,data);
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
					},
					preHook:function(){ 
					
					},
					postHook:function(){ 
					
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
					},
					preHook:function(){ 
					
					},
					postHook:function(){ 
					
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
					},
					preHook:function(){ 
					
					},
					postHook:function(){ 
					
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
					},
					preHook:function(){ 
					
					},
					postHook:function(){ 
					
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
				}, {id:data.id, type:data.type, name:data.username,fullname:data.fullname,lastaccess:data.lastAccess,ip:data.ip});
				
				$('#del' + data.id).on({
					click: sec.delUser
				}, {id:data.id, type:data.type, name:data.username});
			},
			getUserList : function(id,name,pg,src,mem,stat,exp){
				SecurityServiceJS.getUserList(id,pg,src,mem,stat,exp,{
					callback:function(data){
						var list = data.list;
						if (list.length>0){
							var content = '';
							$('.conTr').remove();
							for(var i=0; i<list.length; i++){
								content = '<tr class="conTr"><td class="txtAC"><a href="javascript:void(0);" id="del'+list[i].id+'"><img src="../images/icon_del.png"></a></td><td><a href="javascript:void(0);" id="user'+list[i].id+'">'+list[i].username+'</a></td><td class="txtAC hl">'+list[i].status+'</td><td class="txtAC">'+list[i].expired+'</td><td class="txtAC">'+list[i].dateStarted+'</td><td class="txtAC">'+list[i].lastAccess+' days ago</td></tr>';
								$('.conTable').append(content);
								sec.setUserValues(list[i]);
							}					
							sec.showPaging(pg,id,name,data.totalSize);
						}else
							alert('No record found.');
					},
					preHook:function(){ 
					
					},
					postHook:function(){ 
					
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

				sec.getStatList();
				sec.getExpList();
				sec.getRoleList();
				sec.getRole(null);
			},
			delUser : function(e){
				var data = e.data;
		        if (confirm("Are you sure you want to delete this user ?")){                  
		          	SecurityServiceJS.deleteUser(data.type,data.id,data.name,{
						callback:function(data){
							if(data.status == '200'){
								alert(data.message);
								sec.getUserList(sec.curid,sec.curname,1,null,sec.cursrc,sec.curmem,sec.curstat,sec.curexp);
							}else{
								alert(data.message);
							}
						},
						preHook:function(){ 
						
						},
						postHook:function(){ 
						
						}			
		          	});	
		        }  
			}
		};

		sec.init();	
	});
})(jQuery);	