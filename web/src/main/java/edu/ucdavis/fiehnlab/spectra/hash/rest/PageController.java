package edu.ucdavis.fiehnlab.spectra.hash.rest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * simple controller for documentation reasons
 */
@Controller
public class PageController {
    @RequestMapping("/")
    public String indexPage(Model model) {
        return "index";
    }
}
