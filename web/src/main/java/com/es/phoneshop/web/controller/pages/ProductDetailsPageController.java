package com.es.phoneshop.web.controller.pages;

import com.es.core.cart.CartService;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.PhoneDao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.Optional;

@Controller
@RequestMapping(value = "/productDetails/{id}")
public class ProductDetailsPageController {
    @Resource
    PhoneDao phoneDao;

    @Resource
    private CartService cartService;

    @RequestMapping(method = RequestMethod.GET)
    public String showProductDetails(@PathVariable("id") long id, Model model){
        Optional<Phone> phone = phoneDao.get(id);

        if(phone.isPresent()){
            model.addAttribute("phone", phone.get());
            model.addAttribute("itemsAmount", cartService.getItemsAmount());
            model.addAttribute("overallPrice", cartService.getOverallPrice());
            return "productDetails";
        } else {
            throw new IllegalArgumentException();
        }
    }
}
