package com.es.phoneshop.web.controller.pages;

import com.es.core.cart.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/error")
public class ErrorPageController {

    @Resource
    private CartService cartService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String errorPage(Model model){
        model.addAttribute("itemsAmount", cartService.getItemsAmount());
        model.addAttribute("overallPrice", cartService.getOverallPrice());
        return "error";
    }
}
