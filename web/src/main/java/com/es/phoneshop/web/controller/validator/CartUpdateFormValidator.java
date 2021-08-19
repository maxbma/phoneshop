package com.es.phoneshop.web.controller.validator;

import com.es.core.cart.CartItem;
import com.es.phoneshop.web.controller.forms.CartUpdateForm;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Service
public class CartUpdateFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return CartUpdateForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        CartUpdateForm form = (CartUpdateForm) o;
        int itemNumberInCart = 0;
        for(CartItem item : form.getItems()){
            if (item.getQuantity() == null) errors.rejectValue("items[" + itemNumberInCart + "].quantity", "nullQ", "Fill this field correctly");
            else if (item.getQuantity() < 1)
                errors.rejectValue("items[" + itemNumberInCart + "].quantity", "negQ", "Quantity should be more 0");
            itemNumberInCart++;
        }
    }
}
