package com.search.manager.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class DashboardController {

    private static final Logger logger =
            LoggerFactory.getLogger(DashboardController.class);

    @RequestMapping
    public String execute(HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("keyword", StringUtils.defaultIfBlank(request.getParameter("keyword"), "apple"));

        return "statistic/topkeyword";
    }
}