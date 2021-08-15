package com.es.phoneshop.web.controller.validator;

import com.es.core.cart.CartItem;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Service
public class CartValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return CartItem.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        CartItem cartItem = (CartItem) o;
        try {
            if (cartItem.getQuantity() == null) errors.rejectValue("quantity", "nullQ", "Fill this field");
            else if (Integer.parseInt(cartItem.getQuantity()) < 1)
                errors.rejectValue("quantity", "negQ", "Quantity should be more 0");
        } catch (NumberFormatException e){
            errors.rejectValue("quantity", "numbQ", "Wrong format");
        }
    }
}