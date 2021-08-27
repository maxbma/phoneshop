package com.es.phoneshop.web.controller.pages;

import com.es.core.cart.CartItem;
import com.es.core.cart.CartService;
import com.es.core.cart.CartTotal;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        Map<Long,Long> cartItemsCopy = new HashMap<>(cartService.getCart().getItems());
        Set<Map.Entry<Phone,Long>> phoneEntrySet = cartService.getPhoneMap(cartItemsCopy).entrySet();
        CartTotal cartTotal = cartService.getCartTotal(cartItemsCopy);
        if(!model.containsAttribute("cartUpdateForm")){
            model.addAttribute("cartUpdateForm", setUpdateForm(phoneEntrySet));
        }
        model.addAttribute("phoneEntrySet", phoneEntrySet);
        model.addAttribute("itemsAmount", cartTotal.getItemsAmount());
        model.addAttribute("overallPrice", cartTotal.getOverallPrice());
        return "cart";
    }

    @RequestMapping(value = "/update",method = RequestMethod.PUT)
    public String updateCart(@ModelAttribute("cartUpdateForm") @Valid CartUpdateForm cartUpdateForm,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        return updatePhoneCart(cartUpdateForm, result, redirectAttributes);
    }

    @RequestMapping(value = "/update",method = RequestMethod.DELETE)
    public String deletePhoneFromCart(@RequestParam(name="delete", required = false) Long phoneId){
        cartService.remove(phoneId);
        return "redirect:./";
    }

    private String updatePhoneCart(CartUpdateForm form, BindingResult result,
                                   RedirectAttributes redirectAttributes){
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("cartUpdateForm", form);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.cartUpdateForm", result);
        } else {
            try{
                cartService.update(form.getItems());
            } catch (OutOfStockException e){
                for(Integer number : e.getItemNumbersList()){
                    result.rejectValue("items[" + number + "].quantity", "outOfStock", e.getMessage());
                }
                redirectAttributes.addFlashAttribute("cartUpdateForm", form);
                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.cartUpdateForm", result);
            }
        }
        return "redirect:./";
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
