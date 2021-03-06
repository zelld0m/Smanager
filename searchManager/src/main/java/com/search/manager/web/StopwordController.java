package com.search.manager.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.search.manager.service.StopwordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/stopword")
@Scope(value = "prototype")
public class StopwordController {

    private static final Logger logger =
            LoggerFactory.getLogger(StopwordController.class);
    @SuppressWarnings("unused")
    @Autowired
    private StopwordService stopwordService;

    @RequestMapping(value = "/{store}")
    public String execute(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String store) {
        model.addAttribute("store", store);

        return "lexicon/stopword";
    }
}
