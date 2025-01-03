package Damas;

/**
 *
 * @author pablo
 */
public class Dama extends Ficha {

    public Dama(coordenadas p, String col) {
        super(p, col);
    }

    public void calcularDestinos(Partida J) {
        //Borro los antiguos destinos de la ficha
        destinos.clear();
        destinosCaptura.clear();

        coordenadas[] direcciones = new coordenadas[4];
        direcciones[0] = new coordenadas(1, 1);
        direcciones[1] = new coordenadas(1, -1);
        direcciones[2] = new coordenadas(-1, 1);
        direcciones[3] = new coordenadas(-1, -1);

        //En cada direccion busco casillas vacias o enemigos que pueda capturar
        for (int i = 0; i < 4; i++) {
            try {
                coordenadas q = this.getPosicion().add(direcciones[i]);

                if (J.getFichaAt(q) == null) {
                    destinos.add(q);
                }

                coordenadas q2 = this.getPosicion().add(direcciones[i].por(2));

                if (J.getFichaAt(q2) == null && J.getFichaAt(q) != null && J.getFichaAt(q).getColor().equals(this.Rival())) {
                    destinos.add(q2);
                    destinosCaptura.add(q2);
                }

            } catch (Exception e) {
            }
        }
    }

}
