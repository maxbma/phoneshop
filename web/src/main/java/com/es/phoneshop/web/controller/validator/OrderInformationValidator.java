package com.es.phoneshop.web.controller.validator;

import com.es.phoneshop.web.controller.forms.OrderInformationForm;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Service
public class OrderInformationValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return OrderInformationForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        OrderInformationForm form = (OrderInformationForm) o;
        if(form.getFirstName().length() == 0){
            errors.rejectValue("firstName", "emptyString", "The value is required");
        }
        if(form.getLastName().length() == 0){
            errors.rejectValue("lastName", "emptyString", "The value is required");
        }
        if(form.getAddress().length() == 0){
            errors.rejectValue("address", "emptyString", "The value is required");
        }
        if(form.getPhoneNumber().length() == 0){
            errors.rejectValue("phoneNumber", "emptyString", "The value is required");
        }
    }
}
