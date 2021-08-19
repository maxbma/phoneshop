<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
  <title>Phone shop</title>
  <jsp:include page="header.jsp" />
  <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/style.css" />">
</head>

<body>
<b align="left" style="font-size: 30px; margin-left: 20px">Phones</b>
<form method="get">
  <input style="margin-left: 50px" type="text" name="search"/>
  <input type="submit" value="Search">
  <c:if test="${search != null}">
    <a href="${contextUrl}productList">
      <button type="button">Reset search</button>
    </a>
  </c:if>
</form>
<p>
<table class="tbl">
  <thead>
  <tr>
    <th width="235">Image</th>
    <th>Brand
      <b style="float: right">
        <a href="?order=brand+asc&search=${search}"> ↑ </a>
        <b>	&nbsp;</b>
        <a href="?order=brand+desc&search=${search}"> ↓ </a>
      </b>
    </th>
    <th>Model
      <b style="float: right">
        <a href="?order=model+asc&search=${search}"> ↑ </a>
        <b>	&nbsp;</b>
        <a href="?order=model+desc&search=${search}"> ↓ </a>
      </b>
    </th>
    <th>Color</th>
    <th>Display size
      <b style="float: right">
        <a href="?order=displaySizeInches+asc&search=${search}"> ↑ </a>
        <b>	&nbsp;</b>
        <a href="?order=displaySizeInches+desc&search=${search}"> ↓ </a>
      </b>
    </th>
    <th>Price
      <b style="float: right">
        <a href="?order=price+asc&search=${search}"> ↑ </a>
        <b>	&nbsp;</b>
        <a href="?order=price+desc&search=${search}"> ↓ </a>
      </b>
    </th>
    <th>Quantity</th>
    <th>Action</th>
  </tr>
  </thead>
  <c:forEach var="phone" items="${phones}">
    <tr>
      <td>
          <a href="${contextUrl}productDetails/${phone.id}">
              <img src="https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/${phone.imageUrl}">
          </a>
      </td>
      <td>${phone.brand}</td>
      <td>
          <a href="${contextUrl}productDetails/${phone.id}">${phone.model}</a>
      </td>
      <td>
        <c:forEach var="color" items="${phone.colors}">
          <c:out value=" ${color.code}"/>
          <br/>
        </c:forEach>
      </td>
      <td>${phone.displaySizeInches}"</td>
      <td>$ ${phone.price}</td>
      <form>
        <td>
          <input type="text" name="quantity" value="1"/>
          <p id="error${phone.id}" style="color: red"></p>
        </td>
        <td>
          <input type="hidden" name="phoneId" value="${phone.id}"/>
          <input value="Add to cart" onclick="submitForm(this.form)" type="button">
        </td>
      </form>
    </tr>
  </c:forEach>
</table>
</p>
<table class="pages">
  <tr>
    <td> <a href="?page=1&order=${order}&search=${search}" style="text-decoration: none; color: black"><<</a> </td>
    <c:forEach var="pageNum" items="${pagesNumeration}">
      <c:if test="${pageNum != page}">
        <td>
          <a href="?page=${pageNum}&order=${order}&search=${search}" style="text-decoration: none; color: black">${pageNum}</a>
        </td>
      </c:if>

      <c:if test="${pageNum == page}">
        <td style="background-color: #0000ff;">
          <a href="?page=${pageNum}&order=${order}&search=${search}" style="color: white; text-decoration: none">${pageNum}</a>
        </td>
      </c:if>
    </c:forEach>
    <td> <a href="?page=${total}&order=${order}&search=${search}" style="text-decoration: none; color: black"> >> </a> </td>
  </tr>
</table>
<br/>
</body>
</html>