package com.es.phoneshop.web.controller.pages;

import com.es.core.cart.CartService;
import com.es.core.cart.CartTotal;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.PhoneDao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping(value = "/productDetails/{id}")
public class ProductDetailsPageController {
    @Resource
    PhoneDao phoneDao;

    @Resource
    private CartService cartService;

    @RequestMapping(method = RequestMethod.GET)
    public String showProductDetails(@PathVariable("id") long id, Model model, RedirectAttributes redirectAttributes){
        Optional<Phone> phone = phoneDao.get(id);

        if(phone.isPresent()){
            Map<Long,Long> cartItemsCopy = new HashMap<>(cartService.getCart().getItems());
            CartTotal cartTotal = cartService.getCartTotal(cartItemsCopy);
            model.addAttribute("itemsAmount", cartTotal.getItemsAmount());
            model.addAttribute("overallPrice", cartTotal.getOverallPrice());
            model.addAttribute("phone", phone.get());
            return "productDetails";
        } else {
            redirectAttributes.addFlashAttribute("error", "Phone not found");
            return "redirect:/error";
        }
    }
}
