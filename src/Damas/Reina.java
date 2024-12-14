package Damas;

/**
 *
 * @author pablo
 */
public class Reina extends Ficha{
 
    String color;
    
    public Reina (coordenadas p,String col){
        super(p);
        color=col;
    }
  
    public void calcularDestinos(Partida J) {
        destinos.clear();
        destinosCaptura.clear();
        boolean auxBool = false; // Este booleano sera false a priori y se volvera true si en una diagonal puedo comer una ficha, así apartir de ese momento todas
        //la casillas vacias que se encuentren en la diagonal se almacenaran como un destino de captura.Cuando se haya barrido toda la diagonal se pondrá como false.
        coordenadas[] direcciones = new coordenadas[4];
        direcciones[0] = new coordenadas(1,1);
        direcciones[1] = new coordenadas(1,-1);
        direcciones[2] = new coordenadas(-1,1);
        direcciones[3] = new coordenadas(-1,-1);
        
        //Barro la cada diagonal buscando casillas vacias o fichas que pueda capturar
        for(int i=0;i<4;i++){
            for(int j=1;j<8;j++){
                try{
                coordenadas q = this.getPosicion().add(direcciones[i].por(j));
                //Si encuentro una ficha de mi bando dejo de buscar en la diagonal mas destinos
                if(J.getFichaAt(q)!=null && J.getFichaAt(q).getColor().equals(this.getColor())){
                    break;
                }
                //Si la casilla esta vacia es un destino
                if(J.getFichaAt(q)==null) destinos.add(q);
                   
                coordenadas q2 = this.getPosicion().add(direcciones[i].por(j+1));
                
                //Añadimos ahora los destinos que implican una captura
                if((J.getFichaAt(q2)==null && J.getFichaAt(q)!=null && J.getFichaAt(q).getColor().equals(this.Rival())) || (J.getFichaAt(q2)==null && auxBool)) { 
                    destinos.add(q2);
                    destinosCaptura.add(q2);
                    auxBool=true;
                }
           
                }
            catch(Exception e){break;} 
            }
            auxBool=false;
        }
    }   
    
    public String getColor() { return color; }
    
    public String toString(){
        return "R"+this.getColor()+super.getPosicion().toString();
    }
    
}
