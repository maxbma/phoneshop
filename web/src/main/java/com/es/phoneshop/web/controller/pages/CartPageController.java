package com.es.phoneshop.web.controller.pages;

import com.es.core.cart.CartItem;
import com.es.core.cart.CartService;
import com.es.core.model.phone.Phone;
import com.es.core.order.OutOfStockException;
import com.es.phoneshop.web.controller.forms.CartUpdateForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Controller
@RequestMapping(value = "/cart")
public class CartPageController {
    @Resource
    private CartService cartService;

    @Autowired
    @Qualifier("cartUpdateFormValidator")
    private Validator cartUpdateFormValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(cartUpdateFormValidator);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getCart(Model model) {
        Set<Map.Entry<Phone,Long>> phoneEntrySet = cartService.getPhoneMap().entrySet();
        model.addAttribute("phoneEntrySet", phoneEntrySet);
        model.addAttribute("cartUpdateForm", setUpdateForm(phoneEntrySet));
        model.addAttribute("itemsAmount", cartService.getItemsAmount());
        model.addAttribute("overallPrice", cartService.getOverallPrice());
        return "cart";
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public String updateCart(@RequestParam(name="delete", required = false) Long deleteId,
                           @ModelAttribute("cartUpdateForm") @Valid CartUpdateForm cartUpdateForm, BindingResult result, Model model) {
        if(deleteId != null){
            return deletePhone(deleteId);
        } else{
            return updatePhoneCart(cartUpdateForm, result, model);
        }
    }

    private String deletePhone(long phoneId){
        cartService.remove(phoneId);
        return "redirect:./";
    }

    private void getCartItems(Model model){
        Set<Map.Entry<Phone,Long>> phoneEntrySet = cartService.getPhoneMap().entrySet();
        model.addAttribute("phoneEntrySet", phoneEntrySet);
        model.addAttribute("itemsAmount", cartService.getItemsAmount());
        model.addAttribute("overallPrice", cartService.getOverallPrice());
    }

    private String updatePhoneCart(CartUpdateForm form, BindingResult result, Model model){
        if (result.hasErrors()) {
            getCartItems(model);
            return "cart";
        } else {
            Map<Long, Long> items= new HashMap<>();
            for(CartItem item : form.getItems()){
                items.put(item.getPhoneId(), item.getQuantity());
            }
            try{
                cartService.update(items);
            } catch (OutOfStockException e){
                String[] errorMessage = e.getMessage().split(":");
                result.rejectValue("items[" + errorMessage[1] + "].quantity", "outOfStock", errorMessage[0]);
                getCartItems(model);
                return "cart";
            }
            return "redirect:./";
        }
    }

    private CartUpdateForm setUpdateForm(Set<Map.Entry<Phone,Long>> entrySet){
        CartUpdateForm form = new CartUpdateForm();
        ArrayList<CartItem> itemsList = new ArrayList<>(entrySet.size());
        for(Map.Entry<Phone,Long> entry : entrySet){
            CartItem item = new CartItem();
            item.setPhoneId(entry.getKey().getId());
            item.setQuantity(entry.getValue());
            itemsList.add(item);
        }
        form.setItems(itemsList);
        return form;
    }
}
