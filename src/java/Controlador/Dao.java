
package Controlador;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Wilb928
 */
@ManagedBean
public class Dao {

    private List<String> listaDistritos;
    private String todos;
    
    @PostConstruct
    public void init() {
        todos="TODOS";
        listaDistritos=listarDistritos();
        listaDistritos.add(0,todos);
    }

    public String getTodos() {
        return todos;
    }    
    
    public List<String> getListaDistritos() {
        return listaDistritos;
    }
        
    public List<String> listarDistritos(){
        Conexion c=Conexion.getConexion();
        return c.select("SELECT distinct distrito FROM zona;");
    }    
}
