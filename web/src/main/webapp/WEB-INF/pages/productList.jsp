<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
  <title>Phone shop</title>
  <style>
    .tbl {
      width: 100%;
      border: none;
      margin-bottom: 20px;
    }
    .tbl thead th {
      font-weight: bold;
      text-align: left;
      border: none;
      padding: 10px 15px;
      background: #d8d8d8;
      font-size: 14px;
      border-left: 1px solid #ddd;
      border-right: 1px solid #ddd;
    }

    .tbl tbody td {
      text-align: left;
      border-left: 1px solid #ddd;
      border-right: 1px solid #ddd;
      padding: 10px 15px;
      font-size: 14px;
      vertical-align: top;
    }
    .tbl thead tr th:first-child, .table tbody tr td:first-child {
      border-left: none;
    }
    .tbl thead tr th:last-child, .table tbody tr td:last-child {
      border-right: none;
    }
    .tbl tbody tr:nth-child(even){
      background: #f3f3f3;
    }

    .pages {
      width: 30%;
      margin-left: 30%;
      margin-right: 30%;
      border: 1px;
      margin-bottom: 20px;
    }

    .pages tbody td {
      text-align: left;
      border: 1px solid #ddd;
      padding: 10px 15px;
      font-size: 14px;
      vertical-align: top;
    }
  </style>
</head>

<body>
<img src="https://cdn.iconscout.com/icon/free/png-256/smartphone-1703329-1446727.png" height="40px"/>
<b style="font-size: 40px; font-weight: bolder; margin-left: 5px; font-family: 'Calibri Light'">Phonify</b>
<form method="get">
  <c:if test="${sessionScope.containsKey(\"itemsAmount\")}">
    <input type="submit" style="float: right; margin-right: 10px" value="My cart: ${itemsAmount} items ${overallPrice}$" />
  </c:if>
  <c:if test="${sessionScope.containsKey(\"itemsAmount\") == false}" >
    <input type="submit" style="float: right; margin-right: 10px" value="My cart: 0 items 0$" />
  </c:if>
</form>
<br/>
<hr/>
<b align="left" style="font-size: 30px; margin-left: 20px">Phones</b>
<form method="get">
  <input style="margin-left: 50px" type="text" name="search"/>
  <input type="submit" value="Search">
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
        <img src="https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/${phone.imageUrl}">
      </td>
      <td>${phone.brand}</td>
      <td>${phone.model}</td>
      <td>
        <c:forEach var="color" items="${phone.colors}">
          <c:out value=" ${color.code}"/>
          <br/>
        </c:forEach>
      </td>
      <td>${phone.displaySizeInches}"</td>
      <td>$ ${phone.price}</td>
      <form method="post" action="${contextUrl}ajaxCart">
        <td>
          <input type="text" name="quantity" value="1"/>

          <c:if test="${phone.id == errorId}">
            <p style="color: red">${error}</p>
            ${sessionScope.remove("error")}
            ${sessionScope.remove("errorId")}
          </c:if>
        </td>
        <td>
          <input type="hidden" name="phoneId" value="${phone.id}"/>
          <input type="hidden" name="page" value="${page}" />
          <input type="hidden" name="order" value="${order}" />
          <input type="hidden" name="search" value="${search}" />
          <input type="submit" class="btn-class" value="Add to" />
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