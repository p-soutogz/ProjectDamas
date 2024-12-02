/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package excepciones;

import Damas.coordenadas;
/**
 *
 * @author pablo
 */
public class CapturadoException extends Exception {
    
    private coordenadas p; //Coordenadas de la ficha que acaba de comer
    
    public CapturadoException(coordenadas p)
    {
        super();
        this.p=p;
    }
    public coordenadas getcoordenadas ()
    {
        return this.p;
    }
    
}
