package Damas;

import java.util.Objects;
import java.util.ArrayList;

/**
 *
 * @author pablo
 */
public abstract class Ficha {
    
    private coordenadas posicion;
    protected ArrayList<coordenadas> destinosCaptura;
    protected ArrayList<coordenadas> destinos;
    
    public Ficha(coordenadas p){
        posicion=p;
        destinos = new ArrayList<>();
        destinosCaptura = new ArrayList<>();
    }
    
    public coordenadas getPosicion() {
        return posicion;
    }

    public void setPosicion(coordenadas posicion) {
        this.posicion = posicion;
    }
    
    public abstract String getColor();
    
    public abstract void calcularDestinos(Juego J);
     
    public boolean esValido(coordenadas p){   
        for (int i = 0; i < destinosCaptura.size(); i++){
            if (p.equals(destinosCaptura.get(i))){
                return true;
            }
        }
        if(destinosCaptura.isEmpty()){
            for (int i = 0; i < destinos.size(); i++){
                if (p.equals(destinos.get(i))){
                return true;
                }
            } 
    }
    
    return false;   
    } 
    
    public boolean esCaptura(coordenadas p) {    
        for (int i = 0; i < destinosCaptura.size(); i++){
            if (p.equals(destinosCaptura.get(i))){
                return true;
            }
        }
        return false;   
    }
    
    public String Rival (){
        if(this.getColor().equals("N")) return "B";
        if(this.getColor().equals("B")) return "N";
        return " ";
    }
    
    public abstract String toString();
    
    public boolean equals(Object obj) {
        if (this == obj) return true; // Compara si son la misma referencia
        if (obj == null || getClass() != obj.getClass()) return false; // Verifica el tipo

        Ficha other = (Ficha) obj;
        // Compara las coordenadas y el color
        return Objects.equals(posicion, other.posicion);
    }

    public int hashCode() {
        // Genera un hash basado en las coordenadas y el color
        return Objects.hash(posicion);
    }
    
}



