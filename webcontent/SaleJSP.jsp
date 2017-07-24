<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.List"%>
<%@ page import="com.java.Kartenverkauf"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Startseite</title>
</head>
<body>
	<h3>Hier können Sie alle Plätze und ihren Status einsehen:</h3>


	<%!private static int soldSeats;
	private static int reservedSeats;
	private static int freeSeats;
	private static Kartenverkauf sale;
	private static String text;
	private static boolean showBegins;%>
	
	<%
		ServletContext context = getServletContext();
		sale = (Kartenverkauf) context.getAttribute("SaleObject");
		text = "";
		freeSeats = sale.getFreeSeats();
		reservedSeats = sale.getReservationSeats();
		soldSeats = sale.getSoldSeats();

		if (sale.getShowBegins() == true) {
			text = "Es sind keine Reservierungen mehr möglich da die Vorstellung bald beginnt!";
			} else if (freeSeats == 0) {
				text = "Es sind keine Reservierungen mehr möglich da alle Plätze belegt sind!";
		} else {
			text = "Es sind noch Plätze verfügbar!";
		}
	%>
	<%=text %>
	
	<br>
	<br>
	<table border='2'>
		<%=sale.getUpdateTable() %>
	</table>
	<br>
	<br>
	<table border="2">
		<tr>
			<!--  <th><%=freeSeats%>&nbsp; Freie Plätze</th>-->
			<th style='background-color: green;'><%=freeSeats%>&nbsp;Freie
				Plätze</th>
		</tr>
		<tr>
			<th style='background-color: yellow;'><%=reservedSeats%>&nbsp;Reservierte
				Plätze</th>
		</tr>
		<tr>
			<th style='background-color: red;'><%=soldSeats%>&nbsp;Verkaufte
				Plätze</th>
		</tr>
	</table>


	<br>
	<br> 1.
	<a href="verkauf_eines_freien_tickets.html">Verkauf eines freien
		Tickets</a>
	<br>
	<br> 2.
	<a href="reservierung_eines_tickets.html">Reservierung eines
		Tickets</a>
	<br>
	<br> 3.
	<a href="verkauf_eines_reservierten_tickets.html">Verkauf eines
		reservierten Tickets</a>
	<br>
	<br> 4.
	<a href="stornierung_eines_tickets.html">Stornierung eines Tickets</a>
	<br>
	<br> 5.
	<a href="reservierungen_aufheben.html">Alle Reservierungen aufheben</a>
	<br>
	<br> 6.
	<a href="status_anzeigen.html">Status eines bestimmten Platzes
		anzeigen lassen</a>
	<br>
	<br>
	7.
	<a href="reservierungen_wieder_zulassen.html">ReservierungsSperre wieder rückgängig machen</a>
	<br>
	<br>
	8.
	<a href="datenbank_loeschen.html">Datenbank löschen</a>
	


</body>
</html>