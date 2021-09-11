function submitForm(form){
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");
    var csrfToken = $("meta[name='_csrf']").attr("content");

    let quantity = form.elements["quantity"].value;
    let phoneId = form.elements["phoneId"].value;

    if (! /[0-9]+$/.test(quantity)){
        $("#error" + phoneId).text("Wrong format");
        return;
    }
    $.ajax({
        type: 'POST',
        beforeSend: function(request){
          request.setRequestHeader(csrfHeader,csrfToken);
        },
        data: {
            quantity: quantity,
            phoneId: phoneId,
            csrfParameter: csrfToken
        },
        url: ctx + "ajaxCart",
        success: (function(data) {
            $("#itemsAmount").text(data.itemsAmount);
            $("#overallPrice").text(data.overallPrice);
            $("#error" + phoneId).text(data.errorMsg);
        }),
    });
}