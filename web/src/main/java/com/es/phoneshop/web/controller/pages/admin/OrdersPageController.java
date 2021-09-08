package com.es.phoneshop.web.controller.pages.admin;

import com.es.core.model.order.Order;
import com.es.core.model.order.OrderDao;
import com.es.core.model.order.OrderStatus;
import com.es.core.order.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping(value = "/admin/orders")
public class OrdersPageController {

    @Resource
    private OrderService orderService;
    @Resource
    private OrderDao orderDao;

    @RequestMapping(method = RequestMethod.GET)
    public String showOrders(Model model){
        List<Order> orderList = orderService.getAllOrders();
        model.addAttribute("orderList", orderList);
        return "admin/orders";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String showOrderInfo(@PathVariable("id") long orderId, Model model, RedirectAttributes redirectAttributes){
        Order order = orderService.getOrderById(orderId);
        if(order == null){
            redirectAttributes.addFlashAttribute("orderForAdminError", "Order not found");
            return "redirect:/admin/orders";
        }
        model.addAttribute("order", order);
        return "admin/orderInfo";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String changeOrderStatus(@PathVariable("id") long orderId,
                                @RequestParam("status") Integer statusId,
                                    RedirectAttributes redirectAttributes){
        if(!orderDao.exists(orderId)){
            redirectAttributes.addFlashAttribute("errorChangingStatus", "Order not exists");
            return "redirect:/admin/orders";
        } else if(!orderDao.isCurrentStatusNew(orderId)){
            redirectAttributes.addFlashAttribute("errorChangingStatus", "Order status must be NEW to update it");
        } else if(!checkIfStatusIdExists(statusId)){
            redirectAttributes.addFlashAttribute("errorChangingStatus", "Status ID not exists");
        } else{
            orderDao.changeStatus(orderId,statusId);
        }
        return "redirect:/admin/orders/"+orderId;
    }

    private boolean checkIfStatusIdExists(Integer statusId){
        for(OrderStatus status : OrderStatus.values()){
            if(status.getStatusId() == statusId) return true;
        }
        return false;
    }
}
