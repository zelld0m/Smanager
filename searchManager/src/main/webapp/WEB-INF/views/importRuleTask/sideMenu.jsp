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
    <div class="info fsize12 clearfix" style="padding: 20px 5px 5px 5px;">
    	<label class="txtLabel">Status:</label>
    	<label class="details">
	    	<select class="w230" id="statusFilter">
	    		<option value="">-- Select Status --</option>
	    		<c:forEach items="${statuses}" var="status"> 
	    			<option value="${status.name}">${status.displayText}</option>
	    		</c:forEach>
	    	</select>
    	</label> 
    	<label class="txtLabel">Type:<br/></label>
    	<label class="details">
	    	<select class="w230" id="typeFilter">
	    		<option value="">-- Select Type --</option>
	    		<c:forEach items="${types}" var="type"> 
	    			<option value="${type.displayText}">${type.displayText}</option>
	    		</c:forEach>	    		
	    	</select>
    	</label>
    	<label class="txtLabel">Rule Type:<br/></label>
    	<label class="details">
	    	<select class="w230" id="ruleTypeFilter">
	    		<option value="">-- Select Rule Type --</option>
	    		<c:forEach items="${ruleTypes}" var="ruleType"> 
	    			<option value="${ruleType.name}">${ruleType.displayName}</option>
	    		</c:forEach>	    		
	    	</select>
    	</label>      	    
    	<label class="txtLabel">Rule Name:<br/></label>
    	<label class="details">
	    	<input type="text" class="w225" id="targetRuleName" maxlength="200" style="padding:2px 1px 2px 2px;">
    	</label>     	
    	<c:if test="${!isTargetStore}">    	
	    	<label class="txtLabel">Target:</label>
	    	<label class="details">
		    	<select class="w230" id="targetFilter">
		    		<option value="">-- Select Target --</option>
		    		<c:forEach items="${targetStores}" var="targetStore"> 
		    			<option value="${targetStore}">${targetStore}</option>
		    		</c:forEach>
		    	</select>
	    	</label>    	 
    	</c:if>   		
      	<div class="clearB"></div>
		<div align="right" class="padR5 marT10" style="margin-right:-5px">
		    <a id="filterBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold" id="goBtn">Filter</div></a>
			<a id="resetBtn" href="javascript:void(0);" class="buttons btnGray clearfix"><div class="buttons fontBold" id="resetBtn">Reset</div></a>
		</div>
    </div>
    <!--  end info -->
	</div>