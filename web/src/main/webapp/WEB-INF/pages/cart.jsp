<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Cart</title>
    <jsp:include page="header.jsp"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/style.css"/>">
    <script language="javascript" type="text/javascript" src="<c:url value="/resources/js/setFormMethod.js"/> "></script>
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
        <th>Price</th>
        <th>Quantity</th>
        <th>Action</th>
    </tr>
    </thead>
    <c:if test="${orderErrorMsg != null}">
        <script>
            var msg = "${orderErrorMsg}";
            alert(msg);
        </script>
    </c:if>
    <form:form action="${contextUrl}cart/update" modelAttribute="cartUpdateForm">
    <input type="hidden" id="meth" name="_method">
    <c:forEach var="phoneEntry" items="${phoneEntrySet}" varStatus="loop">
        <tr>
            <td>${phoneEntry.key.brand}</td>
            <td>${phoneEntry.key.model}</td>
            <td>
                <c:forEach var="color" items="${phoneEntry.key.colors}">
                    <c:out value="${color.code} " />
                </c:forEach>
            </td>
            <td>${phoneEntry.key.displaySizeInches}"</td>
            <td>${phoneEntry.key.price}$</td>
            <td>
                <form:input path="items[${loop.index}].quantity"/>
                <br>
                <form:errors path="items[${loop.index}].quantity" style="color:red" />
                <form:hidden path="items[${loop.index}].phoneId"/>
            </td>
            <td>
                <form:button name="delete" value="${phoneEntry.key.id}" onclick="setDelete()">Delete</form:button>
            </td>
        </tr>
    </c:forEach>
</table>
<script>

</script>
<div align="right" style="margin-right: 7%">
    <c:if test="${phoneEntrySet.size() > 0}">
        <input type="submit" name="update" value = "Update" onclick="setPut()"/>
    </c:if>
    </form:form>
    <c:if test="${phoneEntrySet.size() > 0}" >
        <form action="${contextUrl}order" style="float: right;margin-left: 5px">
            <input type="submit" value="Order"/>
        </form>
    </c:if>
</div>
</form>
</body>
</html>
