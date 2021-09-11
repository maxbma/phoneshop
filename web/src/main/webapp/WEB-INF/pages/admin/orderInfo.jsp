<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Order №${order.id}</title>
    <jsp:include page="header.jsp"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/style.css"/>">
</head>
<body>
<c:if test="${errorChangingStatus != null}">
    <script>
        alert('${errorChangingStatus}');
    </script>
</c:if>
<p style="font-size: large; font-weight: bold">Order number: ${order.id}</p>
<b style="float: right">Status: ${order.status}</b>
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
    <c:forEach var="orderItem" items="${order.orderItems}">
        <tr>
            <td>${orderItem.phone.brand}</td>
            <td>${orderItem.phone.model}</td>
            <td>
                <c:forEach var="color" items="${orderItem.phone.colors}">
                    <c:out value="${color.code} " />
                </c:forEach>
            </td>
            <td>${orderItem.phone.displaySizeInches}"</td>
            <td>${orderItem.quantity}</td>
            <td>${orderItem.phone.price}$</td>
        </tr>
    </c:forEach>
    <tr>
        <td style="border: none; background-color: white"></td>
        <td style="border: none; background-color: white"></td>
        <td style="border: none; background-color: white"></td>
        <td style="border: none; background-color: white"></td>
        <td>Subtotal</td>
        <td>${order.subtotal}$</td>
    </tr>
    <tr>
        <td style="border: none; background-color: white"></td>
        <td style="border: none; background-color: white"></td>
        <td style="border: none; background-color: white"></td>
        <td style="border: none; background-color: white"></td>
        <td>Delivery</td>
        <td>${order.deliveryPrice}$</td>
    </tr>
    <tr>
        <td style="border: none; background-color: white"></td>
        <td style="border: none; background-color: white"></td>
        <td style="border: none; background-color: white"></td>
        <td style="border: none; background-color: white"></td>
        <td>Total</td>
        <td>${order.totalPrice}$</td>
    </tr>
</table>
<table>
    <tr>
        <td>First name</td>
        <td>${order.firstName}</td>
    </tr>
    <tr>
        <td>Last name</td>
        <td>${order.lastName}</td>
    </tr>
    <tr>
        <td>Address</td>
        <td>${order.deliveryAddress}</td>
    </tr>
    <tr>
        <td>Phone</td>
        <td>${order.contactPhoneNo}</td>
    </tr>
</table>
<p>${order.additionalInfo}</p>
<br/>
<a href="${contextUrl}admin/orders">
    <button>Back</button>
</a>
<c:if test="${order.status.statusId == 1}">
    <form action="${contextUrl}admin/orders/${order.id}?status=2" method="post" style="display:inline">
        <sec:csrfInput/>
        <input type="submit" value="Delivered"/>
    </form>
    <form action="${contextUrl}admin/orders/${order.id}?status=3" method="post" style="display:inline">
        <sec:csrfInput/>
        <input type="submit" value="Rejected"/>
    </form>
</c:if>
</body>
</html>
