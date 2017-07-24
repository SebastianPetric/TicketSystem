package com.listener;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;



import javax.sql.DataSource;

import com.java.Kartenverkauf;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

@WebListener
public class SaleListener implements ServletContextListener {

	public SaleListener() {

	}

	public void contextInitialized(ServletContextEvent sce) {
		try {
			Context initialContext = new InitialContext();
			Context context = (Context)initialContext.lookup("java:comp/env");
			DataSource datasource = (DataSource) context.lookup("jdbc/kartenverkauf");
			Connection connection = datasource.getConnection();
			Statement statement = connection.createStatement();
			connection.setAutoCommit(false);
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS sitzplatz(id int(100) NULL, name varchar(50) NULL, status varchar(100) NULL)");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS statusDerPlätze(freiePlätze int(100) NULL, reserviertePlätze int(100) NULL, verkauftePlätze int(100) NULL )");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS showBegins(showBeginnt varchar(10) NULL)");
			connection.commit();
			
			Kartenverkauf sale = new Kartenverkauf(100,datasource);
			sce.getServletContext().setAttribute("SaleObject",sale);
		} catch (Exception e) {
			System.out.println("Problem beim Zugriff auf Datasource, start von Demo gescheitert:\n" + e);
			throw new RuntimeException(e);
		}
	}
			
	public void contextDestroyed(ServletContextEvent arg0) {

	}

}
