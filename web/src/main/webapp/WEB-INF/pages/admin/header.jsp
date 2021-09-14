<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<sec:csrfMetaTags />

<c:url value="/j_spring_security_logout" var="logoutUrl" />
<c:url var="contextUrl" value = "/" scope="request"/>
<meta charset="UTF-8">
<img src="https://cdn.iconscout.com/icon/free/png-256/smartphone-1703329-1446727.png" height="40px"/>
<b style="font-size: 40px; font-weight: bolder; margin-left: 5px; font-family: 'Calibri Light'">Phonify</b>
<p style="margin-right: 5%; font-size: 20px; float: right">
    <b><sec:authentication property="principal.username"/></b>
<sec:authorize access="hasRole('ROLE_ADMIN')">
    <form action="${logoutUrl}" method="post">
        <input value="Logout" type="submit"/>
        <sec:csrfInput/>
    </form>
</sec:authorize>
</p>