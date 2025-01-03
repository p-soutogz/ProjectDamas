package Damas;

import excepciones.*;
import java.util.ArrayList;

/**
 *
 * @author pablo
 */

public class Partida {

    private ArrayList<Ficha> Blancas; //Array que guardara referencias a todas la fichas del equipo blanco
    private ArrayList<Ficha> Negras; //Array que guardara referencias a todas la fichas del equipo negro
    private String turno;
    private boolean puedeSeguirCapturando;//Sera un indicador que sera true cuando una dama haya comido una pieza y pueda seguir comiendo.
    private StringBuffer Historial;

    public Partida() {
        initComponents();
    }

    public void initComponents() {
        turno = "B";
        puedeSeguirCapturando = false;
        Historial = new StringBuffer();
        Blancas = new ArrayList<>();
        Negras = new ArrayList<>();

        int c = 1;
        for (int i = 0; i < 3; i++) {
            for (int j = c % 2; j < 8; j += 2) {
                Negras.add(new Dama(new coordenadas(i, j), "N"));
            }
            c++;
        }
        for (int i = 5; i < 8; i++) {
            for (int j = c % 2; j < 8; j += 2) {
                Blancas.add(new Dama(new coordenadas(i, j), "B"));
            }
            c++;
        }
    }

    //Varios getters y setters asi como funciones auxiliares sencillas
    public String getHistorial() {
        return Historial.toString();
    }

    public Ficha getFichaAt(coordenadas p) {

        for (int i = 0; i < Blancas.size(); i++) {
            if (Blancas.get(i).getPosicion().equals(p)) {
                return Blancas.get(i);
            }
        }
        for (int i = 0; i < Negras.size(); i++) {
            if (Negras.get(i).getPosicion().equals(p)) {
                return Negras.get(i);
            }
        }
        return null;
    }

    public String getTurno() {
        return turno;
    }

    public void borrarFicha(coordenadas p) {
        Ficha faux = this.getFichaAt(p);
        if (faux == null) {
            return;
        }
        if (faux.getColor() == "B") {
            Blancas.remove(this.getFichaAt(p));
        } else {
            Negras.remove(this.getFichaAt(p));
        }
    }

    public void changeTurno() {
        if (turno.equals("B")) {
            this.turno = "N";
        } else {
            this.turno = "B";
        }
    }

    public boolean isTurno(coordenadas p) {
        if (this.getFichaAt(p) == null) {
            return false;
        } else if (this.getFichaAt(p).getColor() == turno) {
            return true;
        }
        return false;
    }

    public boolean puedeSeguirCapturando() {
        return puedeSeguirCapturando;
    }

    public void setPuedeSeguirCapturando(boolean puedeSeguirCapturando) {
        this.puedeSeguirCapturando = puedeSeguirCapturando;
    }

    //Funcion que me indica si el bando de la ficha introducida como argumento puede capturar alguna ficha del rival
    public boolean puedesCapturar() {
        if (turno.equals("B")) {
            for (int i = 0; i < Blancas.size(); i++) {
                Blancas.get(i).calcularDestinos(this);
                if (!Blancas.get(i).destinosCaptura.isEmpty()) {
                    return true;
                }
            }
            return false;
        } else {
            for (int i = 0; i < Negras.size(); i++) {
                Negras.get(i).calcularDestinos(this);
                if (!Negras.get(i).destinosCaptura.isEmpty()) {
                    return true;
                }
            }
            return false;
        }
    }

    //Funcion que me indica si el bando de la ficha introducida como argumento puede moverse algun otro sitio, en caso contrario habra empate
    public boolean puedesMoverte() {
        if (turno.equals("B")) {
            for (int i = 0; i < Blancas.size(); i++) {
                Blancas.get(i).calcularDestinos(this);
                if (!Blancas.get(i).destinos.isEmpty()) {
                    return true;
                }
            }
            return false;
        } else {
            for (int i = 0; i < Negras.size(); i++) {
                Negras.get(i).calcularDestinos(this);
                if (!Negras.get(i).destinos.isEmpty()) {
                    return true;
                }
            }
            return false;
        }
    }

    //Funcion que mueve una ficha de una posicion de tablero a otra y elimina las fichas sobre las que salta
    public void moverFicha(coordenadas p, coordenadas q) throws VictoriaException {
        this.getFichaAt(p).moveTo(q);
        this.borrarFicha(p);
        if (!q.equals(p)) {
            this.eliminarFichasComidas(p, q);
        }
        this.coronar();
        this.getFichaAt(q).calcularDestinos(this);
    }

    //Funcion que modifica los atributos de tu partida
    public void modificarPartida(ArrayList<coordenadas> pointers) throws PuedoSeguirCapturandoException, VictoriaException {
        coordenadas p = pointers.get(0);
        coordenadas q = pointers.get(1);
        Ficha pFicha = getFichaAt(p);

        pFicha.calcularDestinos(this);

        if (!pFicha.esDestino(q)) {
            return;
        } //Si no es un destino valido no modifica la partida

        if (pFicha.esDestinoCaptura(q)) { //Si es un destino de captura sienpre me voy a poder mover ahí
            this.moverFicha(p, q);
            if (!getFichaAt(q).destinosCaptura.isEmpty()) //Si además despues de moverve sigue habiendo destinos de capuras entonces puede seguir comiendo
            {
                throw new PuedoSeguirCapturandoException(q);
            }
            this.changeTurno();
        } else {
            this.moverFicha(p, q);
            this.changeTurno();
            if (!this.puedesMoverte()) {
                throw new VictoriaException("Empate");//Si el rival no se puede mover es empate
            }
        }
    }

    //Funcion que se llama en el metodo moverFicha para eliminar todas las fichas sobre las que has pasado al realizar el movimiento
    private void eliminarFichasComidas(coordenadas q, coordenadas p) throws VictoriaException {
        int m = Math.abs(q.getX() - p.getX());
        coordenadas d = coordenadas.direccion(q, p);
        Historial.append(q.toString() + p.toString());
        for (int i = 1; i < m; i++) {
            try {
                coordenadas aux = q.add(d.por(i));
                Ficha faux = this.getFichaAt(aux);
                if (faux instanceof Dama || faux instanceof Reina) {
                    if (faux.getColor().equals("B") && i > 0) {
                        Blancas.remove(faux);
                    } else if (this.getFichaAt(aux).getColor().equals("N") && i > 0) {
                        Negras.remove(faux);
                    }
                    this.borrarFicha(aux);

                    //Revisamos si ya gano algun bando o si se ha empatado
                    if (Blancas.size() == 0) {
                        throw new VictoriaException("Ganan Negras");
                    }
                    if (Negras.size() == 0) {
                        throw new VictoriaException("Ganan Blancas");
                    }
                    if (Negras.size() == 1 && Blancas.size() == 1) {
                        throw new VictoriaException("Empate");
                    }
                }
            } catch (FueraTableroException e) {
            }
        }
        Historial.append("\n");
    }

    //Funcion que barre la primera y ultima fila buscando posibles damas que promocionar a reinas
    private void coronar() {
        for (int i = 0; i < 8; i++) {
            coordenadas q = new coordenadas(0, i);
            coordenadas k = new coordenadas(7, i);
            Ficha fq = getFichaAt(q);
            Ficha fk = getFichaAt(k);
            if (fq != null && fq.getColor().equals("B") && fq instanceof Dama) {
                Blancas.remove(fq);
                Blancas.add(new Reina(q, "B"));
            }
            if (fk != null && fk.getColor().equals("N") && fk instanceof Dama) {
                Negras.remove(fk);
                Negras.add(new Reina(k, "N"));
            }
        }
    }

}
  
       

    


