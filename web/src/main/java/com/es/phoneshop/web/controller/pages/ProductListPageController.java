package com.es.phoneshop.web.controller.pages;

import com.es.core.cart.CartService;
import com.es.core.model.phone.Color;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.PhoneDao;
import com.es.core.page.PageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping (value = "/productList")
public class ProductListPageController {
    @Resource
    private PhoneDao phoneDao;

    @Resource
    private CartService cartService;

    @Resource
    private PageService pageService;

    private final static int PAGE_LIMIT = 10;

    @RequestMapping(method = RequestMethod.GET)
    public String showProductList(Model model,
                                  @RequestParam(required = false, defaultValue = "1") int page,
                                  @RequestParam(required = false, defaultValue = "id asc") String order,
                                  @RequestParam(required = false) String search) {
        int offset = (page-1)*PAGE_LIMIT;
        int totalPages;
        List<Phone> phones;
        totalPages = pageService.getPagesAmount(search);
        if(search == null){
            phones = phoneDao.findAll(order, offset, PAGE_LIMIT);
        } else {
            phones = phoneDao.findSearchedPhones(search, order, offset, PAGE_LIMIT);
        }
        if(search!=null) model.addAttribute("search", search);
        model.addAttribute("phones", setColors(phones));
        model.addAttribute("order", order);
        model.addAttribute("total", totalPages);
        model.addAttribute("page", page);
        model.addAttribute("itemsAmount", cartService.getItemsAmount());
        model.addAttribute("overallPrice", cartService.getOverallPrice());
        model.addAttribute("pagesNumeration", pageService.getPagesNumeration(page, totalPages));
        return "productList";
    }

    private List<Phone> setColors(List<Phone> phoneList){
        for(Phone phone : phoneList){
            phone.setColors(new HashSet<Color>(phoneDao.getPhoneColors(phone.getId())));
        }
        return phoneList;
    }
}
