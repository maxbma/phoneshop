package com.es.phoneshop.web.controller.validator;

import com.es.core.model.order.QuickOrderItem;
import com.es.phoneshop.web.controller.forms.QuickOrderForm;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Service
public class QuickOrderFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return QuickOrderForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        QuickOrderForm form = (QuickOrderForm) o;
        int itemNumber = 0;
        for(QuickOrderItem item : form.getQuickOrderItems()){
            if(item.getQuantity() != null && item.getPhoneModel().isBlank()){
                errors.rejectValue("quickOrderItems[" + itemNumber + "].phoneModel", "nullModel", "Fill this field");
            } else if(item.getQuantity() == null && !item.getPhoneModel().isBlank()){
                errors.rejectValue("quickOrderItems[" + itemNumber + "].quantity", "nullQ", "Fill this field");
            } else if (item.getQuantity() == null && item.getPhoneModel().isBlank()){
                itemNumber++;
                continue;
            } else if(item.getQuantity() < 1) {
                errors.rejectValue("quickOrderItems[" + itemNumber + "].quantity", "negQ", "Quantity should be more 0");
            }
            itemNumber++;
        }
    }
}
