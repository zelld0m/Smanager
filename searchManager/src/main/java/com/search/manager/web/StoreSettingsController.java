package com.search.manager.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Philip Mark Gutierrez
 * @since September 02, 2013
 * @version 1.0
 */
@Controller
@RequestMapping("/store_settings")
@Scope(value = "prototype")
public class StoreSettingsController {

    @RequestMapping(value = "/{store}")
    public String execute(HttpServletRequest request, HttpServletResponse response, 
    Model model, @PathVariable String store) {
        model.addAttribute("store", store);

        return "store_settings/store_settings";
    }
}
