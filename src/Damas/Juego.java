package Damas;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.ArrayList;
import excepciones.*;
/**
 *
 * @author pablo
 */

public class Juego {
    
    Ficha[][] Tablero; 
    ArrayList<Ficha> Blancas;
    ArrayList<Ficha> Negras;
    String turno;
    private boolean hasCapturado;//Sera un indicador que sera true cuando una dama haya comido una pieza siempre que pueda comer mas.
            
    public Juego ()
    {    
        initComponets();    
    }
    
    private void initComponets()
    {          
        turno = "B";
        hasCapturado = false;
        Tablero = new Ficha[8][8];
        Blancas = new ArrayList<>();
        Negras = new ArrayList<>();
           
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                Tablero[i][j] = new Ficha(new coordenadas(i,j));
            }
        }
        int c = 1;
        for (int i = 0; i < 3; i++)
        {
            for (int j = c % 2; j < 8; j += 2)
            {
                Tablero[i][j] = new Dama(new coordenadas(i,j),"N");
                Negras.add(Tablero[i][j]);
            }
            c++;
        }

        for (int i = 5; i < 8; i++)
        {
            for (int j = c % 2; j < 8; j += 2)
            {
                Tablero[i][j] = new Dama(new coordenadas(i,j),"B");
                Blancas.add(Tablero[i][j]);
            }
            c++;
        }
        
    }
    
    public Ficha getFichaAt(coordenadas p)
    {
        return Tablero[p.x][p.y];
    }
    
    public void setFichaAt(Ficha f,coordenadas p)
    {
        Tablero[p.x][p.y]=f;
        Tablero[p.x][p.y].pos=p;
    }

    public String getTurno() {
        return turno;
    }

    public void changeTurno() {
        if(turno.equals("B")) this.turno = "N";
        else{this.turno="B";}
    }
    
    public boolean isTurno(coordenadas p){
        if(this.getFichaAt(p).getColor()==turno) return true;
        return false;
    }
    
    public boolean isHasCapturado() {
        return hasCapturado;
    }

    public void setHasCapturado(boolean hasCapturado) {
        this.hasCapturado = hasCapturado;
    }
   
    public void modificarTablero(ArrayList<coordenadas> pointers) throws CapturadoException
    {     
        coordenadas q = pointers.get(0);
        coordenadas p = pointers.get(1);
        
        this.getFichaAt(q).calcularDestinos(this);
        
        if(!this.getFichaAt(q).esValido(p)) { return; }
        
        if(this.getFichaAt(q).esCaptura(p)) { 
            
            this.setFichaAt(this.getFichaAt(q),p);
            this.eliminarFichasComidas(q,p);
            this.coronar();
            this.getFichaAt(p).calcularDestinos(this);
            if(!this.getFichaAt(p).destinosCaptura.isEmpty())
            {
               throw new CapturadoException(p);  
            }
            this.changeTurno();
        }
        else
        {
            this.setFichaAt(this.getFichaAt(q),p);
            this.eliminarFichasComidas(q,p); 
            this.coronar();
            this.changeTurno();
        }   
    }
    
    public void eliminarFichasComidas(coordenadas q,coordenadas p)
    {
        int m=Math.abs(q.x-p.x);
        coordenadas d=coordenadas.direccion(q,p);
        for(int i=0; i<m; i++)
        {
            try
            {
                coordenadas aux = q.add(d.por(i));
                this.setFichaAt(new Ficha(aux), aux);
            }
            catch(Exception e)
            {
                
            }
        }
    }
    
    public void coronar()
    {
        for(int i=0;i<8;i++)
        {
            coordenadas q = new coordenadas(0,i);
            coordenadas k = new coordenadas(7,i);
            if(this.getFichaAt(q).getColor().equals("B")) this.setFichaAt(new Reina(q,"B"),q);
            if(this.getFichaAt(k).getColor().equals("N")) this.setFichaAt(new Reina(k,"N"),k);
        }
    }
}