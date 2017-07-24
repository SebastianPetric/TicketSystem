<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" isErrorPage="false"%>
<%@ page import="java.util.List"%>
<%@ page import="com.java.Kartenverkauf"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Operation erfolgreich!</title>
</head>
<body>
	<h3>Operation war erfolgreich!</h3>
	<p>
	<%
		ServletContext context = getServletContext();
		Kartenverkauf sale = (Kartenverkauf) context.getAttribute("SaleObject");
	%>
	<%=sale.getSuccessMessage()%>
	
	</p>
	<br>
	<a href="SaleJSP.jsp">Zurück zur Hauptseite</a>
</body>
</html>