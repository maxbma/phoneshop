package com.es.phoneshop.web.controller.pages;

import com.es.core.cart.CartService;
import com.es.core.cart.CartTotal;
import com.es.core.model.order.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/orderOverview/{id}")
public class OrderOverviewPageController {

    @Resource
    private CartService cartService;

    @RequestMapping(method = RequestMethod.GET)
    public String getOrderOverview(@PathVariable("id") String orderId, Model model,
                                   HttpServletRequest request, RedirectAttributes redirectAttributes){
        Order order = ((Map<String, Order>)request.getSession().getAttribute("sessionOrderMap")).get(orderId);
        if(order == null) {
            redirectAttributes.addFlashAttribute("orderErrorMsg", "Order not found");
            return "redirect:/cart";
        }
        model.addAttribute("order", order);
        Map<Long,Long> cartItemsCopy = new HashMap<>(cartService.getCart().getItems());
        CartTotal cartTotal = cartService.getCartTotal(cartItemsCopy);
        model.addAttribute("itemsAmount", cartTotal.getItemsAmount());
        model.addAttribute("overallPrice", cartTotal.getOverallPrice());
        return "orderOverview";
    }
}
