package Damas;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.ArrayList;
import excepciones.*;
import java.util.List;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.*;

/**
 *
 * @author pablo
 */

public class Juego {
   
    private Ficha[][] Tablero; 
    private ArrayList<Ficha> Blancas;
    private ArrayList<Ficha> Negras;
    public String turno;
    public boolean hasCapturado;//Sera un indicador que sera true cuando una dama haya comido una pieza siempre que pueda comer mas.
    public StringBuffer Historial; 
    
    public Juego ()
    {    
        initComponents(); 
    }
    
    private void initComponents()
    {          
        turno = "B";
        hasCapturado = false;
        Tablero = new Ficha[8][8];
        Historial = new StringBuffer();
        Blancas = new ArrayList<>();
        Negras = new ArrayList<>();
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
      
    public Ficha getFichaAt(coordenadas p)
    {
        return Tablero[p.x][p.y];
    }
    
    public void moveFicha(coordenadas p,coordenadas q) throws VictoriaException
    {
        Ficha aux = this.getFichaAt(p);
        Tablero[q.x][q.y]=aux;
        Tablero[q.x][q.y].setPosicion(q);
        Tablero[p.x][p.y]=null;
        if(!q.equals(p))this.eliminarFichasComidas(p,q);
        aux.calcularDestinos(this);
    }
    
    public void setFicha(Ficha f,coordenadas p) 
    {
        Tablero[p.x][p.y]=f;      
        Tablero[p.x][p.y].setPosicion(p);
    }
    
    public void borrarFicha(coordenadas p) 
    {
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
    
    public boolean HasCapturado() {
        return hasCapturado;
    }

    public void setHasCapturado(boolean hasCapturado) {
        this.hasCapturado = hasCapturado;
    }
   
    public void modificarTablero(ArrayList<coordenadas> pointers) throws CapturadoException,VictoriaException
    {     
        coordenadas p = pointers.get(0);
        coordenadas q = pointers.get(1);
        Ficha fp=getFichaAt(p);
        Ficha fq=getFichaAt(q);
        
        fp.calcularDestinos(this);
          
        if(!fp.esValido(q)) { return; }
        
        if(fp.esCaptura(q)) { 
            this.moveFicha(p,q);
            if(!getFichaAt(q).destinosCaptura.isEmpty())
            {
                this.coronar();
                throw new CapturadoException(q);  
            }
            this.changeTurno();
            this.coronar();
        }
        else
        {
            this.moveFicha(p,q);
            this.changeTurno();
            this.coronar();
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
                    
                    if(Blancas.size()==0) throw new VictoriaException("Ganan Negras");
                    if(Negras.size()==0) throw new VictoriaException("Ganan Blancas");
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
            Ficha fq = getFichaAt(q);
            Ficha fk = getFichaAt(k);
            if(fq!=null && fq.getColor().equals("B") && fq instanceof Dama){
                this.setFicha(new Reina(q,"B"), q);
                Historial.deleteCharAt(Historial.length()-1);
                Historial.append("c\n");
            }
            if(fk!=null && fk.getColor().equals("N") && fk instanceof Dama){
                this.setFicha(new Reina(k,"N"), k);
                Historial.deleteCharAt(Historial.length()-1);
                Historial.append("c\n");
            }
            
        }
    }
    
    public void GuardarPartida(String archivo)
    { 
        StringBuffer str = new StringBuffer();  
     
        str.append(Historial+"\n");
        
        str.append(turno+"\n");
        
        if(hasCapturado)str.append("T"+DamasGUI.Pointers.get(0).toString()+"\n");
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
            // Crear el archivo si no existe
            if (!archivoNuevo.exists()) {
                if (archivoNuevo.createNewFile()) {
                    System.out.println("Archivo creado: " + archivoNuevo.getAbsolutePath());
                } else {
                    System.out.println("No se pudo crear el archivo.");
                    return;
                }
            }

            // Escribir en el archivo
            try (FileWriter escritor = new FileWriter(archivoNuevo)) {
                escritor.write(str.toString());
                System.out.println("Contenido escrito en el archivo.");
            }

        } catch (IOException e) {
            System.out.println("Ocurrió un error: " + e.getMessage());
        }
    }
          
    public void CargarPartida(String partida){
        
        Tablero=new Ficha[8][8];
        Blancas.clear();
        Negras.clear();
        
        List<String> lines=new ArrayList<>();
        
        File archivo = new File(partida);

        // Verificar si el archivo existe
        if (!archivo.exists()) {
            System.out.println("El archivo no existe: " + archivo.getAbsolutePath());
            return;
        }

        try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
            String linea;
            System.out.println("Contenido del archivo (omitiendo líneas vacías):");

            // Leer línea por línea
            while ((linea = lector.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                 // Procesa directamente cada línea en lugar de acumular todas
                    System.out.println(linea);
                    lines.add(linea);
                }
            }
            

        } catch (IOException e) {
            System.out.println("Ocurrió un error al leer el archivo: " + e.getMessage());
        }
        
        int i=0;
        while(!(lines.get(i).endsWith("N")||lines.get(i).endsWith("B")))
        {
            Historial.append(lines.get(i)+"\n");
            i++;
        }
        System.out.print(Historial);
        
        if(lines.get(i).charAt(0)=='N')turno="N";
        else turno="B";
        
        i++;
        System.out.print(turno+"\n");
        if(lines.get(i).equals("F"))hasCapturado=false;
        else{
            hasCapturado=true;
            DamasGUI.Pointers.clear();
            DamasGUI.Pointers.add(new coordenadas(lines.get(i).charAt(1)-'0',lines.get(i).charAt(2)-'0'));
        }
        i++;
        
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
            setFicha(Blancas.get(i), Blancas.get(i).getPosicion());
        }
        for(i=0;i<Negras.size();i++){
            setFicha(Negras.get(i), Negras.get(i).getPosicion());
        }
        
        //if(turno.equals("N"))DamasGUI.Pointers.add(Negras.getLast().getPosicion());
        //if(turno.equals("B"))DamasGUI.Pointers.add(Negras.getLast().getPosicion());
        
        ImpFichas();
           
    }
    
    public void nuevaPartida(){
        initComponents();   
    }
    
    public void retoceder()
    {
        if(Historial.length()<4) return;
        String his = this.Historial.toString();
        coordenadas p, q, aux;
        List<String> lines = new ArrayList<>(his.lines().toList());
        String mov = lines.get(lines.size() - 1);
        String movAnterior;
        Ficha faux;
        
        int T = (mov.length()/4);
        p = new coordenadas(mov.charAt(0)-'0',mov.charAt(1)-'0');
        q = new coordenadas(mov.charAt(2)-'0',mov.charAt(3)-'0');
        if(mov.length()%4==0)faux = this.getFichaAt(q);
        else faux = new Dama(q,this.getFichaAt(q).getColor());
        faux.setPosicion(p);
        this.setFicha(faux, p);
        this.borrarFicha(q);
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
        
        if(!(mov.length()>5 && mov.charAt(5)!=turno.charAt(0))) {    
            this.changeTurno();
        }
        
        this.hasCapturado=false;
        
        if(lines.size()>=2) {
            
            movAnterior=lines.get(lines.size() - 2);
            if(movAnterior.length()>5 && movAnterior.charAt(5)!=turno.charAt(0)){
                this.hasCapturado=true;
                DamasGUI.Pointers.add(p);
            }        
        }
        lines.removeLast();
        Historial = new StringBuffer(String.join("\n", lines)+"\n");
    }
    
    public boolean puedesCapturar(Ficha f)
    {
        if(f.getColor()=="B")
        {
            for(int i=0; i<Blancas.size();i++)
            {
                Blancas.get(i).calcularDestinos(this);
                if(!Blancas.get(i).destinosCaptura.isEmpty()) return true;
            }
            return false;
        }
        else
        {
            for(int i=0; i<Negras.size();i++)
            {
                Negras.get(i).calcularDestinos(this);
                if(!Negras.get(i).destinosCaptura.isEmpty()) return true;
            }
            return false;
        }
    }
    
    public void ImpFichas()
    {
        System.out.print("\n Fichas Blancas: \n");
        for(int i = 0; i< Blancas.size(); i++)
        {
            System.out.print(Blancas.get(i).toString());
        }
        
        System.out.print("\n Fichas Negras: \n");
        
        for(int i = 0; i< Negras.size(); i++)
        {
            System.out.print(Negras.get(i).toString());

        }
        
    }
    
    
}
