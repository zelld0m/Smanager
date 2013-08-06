package com.search.manager.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/browse")
@Scope(value = "prototype")
public class BrowseController {

    private static final Logger logger =
            LoggerFactory.getLogger(BrowseController.class);

    @RequestMapping(value = "/{store}")
    public String execute(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String store) {
        model.addAttribute("store", store);

        return "browse/simulator";
    }
}
