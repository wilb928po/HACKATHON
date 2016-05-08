/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
 
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.HorizontalBarChartModel;
import org.primefaces.model.chart.ChartSeries;
 
@ManagedBean
public class ChartView implements Serializable {
     
    private String distrito; 

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getDistrito() {
        return distrito;
    }        
    
    private HorizontalBarChartModel horizontalBarModel;
 
    @PostConstruct
    public void init() {
        distrito="TODOS";
        createBarModels();
    }
      
    public HorizontalBarChartModel getHorizontalBarModel() {
        return horizontalBarModel;
    }    
     
    private void createBarModels() {       
        createHorizontalBarModel("TODOS");
    }             
    
    private void createHorizontalBarModel(String distrito) {
        horizontalBarModel = new HorizontalBarChartModel();        
        StringBuilder sql=builderConsulta();
        if(!distrito.equals("TODOS")){
            sql.append(" group by distrito having distrito='").append(distrito).append("';");
        }
        List<Object[]> list=Conexion.getConexion().select(sql.toString(), 12);
        Object[] array=list.get(0);        
        Number max,aux = 0;
        int porcentaje;
        ChartSeries no = new ChartSeries();
        no.setLabel("NO");
        no.set("LUZ", max=(Number) array[1]);
        porcentaje=max.intValue();
        max=maxNum(max, aux);
        no.set("AGUA", aux=(Number) array[3]);
        max=maxNum(max, aux);
        no.set("ALCANTARILLADO", aux=(Number) array[5]);
        max=maxNum(max, aux);
        no.set("GAS", aux=(Number) array[7]);
        max=maxNum(max, aux);
        no.set("TELEFONO FIJO", aux=(Number) array[9]);
        max=maxNum(max, aux);
        no.set("INTERNET", aux=(Number) array[11]);        
        max=maxNum(max, aux);
        ChartSeries si = new ChartSeries();
        si.setLabel("SI");
        aux=(Number) array[0];
        porcentaje+=aux.intValue();
        int dif=(int)((aux.floatValue()/porcentaje)*100);
        si.set("LUZ[ SI TIENE : "+dif+"% , NO TIENE "+(100-dif)+"% ]", aux);
        max=maxNum(max, aux);
        aux=(Number) array[2];
        dif=(int)((aux.floatValue()/porcentaje)*100);
        si.set("AGUA[ SI TIENE : "+dif+"% , NO TIENE "+(100-dif)+"% ]", aux);
        max=maxNum(max, aux);
        aux=(Number) array[4];
        dif=(int)((aux.floatValue()/porcentaje)*100);
        si.set("ALCANTARILLADO[ SI TIENE : "+dif+"% , NO TIENE "+(100-dif)+"% ]", aux);
        max=maxNum(max, aux);
        aux=(Number) array[6];
        dif=(int)((aux.floatValue()/porcentaje)*100);
        si.set("GAS[ SI TIENE : "+dif+"% , NO TIENE "+(100-dif)+"% ]", aux);
        max=maxNum(max, aux);
        aux=(Number) array[8];
        dif=(int)((aux.floatValue()/porcentaje)*100);
        si.set("TELEFONO FIJO[ SI TIENE : "+dif+"% , NO TIENE "+(100-dif)+"% ]", aux);
        max=maxNum(max, aux);
        aux=(Number) array[10];
        dif=(int)((aux.floatValue()/porcentaje)*100);
        si.set("INTERNET[ SI TIENE : "+dif+"% , NO TIENE "+(100-dif)+"% ]", aux);
        max=maxNum(max, aux);
 
        horizontalBarModel.addSeries(si);
        horizontalBarModel.addSeries(no);
                         
        horizontalBarModel.setTitle("DISPONIBILIDAD POR VIVIENDA");
        horizontalBarModel.setLegendPosition("e");
        horizontalBarModel.setShowPointLabels(true);
        horizontalBarModel.setAnimate(true);        
        horizontalBarModel.setSeriesColors("0000FF,009900");
         
        Axis xAxis = horizontalBarModel.getAxis(AxisType.X);
        xAxis.setLabel("DISPONIBILIDAD POR VIVIENDA");
        xAxis.setMin(0);
        xAxis.setMax(max.intValue()+1000);
         
        Axis yAxis = horizontalBarModel.getAxis(AxisType.Y);
        yAxis.setLabel("SERVICIOS BASICOS");        
    }
    
    private StringBuilder builderConsulta(){
        StringBuilder sb=new StringBuilder();
        sb.append("SELECT sum(if(Luz=1,1,0))as Luz,sum(if(Luz=0,1,0))as Luz_No").
            append(",sum(if(Agua=1,1,0))as Agua,sum(if(Agua=0,1,0))as Agua_No").
            append(",sum(if(Alc=1,1,0))as Alc,sum(if(Alc=0,1,0))as Alc_No").
            append(",sum(if(Gas=1,1,0))as Gas,sum(if(Gas=0,1,0))as Gas_No").
            append(",sum(if(TelFijo=1,1,0))as TelFijo,sum(if(TelFijo=0,1,0))as TelFijo_No").
            append(",sum(if(_Int=1,1,0))as _Int,sum(if(_Int=0,1,0))as _Int_No").
            append(" FROM datos");
        return sb;
    }
    
    private Number maxNum(Number a,Number b){
        return a.intValue()>b.intValue()?a:b;
    } 
    
    public void actualizar(){
        createHorizontalBarModel(distrito);
    }
}
