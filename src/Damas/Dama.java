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
public class Dama extends Ficha{
    
    String color;
    
  public Dama(coordenadas p,String col){
      super(p);
      color=col;
  }
  
  public Dama (Dama d)
    {
        super(d.getPos());
        color=d.color;
    }
  
  public boolean isEmpty() { return false; }
  
  public void calcularDestinos(Juego J) 
    {
        destinos.clear();
        destinosCaptura.clear();
        
        coordenadas[] d = new coordenadas[4];
        d[0] = new coordenadas(1,1);
        d[1] = new coordenadas(1,-1);
        d[2] = new coordenadas(-1,1);
        d[3] = new coordenadas(-1,-1);
        
        for(int i=0;i<4;i++)
        {
            try
            {
            coordenadas q = this.getPos().add(d[i]);
            
            if(J.getFichaAt(q).isEmpty()) destinos.add(q);
                   
            coordenadas q2 = this.getPos().add(d[i].por(2));
            
            if(J.getFichaAt(q2).isEmpty() && J.getFichaAt(q).getColor().equals(this.Rival())) 
            {
                destinos.add(q2);
                destinosCaptura.add(q2);
            }
           
            }
            catch(Exception e){} 
        }
    }
  
  public boolean esValido(coordenadas p)
    {
    for (int i = 0; i < destinosCaptura.size(); i++)
    {
        if (p.equals(destinosCaptura.get(i)))
        {
            return true;
        }
    }
    if(destinosCaptura.isEmpty())
    {
        for (int i = 0; i < destinos.size(); i++)
        {
            if (p.equals(destinos.get(i)))
            {
            return true;
            }
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
  
  public String toString()
    {
        return "D";
    }
  
}
