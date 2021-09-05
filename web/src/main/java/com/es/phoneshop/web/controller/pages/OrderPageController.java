package com.es.phoneshop.web.controller.pages;

import com.es.core.cart.CartService;
import com.es.core.cart.CartTotal;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderStatus;
import com.es.core.model.phone.Phone;
import com.es.core.order.OrderService;
import com.es.core.order.OutOfStockException;
import com.es.phoneshop.web.controller.forms.OrderInformationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping(value = "/order")
public class OrderPageController {

    @Resource
    private OrderService orderService;

    @Resource
    private CartService cartService;

    @Autowired
    @Qualifier("orderInformationValidator")
    private Validator orderInformationValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder){
        binder.setValidator(orderInformationValidator);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getOrder(Model model, RedirectAttributes redirectAttributes){
        Map<Long,Long> cartItemsCopy = new HashMap<>(cartService.getCart().getItems());
        if(cartItemsCopy.isEmpty()){
            redirectAttributes.addFlashAttribute("orderErrorMsg", "Cart is empty");
            return "redirect:/cart";
        }
        Set<Map.Entry<Phone, Long>> phoneSet = cartService.getPhoneMap(cartItemsCopy).entrySet();
        CartTotal cartTotal = cartService.getCartTotal(cartItemsCopy);
        BigDecimal overallPrice = cartTotal.getOverallPrice();
        BigDecimal deliveryPrice = new BigDecimal(orderService.getDeliveryPrice());
        BigDecimal totalPrice = overallPrice.add(deliveryPrice);
        if(!model.containsAttribute("orderInformationForm")){
            model.addAttribute("orderInformationForm", new OrderInformationForm());
        }
        model.addAttribute("itemsAmount", cartTotal.getItemsAmount());
        model.addAttribute("overallPrice", overallPrice);
        model.addAttribute("phoneEntrySet", phoneSet);
        model.addAttribute("deliveryPrice", deliveryPrice);
        model.addAttribute("totalPrice", totalPrice);
        return "order";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String placeOrder(@ModelAttribute("orderInformationForm") @Valid OrderInformationForm orderInfo,
                             BindingResult result, HttpServletRequest request, RedirectAttributes redirectAttributes) throws OutOfStockException {
        if(result.hasErrors()){
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.orderInformationForm", result);
            redirectAttributes.addFlashAttribute("orderInformationForm", orderInfo);
            return "redirect:/order";
        } else {
            Map<Long,Long> cartItemsCopy = new HashMap<>(cartService.getCart().getItems());
            if(cartItemsCopy.isEmpty()){
                redirectAttributes.addFlashAttribute("orderErrorMsg", "Cart is empty");
                return "redirect:/cart";
            }
            Map<Phone, Long> phoneMap = cartService.getPhoneMap(cartItemsCopy);
            try {
                orderService.validateStocks(cartItemsCopy);
            } catch (OutOfStockException e){
                for (long phoneId : e.getPhoneIdList()){
                    cartService.remove(phoneId);
                }
                redirectAttributes.addFlashAttribute("orderErrorMsg", "Some products from your order were out of stock and deleted from your cart");
                return "redirect:/cart";
            }

            Order order = orderService.createOrder(phoneMap);
            setOrderInformation(orderInfo, order);
            setOrderPrices(cartService.getCartTotal(cartItemsCopy), order);
            order.setStatus(OrderStatus.NEW);

            try{
                orderService.placeOrder(order, cartItemsCopy);
            } catch (ConstraintViolationException constraintException){
                try{
                    orderService.validateStocks(cartItemsCopy);
                } catch (OutOfStockException e){
                    for (long phoneId : e.getPhoneIdList()){
                        cartService.remove(phoneId);
                    }
                    redirectAttributes.addFlashAttribute("orderErrorMsg", "Some products from your order were out of stock and deleted from your cart");
                    return "redirect:/cart";
                }
                redirectAttributes.addFlashAttribute("error", "Unknown error");
                return "redirect:/error";
            }
            cartService.cleanCart(cartItemsCopy);

            String id = UUID.randomUUID().toString();

            HttpSession session = request.getSession();
            Map<String,Order> sessionOrderMap;
            Object mapObject = session.getAttribute("sessionOrderMap");
            if(mapObject == null){
                sessionOrderMap = new ConcurrentHashMap<>();
                session.setAttribute("sessionOrderMap", sessionOrderMap);
            } else{
                sessionOrderMap = (Map<String, Order>) mapObject;
            }
            sessionOrderMap.put(id,order);

            return "redirect:/orderOverview/"+id;
        }
    }

    private void setOrderInformation(OrderInformationForm form, Order order){
        order.setFirstName(form.getFirstName());
        order.setLastName(form.getLastName());
        order.setDeliveryAddress(form.getAddress());
        order.setContactPhoneNo(form.getPhoneNumber());
        order.setAdditionalInfo(form.getAdditionalInfo());
    }

    private void setOrderPrices(CartTotal cartTotal, Order order){
        BigDecimal overallPrice = cartTotal.getOverallPrice();
        BigDecimal deliveryPrice = new BigDecimal(orderService.getDeliveryPrice());
        BigDecimal totalPrice = overallPrice.add(deliveryPrice);

        order.setSubtotal(overallPrice);
        order.setDeliveryPrice(deliveryPrice);
        order.setTotalPrice(totalPrice);
    }
}
