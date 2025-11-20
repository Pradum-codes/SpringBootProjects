package com.pradumcodes.store.controller;

import com.pradumcodes.store.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserInterfaceController {
    @GetMapping("/")
    public String index() {
        return "index";
    }
}
