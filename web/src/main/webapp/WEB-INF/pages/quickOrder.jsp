<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <jsp:include page="header.jsp"/>
</head>
<body>
<table>
    <thead>
    <th>Product model</th>
    <th>Quantity</th>
    </thead>
    <form:form action="${contextUrl}quickOrder/add2cart" modelAttribute="orderModel">
        <c:forEach var="quickOrder" items="${orderModel.quickOrderItems}" varStatus="loop">
            <tr>
                <td>
                    <form:input path="quickOrderItems[${loop.index}].phoneModel"/>
                    <br/>
                    <form:errors path="quickOrderItems[${loop.index}].phoneModel" style="color:red" />
                </td>
                <td>
                    <form:input path="quickOrderItems[${loop.index}].quantity"/>
                    <br/>
                    <form:errors path="quickOrderItems[${loop.index}].quantity" style="color:red" />
                </td>
            </tr>
        </c:forEach>
    </table>
<input type="submit" value="Add 2 cart"/>
</form:form>
</body>
</html>
