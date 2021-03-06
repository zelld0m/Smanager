package com.search.manager.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@Scope(value = "prototype")
public class StatisticController {

    private static final Logger logger = LoggerFactory.getLogger(StatisticController.class);

    @RequestMapping(value = "/topkeyword/{store}")
    public String topKeyword(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String store) {
        model.addAttribute("store", store);

        return "statistic/topkeyword";
    }

    @RequestMapping(value = "/zeroresult/{store}")
    public String zeroResult(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String store) {
        model.addAttribute("store", store);

        return "statistic/zeroresult";
    }

    @RequestMapping(value = "/keywordtrends/{store}")
    public String keywordTrends(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String store) {
        model.addAttribute("store", store);

        return "statistic/keywordtrends";
    }

    @RequestMapping(value = "/reportgenerator/{store}")
    public String reportGenerator(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String store) {
        model.addAttribute("store", store);

        return "statistic/reportgenerator";
    }
}
