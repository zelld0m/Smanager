package com.mall;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class RedirectServlet
 */
public class RedirectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String solrUrl = null;
	public static final Pattern PATTERN = Pattern.compile("solr14/(.*)/",Pattern.DOTALL);       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RedirectServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void init() throws ServletException {
        solrUrl = getServletConfig().getInitParameter("solrUrl");
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/xml");
		PrintWriter out = response.getWriter();
		String uri = request.getRequestURI();
    	String store = null;
    	Matcher matcher = PATTERN.matcher(uri);
    	if (matcher.find()) {
    		store = matcher.group(1);
    	}
    	StringBuilder rurl = new StringBuilder(solrUrl).append(store).append("/select?").append(request.getQueryString());		
		URL url = new URL(rurl.toString());

		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		String inputLine;

		while ((inputLine = in.readLine()) != null) {
			out.println(inputLine);
		}

		in.close();

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
