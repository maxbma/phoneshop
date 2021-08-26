<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Error page</title>
    <jsp:include page="header.jsp"/>
</head>
<body>
<p>Oops, something went wrong...</p>
<p style="font-size: large">${error}</p>
<a href="${contextUrl}productList">Return to product list</a>
</body>
</html>
