<%@page import="com.exc.KartenverkaufException"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" isErrorPage="true"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Error Page</title>
</head>
<body>
	<h3>Bei der Operation ist leider folgender Fehler aufgetreten:</h3>
	
	<%
	KartenverkaufException e = (KartenverkaufException)request.getAttribute("exception");
	%>
	<%=  e.getMessage() %>
	
	<%
	//if (exception instanceof KartenverkaufException)
	%>
	
	<br>
	<br>
	<a href="SaleJSP.jsp">Zurück zur Hauptseite</a>
</body>
</html>