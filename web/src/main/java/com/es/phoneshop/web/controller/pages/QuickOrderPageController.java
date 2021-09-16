package com.es.phoneshop.web.controller.pages;

import com.es.core.cart.CartService;
import com.es.core.cart.CartTotal;
import com.es.core.model.order.QuickOrderItem;
import com.es.core.order.OutOfStockException;
import com.es.phoneshop.web.controller.forms.QuickOrderForm;
import com.es.phoneshop.web.controller.validator.QuickOrderFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/quickOrder")
public class QuickOrderPageController {

    @Autowired
    @Qualifier("quickOrderFormValidator")
    private QuickOrderFormValidator validator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(validator);
    }

    @Resource
    private CartService cartService;

    @RequestMapping(method = RequestMethod.GET)
    public String showQuickOrderPage(Model model){
        Map<Long,Long> cartItemsCopy = new HashMap<>(cartService.getCart().getItems());
        CartTotal cartTotal = cartService.getCartTotal(cartItemsCopy);
        if(!model.containsAttribute("orderModel")){
            model.addAttribute("orderModel", setQuickOrderModel());
        }
        model.addAttribute("itemsAmount", cartTotal.getItemsAmount());
        model.addAttribute("overallPrice", cartTotal.getOverallPrice());
        return "quickOrder";
    }

    @RequestMapping(value = "/add2cart", method = RequestMethod.POST)
    public String addToCart(@ModelAttribute("orderModel") @Valid QuickOrderForm form,
                            BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("orderModel", form);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.orderModel", bindingResult);
        }else{
            try{
                cartService.quickAdd(form.getQuickOrderItems());
            } catch(OutOfStockException e){
                for(Integer number : e.getItemNumbersList()){
                    bindingResult.rejectValue("quickOrderItems[" + number + "].quantity", "outOfStock", e.getMessage());
                }
                redirectAttributes.addFlashAttribute("orderModel", form);
                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.orderModel", bindingResult);
            }
        }
        return "redirect:/quickOrder";
    }

    private QuickOrderForm setQuickOrderModel(){
        QuickOrderForm form = new QuickOrderForm();
        form.setQuickOrderItems(Arrays.asList(
                new QuickOrderItem(),
                new QuickOrderItem(),
                new QuickOrderItem(),
                new QuickOrderItem(),
                new QuickOrderItem(),
                new QuickOrderItem(),
                new QuickOrderItem(),
                new QuickOrderItem()
        ));
        return form;
    }
}
