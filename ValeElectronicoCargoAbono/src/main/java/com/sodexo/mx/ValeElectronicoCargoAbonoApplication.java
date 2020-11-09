package com.sodexo.mx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.sodexo.mx.dbconnectios.CreateConnectionDB;


@SpringBootApplication
public class ValeElectronicoCargoAbonoApplication implements CommandLineRunner {
	@Autowired
    JdbcTemplate jdbcTemplate;
	
	
	
	public static void main(String[] args) {
		SpringApplication.run(ValeElectronicoCargoAbonoApplication.class, args);
			//CreateConnectionDB  conn = new CreateConnectionDB();
			//conn.testQuery();
	
		
	}
	

	
	
	
	public void run(String... args) throws Exception {
        System.out.println("Consulta");        
        jdbcTemplate.query("select NID_CA,NCUENTA,NTARJETA,NCLIENTE,VMOTIVO,VMONTO,VCLAVE_MOV,VCARGO_ABONO,to_date(PROXIMA_EJECUCION,'dd/mm/yyyy')PROXIMA_EJECUCION,NINTENTO,NESTATUS,NMAX_INTENTOS,DFECHA_FIN,VPERIODICIDAD,VTIPO_POLIZA,NPRODUCTO from  SEV_VCARGA_ABONO where rownum <=10", (rs)-> {
        //System.out.printf("%-30.30s  %-30.30s%n %-30.30s%n", NID= rs.getString("NID_CA"), rs.getString("NCUENTA"), rs.getString("NTARJETA")); 	
        	
        	String NID_CA= rs.getString("NID_CA");
        	String NCUENTA= rs.getString("NCUENTA");
        	String NTARJETA= rs.getString("NTARJETA");
        	String NCLIENTE= rs.getString("NCLIENTE");
        	String VMOTIVO= rs.getString("VMOTIVO");
        	String VMONTO= rs.getString("VMONTO");
        	String VCLAVE_MOV= rs.getString("VCLAVE_MOV");
        	String VCARGO_ABONO= rs.getString("VCARGO_ABONO");
        	String PROXIMA_EJECUCION= rs.getString("PROXIMA_EJECUCION");
        	String NINTENTO= rs.getString("NINTENTO");
        	String NESTATUS= rs.getString("NESTATUS");
        	String NMAX_INTENTOS= rs.getString("NMAX_INTENTOS");
        	String DFECHA_FIN= rs.getString("DFECHA_FIN");
        	String VPERIODICIDAD= rs.getString("VPERIODICIDAD");
        	String VTIPO_POLIZA= rs.getString("VTIPO_POLIZA");
        	String NPRODUCTO= rs.getString("NPRODUCTO");
	  	
	  	System.out.printf( "NID_CA"+NID_CA);   
	  	
    
        });
    }
	

	

	
	
	
}
