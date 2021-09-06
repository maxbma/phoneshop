<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Orders</title>
    <jsp:include page="header.jsp"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/style.css"/>">
</head>
<body>
<c:if test="${orderForAdminError != null}">
    <script>
        alert('${orderForAdminError}');
    </script>
</c:if>
<p style="font-weight: bolder; font-size: large">Orders</p>
<table class="tbl">
    <thead>
        <th>Order number</th>
        <th>Customer</th>
        <th>Phone</th>
        <th>Address</th>
        <th>Date</th>
        <th>Total price</th>
        <th>Status</th>
    </thead>
    <c:forEach var="order" items="${orderList}">
        <tr>
            <td>
                <a href="${contextUrl}admin/orders/${order.id}">
                    ${order.id}
                </a>
            </td>
            <td><c:out value="${order.firstName} ${order.lastName}"/></td>
            <td>${order.contactPhoneNo}</td>
            <td>${order.deliveryAddress}</td>
            <td>${order.date}</td>
            <td>${order.totalPrice}$</td>
            <td>${order.status}</td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
