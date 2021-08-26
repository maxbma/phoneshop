package com.es.phoneshop.web.controller;

import com.es.core.cart.CartItem;
import com.es.core.cart.CartService;
import com.es.core.cart.CartTotal;
import com.es.core.order.OutOfStockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping(value = "/ajaxCart")
public class AjaxCartController {
    @Resource
    private CartService cartService;

    @Autowired
    @Qualifier("cartValidator")
    private Validator cartValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(cartValidator);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView addPhone(@Valid CartItem cartItem,
                           BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView(new MappingJackson2JsonView());
        if(bindingResult.hasErrors()) {
            String msg = bindingResult.getFieldError().getDefaultMessage();
            modelAndView.addObject("errorMsg", msg);
            modelAndView.addObject("errorId", cartItem.getPhoneId());
        } else{
            try{
                cartService.addPhone(cartItem.getPhoneId(), cartItem.getQuantity());
                Map<Long,Long> cartItemsCopy = new HashMap<>(cartService.getCart().getItems());
                CartTotal cartTotal = cartService.getCartTotal(cartItemsCopy);
                modelAndView.addObject("itemsAmount", cartTotal.getItemsAmount());
                modelAndView.addObject("overallPrice", cartTotal.getOverallPrice());
            } catch (OutOfStockException e){
                modelAndView.addObject("errorMsg", e.getMessage());
                modelAndView.addObject("errorId", cartItem.getPhoneId());
            }
        }
        return modelAndView;
    }
}
