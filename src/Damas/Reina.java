package Damas;

import excepciones.*;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author pablo
 */
public class Reina extends Ficha{
 
    String color;
    
    public Reina (coordenadas p,String col){
        super(p);
        color=col;
    }
  
    public void calcularDestinos(Juego J) {
        destinos.clear();
        destinosCaptura.clear();
        boolean auxBool = false;
        
        coordenadas[] d = new coordenadas[4];
        d[0] = new coordenadas(1,1);
        d[1] = new coordenadas(1,-1);
        d[2] = new coordenadas(-1,1);
        d[3] = new coordenadas(-1,-1);
        
        for(int i=0;i<4;i++)
        {
            for(int j=1;j<8;j++)
            {
                try{
                coordenadas q = this.getPosicion().add(d[i].por(j));
            
                if(J.getFichaAt(q)==null) destinos.add(q);
                   
                coordenadas q2 = this.getPosicion().add(d[i].por(j+1));
            
                if((J.getFichaAt(q2)==null && J.getFichaAt(q)!=null && J.getFichaAt(q).getColor().equals(this.Rival())) || (J.getFichaAt(q2)==null && auxBool)) 
                {
                    destinos.add(q2);
                    destinosCaptura.add(q2);
                    auxBool=true;
                }
           
                }
            catch(Exception e){break;} 
            }
            auxBool=false;
        }
    }   
    
    public String getColor() { return color; }
    
    public String toString(){
        return "R"+this.getColor()+super.getPosicion().toString();
    }
    
}
