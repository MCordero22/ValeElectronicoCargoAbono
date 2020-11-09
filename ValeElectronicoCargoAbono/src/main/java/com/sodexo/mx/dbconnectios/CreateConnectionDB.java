package com.sodexo.mx.dbconnectios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CreateConnectionDB {
	
	public void testQuery() {
		try {
			Connection conn =openConnectionOracle();
			
			String query = "select * from om_catalog_users where username = ?";
            PreparedStatement st = conn.prepareStatement(query);
            
            st.setString(1, "mcordero");
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
            	System.out.println("respuesta  " +rs.getString("FIRST_NAME"));
            }
		}catch(Exception ex) {
			
		}
	}
	
	
	public static Connection openConnectionOracle() throws Exception {
		
        //System.out.println("++++++++++++++  OPEN CONN");
        String myDriver = "oracle.jdbc.OracleDriver";
        //String myUrl = "jdbc:oracle:thin:@192.168.1.103:1521/xe";
        //String myUrl = "jdbc:oracle:thin:@34.122.188.80:1521/xe";
        String myUrl = "jdbc:oracle:thin:@172.21.20.143:1521/CVBPELD";
        Class.forName(myDriver);
        //Connection conn = DriverManager.getConnection(myUrl, "SISAMEX", "welcome1");
        Connection conn = DriverManager.getConnection(myUrl, "INTFWK", "welcome1");
        //System.out.println("++++++++++++++  END OPEN CONN");
        return conn;

    }

}
