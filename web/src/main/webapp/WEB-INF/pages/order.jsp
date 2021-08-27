<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Cart</title>
    <jsp:include page="header.jsp"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/style.css"/>">
</head>
<body>
<a href="${contextUrl}productList" style="margin-left: 30px">
    <button>Back to product list</button>
</a>
<table class="tbl">
    <thead>
    <tr>
        <th>Brand</th>
        <th>Model</th>
        <th>Color</th>
        <th>Display size</th>
        <th>Quantity</th>
        <th>Price</th>
    </tr>
    </thead>
    <c:forEach var="phoneEntry" items="${phoneEntrySet}">
        <tr>
            <td>${phoneEntry.key.brand}</td>
            <td>${phoneEntry.key.model}</td>
            <td>
                <c:forEach var="color" items="${phoneEntry.key.colors}">
                    <c:out value="${color.code} " />
                </c:forEach>
            </td>
            <td>${phoneEntry.key.displaySizeInches}"</td>
            <td>${phoneEntry.value}</td>
            <td>${phoneEntry.key.price}$</td>
        </tr>
    </c:forEach>
    <tr>
        <td style="border: none; background-color: white"></td>
        <td style="border: none; background-color: white"></td>
        <td style="border: none; background-color: white"></td>
        <td style="border: none; background-color: white"></td>
        <td>Subtotal</td>
        <td>${overallPrice}$</td>
    </tr>
    <tr>
        <td style="border: none; background-color: white"></td>
        <td style="border: none; background-color: white"></td>
        <td style="border: none; background-color: white"></td>
        <td style="border: none; background-color: white"></td>
        <td>Delivery</td>
        <td>${deliveryPrice}$</td>
    </tr>
    <tr>
        <td style="border: none; background-color: white"></td>
        <td style="border: none; background-color: white"></td>
        <td style="border: none; background-color: white"></td>
        <td style="border: none; background-color: white"></td>
        <td>Total</td>
        <td>${totalPrice}$</td>
    </tr>
</table>
<form:form method="post" modelAttribute="orderInformationForm">
    <table>
        <tr>
            <td>First name*</td>
            <td>
                <form:input path="firstName"/>
                <br/>
                <form:errors path="firstName" style="color:red"/>
            </td>
        </tr>
        <tr>
            <td>Last name*</td>
            <td>
                <form:input path="lastName"/>
                <br/>
                <form:errors path="lastName" style="color:red"/>
            </td>
        </tr>
        <tr>
            <td>Address</td>
            <td>
                <form:input path="address"/>
                <br/>
                <form:errors path="address" style="color:red"/>
            </td>
        </tr>
        <tr>
            <td>Phone</td>
            <td>
                <form:input path="phoneNumber"/>
                <br/>
                <form:errors path="phoneNumber" style="color:red"/>
            </td>
        </tr>
    </table>
    <form:textarea path="additionalInfo" rows="3" cols="32" />
    <br/>
    <input type="submit" value="Order"/>
</form:form>
</body>
</html>
