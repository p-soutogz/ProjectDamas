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
    
    public Reina (coordenadas p,String col)
    {
        super(p);
        color=col;
        
    }
    public Reina (Reina r)
    {
        super(r.getPos());
        color=r.color;
    }
     public boolean isEmpty(){
        return false;
    }
     public void calcularDestinos(Juego J) 
    {
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
                coordenadas q = this.getPos().add(d[i].por(j));
            
                if(J.getFichaAt(q).isEmpty()) destinos.add(q);
                   
                coordenadas q2 = this.getPos().add(d[i].por(j+1));
            
                if((J.getFichaAt(q2).isEmpty() && J.getFichaAt(q).getColor().equals(this.Rival())) || (J.getFichaAt(q2).isEmpty() && auxBool)) 
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
  
     
    public boolean esValido(coordenadas p)
    {  
    int n = destinos.size();
    for (int i = 0; i < n; i++)
    {
        if (p.equals(destinos.get(i)))
        {
            return true;
        }
    }
    return false;   
    }
    
    public boolean esCaptura(coordenadas p) {  
      
    int n = destinosCaptura.size();
    for (int i = 0; i < n; i++)
    {
        if (p.equals(destinosCaptura.get(i)))
        {
            return true;
        }
    }
    return false;   
  }
     
    public String getColor() { return color; }
    
}
