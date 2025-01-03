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
    private String color;

    public Ficha(coordenadas p, String col) {
        posicion = p;
        color = col;
        destinos = new ArrayList<>(); //Posibles destinos a los que se puede mover una ficha a priori(Sin tener en cuenta si ya a comido antes o si alguna otra ficha de su equipo puede comer)
        destinosCaptura = new ArrayList<>();//Subconjunto de destinos a en los cuales se captura una ficha rival si te mueves a ellos
    }

    //Gettes y setters
    public coordenadas getPosicion() {
        return posicion;
    }

    public void moveTo(coordenadas posicion) {
        this.posicion = posicion;
    }

    public String getColor() {
        return color;
    }

    //Funcion que en un momento dado de la partida calcula los destinos y los destinos de captura
    public abstract void calcularDestinos(Partida J);

    //Funcion que comprueva si una coordenada es un destino.
    public boolean esDestino(coordenadas p) {
        for (int i = 0; i < destinosCaptura.size(); i++) {
            if (p.equals(destinosCaptura.get(i))) {
                return true;
            }
        }
        if (destinosCaptura.isEmpty()) {
            for (int i = 0; i < destinos.size(); i++) {
                if (p.equals(destinos.get(i))) {
                    return true;
                }
            }
        }

        return false;
    }

    //Funcion que comprueva si una coordenada es un destino de captura
    public boolean esDestinoCaptura(coordenadas p) {
        for (int i = 0; i < destinosCaptura.size(); i++) {
            if (p.equals(destinosCaptura.get(i))) {
                return true;
            }
        }
        return false;
    }

    public String Rival() {
        if (this.getColor().equals("N")) {
            return "B";
        }
        if (this.getColor().equals("B")) {
            return "N";
        }
        return " ";
    }

    //Redefiniciones de los metodos equals u hashcode necesarios para utilizar correctamente luego el metodo remove 
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; // Compara si son la misma referencia
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false; // Verifica el tipo
        }
        Ficha other = (Ficha) obj;
        // Compara las coordenadas y el color
        return Objects.equals(posicion, other.posicion);
    }

    public int hashCode() {
        // Genera un hash basado en las coordenadas y el color
        return Objects.hash(posicion);
    }

}

