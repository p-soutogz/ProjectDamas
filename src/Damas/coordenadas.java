
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
public class coordenadas {
    
    public int x,y;
    
    public coordenadas (int a,int b) 
    {
        x=a;
        y=b;
    }
    public coordenadas ()
    {
        x=0;
        y=0;
    }
    public coordenadas add (coordenadas p) throws Exception
    {
        if(this.x+p.x<8 && 0<=this.x+p.x && this.y+p.y<8 && 0<=this.y+p.y)
        {
        return new coordenadas(this.x+p.x,this.y+p.y);   
        }
        
        throw new FueraTableroException();
        
    }
    public coordenadas por (int l) {   return new coordenadas(this.x*l,this.y*l);}
    
    public static coordenadas direccion (coordenadas p, coordenadas q)
    {
        int m = Math.abs(q.x-p.x);
        return new coordenadas((q.x-p.x)/m,(q.y-p.y)/m);
    }
    
    public boolean equals (coordenadas p)
    {
        if(this.x==p.x && this.y==p.y) return true;
        return false;
    }
    
    public String toString()
    {
        return "("+x+","+y+")";
    }
    
     
    
    
}
