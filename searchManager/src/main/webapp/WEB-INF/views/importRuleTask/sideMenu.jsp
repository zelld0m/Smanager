<%@ include file="/WEB-INF/includes/includes.jsp" %> 		
		<div class="clearB floatL w240">
    	<div id="sideHeader" class="sideHeader posRel">
    		<img src="../images/corner_tl.png" class="curveTL">
    		<img src="../images/corner_tr.png" class="curveTR">
			Search Refinement
			<img src="../images/corner_bl.png" class="curveBL">
			<img src="../images/corner_br.png" class="curveBR">
    	</div>

    	<!--  info -->
    <div class="info fsize12 clearfix">
    	<label class="txtLabel">Status:</label>
    	<label class="details marR15">
	    	<select class="w205" id="statusFilter">
	    		<option value="">-- Select Status --</option>
	    		<c:forEach items="${statuses}" var="status"> 
	    			<option value="${status.name}">${status.displayText}</option>
	    		</c:forEach>
	    	</select>
    	</label> 
    	<label class="txtLabel">Type:&nbsp;&nbsp;</label>
    	<label class="details marR15">
	    	<select class="w205" id="typeFilter">
	    		<option value="">-- Select Type --</option>
	    		<c:forEach items="${types}" var="type"> 
	    			<option value="${type.displayText}">${type.displayText}</option>
	    		</c:forEach>	    		
	    	</select>
    	</label>    
    	<c:if test="${!isTargetStore}">    	
	    	<label class="txtLabel">Target:</label>
	    	<label class="details marR15">
		    	<select class="w205" id="targetFilter">
		    		<option value="">-- Select Target --</option>
		    		<c:forEach items="${targetStores}" var="targetStore"> 
		    			<option value="${targetStore}">${targetStore}</option>
		    		</c:forEach>
		    	</select>
	    	</label>    	 
    	</c:if>   		
      	<div class="clearB"></div>
		<div align="right" class="padR5 marT10">
		    <a href="#" onclick="javascript:importRuleTask.filterPage();" class="buttons btnGray clearfix"><div class="buttons fontBold" id="goBtn">Filter</div></a>
			<a href="../autoimport/pcmall" class="buttons btnGray clearfix"><div class="buttons fontBold" id="resetBtn">Reset</div></a>
		</div>
    </div>
    <!--  end info -->
	</div>