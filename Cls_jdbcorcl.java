/*----------------------------------------------------------------------
    Cls_jdbcorcl.java
    21-08-2019
    Descripción:
        Clase especializada en la operativa de base de datos oracle
        version prototipo
        Oscar Barrios Landa
    email:
        barrioslandaoscar@gmail.com
-----------------------------------------------------------------------*/

package general;

import java.sql.Connection;
//import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.sql.CallableStatement;
import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLType;

public class Cls_jdbcorcl {

	private final String d_controlador = "oracle.jdbc.driver.OracleDriver";
	private String d_bdjdbc;
	private String d_bdhost;
	private String d_bdport;
	private String d_bdinstance;
	private String d_bdurl;
	private String d_bduser;
	private String d_bdpassword;
	private String d_bdquery;
	private Object[][] arr_parametersreturn;

	private Connection obj_conexion;
	private CallableStatement obj_stmtproc;

	//-- Construct
	public Cls_jdbcorcl(){
		this.setD_bdjdbc("jdbc:oracle:thin");
		this.setD_bdhost("@localhost");
		this.setD_bdport("1521");
		this.setD_bdinstance("orcl");
	    this.setD_bdurl("");
	    this.setD_bduser("");
	    this.setD_bdpassword("");
		this.setD_bdquery("");
		this.obj_conexion = null;
		this.obj_stmtproc = null;
	}

	public String 	getD_bdjdbc()
	{
		return d_bdjdbc;
	}
	public void 	setD_bdjdbc(String d_bdjdbc)
	{
		this.d_bdjdbc = d_bdjdbc;
	}
	public String 	getD_bdhost()
    {
		return d_bdhost;
	}
	public void 	setD_bdhost(String d_bdhost)
	{
		this.d_bdhost = d_bdhost;
	}
	public String 	getD_bdport()
	{
		return d_bdport;
	}
	public void 	setD_bdport(String d_bdport)
	{
		this.d_bdport = d_bdport;
	}
	public String 	getD_bdinstance()
	{
		return d_bdinstance;
	}
	public void 	setD_bdinstance(String d_bdinstance)
	{
		this.d_bdinstance = d_bdinstance;
	}
	public String 	getD_bdquery()
	{
		return d_bdquery;
	}
	public void 	setD_bdquery(String d_bdquery)
	{
		this.d_bdquery = d_bdquery;
	}
	public String 	getD_bdurl()
	{
		return d_bdurl;
	}
	public void 	setD_bdurl(String d_bdurl)
	{
		this.d_bdurl = d_bdurl;
	}
	public String 	getD_bdpassword()
	{
		return d_bdpassword;
	}
	public void 	setD_bdpassword(String d_bdpassword)
	{
		this.d_bdpassword = d_bdpassword;
	}
	public String 	getD_bduser()
	{
		return d_bduser;
	}
	public void 	setD_bduser(String d_bduser)
	{
		this.d_bduser = d_bduser;
	}
	public Object[][] getArr_parametersreturn()
	{
		return arr_parametersreturn;
	}
	public void 	  setArr_parametersreturn(Object[][] arr_parametersreturn)
	{
		this.arr_parametersreturn = arr_parametersreturn;
	}


	public int getN_conectar()
	{

		d_bdurl = d_bdjdbc+":"+d_bdhost+":"+d_bdport+":"+d_bdinstance;

		try
		{
		  Class.forName(d_controlador);
		  obj_conexion = DriverManager.getConnection(d_bdurl,d_bduser,d_bdpassword);

		  if(obj_conexion == null)
			  return 1;

		  return 0;
		}
		catch (SQLException SQLe)
		{
		  SQLe.printStackTrace();
		  return 1;
		}
		catch(ClassNotFoundException noEncontroClase)
		{
		  noEncontroClase.printStackTrace();
		  return 1;
		}

	}

	public int getN_procqueryget(Object[][] arr_parametersIN,
							  Object[][] arr_parametersOUT)
	{

		int n_counter = 0;
		Map<String,Object> map_valuesreturn = null;

		try
		{
			obj_stmtproc = obj_conexion.prepareCall("{call proc_getuser(?,?,?,?,?,?,?,?,?,?)}");

			//-- Parametros de Entrada
			if(arr_parametersIN != null)
				while(n_counter < arr_parametersIN.length)
				{
					obj_stmtproc.setObject(String.valueOf(arr_parametersIN[n_counter][0]),
														  arr_parametersIN[n_counter][1]);
					++n_counter;
				}

			//-- Parametros de Salida
			if(arr_parametersOUT != null)
			{
				n_counter = 0;
				map_valuesreturn = new HashMap<String,Object>();

				while(n_counter < arr_parametersOUT.length)
				{
					String d_parametername = String.valueOf(arr_parametersOUT[n_counter][0]);
					obj_stmtproc.registerOutParameter(d_parametername,
												      (SQLType) arr_parametersOUT[n_counter][1]);

					if(!map_valuesreturn.containsKey(d_parametername))
						map_valuesreturn.put(d_parametername, null);

					++n_counter;
				}
			}

			obj_stmtproc.execute();

			if(map_valuesreturn != null)
			{
				n_counter = 0;
				 while(n_counter < arr_parametersOUT.length)
				 {
					 String d_parametername = String.valueOf(arr_parametersOUT[n_counter][0]);
					 map_valuesreturn.put(d_parametername, obj_stmtproc.getObject(d_parametername));
					 n_counter++;
				 }
			}


			return 0;

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 1;
		}
		finally
		{
			try
			{
				obj_stmtproc.close();
				obj_conexion.close();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}


}
