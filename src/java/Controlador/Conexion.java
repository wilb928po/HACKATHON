
package Controlador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Conexion {
  
    private static final String DRIVER_MY_SQL="com.mysql.jdbc.Driver";
    private static final String DRIVER_POSTGRE_SQL="com.postgresql.jdbc.Driver";
    
    public enum DRIVER{MySql,PostgreSQL};
    
    private String dataBase = "darmet";
    private String login = "root";
    private String password = "root";
    private String host = "localhost";
    private String port = "3306";  
    private Connection connection = null;
    private String msj;
    private boolean exito;
    private Exception exception;

    public Conexion(){
        
    }    
    
    public String getUrl() {
        try {
            return connection!=null?connection.getMetaData().getURL():null;
        } catch (SQLException ex) {
            return null;
        }
    }
    
    private String getUrl(DRIVER driver) {
        return driver==DRIVER.MySql?"jdbc:mysql://"+host+":"+port+"/"+dataBase:
                                    "jdbc:postgrade://"+host+"/"+dataBase;
//        return driver==DRIVER.MySql?"jdbc:mysql://"+host+"/"+dataBase:
//                                    "jdbc:postgrade://"+host+"/"+dataBase;
    }
    
    public void conectar(DRIVER driver){
        try{                    
            switch(driver){
                case MySql: Class.forName(DRIVER_MY_SQL); break;
                case PostgreSQL: Class.forName(DRIVER_POSTGRE_SQL); break;
            }
            connection=DriverManager.getConnection(getUrl(driver),login,password);
            if(connection!=null){
                msj="OK base de datos "+dataBase+" listo!!!";
                exito=true;
                connection.setAutoCommit(false);
            }
        }catch(SQLException|ClassNotFoundException e){
            exception=e;
            msj="Error: no se pudo conectar a la base de datos "+dataBase+"!!!";
            exito=false;
            System.out.println(e.toString());
        }     
    }
    
    public void desconectar(){
        if(connection!=null){
            try {
                connection.close();
            } catch (SQLException ex) {
                exception=ex;
            }
        }
        msj="La conexion a la  base de datos "+dataBase+" a terminado!!!";
        connection = null;        
    }
    
    public boolean existeEnlace(){
        return connection!=null;        
    }
    
    public String getDataBase() {
        return dataBase;
    }

    public void setDataBase(String dataBase) {
        this.dataBase = dataBase;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }    

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Connection getConnection(){
        return this.connection;
    }

    public String getMsj() {
        return msj;
    }

    public boolean isExito() {
        return exito;
    }
    
    public DatabaseMetaData getMetaDatos(){
        try {
            return connection!=null?connection.getMetaData():null;
        } catch (SQLException ex) {
            return null;
        }        
    }
    //___________________________________________________________________________________ Soy una barra separadora :)
    /* METODO PARA REALIZAR UNA CONSULTA A LA BASE DE DATOS
    * INPUT:  
    *      table => nombre de la tabla donde se realizara la consulta, puede utilizarse tambien INNER JOIN
    *      fields => String con los nombres de los campos a devolver Ej.: campo1,campo2campo_n
    *      where => condicion para la consulta
    * OUTPUT: un object[][] con los datos resultantes, sino retorna NULL
    */
    public Object [][] select(String table, String fields, String where){
      int registros = 0;      
      String colname[] = fields.split(",");

      //Consultas SQL
      String q ="SELECT " + fields + " FROM " + table;
      String q2 = "SELECT count(*) as total FROM " + table;
      if(where!=null)
      {
          q+= " WHERE " + where;
          q2+= " WHERE " + where;
      }
      //obtenemos la cantidad de registros existentes en la tabla
      try{
         PreparedStatement pstm = connection.prepareStatement(q2);
          try (ResultSet res = pstm.executeQuery()) {
              res.next();
              registros = res.getInt("total");
          }
      }catch(SQLException e){
          System.out.println("");   
      }
    //se crea una matriz con tantas filas y columnas que necesite
    Object[][] data = new String[registros][fields.split(",").length];
    //realizamos la consulta sql y llenamos los datos en la matriz "Object"
      try{
         PreparedStatement pstm = connection.prepareStatement(q);
          try (ResultSet res = pstm.executeQuery()) {
              int i = 0;
              while(res.next()){
                  for(int j=0; j<=fields.split(",").length-1;j++){
                      data[i][j] = res.getString( colname[j].trim() );
                  }
                  i++;
              }
          }
          }catch(SQLException e){
         Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, e);
    }
    return data;
    }
    //___________________________________________________________________________________ Soy una barra separadora :)
    /* METODO PARA INSERTAR UN REGISTRO EN LA BASE DE DATOS
    * INPUT:
        table = Nombre de la tabla
        fields = String con los nombres de los campos donde insertar Ej.: campo1,campo2campo_n
        values = String con los datos de los campos a insertar Ej.: valor1, valor2, valor_n
    */
    //___________________________________________________________________________________ Soy una barra separadora :)
    public boolean insert(String table, String fields, String values){
        boolean res=false;
        //Se arma la consulta
        String q=" INSERT INTO " + table + " ( " + fields + " ) VALUES ( " + values + " ) ";
        //se ejecuta la consulta
        
        try(PreparedStatement pstm = connection.prepareStatement(q)){
                pstm.execute();
                res=true;
            } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }           
      return res;
    }

    private static Conexion c;
    
    public static Conexion getConexion(){
        try {
            if(c==null||!c.getConnection().isClosed()){
                c=new Conexion();
                c.setDataBase("hackathon");
                c.setHost("localhost");
                c.setLogin("root");
                c.setPassword("1234");
                c.setPort("3306");
                c.conectar(DRIVER.MySql);
            }            
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            c=new Conexion();
                c.setDataBase("hackathon");
                c.setHost("localhost");
                c.setLogin("root");
                c.setPassword("1234");
                c.setPort("3306");
                c.conectar(DRIVER.MySql);
            return null;
        }
        return c;
    }
   
    public int count(String tabla){
        try {
            Statement exec=connection.createStatement();
            ResultSet rs = exec.executeQuery("SELECT COUNT(1) FROM "+tabla);
            rs.next();
            int registros=rs.getInt(1);
            return registros;
        } catch (SQLException ex) {
            System.out.println(ex);
            return 0;
        }
    }
    
    public boolean confirmarTransacion(){
        try {                        
            connection.commit();
            return true;
        } catch (SQLException ex) {
            msj=ex.toString(); 
            return false;
        }
    }
    
    public boolean rechazarTransacion() throws SQLException{
        try {                        
            connection.rollback();
            return true;
        } catch (SQLException ex) {
            msj=ex.toString(); 
            return false;
        }        
    }        
    
    public List<String> select(String sql){
        ArrayList<String> list=new ArrayList<>();
        try {            
            PreparedStatement pstm = connection.prepareStatement(sql);
            ResultSet res = pstm.executeQuery();            
            while(res.next()){
                list.add(res.getString(1));                
            }
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }
    
    public List<Object[]> select(String sql, int columns){
        ArrayList<Object[]> list=new ArrayList<>();
        try {            
            PreparedStatement pstm = connection.prepareStatement(sql);
            ResultSet res = pstm.executeQuery();            
            while(res.next()){
                Object[] array=new Object[columns];
                for (int i = 0; i < columns; i++) {
                    array[i]=res.getObject(i+1);
                }
                list.add(array);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }
}
