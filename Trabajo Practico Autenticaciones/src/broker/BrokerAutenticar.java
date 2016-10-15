package broker;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import conexionBD.Conexion;
import mensajes.*;
import respuesta.Estado;
import respuesta.Respuesta;

public class BrokerAutenticar implements Broker {
	
	private String consulta="";
	private Autenticar mensaje=null;
	private Conexion conexion;

	public BrokerAutenticar(Autenticar mensaje) {
		this.mensaje=mensaje;
		conexion=Conexion.getInstance();
		if (claveCorrecta(mensaje.getPassword())) {
			this.consulta="insert into autenticaciones (`username`, `host`,`timestamp`) "
					+ "select username,?,now() from usuarios where username=? and password=?";
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
				statement.setString(1,mensaje.getHost());
				statement.setString(2,mensaje.getUsuario());
				statement.setString(3,mensaje.getPassword());
				rs=statement.executeUpdate();
				conexion.getConexion().setAutoCommit(true);
				if (rs!=0) {
					estado="OK";
					desc="Inicio de sesion satisfactorio";
				}	
			}else {
				estado="ERROR";
				desc="Usuario o clave incorrecta";
			}
			} catch (Exception e) {
				estado="ERROR";
				e.printStackTrace();
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
			if (!rs.next()){
                System.out.println("no hay registros");
			}
			pass=rs.getString(1);
			
			
			conexion.getConexion().setAutoCommit(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pass.equals(passAdmin);
	}

}
