<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>${phone.model}</title>
    <jsp:include page="header.jsp"/>
    <style>
        table, th, td {
            border: 1px solid black;
            border-collapse: collapse;
            margin: 5px;
        }
        th, td {
            padding: 5px;
            text-align: left;
        }
    </style>
</head>
<body>
<a href="${contextUrl}productList" style="margin-left: 30px">
    <button>Back to product list</button>
</a>
<h2>${phone.model}</h2>
<img src="https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/${phone.imageUrl}">
<table align="right" style="margin-right: 40%; width: 20%">
    <caption>Display</caption>
    <tr>
        <td>Size</td>
        <td>${phone.displaySizeInches}"</td>
    </tr>
    <tr>
        <td>Resolution</td>
        <td>${phone.displayResolution}</td>
    </tr>
    <tr>
        <td>Technology</td>
        <td>${phone.displayTechnology}</td>
    </tr>
    <tr>
        <td>Pixel density</td>
        <td>${phone.pixelDensity}</td>
    </tr>
</table>

<table align="right" style="margin-right: 40%; width: 20%">
    <caption>Dimension & weight</caption>
    <tr>
        <td>Length</td>
        <td>${phone.lengthMm}</td>
    </tr>
    <tr>
        <td>Width</td>
        <td>${phone.widthMm}</td>
    </tr>
    <tr>
        <td>Color</td>
        <td>
            <c:forEach var="color" items="${phone.colors}">
                <c:out value="${color.code} "/>
            </c:forEach>
            <c:if test="${phone.colors.size() == 0}">
                -
            </c:if>
        </td>
    </tr>
    <tr>
        <td>Weight</td>
        <td>${phone.weightGr}</td>
    </tr>
</table>
<table align="right" style="margin-right: 40%; width: 20%">
    <caption>Camera</caption>
    <tr>
        <td>Front</td>
        <td>${phone.frontCameraMegapixels}</td>
    </tr>
    <tr>
        <td>Back</td>
        <td>${phone.backCameraMegapixels}</td>
    </tr>
</table>
<table align="right" style="margin-right: 40%; width: 20%">
    <caption>Battery</caption>
    <tr>
        <td>Talk time</td>
        <td>${phone.talkTimeHours}</td>
    </tr>
    <tr>
        <td>Stand by time</td>
        <td>${phone.standByTimeHours}</td>
    </tr>
    <tr>
        <td>Battery capacity</td>
        <td>${phone.batteryCapacityMah}</td>
    </tr>
</table>
<table align="right" style="margin-right: 40%; width: 20%">
    <caption>Other</caption>
    <tr>
        <td>Device type</td>
        <td>${phone.deviceType}</td>
    </tr>
    <tr>
        <td>Bluetooth</td>
        <td>${phone.bluetooth}</td>
    </tr>
</table>
<p>${phone.description}</p>
<p style="font-size: large; font-weight: bolder; margin-left: 20px">Price: ${phone.price}$</p>
<form style="margin-left: 20px">
    <input type="text" name="quantity"/>
    <p id="error${phone.id}" style="color: red"></p>
    <input type="hidden" name="phoneId" value="${phone.id}"/>
    <input value="Add to cart" onclick="submitForm(this.form)" type="button">
</form>
</body>
</html>
