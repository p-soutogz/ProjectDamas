package Damas;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.ArrayList;
/**
 *
 * @author pablo
 */
public class Ficha {
    
    private coordenadas pos;
    ArrayList<coordenadas> destinosCaptura;
    ArrayList<coordenadas> destinos;
    
    public Ficha(coordenadas p)
    {
        pos=p;
        destinos = new ArrayList<>();
        destinosCaptura = new ArrayList<>();
    }
    public Ficha(Ficha f)
    {
        pos=f.pos; 
    }

    public coordenadas getPos() {
        return pos;
    }

    public void setPos(coordenadas pos) {
        this.pos = pos;
    }
    
    public String getColor() { return " "; }
    
    public boolean isEmpty(){ return true; }
    
    public void calcularDestinos(Juego J) { return; }
    
    public void imprimirDestinos()
    {
        if(destinos!=null && !destinos.isEmpty())
        {
            System.out.print("Destinos="+destinos.toString()+"\n"+"Destinos de Captura="+destinosCaptura.toString()+"\n"+"Posicion ficha= "+this.pos.toString()+"\n");
        }
    }
    public boolean esValido(coordenadas p) {  return true; }
    
    public boolean esCaptura(coordenadas p) {  return false; }
    
    public String Rival ()
    {
        if(this.getColor().equals("N")) return "B";
        if(this.getColor().equals("B")) return "N";
        return " ";
    }
    
    public String toString()
    {
        return "/";
    }


}



