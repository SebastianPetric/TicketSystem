package com.java;

import java.sql.*;

import javax.sql.DataSource;

import com.exc.KartenverkaufException;

public class Kartenverkauf {

	private static int allSeats;
	private static boolean showBegins;
	private static int freeSeats;
	private static int reservationSeats;
	private static int soldSeats;
	private final String soldSeatsCSS = "style='background-color:red;'";
	private final String reservationSeatsCSS = "style=' background-color:yellow;'";
	private final String freeSeatsCSS = "style=' background-color:green;'";
	public String successMessage;
	public DataSource datasource = null;

	public Kartenverkauf(int all, DataSource data) throws SQLException {		
		this.allSeats = all;
		this.datasource = data;
		setSeats(allSeats);
		setShowBegins();
		setStatusOfSeatTypes();
	}

	// Sitze setzen
	public synchronized void setSeats(int allSeats) throws SQLException {		
		Connection con = null;
		try{
		con = this.datasource.getConnection();
		Statement statement = null;
		PreparedStatement pstate=con.prepareStatement("INSERT INTO sitzplatz VALUES(?,'','frei')");
		PreparedStatement pstate1=con.prepareStatement("SELECT COUNT(*) FROM sitzplatz");
		ResultSet resSet = null;
		
		statement = con.createStatement();
		resSet=pstate1.executeQuery();
		resSet.first();
		int count = resSet.getInt(1);
		if (count == 0) {
			for (int i = 1; i <= allSeats; i++) {
				//(1,i)--> Setze in das erste Fragezeichen i ein
				pstate.setInt(1, i);
				pstate.execute();
				}
		} else {
		}
	}finally{
		try{
		con.close();
		}catch (KartenverkaufException e) {
			throw new KartenverkaufException(
					"Upps! Da ist ein Fehler auf der Datenbank aufgetreten! Versuchen Sie es später noch einmal");
	}
	}
	}
	
	//ShowBeginnt setzen
	
	public synchronized void setShowBegins() throws SQLException{	
		Connection con=null;
		try{
		con = this.datasource.getConnection();
		Statement statement = null;
		PreparedStatement pstate=con.prepareStatement("SELECT COUNT(*) FROM showbegins");
		PreparedStatement pstate1=con.prepareStatement("INSERT INTO showbegins VALUES('false')");
		ResultSet resSet = null;

		statement = con.createStatement();
		resSet=pstate.executeQuery();
		resSet.first();
		int count = resSet.getInt(1);
		if(count==0){
			pstate1.execute();	
		}
		}finally{
			try{
			con.close();
			}catch (SQLException e) {
				System.out.println("Schwerwiegender Datenbankfehler! Versuchen Sie es Später noch einmal!");
			}
		}
	}
	
//Status der Sitztypen setzen
	
	public synchronized void setStatusOfSeatTypes() throws SQLException{
		Connection con=null;
		
		try{
		con=this.datasource.getConnection();
		Statement statement= con.createStatement();
		PreparedStatement pstate= con.prepareStatement("INSERT INTO statusderplätze VALUES(?,0,0)");
		PreparedStatement pstate1=con.prepareStatement("SELECT COUNT(*) FROM statusderplätze");
		PreparedStatement pstate2=con.prepareStatement("SELECT * FROM statusderplätze");
		ResultSet resSet= null;
		
		resSet=pstate1.executeQuery();
		resSet.first();
		int count= resSet.getInt(1);
		if(count==0){
					pstate.setInt(1, allSeats);
					pstate.execute();
		}else{
			resSet=pstate2.executeQuery();
			resSet.first();
			int counter=0;
			
			while(resSet.next()){
				this.freeSeats= resSet.getInt("freiePlätze");
				this.reservationSeats= resSet.getInt("reserviertePlätze");
				this.soldSeats= resSet.getInt("verkauftePlätze");	
			}
			counter++;
		}
		}finally{
			try{
			con.close();
			}catch (SQLException e) {
				System.out.println("Schwerwiegender Datenbankfehler! Versuchen Sie es Später noch einmal!");
			}
		}
	}
	

	// Freien Platz kaufen
	public synchronized void sellFreeTicket(int seatNumber) throws SQLException {
		boolean isFree=false;
		boolean isReserved=false;
		boolean isSold=false;
		Connection con=null;
		
		try{
		con= this.datasource.getConnection();
		Statement statement=con.createStatement();
		PreparedStatement pstate=con.prepareStatement("SELECT * FROM sitzplatz WHERE id=?");
		PreparedStatement pstate1=con.prepareStatement("UPDATE statusderplätze SET freiePlätze=?, reserviertePlätze=?,verkauftePlätze=?");
		PreparedStatement pstate2= con.prepareStatement("UPDATE sitzplatz SET status='verkauft' WHERE id=?");
		PreparedStatement pstate3=con.prepareStatement("SELECT * FROM statusderplätze");
		ResultSet resSet=null;
		String status=null;
		
			if (seatNumber > 0 && seatNumber <= allSeats) {
				pstate.setInt(1, seatNumber);
				resSet=pstate.executeQuery();
				resSet.first();
			String zustand = resSet.getString(3);
			status=new String(zustand);
			if(status.equals("frei")){
				isFree = true;
			}else if(status.equals("reserviert")){
				isReserved=true;
			}else if(status.equals("verkauft")){
				isSold=true;
			}
			if (!isFree) {
				if (isReserved) {
					throw new KartenverkaufException(
							"Fehler! Platz "
									+ seatNumber
									+ " ist schon reserviert und steht nicht mehr zum allgemeinen Verkauf zur Verfügung! Verfizieren Sie die Person!");
				} else if (isSold) {
					throw new KartenverkaufException(
							"Fehler! Der Sitzplatz ist schon verkauft und steht nicht mehr zum Verkauf zur Verfügung!");
				}
			} else {
				resSet=pstate3.executeQuery();
				resSet.first();
				freeSeats=resSet.getInt(1)-1;
				soldSeats=resSet.getInt(3)+1;
				reservationSeats=resSet.getInt(2);
				pstate1.setInt(1, freeSeats);
				pstate1.setInt(2, reservationSeats);
				pstate1.setInt(3, soldSeats);
				pstate1.execute();
				pstate2.setInt(1, seatNumber);
				pstate2.execute();
				successMessage = "Platz " + seatNumber + " verkauft!";
			}
		} else {
			throw new KartenverkaufException("Fehler! Sitzplatz " + seatNumber
					+ " existiert nicht! Der Platz muss zwischen 1 und "
					+ allSeats + " liegen! Versuchen Sie es noch einmal!");
		}
	}finally{
		try{
		con.close();
		}catch (SQLException e) {
			System.out.println("Schwerwiegender Datenbankfehler! Versuchen Sie es Später noch einmal!");
		}
	}
	}
	

	// Platz reservieren
	public synchronized void reservationOfTicket(int seatNumber, String name) throws SQLException {
		boolean isFree=false;
		boolean isReserved=false ;
		boolean isSold=false;
		boolean showBegins=false;
		Connection con =null;
		
		try{
		con=this.datasource.getConnection();
		Statement statement=con.createStatement();
		PreparedStatement pstate=con.prepareStatement("SELECT * FROM sitzplatz WHERE id=?");
		PreparedStatement pstate1=con.prepareStatement("UPDATE statusderplätze SET freiePlätze=?, reserviertePlätze=?,verkauftePlätze=?");
		PreparedStatement pstate2=con.prepareStatement("UPDATE sitzplatz SET status='reserviert', name=? WHERE id=?");
		PreparedStatement pstate3=con.prepareStatement("SELECT * FROM showbegins");
		PreparedStatement pstate4=con.prepareStatement("SELECT * FROM statusderplätze");
		ResultSet resSet=null;
		if (seatNumber > 0 && seatNumber <= allSeats) {
			pstate.setInt(1, seatNumber);
			//resSet=statement.executeQuery("SELECT * FROM sitzplatz WHERE id="+seatNumber+"");
			resSet= pstate.executeQuery();
			resSet.first();
			String status=resSet.getString(3);
			if(status.equals("frei")){
				isFree=true;
			}else if(status.equals("reserviert")){
				isReserved=true;
			}else if(status.equals("verkauft")){
				isSold=true;
			}
			resSet=pstate3.executeQuery();
			//resSet=statement.executeQuery("SELECT * FROM showbegins");
			resSet.first();
			String isBeginning=resSet.getString(1);
			if(isBeginning.equals("true")){
				showBegins=true;
			}else showBegins=false;
			if (!showBegins) {
				if (!isFree) {
					if (isReserved) {
						pstate.setInt(1, seatNumber);
						//resSet =statement.executeQuery("SELECT * FROM sitzplatz WHERE id="+seatNumber+"");
						resSet=pstate.executeQuery();
						resSet.first();						
						String personWhoReserved=resSet.getString(2);		
						throw new KartenverkaufException("Fehler! Sitzplatz "
								+ seatNumber + "  wurde schon von "
								+ personWhoReserved
								+ " reserviert und steht deshalb für " + name
								+ " nicht mehr zur Reservierung zur Verfügung!");
					} else if (isSold) {
						throw new KartenverkaufException(
								"Fehler! Sitzplatz "
										+ seatNumber
										+ "  ist schon verkauft und steht nicht mehr zur Reservierung zur Verfügung!");
					} else {
						throw new KartenverkaufException(
								"Es tut uns Leid! Es ist ein Fehler unterlaufen! Versuchen Sie es noch einmal!");
					}
				} else {
					resSet=pstate4.executeQuery();
					//resSet=statement.executeQuery("SELECT * FROM statusderplätze");
					resSet.first();
					freeSeats=resSet.getInt(1)-1;
					soldSeats=resSet.getInt(3);
					reservationSeats=resSet.getInt(2)+1;
					con.setAutoCommit(false);
					pstate1.setInt(1, freeSeats);
					pstate1.setInt(2, reservationSeats);
					pstate1.setInt(3, soldSeats);
					pstate1.execute();
					//statement.executeUpdate("UPDATE statusderplätze SET freiePlätze="+freeSeats+", reserviertePlätze="+reservationSeats+",verkauftePlätze="+soldSeats+"");
					pstate2.setString(1, name);
					pstate2.setInt(2, seatNumber);
					pstate2.execute();
					//statement.executeUpdate("UPDATE sitzplatz SET status='reserviert', name='"+name+"' WHERE id="+seatNumber+"");
					con.commit();
					
					successMessage = "Alles geklappt! Der Platz " + seatNumber
							+ " wurde auf den Namen " + name + " reserviert!";
				}
			} else {
				throw new KartenverkaufException(
						"Diese Option wurde abgeschaltet! Es sind keine Reservierungen mehr möglich, da die Show in einer halben Stunde beginnt!");
			}
		} else {
			throw new KartenverkaufException("Fehler! Sitzplatz " + seatNumber
					+ "  existiert nicht! Der Platz muss zwischen 1 und "
					+ getAllSeats() + " liegen! Versuchen Sie es noch einmal!");
		}
		}finally{
			try{
			con.close();
			}catch(SQLException e){
				System.out.println("Schwerwiegender Datenbankfehler! Versuchen Sie es Später noch einmal!");
			}
		}	
	}

	
	// reservierten Platz kaufen
	public synchronized void sellReservation(int seatNumber, String name) throws SQLException {
		boolean isFree=false;
		boolean isReserved=false ;
		boolean isSold=false;
		boolean showBegins=false;
		Connection con=null;
		try{
		con=this.datasource.getConnection();
		Statement statement=con.createStatement();
		PreparedStatement pstate=con.prepareStatement("SELECT * FROM sitzplatz WHERE id=?");
		PreparedStatement pstate1=con.prepareStatement("UPDATE statusderplätze SET freiePlätze=?, reserviertePlätze=?,verkauftePlätze=?");
		PreparedStatement pstate2=con.prepareStatement("UPDATE sitzplatz SET status='verkauft', name='' WHERE id=?");		
		PreparedStatement pstate3=con.prepareStatement("SELECT * FROM showbegins");
		PreparedStatement pstate4=con.prepareStatement("SELECT * FROM statusderplätze");
		ResultSet resSet=null;
		
		resSet=pstate3.executeQuery();
		resSet.first();
		String isBeginning=resSet.getString(1);
		if(isBeginning.equals("true")){
			showBegins=true;
		}else showBegins=false;

		pstate.setInt(1, seatNumber);
		resSet=pstate.executeQuery();
		resSet.first();
		String status=resSet.getString(3);
		if(status.equals("frei")){
			isFree=true;
		}else if(status.equals("reserviert")){
			isReserved=true;
		}else if(status.equals("verkauft")){
			isSold=true;
		}
		pstate.setInt(1, seatNumber);
		resSet=pstate.executeQuery();
		resSet.first();
		String nameSitz=resSet.getString(2);
		if (seatNumber > 0 && seatNumber <= allSeats) {
			if (!showBegins) {
					if (isReserved
						&& nameSitz.equals(name)) {
					resSet=pstate4.executeQuery();
					resSet.first();
					freeSeats=resSet.getInt(1);
					soldSeats=resSet.getInt(3)+1;
					reservationSeats=resSet.getInt(2)-1;
					con.setAutoCommit(false);
					pstate1.setInt(1, freeSeats);
					pstate1.setInt(2, reservationSeats);
					pstate1.setInt(3, soldSeats);
					pstate1.execute();
					pstate2.setInt(1, seatNumber);
					pstate2.execute();
					con.commit();
					successMessage = "Glückwunsch! Der reservierte Platz "
							+ seatNumber + " wurde erfolgreich an " + name
							+ " verkauft!";
				} else if (isReserved
						&& !nameSitz
								.equals(name)) {
						throw new KartenverkaufException(
							"Beim Platz "
									+ seatNumber
									+ "  handelt sich um einen reservierten Platz von: "
									+ nameSitz
									+ " und kann deswegen nicht an " + name
									+ " verkauft werden!");
				} else if (isFree) {
					throw new KartenverkaufException(
							"Beim Platz "
									+ seatNumber
									+ "  handelt sich um einen freien Platz und nicht um einen reservierten Platz! Deswegen kann er nicht an "
									+ name + " verkauft werden!");
				} else if (isSold) {
					throw new KartenverkaufException(
							"Beim Platz "
									+ seatNumber
									+ "  handelt sich um einen schon verkauften Platz und nicht um einen reservierten Platz! Deswegen kann die Operation nicht durchgeführt werden!");
				}
			} else {
				throw new KartenverkaufException(
						"Diese Option wurde abgeschaltet! Es können keine reservierten Karten mehr gekauft werden, da die Show in einer halben Stunde beginnt und alle Reservierungen entfernt wurden!");
			}

		} else {
			throw new KartenverkaufException("Fehler! Sitzplatz " + seatNumber
					+ "  existiert nicht! Der Platz muss zwischen 1 und "
					+ getAllSeats() + " liegen! Versuchen Sie es noch einmal!");
		}
		}finally{
			try{
			con.close();
			}catch (SQLException e) {
				System.out.println("Schwerwiegender Datenbankfehler! Versuchen Sie es Später noch einmal!");
			}
		}
	}

	
	// Reservierten oder gekauften Platz stornieren
	public synchronized void stornoTicket(int seatNumber) throws SQLException {
		boolean isFree=false;
		boolean isReserved=false ;
		boolean isSold=false;
		Connection con=null;
		
		try{
		con=this.datasource.getConnection();
		Statement statement=con.createStatement();
		PreparedStatement pstate=con.prepareStatement("SELECT * FROM sitzplatz WHERE id=?");
		PreparedStatement pstate1=con.prepareStatement("UPDATE statusderplätze SET freiePlätze=?, reserviertePlätze=?,verkauftePlätze=?");
		PreparedStatement pstate2=con.prepareStatement("UPDATE sitzplatz SET status='frei', name='' WHERE id=?");		
		PreparedStatement pstate3=con.prepareStatement("SELECT * FROM statusderplätze");
		PreparedStatement pstate4=con.prepareStatement("SELECT * FROM statusderplätze");
		ResultSet resSet=null;
		
		pstate.setInt(1, seatNumber);
		resSet=pstate.executeQuery();
		resSet.first();
		String status=resSet.getString(3);
		if(status.equals("frei")){
			isFree=true;
		}else if(status.equals("reserviert")){
			isReserved=true;
		}else if(status.equals("verkauft")){
			isSold=true;
		}
		pstate.setInt(1, seatNumber);
		resSet=pstate.executeQuery();
		resSet.first();
		String nameSitz=resSet.getString(2);
		if (seatNumber > 0 && seatNumber <= allSeats) {
			if (!isFree) {
				if (isReserved) {
					resSet=pstate3.executeQuery();
					resSet.first();
					freeSeats=resSet.getInt(1)+1;
					soldSeats=resSet.getInt(3);
					reservationSeats=resSet.getInt(2)-1;
					
					con.setAutoCommit(false);
					pstate1.setInt(1, freeSeats);
					pstate1.setInt(2, reservationSeats);
					pstate1.setInt(3, soldSeats);
					pstate1.execute();
					pstate2.setInt(1, seatNumber);
					pstate2.execute();
					con.commit();
					
					successMessage = "Sie haben einen reservierten Platz"
							+ seatNumber + " erfolgreich storniert!";
				} else if (isSold) {
					
					resSet=pstate4.executeQuery();
					resSet.first();
					freeSeats=resSet.getInt(1)+1;
					soldSeats=resSet.getInt(3)-1;
					reservationSeats=resSet.getInt(2);
					
					con.setAutoCommit(false);
					pstate1.setInt(1, freeSeats);
					pstate1.setInt(2, reservationSeats);
					pstate1.setInt(3, soldSeats);
					pstate1.execute();
					pstate2.setInt(1, seatNumber);
					pstate2.execute();
					con.commit();
			
					successMessage = "Sie haben einen verkauften Platz "
							+ seatNumber + " erfolgreich storniert!";
				} else {
					throw new KartenverkaufException(
							"Fehler! Upps da ist ein Fehler bei der Stornierungen aufgetreten! Versuchen Sie es noch einmal! ");
				}

			} else {
				throw new KartenverkaufException(
						"Fehler! Beim Platz "
								+ seatNumber
								+ " handelt sich um einen freien Platz! Dieser kann nicht storniert werden!");
			}
			} else {
			throw new KartenverkaufException("Fehler! Sitzplatz " + seatNumber
					+ "  existiert nicht! Der Platz muss zwischen 1 und "
					+ getAllSeats() + " liegen! Versuchen Sie es noch einmal!");
		}
		}finally{
			try{
			con.close();
			}catch (SQLException e) {
				System.out.println("Schwerwiegender Datenbankfehler! Versuchen Sie es Später noch einmal!");
			}
		}
	}

	
	// Status eines Platzes
	public synchronized void getStatus(int seatNumber) throws SQLException {
		Connection con=null;
		
		try{
		con=this.datasource.getConnection();
		Statement statement=con.createStatement();
		PreparedStatement pstate=con.prepareStatement("SELECT * FROM sitzplatz WHERE id=?");
		ResultSet resSet=null;
		
		pstate.setInt(1, seatNumber);
		resSet=pstate.executeQuery();
		resSet.first();
		String name=resSet.getString(2);
		String status=resSet.getString(3);
		if (seatNumber > 0 && seatNumber <= allSeats) {

			successMessage = "<b>Status:</b><br><br>Platz: " + seatNumber
					+ "<br>" + "Name: "
					+ name + "<br>"
					+ "Status: " + status;
		} else {
			throw new KartenverkaufException("Fehler! Sitzplatz " + seatNumber
					+ "  existiert nicht! Der Platz muss zwischen 1 und "
					+ getAllSeats() + " liegen! Versuchen Sie es noch einmal!");
		}
		}finally{
			try{
			con.close();
			}catch (SQLException e) {
				System.out.println("Schwerwiegender Datenbankfehler! Versuchen Sie es Später noch einmal!");
			}
		}
	}
	
	
	// Gibt immer den Aktuellen stand der Tabelle wider!

	public synchronized String getUpdateTable() throws SQLException {
		Connection con = null;
		
		try{
		con=this.datasource.getConnection();
		Statement statement=con.createStatement();
		PreparedStatement pstate=con.prepareStatement("SELECT * FROM sitzplatz WHERE id=?");
		ResultSet resSet=null;
		
		try {
			String updatedTable = "";
			int showIndex = 1;
			int x = 0;
			int y = 0;

			do {
				updatedTable += "<tr>";
				do {
					if (x < (int) Math.sqrt(allSeats)) {
						pstate.setInt(1, showIndex);
						resSet=pstate.executeQuery();
						resSet.first();
						String status=resSet.getString(3);
						if (status.equals("frei")) {
							updatedTable += "<th " + freeSeatsCSS + ">"
									+ showIndex + "</th>";
							x++;
							showIndex++;
						}
					}
					if (x < (int) Math.sqrt(allSeats)) {
						pstate.setInt(1, showIndex);
						resSet=pstate.executeQuery();
						resSet.first();
						String status=resSet.getString(3);
						if (status.equals("reserviert")) {
							updatedTable += "<th " + reservationSeatsCSS + ">"
									+ showIndex + "</th>";
							showIndex++;
							x++;
						}
					}
					if (x < (int) Math.sqrt(allSeats)) {
						pstate.setInt(1, showIndex);
						resSet=pstate.executeQuery();
						resSet.first();
						String status=resSet.getString(3);
						if (status.equals("verkauft")) {
							updatedTable += "<th " + soldSeatsCSS + ">"
									+ showIndex + "</th>";
							showIndex++;
							x++;
						}
					}
				} while (x < (int) Math.sqrt(allSeats));
				x = 0;
				updatedTable += "</tr>";
				y++;
			} while (y < (int) Math.sqrt(allSeats));
			con.close();
			return updatedTable;
		} catch (KartenverkaufException e) {
			throw new KartenverkaufException(
					"Upps da ist uns ein Fehler beim erstellen der Tabelle passiert!");
		}
		}finally{
			try{
			con.close();
			}catch (SQLException e) {
				System.out.println("Schwerwiegender Datenbankfehler! Versuchen Sie es Später noch einmal!");
			}
		}
		
	}
	//Ob Reservierung möglich ist
	public synchronized void reservationPossible() throws SQLException {
		Connection con=null;
		
		try{
		con=this.datasource.getConnection();
		Statement statement=con.createStatement();
		PreparedStatement pstate=con.prepareStatement("SELECT * FROM statusderplätze");
		PreparedStatement pstate1=con.prepareStatement("SELECT * FROM showbegins");
		PreparedStatement pstate2=con.prepareStatement("UPDATE showbegins SET showBeginnt='false'");
		PreparedStatement pstate3=con.prepareStatement("UPDATE showbegins SET showBeginnt='true'");
		ResultSet resSet=null;
		
		resSet=pstate.executeQuery();
		resSet.first();
		freeSeats=resSet.getInt(1);
		soldSeats=resSet.getInt(3);
		reservationSeats=resSet.getInt(2);
		
		resSet=pstate1.executeQuery();
		resSet.first();
		String isBeginning=resSet.getString(1);
		try {
			if (freeSeats != 0 && isBeginning.equals("false")==true) {
				pstate2.execute();
				showBegins = false;
			} else {
				pstate3.execute();
				showBegins = true;
			}
		} catch (KartenverkaufException e) {
			throw new KartenverkaufException(
					"Upps da ist uns ein Fehler unterlaufen!!");
		}
		}finally{
			try{
			con.close();
			}catch (SQLException e) {
				System.out.println("Schwerwiegender Datenbankfehler! Versuchen Sie es Später noch einmal!");
			}
		}
	}
	
	//Status der PLätze anzeigen lassen
	public synchronized void setStats() throws SQLException {
		Connection con=null;
		
		try{
		con=this.datasource.getConnection();
		Statement statement=con.createStatement();
		PreparedStatement pstate=con.prepareStatement("SELECT * FROM statusderplätze");
		ResultSet resSet=null;
		try {
			resSet=pstate.executeQuery();
			resSet.first();
			freeSeats=resSet.getInt(1);
			soldSeats=resSet.getInt(3);
			reservationSeats=resSet.getInt(2);
		} catch (KartenverkaufException e) {
			throw new KartenverkaufException(
					"Upps da ist uns ein Fehler unterlaufen!!");
		}
		}finally{
			try{
			con.close();
			}catch (SQLException e) {
				System.out.println("Schwerwiegender Datenbankfehler! Versuchen Sie es Später noch einmal!");
			}
		}
	}

	//Show Beginnt
	public synchronized void preventReservation() throws SQLException {
		Connection con=null;
		
		try{
		con=this.datasource.getConnection();
		Statement statement=con.createStatement();
		PreparedStatement pstate=con.prepareStatement("SELECT * FROM sitzplatz WHERE id=?");
		PreparedStatement pstate1=con.prepareStatement("UPDATE statusderplätze SET freiePlätze=?, reserviertePlätze=?,verkauftePlätze=?");
		PreparedStatement pstate2=con.prepareStatement("UPDATE sitzplatz SET status='frei', name='' WHERE id=?");		
		PreparedStatement pstate3=con.prepareStatement("UPDATE showbegins SET showBeginnt='true'");
		PreparedStatement pstate4=con.prepareStatement("SELECT * FROM statusderplätze");
		ResultSet resSet=null;
		try {
			pstate3.execute();
			for (int i = 1; i <= allSeats; i++) {
				resSet=pstate4.executeQuery();
				resSet.first();
				freeSeats=resSet.getInt(1);
				soldSeats=resSet.getInt(3);
				reservationSeats=resSet.getInt(2);
				pstate.setInt(1, i);
				resSet=pstate.executeQuery();
				resSet.first();
				String status=resSet.getString(3);
				if (status.equals("reserviert")) {
					resSet=pstate4.executeQuery();
					resSet.first();
					reservationSeats=resSet.getInt(2)-1;
					freeSeats=resSet.getInt(1)+1;
					con.setAutoCommit(false);
					pstate1.setInt(1, freeSeats);
					pstate1.setInt(2, reservationSeats);
					pstate1.setInt(3, soldSeats);
					pstate1.execute();
					pstate2.setInt(1, i);
					pstate2.execute();
					con.commit();
				}
			}
			successMessage = "Sie haben die Reservierungen alle erfolgreich storniert!";
		} catch (KartenverkaufException e) {
			throw new KartenverkaufException(
					"Upps! Da ist uns ein Fehler passiert beim auflösen aller Reservierungen!");
		}
		}finally{
			try{
			con.close();
			}catch (SQLException e) {
				System.out.println("Schwerwiegender Datenbankfehler! Versuchen Sie es Später noch einmal!");
			}
		}
	}
	
	//Reservierungssperre aufheben
			public synchronized void redoReservationBlock() throws SQLException{
				Connection con=null;
				
				try{
				con=this.datasource.getConnection();
				Statement statement=con.createStatement();
				PreparedStatement pstate=con.prepareStatement("UPDATE showbegins SET showBeginnt='false'");
				ResultSet resSet=null;
				try {
					pstate.execute();
					successMessage = "Sie können wieder ganz normal Reservieren!";
				} catch (KartenverkaufException e) {
					throw new KartenverkaufException(
							"Upps! Da ist uns ein Fehler passiert beim Rückgängig machen der Reservierungssperre!");
				}
				}finally{
					try{
					con.close();
					}catch (SQLException e) {
						System.out.println("Schwerwiegender Datenbankfehler! Versuchen Sie es Später noch einmal!");
					}
				}
			}

			//Datenbank löschen
			public synchronized void deleteDatabase() throws SQLException{
				Connection con= null;
				
				try{
				con=this.datasource.getConnection();
				Statement statement=con.createStatement();
				PreparedStatement pstate=con.prepareStatement("DROP TABLE sitzplatz");
				PreparedStatement pstate1=con.prepareStatement("DROP TABLE statusDerPlätze");
				PreparedStatement pstate2=con.prepareStatement("DROP TABLE showBegins");
				PreparedStatement pstate3=con.prepareStatement("CREATE TABLE IF NOT EXISTS sitzplatz(id int(100) NULL, name varchar(50) NULL, status varchar(100) NULL)");
				PreparedStatement pstate4=con.prepareStatement("CREATE TABLE IF NOT EXISTS statusDerPlätze(freiePlätze int(100) NULL, reserviertePlätze int(100) NULL, verkauftePlätze int(100) NULL )");
				PreparedStatement pstate5=con.prepareStatement("CREATE TABLE IF NOT EXISTS showBegins(showBeginnt varchar(10) NULL)");
				ResultSet resSet=null;			
				try {				
					con.setAutoCommit(false);
					pstate.execute();
					pstate1.execute();
					pstate2.execute();
					pstate3.execute();
					pstate4.execute();
					pstate5.execute();
					con.commit();		
					successMessage = "Die Datenbank wurde erfolgreich erneuert!";
				} catch (KartenverkaufException e) {
					throw new KartenverkaufException(
							"Upps! Da ist uns ein Fehler passiert beim Löschen der Datenbank!");
				}
				}finally{
					try{
					con.close();
					}catch (SQLException e) {
						System.out.println("Schwerwiegender Datenbankfehler! Versuchen Sie es Später noch einmal!");
					}
				}
			}


	public synchronized boolean getShowBegins() throws SQLException {
		setStats();
		reservationPossible();
		return showBegins;
	}

	public synchronized int getFreeSeats() throws SQLException {
		setStats();
		return freeSeats;
	}

	public synchronized int getReservationSeats() throws SQLException {
		setStats();
		return reservationSeats;
	}

	public synchronized int getSoldSeats() throws SQLException {
		setStats();
		return soldSeats;
	}

	public synchronized int getAllSeats() {
		return allSeats;
	}

	public synchronized String getSuccessMessage() {
		return successMessage;
	}
}
