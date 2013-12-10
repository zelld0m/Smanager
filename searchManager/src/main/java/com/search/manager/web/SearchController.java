package com.search.manager.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SearchController extends AbstractSearchController {

	@RequestMapping("/search/**")
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		super.handleRequest(request, response);
	}

	protected String getRequestPath(HttpServletRequest request) {
		String start = request.getContextPath() + "/search";
		int idx = request.getRequestURI().indexOf(start);
		return "http:/"
				+ request.getRequestURI().substring(start.length() + idx);
	}

}