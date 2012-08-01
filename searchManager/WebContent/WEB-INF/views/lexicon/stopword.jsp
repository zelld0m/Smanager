<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="lexicon"/>
<c:set var="submenu" value="stopword"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!--  slider checkbox -->
<link type="text/css" rel="stylesheet" href="<spring:url value="/css/lexicon/linguistics.css" />">
<script type="text/javascript" src="<spring:url value="/js/lexicon/synonym.js" />" ></script>

 <!-- Start Left Side -->
<div class="clearB floatL minW240 sideMenuArea">
   <div class="clearB floatL w240">
    	<div class="sideHeader">Site Updates</div>
        <div class="clearB floatL w230 padL5">
			<ul class="listSU fsize11 marT10">
				<li><p class="notification"><strong>lorem 2 items</strong> Etiam dui justo, consect<br>
				<a href="#">20 minutes ago</a></p></li>
				<li class="alt"><p class="notification"><strong>lorem 2 items</strong> Etiam dui justo, consect<br>
				<a href="#">20 minutes ago</a></p></li>
				<li><p class="alert"><strong>lorem 2 items</strong> Etiam dui justo, consect<br>
				<a href="#">20 minutes ago</a></p></li>
				<li class="alt"><p class="notification"><strong>lorem 2 items</strong> Etiam dui justo, consect<br>
				<a href="#">20 minutes ago</a></p></li>
				<li><p class="alert"><strong>lorem 2 items</strong> Etiam dui justo, consect<br>
				<a href="#">20 minutes ago</a></p></li>
				<li class="alt"><p class="alert"><strong>lorem 2 items</strong> Etiam dui justo, consect<br>
				<a href="#">20 minutes ago</a></p></li>
				<li><p class="alert"><strong>lorem 2 items</strong> Etiam dui justo, consect<br>
				<a href="#">20 minutes ago</a></p></li>
				<li class="textAR"><a href="#">see all updates  &raquo;</a></li>
			</ul>
    	</div> 
	</div>
</div>
<!--  end left side -->

<!-- add contents here -->
<div class="floatL w730 marL10 marT27">
	<div class="floatL w730 titlePlacer breakWord">
		<h1 class="padT7 padL15 fsize20 fnormal">
			Stopword
		</h1>
	</div>
	<div class="clearB"></div>
	<div style="width:95%" class="marT20 mar0">
    <!--Pagination-->
      <div class="mar0">
        <div class="clearB floatL farial fsize12 fDblue w300 padT10 marL10">Displaying 1 to 25 of 26901 Products</div>
        <div class="floatR farial fsize12 fgray txtAR padT10">
          <div class="txtAR">
            <ul class="pagination">
              <li><a href="#">&lt;&lt;prev</a></li>
              <li><a href="#">1</a></li>
              <li><a href="#">2</a></li>
              <li><a href="#">3</a></li>
              <li><a href="#">next&gt;&gt;</a></li>
            </ul>
          </div>
        </div>
      </div>
      <!--Pagination-->
      <div class="clearB"></div>
      <div class="linguistics marT20">
      <table width="100%">
      	<tr class="borderB">
      		<td class="alt">
      			<div class="fbold">A</div>
      			<ul>
      				<li>an</li>
      				<li>and</li>
      				<li>&</li>
      				<li>are</li>
      				<li>as</li>
      				<li>at</li>
      			</ul>
      		</td>
      		<td>
	      		<div class="fbold">B</div>
	      			<ul>
	      				<li>be</li>
	      				<li>but</li>
	      				<li>buy</li>
	      			</ul>
      		</td>
      		<td class="alt">
	      		<div class="fbold">F</div>
	      			<ul>
	      				<li>for</li>
	      			</ul>
	      	</td>
      		<td>
      			<div class="fbold">I</div>
      			<ul>
      				<li>if</li>
      				<li>in</li>
      				<li>into</li>
      				<li>is</li>
      				<li>it</li>
      			</ul>
      		</td>
      		<td class="alt">
      			<div class="fbold">N</div>
      			<ul>
      				<li>no</li>
      				<li>not</li>
      			</ul>
      		</td>
      	</tr>
      	<tr>
      		<td>
      			<div class="fbold">O</div>
      			<ul>
      				<li>of</li>
      				<li>on</li>
      				<li>or</li>
      			</ul>
      		</td>
      		<td class="alt">
      			<div class="fbold">S</div>
      			<ul>
      				<li>such</li>
      			</ul>
      		</td>
      		<td>
      			<div class="fbold">T</div>
      			<ul>
      				<li>that</li>
      				<li>the</li>
      				<li>their</li>
      				<li>then</li>
      				<li>there</li>
      				<li>these</li>
      				<li>they</li>
      				<li>this</li>
      				<li>to</li>
      			</ul>
      		</td>
      		<td class="alt">
      			<div class="fbold">W</div>
      			<ul>
      				<li>was</li>
      				<li>will</li>
      				<li>with</li>
      			</ul>
      		</td>
      		<td></td>
      	</tr>
      </table>
    </div>
    	
    	  	
	</div>
	<div class="clearB"></div>
	
	
</div>   
<%@ include file="/WEB-INF/includes/footer.jsp" %>	