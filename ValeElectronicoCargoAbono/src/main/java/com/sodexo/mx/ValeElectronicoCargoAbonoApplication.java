package com.sodexo.mx;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.sodexo.mx.buildQuerys.ProcessInfo;
import com.sodexo.mx.dbconnectios.CreateConnectionDB;
import com.sodexo.mx.dto.SevCargaAbonoDTO;
import com.sodexo.mx.pojo.ObjQuery;
import com.sodexo.mx.restservices.OperationsApiRest;
import com.sodexo.mx.utils.Constants;

@SpringBootApplication
public class ValeElectronicoCargoAbonoApplication {
	
	private final static Logger LOGGER = Logger.getLogger("log");

	public static void main(String[] args) throws IOException, SQLException {
		SpringApplication.run(ValeElectronicoCargoAbonoApplication.class, args);

		executeProcess();
		//OperationApiRestApache ins = new OperationApiRestApache();
		//String token = ins.getToken2("usuario5", "P4ssw0rd5", "http://10.52.24.59:9890/intsdx-esb_syc/getToken"); 
		//ins.retiro(token, "529e625c8c2bf37c6334aae495a1d0f8", "50632", "195379", "5062733000422709", "1", "1"); // agrega parametros validos pa probar este servicio

	}

	public static void executeProcess() throws SecurityException, IOException {

		Handler consoleHandler = new ConsoleHandler();
		Handler fileHandler = new FileHandler("../log/Log_valeElectronicoCargosAbonos.log", true);
		SimpleFormatter simpleFormatter = new SimpleFormatter();
		fileHandler.setFormatter(simpleFormatter);
		LOGGER.addHandler(consoleHandler);
		LOGGER.addHandler(fileHandler);

		Properties mainProperties = new Properties();
		FileInputStream file;
		String path = "../config/config.properties";
		file = new FileInputStream(path);
		mainProperties.load(file);
		file.close();
		String user = mainProperties.getProperty("usuario_api");
		String password = mainProperties.getProperty("pass_api");
		String urlSycDep = mainProperties.getProperty("url_syc_dep");
		String urlSycRetiro = mainProperties.getProperty("url_syc_retiro");
		String idAplicacion = mainProperties.getProperty("idAplicacion");
		String urlSycToken = mainProperties.getProperty("url_syc_token");

		LOGGER.log(Level.INFO, "Inicia proceso...");
		int contador=0;
		OperationsApiRest operation = new OperationsApiRest();
		String token = operation.getToken(user, password, urlSycToken);

		try (Connection conn = new CreateConnectionDB().openConnectionOracle(); Statement st = conn.createStatement()) {

			List<ObjQuery> lstQuerys = new ArrayList<ObjQuery>();
			ProcessInfo processInfo = new ProcessInfo();

			ResultSet rs = st.executeQuery(
					"select NID_CA,NCUENTA,NTARJETA,NCLIENTE,VMOTIVO,VMONTO,VCLAVE_MOV,VCARGO_ABONO,to_date(PROXIMA_EJECUCION,'dd/mm/yyyy')PROXIMA_EJECUCION,NINTENTO,NESTATUS,NMAX_INTENTOS,DFECHA_FIN,VPERIODICIDAD,VTIPO_POLIZA,NPRODUCTO from SEV_VCARGA_ABONO  order by 1");

			SevCargaAbonoDTO cargaAbonoDTO = new SevCargaAbonoDTO();

			while (rs.next()) {
				contador=contador+1;
				System.out.println("linea: "+contador);
				cargaAbonoDTO = new SevCargaAbonoDTO(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9),
						rs.getString(10), rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14),
						rs.getString(15), rs.getString(16));

				String val_fecha_fin = "NO";

				int NESTATUS = Integer.parseInt(cargaAbonoDTO.getnEstatus());
				int NMAX_INTENTOS = Integer.parseInt(cargaAbonoDTO.getnMaxIntentos());

				

				if (cargaAbonoDTO.getnCargoAbono().contentEquals("ABN")) {

					lstQuerys.addAll(processInfo.deposito(idAplicacion, urlSycDep, token, cargaAbonoDTO.getNidCA(),
							cargaAbonoDTO.getnTarjeta(), cargaAbonoDTO.getnCuenta(), cargaAbonoDTO.getnCliente(),
							cargaAbonoDTO.getnMonto(), cargaAbonoDTO.getnMotivo(), cargaAbonoDTO.getnIntento(),
							NESTATUS, NMAX_INTENTOS, val_fecha_fin, cargaAbonoDTO.getvPeriodicidad(),
							cargaAbonoDTO.getnClaseMov(), cargaAbonoDTO.getvTipoPoliza(), cargaAbonoDTO.getnProducto(),
							cargaAbonoDTO.getnCargoAbono()));

				} else if (cargaAbonoDTO.getnCargoAbono().contentEquals("CRG")) {

					lstQuerys.addAll(processInfo.retiro(idAplicacion, urlSycRetiro, token, cargaAbonoDTO.getNidCA(),
							cargaAbonoDTO.getnTarjeta(), cargaAbonoDTO.getnCuenta(), cargaAbonoDTO.getnCliente(),
							cargaAbonoDTO.getnMonto(), cargaAbonoDTO.getnMotivo(), cargaAbonoDTO.getnIntento(),
							NESTATUS, NMAX_INTENTOS, val_fecha_fin, cargaAbonoDTO.getvPeriodicidad(),
							cargaAbonoDTO.getnClaseMov(), cargaAbonoDTO.getvTipoPoliza(), cargaAbonoDTO.getnProducto(),
							cargaAbonoDTO.getnCargoAbono()));

				} else {
					System.out.println("No hay configuracion del tipo de movimiento, cargo o abono");
				}

			}
			
			System.out.println("Termina de leer y comienza hacer los update");
			LOGGER.log(Level.INFO, "Termina de leer y comienza hacer los update");
			try (Statement stmtTCargoAbono = conn.createStatement();
					Statement stmtBitacoraCargo = conn.createStatement();
					Statement stmtMovimientosAcla = conn.createStatement();
					Statement stmtCuentasMovim = conn.createStatement();
					Statement stmtMovRestoLaat = conn.createStatement();
					Statement stmtSetCobroMov = conn.createStatement();
					Statement stmtCtrxAj = conn.createStatement();
					Statement stmtCobroComPoliza = conn.createStatement()) {
				conn.setAutoCommit(false);
				for (ObjQuery obj : lstQuerys) {
					if (obj.getTable().equals(Constants.TABLA_CTRX_AJ)) {
						stmtCtrxAj.addBatch(obj.getQuery());
					} else if (obj.getTable().equals(Constants.TABLA_TCARGO_ABONOS)) {
						stmtTCargoAbono.addBatch(obj.getQuery());
					} else if (obj.getTable().equals(Constants.TABLA_CUENTAS_MOVIMIENTOS)) {
						stmtCuentasMovim.addBatch(obj.getQuery());
					} else if (obj.getTable().equals(Constants.TABLA_MOV_RESTO_LAAT)) {
						stmtMovRestoLaat.addBatch(obj.getQuery());
					} else if (obj.getTable().equals(Constants.TABLA_SET_COBRO_COMISIONES_POLIZAS)) {
						stmtCobroComPoliza.addBatch(obj.getQuery());
					} else if (obj.getTable().equals(Constants.TABLA_SET_MOVIMIENTOS_ACLARACIONES)) {
						stmtMovimientosAcla.addBatch(obj.getQuery());
					} else if (obj.getTable().equals(Constants.TABLA_SEV_TBITACORA_CARGOSABONOS)) {
						stmtBitacoraCargo.addBatch(obj.getQuery());
					} else {
						stmtSetCobroMov.addBatch(obj.getQuery());
					}
				}

				stmtCtrxAj.executeBatch();
				stmtTCargoAbono.executeBatch();
				stmtCuentasMovim.executeBatch();
				stmtMovRestoLaat.executeBatch();
				stmtCobroComPoliza.executeBatch();
				stmtMovimientosAcla.executeBatch();
				stmtBitacoraCargo.executeBatch();
				stmtSetCobroMov.executeBatch();

				System.out.println("################ Numero de Lineas: " + lstQuerys.size());
				LOGGER.log(Level.INFO, "Movimientos registrados: " + lstQuerys.size());

				conn.commit();

			} catch (SQLException e) {
				e.printStackTrace();
				LOGGER.log(Level.SEVERE, "Error al insertar movimientos -> " + e);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
