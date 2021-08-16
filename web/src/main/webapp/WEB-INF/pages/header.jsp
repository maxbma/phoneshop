<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:url var="contextUrl" value = "/" scope="request"/>
<script>var ctx = "${contextUrl}"</script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.0/jquery.min.js"></script>
<script>
    function submitForm(form){
        let quantity = form.elements["quantity"].value;
        let phoneId = form.elements["phoneId"].value;
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
</script>
<img src="https://cdn.iconscout.com/icon/free/png-256/smartphone-1703329-1446727.png" height="40px"/>
<b style="font-size: 40px; font-weight: bolder; margin-left: 5px; font-family: 'Calibri Light'">Phonify</b>
<p style="margin-right: 5%; font-size: 20px; float: right">
    <a href="${contextUrl}cart">
        My cart:
    </a>
    <b id="itemsAmount">${itemsAmount}</b> items
    <b id="overallPrice">${overallPrice}</b> $
</p>
<br/>
<hr/>
