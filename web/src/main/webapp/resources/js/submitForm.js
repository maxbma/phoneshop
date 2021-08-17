function submitForm(form){
    let quantity = form.elements["quantity"].value;
    let phoneId = form.elements["phoneId"].value;
    if (! /[0-9]+$/.test(quantity)){
        $("#error" + phoneId).text("Wrong format");
        return;
    }
    $.ajax({
        type: 'POST',
        data: {
            quantity: quantity,
            phoneId: phoneId
        },
        url: ctx + "ajaxCart",
        success: (function(data) {
            $("#itemsAmount").text(data.itemsAmount);
            $("#overallPrice").text(data.overallPrice);
            $("#error" + phoneId).text(data.errorMsg);
        }),
    });
}