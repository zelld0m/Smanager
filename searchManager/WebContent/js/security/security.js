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
				sec.getUserList(sec.curid,sec.curname,'1',sec.cursrc,sec.curmem,sec.curstat,sec.curexp);
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
			getRole : function(id){
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
									content += '<li class="alt"><a href="javascript:sec.getRole(\''+list[i].id+'\');">'+list[i].rolename+'</a></li>';
								else
									content += '<li><a href="javascript:sec.getRole(\''+list[i].id+'\');">'+list[i].rolename+'</a></li>';
							}	
							$('.rolUl').append(content);	
						}
					},
					preHook:function(){ 
					
					},
					postHook:function(){ 
					
					}			
				});
			},
			getUserList : function(id,name,pg,src,mem,stat,exp){
				SecurityServiceJS.getUserList(id,pg,src,mem,stat,exp,{
					callback:function(data){
						var list = data.list;
						if (list.length>0){
							var content = '';
							$('.conTr').remove();
							for(var i=0; i<list.length; i++){
								content += '<tr class="conTr"><td class="txtAC"><a href="javascript:sec.delUser(\''+list[i].type+'\',\''+list[i].id+'\',\''+list[i].username+'\');"><img src="../images/icon_del.png"></a></td><td>'+list[i].username+'</td><td class="txtAC hl">'+list[i].status+'</td><td class="txtAC">'+list[i].expired+'</td><td class="txtAC">'+list[i].dateStarted+'</td><td class="txtAC">'+list[i].lastAccess+' days ago</td></tr>';
							}	
							$('.conTable').append(content);
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

				sec.getStatList();
				sec.getExpList();
				sec.getRoleList();
				sec.getRole('');
			},
			delUser : function(type,id,name){
		        if (confirm("Are you sure you want to delete this user ?")){                  
		          	SecurityServiceJS.deleteUser(type,id,name,{
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