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
    private ArrayList<Ficha> Blancas;
    private ArrayList<Ficha> Negras;
    private int numBlancas;
    private int numNegras;
    private String turno;
    private int numJugadas;
    private boolean hasCapturado;//Sera un indicador que sera true cuando una dama haya comido una pieza siempre que pueda comer mas.
    public StringBuffer Historial; 
    private boolean hasCapturadoHistorial;//Sera un indicador para en la funcion retroceder ondocar que no hay que cambiar el turno;
    
    public Juego ()
    {    
        initComponets(); 
    }
    
    private void initComponets()
    {          
        numJugadas=0;
        turno = "B";
        hasCapturado = false;
        hasCapturadoHistorial=false;
        Tablero = new Ficha[8][8];
        numBlancas = 12; 
        numNegras = 12;
        Historial = new StringBuffer();
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
                Ficha aux = Tablero[i][j] = new Dama(new coordenadas(i,j),"N");
                Negras.add(aux);
            }
            c++;
        }

        for (int i = 5; i < 8; i++)
        {
            for (int j = c % 2; j < 8; j += 2)
            {
                Ficha aux = Tablero[i][j] = new Dama(new coordenadas(i,j),"B");
                Blancas.add(aux);
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
    

    public void moveFicha(coordenadas p,coordenadas q) throws VictoriaException
    {
        Ficha aux = this.getFichaAt(p);
        Tablero[q.x][q.y]=aux;
        Tablero[q.x][q.y].setPos(q);
        this.setFicha(new Ficha(p), p);
        if(!q.equals(p))this.eliminarFichasComidas(p,q);
        aux.calcularDestinos(this);
    }
    
    public void setFicha(Ficha f,coordenadas p) 
    {
        Tablero[p.x][p.y]=f;      
        Tablero[p.x][p.y].setPos(p);
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
            this.moveFicha(q,p);
            if(!this.getFichaAt(p).destinosCaptura.isEmpty())
            {
                this.coronar();
                throw new CapturadoException(p);  
            }
            this.changeTurno();
            this.coronar();
        }
        else
        {
            this.moveFicha(q,p);
            this.changeTurno();
            this.coronar();
            //this.ImpFichas();
        }   
    }
    
    public void eliminarFichasComidas(coordenadas q,coordenadas p) throws VictoriaException
    {
        int m=Math.abs(q.x-p.x);
        coordenadas d=coordenadas.direccion(q,p);
        Historial.append(q.toString()+p.toString());
        for(int i=1; i<m; i++)
        {
            try
            {
                coordenadas aux = q.add(d.por(i));
                Ficha faux = this.getFichaAt(aux);
                if(faux instanceof Dama || faux instanceof Reina){
                    if(faux.getColor().equals("B") && i>0){
                        numBlancas--;
                        Blancas.remove(faux);
                        if(faux instanceof Reina) Historial.append("RB"+aux.toString());
                        if(faux instanceof Dama) Historial.append("DB"+aux.toString());
                    }
                    else if(this.getFichaAt(aux).getColor().equals("N") && i>0){
                        Negras.remove(faux);
                        numNegras--;
                        if(faux instanceof Reina) Historial.append("RN"+aux.toString());
                        if(faux instanceof Dama) Historial.append("DN"+aux.toString());
                    }
                    this.setFicha(new Ficha(aux), aux);
               
                    if(numBlancas==0) throw new VictoriaException("Ganan Negras");
                    if(numNegras==0) throw new VictoriaException("Ganan Blancas");
                }
            }
            catch(FueraTableroException e){}
        }
        Historial.append("\n");
    }
    
    public void coronar()
    {
        for(int i=0;i<8;i++)
        {
            coordenadas q = new coordenadas(0,i);
            coordenadas k = new coordenadas(7,i);
            if(this.getFichaAt(q).getColor().equals("B")) this.setFicha(new Reina(q,"B"), q);
            if(this.getFichaAt(k).getColor().equals("N")) this.setFicha(new Reina(k,"N"), k);
            
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
        if(Historial.isEmpty()) return;
        String his = this.Historial.toString();
        coordenadas p, q, aux;
        int px, py, qx, qy, aux1,aux2;
        List<String> lines = new ArrayList<>(his.lines().toList());
        String mov = lines.get(lines.size() - 1);
        String movAnterior;
        
        int T = (mov.length()/4);
        px = mov.charAt(0)-'0';
        py = mov.charAt(1)-'0';
        qx = mov.charAt(2)-'0';
        qy = mov.charAt(3)-'0';
        p = new coordenadas(px,py);
        q = new coordenadas(qx,qy);
        Ficha faux = this.getFichaAt(q);
        faux.setPos(p);
        this.setFicha(faux, p);
        this.setFicha(new Ficha(q), q);
        int contador=1;
        while(T>contador)
        {
            aux = new coordenadas(mov.charAt(contador*4+2)-'0',mov.charAt(contador*4+3)-'0');
            if(mov.charAt(contador*4)=='D'&& mov.charAt(contador*4+1)=='B') this.setFicha(new Dama(aux,"B"), aux);
            if(mov.charAt(contador*4)=='D'&& mov.charAt(contador*4+1)=='N') this.setFicha(new Dama(aux,"N"), aux);
            if(mov.charAt(contador*4)=='R'&& mov.charAt(contador*4+1)=='B') this.setFicha(new Reina(aux,"B"), aux);
            if(mov.charAt(contador*4)=='R'&& mov.charAt(contador*4+1)=='N') this.setFicha(new Reina(aux,"N"), aux);
            contador++;
        }
        
        DamasGUI.Pointers.clear();
        
        if(!(mov.length()>4 && mov.charAt(5)!=turno.charAt(0))) {    
            this.changeTurno();
        }
        
        this.hasCapturado=false;
        
        if(lines.size()>=2) {
            
            movAnterior=lines.get(lines.size() - 2);
            if(movAnterior.length()>4 && movAnterior.charAt(5)!=turno.charAt(0)){
                this.hasCapturado=true;
                DamasGUI.Pointers.add(p);
            }
                
        }
        lines.removeLast();
        Historial = new StringBuffer(String.join("\n", lines)+"\n");
    }
    
    public void ImpFichas()
    {
        System.out.print("\n Fichas Blancas: \n");
        for(int i = 0; i< Blancas.size(); i++)
        {
            Blancas.get(i).toString();
            System.out.print(i);

        }
        
        System.out.print("\n Fichas Negras: \n");
        
        for(int i = 0; i< Negras.size(); i++)
        {
            System.out.print(i);
            Negras.get(i).toString();
        }
        
    }
    
}
