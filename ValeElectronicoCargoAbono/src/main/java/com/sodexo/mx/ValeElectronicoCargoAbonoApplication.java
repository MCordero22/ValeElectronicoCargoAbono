package com.sodexo.mx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.sodexo.mx.dbconnectios.CreateConnectionDB;

@SpringBootApplication
public class ValeElectronicoCargoAbonoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ValeElectronicoCargoAbonoApplication.class, args);
			CreateConnectionDB  conn = new CreateConnectionDB();
			conn.testQuery();
	}
	
}
