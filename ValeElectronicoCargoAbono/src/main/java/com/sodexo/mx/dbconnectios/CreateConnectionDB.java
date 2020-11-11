package com.sodexo.mx.dbconnectios;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Logger;

public class CreateConnectionDB {
	private final static Logger LOGGER  = Logger.getLogger("log");
	
	public static final String DATABASE_USER = "user";
	public static final String DATABASE_PASSWORD = "password";

	public void testQuery() {

		try {
			Connection conn = openConnectionOracle();

			String query = "select VCLAVE_MOV from SEV.SEV_TCARGOS_ABONOS where NID_CA = ?";
			PreparedStatement st = conn.prepareStatement(query);
			st.setInt(1, 22594);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				System.out.println("respuesta  " + rs.getString("VCLAVE_MOV"));
			}
		} catch (Exception ex) {

		}
	}

	public Connection openConnectionOracle() throws Exception {
		Properties mainProperties = new Properties();	   
		   FileInputStream file;	   
		   String path = "../config/config.properties";	   
		   file = new FileInputStream(path);
		   mainProperties.load(file);
		   file.close();
		   String usuario_db = mainProperties.getProperty("usuario_db");
		   String pass_db = mainProperties.getProperty("pass_db");
		   String url_db = mainProperties.getProperty("url_db");
		   mainProperties.put(DATABASE_USER, usuario_db);
		   mainProperties.put(DATABASE_PASSWORD, pass_db);

		System.out.println("++++++++++++++  OPEN CONN");
		String myDriver = "oracle.jdbc.OracleDriver";
		String myUrl = "jdbc:oracle:thin:@10.52.21.4:1521/SEVDES";
		Class.forName(myDriver);
		Connection conn = DriverManager.getConnection(myUrl, mainProperties);
		System.out.println("++++++++++++++  END OPEN CONN");
		return conn;

	}

}
