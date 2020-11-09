package com.sodexo.mx.dbconnectios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CreateConnectionDB {
	
	public void testQuery() {
		try {
			Connection conn =openConnectionOracle();
			
			String query = "select VCLAVE_MOV from SEV.SEV_TCARGOS_ABONOS where NID_CA = ?";
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1,22594);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
            	System.out.println("respuesta  " +rs.getString("VCLAVE_MOV"));
            }
		}catch(Exception ex) {
			
		}
	}
	
	
	public static Connection openConnectionOracle() throws Exception {
		
        System.out.println("++++++++++++++  OPEN CONN");
        String myDriver = "oracle.jdbc.OracleDriver";
        //String myUrl = "jdbc:oracle:thin:@192.168.1.103:1521/xe";
        //String myUrl = "jdbc:oracle:thin:@34.122.188.80:1521/xe";
        String myUrl = "jdbc:oracle:thin:@10.52.21.4:1521/SEVDES";
        Class.forName(myDriver);
        //Connection conn = DriverManager.getConnection(myUrl, "SISAMEX", "welcome1");
        Connection conn = DriverManager.getConnection(myUrl, "SEV", "SEV001");
        System.out.println("++++++++++++++  END OPEN CONN");
        return conn;

    }

}
