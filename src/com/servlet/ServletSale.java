package com.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.java.Kartenverkauf;
import com.exc.KartenverkaufException;

@WebServlet("/ServletSale")
public class ServletSale extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static int allSeats;

	protected void doAnything(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,KartenverkaufException, SQLException {

		ServletContext context = getServletContext();
		Kartenverkauf sale = (Kartenverkauf) context.getAttribute("SaleObject");
		allSeats = sale.getAllSeats();

		// Freien Platz kaufen
		
		if (request.getParameter("saleFreeSeatNumber") != null) {
			if (!request.getParameter("saleFreeSeatNumber").matches("")) {
				if (!request.getParameter("saleFreeSeatNumber").matches(
						"[a-zA-Z]+")) {
					int saleFreeSeatNumber = Integer.parseInt(request
							.getParameter("saleFreeSeatNumber"));
					sale.sellFreeTicket(saleFreeSeatNumber);
					request.getRequestDispatcher(
							"operation_erfolgreich_ausgefuehrt.jsp").forward(
							request, response);
				} else {
					throw new KartenverkaufException(
							"Sie haben keine Zahl angegeben. Bitte geben Sie einen Platz an, der von 1 bis "
									+ allSeats + " geht!");
				}
			} else {
				throw new KartenverkaufException(
						"Bitte füllen Sie alle Felder aus!");
			}
		}

		// Karte reservieren

		if (request.getParameter("reservationSeatNumber") != null
				&& request.getParameter("reservationName") != null) {
			if (!request.getParameter("reservationSeatNumber").matches("")
					|| !request.getParameter("reservationName").matches("")) {
				if (!request.getParameter("reservationSeatNumber").matches(
						"[a-zA-Z]+")) {
					if (request.getParameter("reservationName").matches(
							"[a-zA-Z]+")) {
						String name = request.getParameter("reservationName");
						int seatNumber = Integer.parseInt(request
								.getParameter("reservationSeatNumber"));
						sale.reservationOfTicket(seatNumber, name);
						request.getRequestDispatcher(
								"operation_erfolgreich_ausgefuehrt.jsp")
								.forward(request, response);
					} else {
						throw new KartenverkaufException(
								"Bitte geben Sie einen gültigen Namen an!");
					}
				} else {
					throw new KartenverkaufException(
							"Sie haben keine Zahl angegeben. Bitte geben Sie einen Platz an, der von 1 bis "
									+ allSeats + " geht!");
				}
			} else {
				throw new KartenverkaufException(
						"Bitte füllen Sie alle Felder aus!");
			}
		}

		// Reservierten Platz kaufen

		if (request.getParameter("saleReservationSeatNumber") != null
				&& request.getParameter("saleReservationName") != null) {
			if (!request.getParameter("saleReservationSeatNumber").matches("")
					&& !request.getParameter("saleReservationName").matches("")) {
				if (!request.getParameter("saleReservationSeatNumber").matches(
						"[a-zA-Z]+")) {
					if (request.getParameter("saleReservationName").matches(
							"[a-zA-Z]+")) {
						String name = request
								.getParameter("saleReservationName");
						int seatNumber = Integer.parseInt(request
								.getParameter("saleReservationSeatNumber"));
						sale.sellReservation(seatNumber, name);
						request.getRequestDispatcher(
								"operation_erfolgreich_ausgefuehrt.jsp")
								.forward(request, response);
					} else {
						throw new KartenverkaufException(
								"Bitte geben Sie einen gültigen Namen an!");
					}
				} else {
					throw new KartenverkaufException(
							"Sie haben keine Zahl angegeben. Bitte geben Sie einen Platz an, der von 1 bis "
									+ allSeats + " geht!");
				}
			} else {
				throw new KartenverkaufException(
						"Bitte füllen Sie alle Felder aus!");
			}
		}

		// Reservierte oder gekaufte Karte stornieren!

		if (request.getParameter("stornoSeatNumber") != null) {
			if (!request.getParameter("stornoSeatNumber").matches("")) {
				if (!request.getParameter("stornoSeatNumber").matches(
						"[a-zA-Z]+")) {
					int stornoSeatNumber = Integer.parseInt(request
							.getParameter("stornoSeatNumber"));
					sale.stornoTicket(stornoSeatNumber);
					request.getRequestDispatcher(
							"operation_erfolgreich_ausgefuehrt.jsp").forward(
							request, response);
				} else {
					throw new KartenverkaufException(
							"Sie haben keine Zahl angegeben. Bitte geben Sie einen Platz an, der von 1 bis "
									+ allSeats + " geht!");
				}
			} else {
				request.getRequestDispatcher("error.jsp").forward(request,
						response);
				throw new KartenverkaufException(
						"Bitte füllen Sie alle Felder aus!");
			}
		}

		// Status anzeigen lassen

		if (request.getParameter("statusSeatNumber") != null) {
			if (!request.getParameter("statusSeatNumber").matches("")) {
				if (!request.getParameter("statusSeatNumber").matches(
						"[a-zA-Z]+")) {
					int statusSeatNumber = Integer.parseInt(request
							.getParameter("statusSeatNumber"));
					sale.getStatus(statusSeatNumber);
					request.getRequestDispatcher(
							"operation_erfolgreich_ausgefuehrt.jsp").forward(
							request, response);
				} else {
					throw new KartenverkaufException(
							"Sie haben keine Zahl angegeben. Bitte geben Sie einen Platz an, der von 1 bis "
									+ allSeats + " geht!");
				}
			} else {
				throw new KartenverkaufException(
						"Bitte füllen Sie alle Felder aus!");
			}
		}

		// Show beginnt!

		if (request.getParameter("deleteReservations") != null) {
			if (!request.getParameter("deleteReservations").matches("")) {
				if (request.getParameter("deleteReservations").matches("JA")
						|| request.getParameter("deleteReservations").matches(
								"ja")
						|| request.getParameter("deleteReservations").matches(
								"JA")
						|| request.getParameter("deleteReservations").matches(
								"Ja")) {
					sale.preventReservation();
					request.getRequestDispatcher(
							"operation_erfolgreich_ausgefuehrt.jsp").forward(
							request, response);
				} else {
					throw new KartenverkaufException(
							"Fehler! Geben Sie bitte Ja ein wenn Sie um den Vorgang abzuschließen oder lassen Sie es bleiben!");
				}
			} else {
				throw new KartenverkaufException(
						"Bitte füllen Sie alle Felder aus!");
			}
		}
		
		
		// ReservierungsSperre wieder aufheben!

		if (request.getParameter("redoReservations") != null) {
			if (!request.getParameter("redoReservations").matches("")) {
				if (request.getParameter("redoReservations").matches("JA")
						|| request.getParameter("redoReservations").matches(
								"ja")
						|| request.getParameter("redoReservations").matches(
								"JA")
						|| request.getParameter("redoReservations").matches(
								"Ja")) {
					sale.redoReservationBlock();
					request.getRequestDispatcher(
							"operation_erfolgreich_ausgefuehrt.jsp").forward(
							request, response);
				} else {
					throw new KartenverkaufException(
							"Fehler! Geben Sie bitte Ja ein wenn Sie um den Vorgang abzuschließen oder lassen Sie es bleiben!");
				}
			} else {
				throw new KartenverkaufException(
						"Bitte füllen Sie alle Felder aus!");
			}
		}
		
		// Datenbank erneuern

				if (request.getParameter("databaseDelete") != null) {
					if (!request.getParameter("databaseDelete").matches("")) {
						if (request.getParameter("databaseDelete").matches("JA")
								|| request.getParameter("databaseDelete").matches(
										"ja")
								|| request.getParameter("databaseDelete").matches(
										"JA")
								|| request.getParameter("databaseDelete").matches(
										"Ja")) {
							sale.deleteDatabase();
							sale.setSeats(allSeats);
							sale.setShowBegins();
							sale.setStatusOfSeatTypes();
							request.getRequestDispatcher(
									"operation_erfolgreich_ausgefuehrt.jsp").forward(
									request, response);
						} else {
							throw new KartenverkaufException(
									"Fehler! Geben Sie bitte Ja ein wenn Sie um den Vorgang abzuschließen oder lassen Sie es bleiben!");
						}
					} else {
						throw new KartenverkaufException(
								"Bitte füllen Sie alle Felder aus!");
					}
				}
			}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,KartenverkaufException {
			try {
				doAnything(request, response);
			} catch (Exception e) {
				request.setAttribute("exception", e);
				request.getRequestDispatcher(
						"/error.jsp")
						.forward(request, response);
			}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,KartenverkaufException {
		try {
			doAnything(request, response);
		} catch (Exception e) {
			request.setAttribute("exception", e);
			request.getRequestDispatcher(
					"/error.jsp")
					.forward(request, response);
		}
	}

}
