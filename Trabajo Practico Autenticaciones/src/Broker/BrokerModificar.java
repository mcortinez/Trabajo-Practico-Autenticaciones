package Broker;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ConexionBD.Conexion;
import Mensajes.Modificar;
import Respuesta.Estado;
import Respuesta.Respuesta;

public class BrokerModificar implements Broker {
	
	private String consulta="";
	private Modificar mensaje=null;
	private Conexion conexion;

	public BrokerModificar(Modificar mensaje) {
		this.mensaje=mensaje;
		conexion=Conexion.getInstance();
		if (claveCorrecta(mensaje.getPassword())) {
			this.consulta="update usuarios "
					+ "set password=? where username=? and password=?";
		}
	}

	@Override
	public Respuesta consultar() {
		Estado respuesta=null;
		String desc="";
		String estado="ERROR";
		int rs=0;
		try {
			if (this.consulta!="") {
				conexion.getConexion().setAutoCommit(false);
				PreparedStatement statement=conexion.getConexion().prepareStatement(consulta);
				statement.setString(1,mensaje.getPasswordNuevo());
				statement.setString(2,mensaje.getUsuario());
				statement.setString(3,mensaje.getPassword());
				rs=statement.executeUpdate();
				conexion.getConexion().setAutoCommit(true);
				if (rs!=0) {
					estado="OK";
					desc="Clave modificada con exito";
				}	
			}else {
				estado="ERROR";
				desc="Usuario o clave incorrecta";
			}
			} catch (Exception e) {
				estado="ERROR";
				desc="Error de conexion";
		}
		respuesta=new Estado(estado,desc);
	
		return respuesta;
	}

	@Override
	public boolean claveCorrecta(String passAdmin) {
		String consulta="select password from usuarios where username=?";
		String pass="";
		ResultSet rs;
		try {
			conexion.getConexion().setAutoCommit(false);
			
			PreparedStatement statement=conexion.getConexion().prepareStatement(consulta);
			statement.setString(1,mensaje.getUsuario());
			rs=statement.executeQuery();
			pass=rs.getString(1);
			
			
			conexion.getConexion().setAutoCommit(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pass==passAdmin;
	}

}
