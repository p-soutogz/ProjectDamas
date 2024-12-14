package Damas;

import excepciones.*; 
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.*;

/**
 *
 * @author pablo
 */

public class Partida {
   
    private Ficha[][] Tablero; //Las entradas null representaran casillas vacias en el tablero
    private ArrayList<Ficha> Blancas; //Array que guardara referencias a todas la fichas del equipo blanco
    private ArrayList<Ficha> Negras; //Array que guardara referencias a todas la fichas del equipo negro
    private String turno;
    private boolean puedeSeguirCapturando;//Sera un indicador que sera true cuando una dama haya comido una pieza y pueda seguir comiendo.
    private StringBuffer Historial; 
    
    public Partida (){    
        initComponents(); 
    }
    
    private void initComponents(){          
        turno = "B";
        puedeSeguirCapturando = false;
        Tablero = new Ficha[8][8];
        Historial = new StringBuffer();
        Blancas = new ArrayList<>();
        Negras = new ArrayList<>();
        
        int c = 1;
        for (int i = 0; i < 3; i++){
            for (int j = c % 2; j < 8; j += 2){
                Ficha aux = Tablero[i][j] = new Dama(new coordenadas(i,j),"N");
                Negras.add(aux);
            }
            c++;
        }
        for (int i = 5; i < 8; i++){
            for (int j = c % 2; j < 8; j += 2){
                Ficha aux = Tablero[i][j] = new Dama(new coordenadas(i,j),"B");
                Blancas.add(aux);
            }
            c++;
        }  
    }
    
    //Varios getters y setters asi como funciones auxiliares sencillas
    
    public Ficha getFichaAt(coordenadas p){
        return Tablero[p.x][p.y];
    }
    
    public void setFicha(Ficha f,coordenadas p) {
        Tablero[p.x][p.y]=f;      
        Tablero[p.x][p.y].setPosicion(p);
    }
    
    public void borrarFicha(coordenadas p) {
        Tablero[p.x][p.y]=null;       
    }
        
    public String getTurno() {
        return turno;
    }

    public void changeTurno() {
        if(turno.equals("B")) this.turno = "N";
        else{this.turno="B";}
    }
    
    public boolean isTurno(coordenadas p){
        if(this.getFichaAt(p)==null) return false;
        else if(this.getFichaAt(p).getColor()==turno) return true;
        return false;
    }
    
    public boolean puedeSeguirCapturando() {
        return puedeSeguirCapturando;
    }

    public void setPuedeSeguirCapturando(boolean puedeSeguirCapturando) {
        this.puedeSeguirCapturando = puedeSeguirCapturando;
    }
    
    //Funcion que me indica si el bando de la ficha introducida como argumento puede capturar alguna ficha del rival
    public boolean puedesCapturar(){
        if(turno.equals("B")){
            for(int i=0; i<Blancas.size();i++){
                Blancas.get(i).calcularDestinos(this);
                if(!Blancas.get(i).destinosCaptura.isEmpty()) return true;
            }
            return false;
        }
        else{
            for(int i=0; i<Negras.size();i++){
                Negras.get(i).calcularDestinos(this);
                if(!Negras.get(i).destinosCaptura.isEmpty()) return true;
            }
            return false;
        }
    } 
    
    //Funcion que me indica si el bando de la ficha introducida como argumento puede moverse algun otro sitio, en caso contrario habra empate
    public boolean puedesMoverte(){
        if(turno.equals("B")){
            for(int i=0; i<Blancas.size();i++){
                Blancas.get(i).calcularDestinos(this);
                if(!Blancas.get(i).destinos.isEmpty()) return true;
            }
            return false;
        }
        else{
            for(int i=0; i<Negras.size();i++){
                Negras.get(i).calcularDestinos(this);
                if(!Negras.get(i).destinos.isEmpty()) return true;
            }
            return false;
        }
    } 
    
    //Funcion que mueve una ficha de una posicion de tablero a otra y elimina las fichas sobre las que salta
    public void moverFicha(coordenadas p,coordenadas q) throws VictoriaException{
        this.setFicha(this.getFichaAt(p), q);
        this.borrarFicha(p);
        if(!q.equals(p))this.eliminarFichasComidas(p,q);
        this.coronar();
        this.getFichaAt(q).calcularDestinos(this);
    }
    
    //Funcion que modifica los atributos de tu partida
    public void modificarPartida(ArrayList<coordenadas> pointers) throws PuedoSeguirCapturandoException,VictoriaException{     
        coordenadas p = pointers.get(0);
        coordenadas q = pointers.get(1);
        Ficha pFicha=getFichaAt(p);
        Ficha qFicha=getFichaAt(q);
        
        pFicha.calcularDestinos(this);
          
        if(!pFicha.esDestino(q)) { return; } //Si no es un destino valido no modifica la partida
        
        if(pFicha.esDestinoCaptura(q)) { //Si es un destino de captura sienpre me voy a poder mover ahí
            this.moverFicha(p,q);
            if(!getFichaAt(q).destinosCaptura.isEmpty()) //Si además despues de moverve sigue habiendo destinos de capuras entonces puede seguir comiendo
            {
                throw new PuedoSeguirCapturandoException(q);  
            }
            this.changeTurno();
        }
        else
        {
            this.moverFicha(p,q);
            this.changeTurno();
            if(!this.puedesMoverte()){
                throw new VictoriaException("Empate");//Si el rival no se puede mover es empate
            }
        }   
    }
    
    //Funcion que se llama en el metodo moverFicha para eliminar todas las fichas sobre las que has pasado al realizar el movimiento
    private void eliminarFichasComidas(coordenadas q,coordenadas p) throws VictoriaException{
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
                        Blancas.remove(faux);
                        if(faux instanceof Reina) Historial.append("RB"+aux.toString());
                        if(faux instanceof Dama) Historial.append("DB"+aux.toString());
                    }
                    else if(this.getFichaAt(aux).getColor().equals("N") && i>0){
                        Negras.remove(faux);
                        if(faux instanceof Reina) Historial.append("RN"+aux.toString());
                        if(faux instanceof Dama) Historial.append("DN"+aux.toString());
                    }
                    this.borrarFicha(aux);
                    
                    //Revisamos si ya gano algun bando o si se ha empatado
                    if(Blancas.size()==0) throw new VictoriaException("Ganan Negras");
                    if(Negras.size()==0) throw new VictoriaException("Ganan Blancas");
                    if(Negras.size()==1 && Blancas.size()==1) throw new VictoriaException("Empate");
                }
            }
            catch(FueraTableroException e){}
        }
        Historial.append("\n");
    }
    
    //Funcion que barre la primera y ultima fila buscando posibles damas que promocionar a reinas
    private void coronar(){
        for(int i=0;i<8;i++){
            coordenadas q = new coordenadas(0,i);
            coordenadas k = new coordenadas(7,i);
            Ficha fq = getFichaAt(q);
            Ficha fk = getFichaAt(k);
            if(fq!=null && fq.getColor().equals("B") && fq instanceof Dama){
                Blancas.remove(fq);
                this.setFicha(new Reina(q,"B"), q);
                Blancas.add(this.getFichaAt(q));
                Historial.deleteCharAt(Historial.length()-1);
                Historial.append("c\n");
            }
            if(fk!=null && fk.getColor().equals("N") && fk instanceof Dama){
                Negras.remove(fk);
                this.setFicha(new Reina(k,"N"), k);
                Negras.add(this.getFichaAt(k));
                Historial.deleteCharAt(Historial.length()-1);
                Historial.append("c\n");
            }    
        }
    }
    
    public void GuardarPartida(String archivo){ 
        
        StringBuffer str = new StringBuffer();  
   
        str.append(Historial);
        
        str.append(turno+"\n");
        
        if(puedeSeguirCapturando)str.append("T"+DamasGUI.Pointers.get(0).toString()+"\n");
        else str.append("F"+"\n");
        
        for(int i = 0; i<Blancas.size(); i++)
        {
           str.append(Blancas.get(i).toString()+"\n");
        }
        for(int i = 0; i<Negras.size(); i++)
        {
           str.append(Negras.get(i).toString()+"\n");
        }
        
        File directorio = new File("PartidasGuardadas");
        File archivoNuevo = new File(directorio,archivo);
        try {
           
            if (!archivoNuevo.exists()) {
                archivoNuevo.createNewFile();
                System.out.println("Partida guardada: " + archivoNuevo.getAbsolutePath());   
            }

            // Escribir en el archivo
            try (FileWriter escritor = new FileWriter(archivoNuevo)) {
                escritor.write(str.toString());
            }
        } catch (IOException e) {}
    }

    public void CargarPartida(String partida){
        
        //Borro los atributos de mi partida actual
        
        Tablero=new Ficha[8][8];
        Blancas.clear();
        Negras.clear();
        
        ArrayList<String> lines=new ArrayList<>();
        
        File archivo = new File(partida);

        try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
            String linea;
            // Leer línea por línea omitiendo lineas vacias
            while ((linea = lector.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    lines.add(linea);
                }
            }
        } catch (IOException e) {
            System.out.println("Ocurrió un error al leer el archivo: " + e.getMessage());
        }
        
        int i=0;
        
        //Voy almacenando en el hostorial hasta que llego a la linea que me dice el turno
        while(!(lines.get(i).endsWith("N")||lines.get(i).endsWith("B"))){
            Historial.append(lines.get(i)+"\n");
            i++;
        }
        //Guardo el turno
        if(lines.get(i).charAt(0)=='N')turno="N";
        else turno="B";
        i++;
        //Esta linea me dice el valor de la variable puedeSeguirComiendo
        if(lines.get(i).equals("F"))puedeSeguirCapturando=false;
        else{ // Si es true debo figar el primer pointer para obligarme a mover la ficha correspndiente en el siguiente turno
            puedeSeguirCapturando=true;
            DamasGUI.Pointers.clear();
            DamasGUI.Pointers.add(new coordenadas(lines.get(i).charAt(1)-'0',lines.get(i).charAt(2)-'0'));
        }
        i++;
        //Ahora voy leyendo y almacenando las fichas blancas y negras
        while(i<lines.size()){
            if(lines.get(i).charAt(0)=='D' && lines.get(i).charAt(1)=='B'){
                Blancas.add(new Dama(new coordenadas(lines.get(i).charAt(2)-'0',lines.get(i).charAt(3)-'0'),"B"));
            }
            else if(lines.get(i).charAt(0)=='R' && lines.get(i).charAt(1)=='B'){       
                Blancas.add(new Reina(new coordenadas(lines.get(i).charAt(2)-'0',lines.get(i).charAt(3)-'0'),"B"));
            }
            else if(lines.get(i).charAt(0)=='D' && lines.get(i).charAt(1)=='N'){
                Negras.add(new Dama(new coordenadas(lines.get(i).charAt(2)-'0',lines.get(i).charAt(3)-'0'),"N"));
            }
            else if(lines.get(i).charAt(0)=='R' && lines.get(i).charAt(1)=='N'){ 
                Negras.add(new Reina(new coordenadas(lines.get(i).charAt(2)-'0',lines.get(i).charAt(3)-'0'),"N"));
            }
            i++;      
        }
        for(i=0;i<Blancas.size();i++){
            this.setFicha(Blancas.get(i), Blancas.get(i).getPosicion());
        }
        for(i=0;i<Negras.size();i++){
            setFicha(Negras.get(i), Negras.get(i).getPosicion());
        }   
    }

    public void nuevaPartida(){
        initComponents();   
    }

    public void retoceder(){   
        if(Historial.length()<4) return; //No hay escrta ninguna jugada
        String his = this.Historial.toString();
        coordenadas p, q, aux;
        ArrayList<String> movimientos = new ArrayList<>(his.lines().toList()); //
        String ultimoMovimiento = movimientos.get(movimientos.size() - 1);
        String penultimoMovimiento;
        Ficha auxFicha;
        
        int T = (ultimoMovimiento.length()/4); //Un movimiento ocupa 4 caracteres y cada ficha que haya comido otros 4
        p = new coordenadas(ultimoMovimiento.charAt(0)-'0',ultimoMovimiento.charAt(1)-'0');
        q = new coordenadas(ultimoMovimiento.charAt(2)-'0',ultimoMovimiento.charAt(3)-'0');
        if(ultimoMovimiento.length()%4==0)auxFicha = this.getFichaAt(q);
        else{
            auxFicha = new Dama(q,this.getFichaAt(q).getColor());
            if(this.getFichaAt(q).getColor().equals("B")){
            Blancas.remove(this.getFichaAt(q));
            Blancas.add(auxFicha);
            }
            else{
            Negras.remove(this.getFichaAt(q));
            Negras.add(auxFicha);    
            }
        }//La unica posibilidad de que la longitud del movimiento no sea multiplo de 4 es que haya coronado en ese movimiento
 
        this.setFicha(auxFicha, p);
        this.borrarFicha(q);
        int contador=1;
        //Ahora vamos a recuperar la fichas que se han comido en el ultimo movimiento
        while(T>contador){
            aux = new coordenadas(ultimoMovimiento.charAt(contador*4+2)-'0',ultimoMovimiento.charAt(contador*4+3)-'0');
            if(ultimoMovimiento.charAt(contador*4)=='D'&& ultimoMovimiento.charAt(contador*4+1)=='B') {
                auxFicha=new Dama(aux,"B");
                this.setFicha(auxFicha, aux);
                Blancas.add(auxFicha);
            } 
            if(ultimoMovimiento.charAt(contador*4)=='D'&& ultimoMovimiento.charAt(contador*4+1)=='N'){
                auxFicha=new Dama(aux,"N");
                this.setFicha(auxFicha, aux);
                Negras.add(auxFicha);
            }
            if(ultimoMovimiento.charAt(contador*4)=='R'&& ultimoMovimiento.charAt(contador*4+1)=='B'){
                auxFicha=new Reina(aux,"B");
                this.setFicha(auxFicha, aux);
                Blancas.add(auxFicha);
            }
            if(ultimoMovimiento.charAt(contador*4)=='R'&& ultimoMovimiento.charAt(contador*4+1)=='N'){
                auxFicha=new Reina(aux,"");
                this.setFicha(auxFicha, aux);
                Negras.add(auxFicha);
            }
            contador++;
        }
        
        DamasGUI.Pointers.clear();
        
        //Quiero que el turno no cambie si en la anterior jugada podia seguir comiendo en cadena
        if(!(ultimoMovimiento.length()>5 && ultimoMovimiento.charAt(5)!=turno.charAt(0))) {    
            this.changeTurno();
        }
        
        this.puedeSeguirCapturando=false; //A priori lo pongo como false
        
        if(movimientos.size()>=2) {
            penultimoMovimiento=movimientos.get(movimientos.size() - 2);
            //Si en el penultimo movimiento capturé y no ha cambiado el turno es que podia seguir comiendo
            if(penultimoMovimiento.length()>5 && penultimoMovimiento.charAt(5)!=turno.charAt(0)){ 
                this.puedeSeguirCapturando=true;
                DamasGUI.Pointers.add(p); //Fijo la posicion de la ficha para obligarme a moverla despues de retroceder
            }        
        }
        movimientos.removeLast();
        Historial = new StringBuffer(String.join("\n", movimientos)+"\n"); //Recupero el historial
    }
}
    


