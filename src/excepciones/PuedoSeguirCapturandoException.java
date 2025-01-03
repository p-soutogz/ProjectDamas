package excepciones;

import Damas.coordenadas;
/**
 *
 * @author pablo
 */
public class PuedoSeguirCapturandoException extends Exception {
    
    private coordenadas p; //Coordenadas de la ficha que acaba de comer
    
    public PuedoSeguirCapturandoException(coordenadas p){
        super();
        this.p=p;
    }
    public coordenadas getcoordenadas (){
        return this.p;
    }
    
}
