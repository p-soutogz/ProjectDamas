package Damas;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import static Damas.DamasGUI.Pointers;
import static Damas.DamasGUI.miPartida;
import java.util.ArrayList;
import excepciones.*;
import java.util.stream.Stream;
import java.util.List;
import java.util.Arrays;

/**
 *
 * @author pablo
 */

public class Juego {
   
    private Ficha[][] Tablero; 
    private int numBlancas;
    private int numNegras;
    private String turno;
    private int numJugadas;
    private boolean hasCapturado;//Sera un indicador que sera true cuando una dama haya comido una pieza siempre que pueda comer mas.
    public StringBuffer Historial; 
    
    public Juego ()
    {    
        initComponets(); 
    }
    
    private void initComponets()
    {          
        numJugadas=0;
        turno = "B";
        hasCapturado = false;
        Tablero = new Ficha[8][8];
        numBlancas = 12; 
        numNegras = 12;
        Historial = new StringBuffer();
           
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
    
    
    public void copy(Juego J)
    {
       Tablero=J.getTablero();
       numBlancas=J.numBlancas;
       numNegras=J.numNegras;
       turno=J.turno;
       numJugadas=J.numJugadas;
       hasCapturado=J.hasCapturado;
       Historial=J.Historial;
               
    }

    public Ficha[][] getTablero() {
        return Tablero;
    }

    public int getNumBlancas() {
        return numBlancas;
    }

    public int getNumNegras() {
        return numNegras;
    }

    public int getNumJugadas() {
        return numJugadas;
    }

    public StringBuffer getHistorial() {
        return Historial;
    }
    
    public Ficha getFichaAt(coordenadas p)
    {
        return Tablero[p.x][p.y];
    }
    

    public void setFichaAt(Ficha f,coordenadas p) throws VictoriaException
    {
        Tablero[p.x][p.y]=f;
        coordenadas q=f.getPos();
        Tablero[p.x][p.y].setPos(p);
        if(!q.equals(p))this.eliminarFichasComidas(q,p);
        f.calcularDestinos(this);
    }

    public void incrementarJugadas()
    {
        numJugadas++;
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
   
    public void modificarTablero(ArrayList<coordenadas> pointers) throws CapturadoException,VictoriaException
    {     
        coordenadas q = pointers.get(0);
        coordenadas p = pointers.get(1);
        this.getFichaAt(q).calcularDestinos(this);
          
        if(!this.getFichaAt(q).esValido(p)) { return; }
        
        if(this.getFichaAt(q).esCaptura(p)) { 
            
            this.setFichaAt(this.getFichaAt(q),p);
            if(!this.getFichaAt(p).destinosCaptura.isEmpty())
            {
                this.imprimirMovimiento(q, p);
                throw new CapturadoException(p);  
            }
            this.changeTurno();
            this.coronar();
            this.imprimirMovimiento(q, p);
        }
        else
        {
            this.setFichaAt(this.getFichaAt(q),p);
            this.changeTurno();
            this.coronar();
            this.imprimirMovimiento(q, p);
        }   
    }
    
    public void eliminarFichasComidas(coordenadas q,coordenadas p) throws VictoriaException
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
                    if(numBlancas==0) throw new VictoriaException("Ganan Negras");
                    if(numNegras==0) throw new VictoriaException("Ganan Blancas");
                }
            }
            catch(FueraTableroException e){}
        }
    }
    
    public void coronar()
    {
        for(int i=0;i<8;i++)
        {
            coordenadas q = new coordenadas(0,i);
            coordenadas k = new coordenadas(7,i);
            if(this.getFichaAt(q).getColor().equals("B")) Tablero[q.x][q.y]=new Reina(q,"B");
            if(this.getFichaAt(k).getColor().equals("N")) Tablero[k.x][k.y]=new Reina(k,"N");
            
        }
    }
    
    public String toString()
    {
        return numJugadas+"\n"+this.TableroToString()+turno+"\n"+numNegras+"\n"+numBlancas+"\n"+hasCapturado+"\n";
    }
    
    public void imprimirMovimiento(coordenadas p, coordenadas q)
    {
        Historial.append(numJugadas+p.toString()+q.toString()+"\n");
    }
    
    private String TableroToString()
    {
        StringBuffer str = new StringBuffer();
        
        for(int i = 0; i<8; i++)
        {
            for(int j=0;j<8;j++)
            {
                str.append(Tablero[i][j].toString());
            }
            str.append("\n");
        }
        
        return str.toString();
    }
    
    public void retoceder()
    {
        Juego J = new Juego();
        int T = numJugadas-1;
        System.out.print(T+"\n");
        String his = this.Historial.toString();
        coordenadas p,q;
        ArrayList<coordenadas> points = new ArrayList<>();
        int px,py,qx,qy;
        List<String> lines = his.lines().toList();
        String mov = new String();
        int n = lines.size();
        for(int i=0; i<T; i++)
        {
            mov=lines.get(i); 
            px=mov.charAt(1)-'0';
            py=mov.charAt(2)-'0';
            qx=mov.charAt(3)-'0';
            qy=mov.charAt(4)-'0';
            p = new coordenadas(px,py);
            q = new coordenadas(qx,qy);
            points.add(p);
            points.add(q);
            try{
                
                J.modificarTablero(points);
                points.clear(); 
                J.setHasCapturado(false);
            }
            catch(VictoriaException e1){
            }
            catch(CapturadoException e2){
                J.setHasCapturado(true);
                points.clear();
                points.add(e2.getcoordenadas());
            }
            finally{
                J.incrementarJugadas();
                System.out.print(J.TableroToString()+"\n");
            }            
        }
        Pointers.clear();;
        if(points.size()>0)Pointers.add(points.get(0));
        if(points.size()>1)Pointers.add(points.get(1));
        this.copy(J);
    }
    
    
}
