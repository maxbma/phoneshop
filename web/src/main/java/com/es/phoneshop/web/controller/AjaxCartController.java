package com.es.phoneshop.web.controller;

import com.es.core.cart.CartItem;
import com.es.core.cart.CartService;
import com.es.core.order.OutOfStockException;
import com.es.core.page.PageService;
import com.es.phoneshop.web.controller.validator.CartValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;


@Controller
@RequestMapping(value = "/ajaxCart")
public class AjaxCartController {
    @Resource
    private CartService cartService;

    @Resource
    private PageService pageService;

    @Autowired
    @Qualifier("cartValidator")
    private Validator cartValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(cartValidator);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String addPhone(@RequestParam(required = false, defaultValue = "1") int page,
                           @RequestParam(required = false, defaultValue = "id asc") String order,
                           @RequestParam(required = false) String search,
                           @Valid CartItem cartItem,
                           BindingResult bindingResult,
                           HttpSession session,
                           Model model) {
        if(bindingResult.hasErrors()) {
            String msg = bindingResult.getFieldError().getDefaultMessage();
            session.setAttribute("error", msg);
            session.setAttribute("errorId", cartItem.getPhoneId());
        } else{
            try{
                cartService.addPhone(cartItem.getPhoneId(), Long.parseLong(cartItem.getQuantity()));
                session.setAttribute("itemsAmount", cartService.getItemsAmount());
                session.setAttribute("overallPrice", cartService.getOverallPrice());
            } catch (OutOfStockException e){
                session.setAttribute("error", e.getMessage());
                session.setAttribute("errorId", cartItem.getPhoneId());
            }
        }

        if(search!=null) model.addAttribute("search", search);
        model.addAttribute("page", page);
        model.addAttribute("order", order);
        return "redirect:/productList";
    }
}
