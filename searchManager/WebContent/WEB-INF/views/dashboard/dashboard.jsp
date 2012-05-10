<%@ include file="/WEB-INF/includes/includes.jsp" %> 
<%@ include file="/WEB-INF/includes/header.jsp" %>
<c:set var="topmenu" value="home"/>
<%@ include file="/WEB-INF/includes/menu.jsp" %>

<!-- page specific dependencies -->
<script src="<spring:url value="/js/dashboard/dashboard.js" />" type="text/javascript"></script>  
<link href="<spring:url value="/css/dashboard/dashboard.css" />" rel="stylesheet" type="text/css">

<div class="clearB floatL minW240 sideMenuArea">
<div class="companyLogo"><a href="#"><img src="<spring:url value="/images/logoMacMall.png" />"></a></div>

<div class="sideHeader">Keyword Trends</div>
<div class="leftStatus">
	<img src="<spring:url value="/images/information.png" />" class="marR3 marBn3">
	<span class="fgreen">Most Searched</span>
</div>

   <table border="0" cellpadding="0" cellspacing="0" class="farial fsize12 fDGray w220 marL8">
   <c:forEach var="i" begin="1" end="5" step="1">
   <tr>
   	<td class="borderB padTB5">
    	<span class="lnk"><a href="#">
    	 Keyword ${i}</a>
    	</span>
   	</td>
       <td class="borderB padTB5 txtAR scorelink">
       	<span class="lnk"><a href="#">Related Info</a></span>
       </td>
   </tr>
   </c:forEach>
        </table>

<div class="leftStatus"><img src="<spring:url value="/images/information.png" />" class="marR3 marBn3"><span class="fgreen">Latest Searched</span></div>
   <table border="0" cellpadding="0" cellspacing="0" class="farial fsize12 fDGray w220 marL8">
   <c:forEach var="i" begin="1" end="5" step="1">
   <tr>
   	<td class="borderB padTB5">
    	<span class="lnk"><a href="#">
    	 Keyword ${i}</a>
    	</span>
   	</td>
       <td class="borderB padTB5 txtAR scorelink">
       	<span class="lnk"><a href="#">Related Info</a></span>
       </td>
   </tr>
   </c:forEach>
  </table>

<div class="sidebarHeader farial fsize16 fwhite bluebgTitle">Twitter Feeds</div>

<div class="pad5 marT10">
    <script src="http://widgets.twimg.com/j/2/widget.js"></script>
	<script>
	new TWTR.Widget({
	  version: 2,
	  type: 'search',
	  search: '${keyword}',
	  interval: 30000,
	  title: 'Twitter Feeds',
	  subject: '',
	  width: 230,
	  height: 300,
	  theme: {
	    shell: {
	      background: '#8ec1da',
	      color: '#ffffff'
	    },
	    tweets: {
	      background: '#ffffff',
	      color: '#444444',
	      links: '#1985b5'
	    }
	  },
	  features: {
	    scrollbar: false,
	    loop: true,
	    live: true,
	    behavior: 'default'
	  }
	}).render().start();
	</script>
	</div>	
</div>    


<div class="floatL w730 marL10 marT27">
<div id="search" class="floatL w730 titlePlacer">
	<form method="post">
	  <!-- img src="<spring:url value="/js/ajaxsolr/images/src_ico.jpg" />" width="29" height="24" align="absmiddle">
	  <input type="text" name="query" id="query" class="farial fsize12 fgray searchBox" value="${keyword!=''? keyword:''}">
	  <a href="javascript:void(0)" id="searchbutton"><img src="<spring:url value="/js/ajaxsolr/images/btn_src.jpg" />" width="79" height="24" align="absmiddle"></a -->
	  
	  <div class="w460 padT10 padL10 floatL fsize20 fnormal breakWord">Keyword</div>
      <div class="floatL w245 txtAR padT7"> <input type="text" name="query" id="query" class="farial fsize12 fgray searchBox searchBoxIconLBg w175" value="${keyword!=''? keyword:''}">
	  <a href="javascript:void(0)" id="searchbutton"><img src="<spring:url value="/js/ajaxsolr/images/btn_GO.png" />"  align="absmiddle"></a> </div> 
	<input type="hidden" id="keyword" name="keyword">
	</form>
</div>

<div class="clearB"></div>

<div class="tabber mar0" style="margin-top:20px; width:98%">

  	<div id="insights" class="tabs">
      		<ul>
		        <li><a href="#sevenDays"><span>7 Days</span></a></li>
		        <li><a href="#oneMonth"><span>1 Month</span></a></li>
		        <li><a href="#oneYear"><span>1 Year</span></a></li>
		    </ul>
      		
        <div id="sevenDays">
       		<div class="floatL w325 marL20 marR12">
				<script type="text/javascript" src="http://www.gmodules.com/ig/ifr?url=http%3A%2F%2Fwww.google.com%2Fig%2Fmodules%2Fgoogle_insightsforsearch_relatedsearches.xml&amp;up__results_type=TOP&amp;up__property=empty&amp;up__search_term=${keyword}&amp;up__location=US&amp;up__category=0&amp;up__time_range=7-d&amp;up__max_results=10&amp;synd=open&amp;w=320&amp;h=350&amp;lang=en-US&amp;title=Google+Insights+for+Search&amp;border=%23ffffff%7C3px%2C1px+solid+%23999999&amp;output=js"></script>
			</div>
			<div class="floatL w325" style="margin-right:1px;">
				<script type="text/javascript" src="http://www.gmodules.com/ig/ifr?url=http%3A%2F%2Fwww.google.com%2Fig%2Fmodules%2Fgoogle_insightsforsearch_relatedsearches.xml&amp;up__results_type=RISING&amp;up__property=empty&amp;up__search_term=${keyword}&amp;up__location=US&amp;up__category=0&amp;up__time_range=7-d&amp;up__max_results=10&amp;synd=open&amp;w=320&amp;h=350&amp;lang=en-US&amp;title=Google+Insights+for+Search&amp;border=%23ffffff%7C3px%2C1px+solid+%23999999&amp;output=js"></script>
			</div>
			<div class="clearB"></div>
			<div class="mar10">
			   <script type="text/javascript" src="http://www.gmodules.com/ig/ifr?url=http%3A%2F%2Fwww.google.com%2Fig%2Fmodules%2Fgoogle_insightsforsearch_interestovertime_searchterms.xml&amp;up__property=empty&amp;up__search_terms=${keyword}&amp;up__location=US&amp;up__category=0&amp;up__time_range=7-d&amp;up__compare_to_category=false&amp;synd=open&amp;w=675&amp;h=300&amp;lang=en-US&amp;title=Google+Insights+for+Search&amp;border=%23ffffff%7C3px%2C1px+solid+%23999999&amp;output=js"></script>         
			</div>
		</div>

		<div id="oneMonth">
		    <div class="floatL w325 marL20 marR12">
				<script type="text/javascript" src="http://www.gmodules.com/ig/ifr?url=http%3A%2F%2Fwww.google.com%2Fig%2Fmodules%2Fgoogle_insightsforsearch_relatedsearches.xml&amp;up__results_type=TOP&amp;up__property=empty&amp;up__search_term=${keyword}&amp;up__location=US&amp;up__category=0&amp;up__time_range=1-m&amp;up__max_results=10&amp;synd=open&amp;w=320&amp;h=350&amp;lang=en-US&amp;title=Google+Insights+for+Search&amp;border=%23ffffff%7C3px%2C1px+solid+%23999999&amp;output=js"></script>
			</div>
			<div class="floatL w325" style="margin-right:1px;">
				<script type="text/javascript" src="http://www.gmodules.com/ig/ifr?url=http%3A%2F%2Fwww.google.com%2Fig%2Fmodules%2Fgoogle_insightsforsearch_relatedsearches.xml&amp;up__results_type=RISING&amp;up__property=empty&amp;up__search_term=${keyword}&amp;up__location=US&amp;up__category=0&amp;up__time_range=1-m&amp;up__max_results=10&amp;synd=open&amp;w=320&amp;h=350&amp;lang=en-US&amp;title=Google+Insights+for+Search&amp;border=%23ffffff%7C3px%2C1px+solid+%23999999&amp;output=js"></script>
			</div>
			<div class="clearB"></div>
			<div class="mar10">
			   <script type="text/javascript" src="http://www.gmodules.com/ig/ifr?url=http%3A%2F%2Fwww.google.com%2Fig%2Fmodules%2Fgoogle_insightsforsearch_interestovertime_searchterms.xml&amp;up__property=empty&amp;up__search_terms=${keyword}&amp;up__location=US&amp;up__category=0&amp;up__time_range=1-m&amp;up__compare_to_category=false&amp;synd=open&amp;w=675&amp;h=300&amp;lang=en-US&amp;title=Google+Insights+for+Search&amp;border=%23ffffff%7C3px%2C1px+solid+%23999999&amp;output=js"></script>         
			</div>
		</div>
		
		<div id="oneYear">
		    <div class="floatL w325 marL10 marR12">
				<script type="text/javascript" src="http://www.gmodules.com/ig/ifr?url=http%3A%2F%2Fwww.google.com%2Fig%2Fmodules%2Fgoogle_insightsforsearch_relatedsearches.xml&amp;up__results_type=TOP&amp;up__property=empty&amp;up__search_term=${keyword}&amp;up__location=US&amp;up__category=0&amp;up__time_range=12-m&amp;up__max_results=10&amp;synd=open&amp;w=320&amp;h=350&amp;lang=en-US&amp;title=Google+Insights+for+Search&amp;border=%23ffffff%7C3px%2C1px+solid+%23999999&amp;output=js"></script>
			</div>
			<div class="floatL w325" style="margin-right:1px;">
				<script type="text/javascript" src="http://www.gmodules.com/ig/ifr?url=http%3A%2F%2Fwww.google.com%2Fig%2Fmodules%2Fgoogle_insightsforsearch_relatedsearches.xml&amp;up__results_type=RISING&amp;up__property=empty&amp;up__search_term=${keyword}&amp;up__location=US&amp;up__category=0&amp;up__time_range=12-m&amp;up__max_results=10&amp;synd=open&amp;w=320&amp;h=350&amp;lang=en-US&amp;title=Google+Insights+for+Search&amp;border=%23ffffff%7C3px%2C1px+solid+%23999999&amp;output=js"></script>
			</div>
			<div class="clearB"></div>
			<div class="mar10" style="width: 675; overflow: auto">
			   <script type="text/javascript" src="http://www.gmodules.com/ig/ifr?url=http%3A%2F%2Fwww.google.com%2Fig%2Fmodules%2Fgoogle_insightsforsearch_interestovertime_searchterms.xml&amp;up__property=empty&amp;up__search_terms=${keyword}&amp;up__location=US&amp;up__category=0&amp;up__time_range=11-m&amp;up__compare_to_category=false&amp;synd=open&amp;w=675&amp;h=300&amp;lang=en-US&amp;title=Google+Insights+for+Search&amp;border=%23ffffff%7C3px%2C1px+solid+%23999999&amp;output=js"></script>         
			</div>
	   </div>
	</div>
</div><!--  end tab -->
</div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>