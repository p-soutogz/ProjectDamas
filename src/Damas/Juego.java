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
    
    private Ficha[][] Tablero; 
    private int numBlancas;
    private int numNegras;
    private String turno;
    private boolean hasCapturado;//Sera un indicador que sera true cuando una dama haya comido una pieza siempre que pueda comer mas.
    private StringBuffer Historial; 
    
    public Juego ()
    {    
        initComponets();    
    }
    
    private void initComponets()
    {          
        turno = "B";
        hasCapturado = false;
        Tablero = new Ficha[8][8];
        numBlancas = 12; 
        numNegras = 12;
           
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
                Ficha aux = Tablero[i][j] = new Dama(new coordenadas(i,j),"N");
            }
            c++;
        }

        for (int i = 5; i < 8; i++)
        {
            for (int j = c % 2; j < 8; j += 2)
            {
                Ficha aux = Tablero[i][j] = new Dama(new coordenadas(i,j),"B");
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
        coordenadas q=f.getPos();
        Tablero[p.x][p.y].setPos(p);
        if(!q.equals(p))this.eliminarFichasComidas(q,p); 
        f.calcularDestinos(this);
        System.out.print("Blancas= "+numBlancas+ "Negras= "+numNegras+"\n");
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
          
        if(!this.getFichaAt(q).esValido(p)) { return; }
        
        if(this.getFichaAt(q).esCaptura(p)) { 
            
            this.setFichaAt(this.getFichaAt(q),p);
            if(!this.getFichaAt(p).destinosCaptura.isEmpty())
            {
               throw new CapturadoException(p);  
            }
            this.changeTurno();
            this.coronar();
        }
        else
        {
            this.setFichaAt(this.getFichaAt(q),p);
            this.changeTurno();
            this.coronar();
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
                Ficha faux = this.getFichaAt(aux);
                if(faux instanceof Dama || faux instanceof Reina){
                    if(this.getFichaAt(aux).getColor().equals("B") && i>0) numBlancas--;
                    else if(this.getFichaAt(aux).getColor().equals("N") && i>0) numNegras--;
                    this.setFichaAt(new Ficha(aux), aux);
                }
            }
            catch(Exception e){}
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