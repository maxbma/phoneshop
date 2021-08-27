package com.es.phoneshop.web.controller.pages;

import com.es.core.cart.CartService;
import com.es.core.cart.CartTotal;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.PhoneDao;
import com.es.core.page.PageService;
import com.es.phoneshop.web.controller.validator.ValidOrder;
import com.es.phoneshop.web.controller.validator.ValidOrderItem;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.es.core.page.PageService.PAGE_LIMIT;

@Controller
@RequestMapping (value = "/productList")
public class ProductListPageController {
    @Resource
    private PhoneDao phoneDao;

    @Resource
    private CartService cartService;

    @Resource
    private PageService pageService;

    @RequestMapping(method = RequestMethod.GET)
    public String showProductList(Model model,
                                          @RequestParam(required = false, defaultValue = "1") int page,
                                          @RequestParam(required = false, defaultValue = "id asc") String order,
                                          @RequestParam(required = false) String search) {
        if(!isUrlValid(order)) return "redirect:/error";
        int totalPages;
        List<Phone> phones;
        totalPages = pageService.getPagesAmount(search);
        if(page < 1 || page > totalPages) return "redirect:/error";
        int offset = (page-1)*PAGE_LIMIT;
        if(search == null || search.isEmpty()){
            phones = phoneDao.findAll(order, offset, PAGE_LIMIT);
        } else {
            phones = phoneDao.findSearchedPhones(search, order, offset, PAGE_LIMIT);
        }
        if(search!=null && !search.isEmpty()) model.addAttribute("search", search);
        model.addAttribute("phones", phones);
        model.addAttribute("order", order);
        model.addAttribute("total", totalPages);
        model.addAttribute("page", page);
        Map<Long,Long> cartItemsCopy = new HashMap<>(cartService.getCart().getItems());
        CartTotal cartTotal = cartService.getCartTotal(cartItemsCopy);
        model.addAttribute("itemsAmount", cartTotal.getItemsAmount());
        model.addAttribute("overallPrice", cartTotal.getOverallPrice());
        model.addAttribute("pagesNumeration", pageService.getPagesNumeration(page, totalPages));
        return "productList";
    }

    private boolean isUrlValid(String orderUrl){
        String[] parts = orderUrl.split(" ");
        boolean validOrderItem= false, validOrder = false;
        for (ValidOrderItem item : ValidOrderItem.values()){
            if(item.name().equalsIgnoreCase(parts[0])) {
                validOrderItem = true;
                break;
            }
        }
        for(ValidOrder order : ValidOrder.values()){
            if(order.name().equalsIgnoreCase(parts[1])){
                return validOrderItem;
            }
        }
        return false;
    }
}
