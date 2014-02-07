package com.search.manager.web;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SearchController extends AbstractSearchController {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

	@RequestMapping("/search/**")
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	    final Long start = new Date().getTime();
        super.handleRequest(request, response);
        logger.info("Request completion time: {}ms", new Date().getTime()-start);  
	}

	protected String getRequestPath(HttpServletRequest request) {
		String start = request.getContextPath() + "/search";
		int idx = request.getRequestURI().indexOf(start);
		return String.format("http:/%s", request.getRequestURI().substring(start.length() + idx));
	}
}