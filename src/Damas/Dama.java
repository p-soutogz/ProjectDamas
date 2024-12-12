package Damas;

/**
 *
 * @author pablo
 */
public class Dama extends Ficha{
    
    String color;
    
    public Dama(coordenadas p,String col){
        super(p);
        color=col;
    }

    public void calcularDestinos(Juego J) {
        destinos.clear();
        destinosCaptura.clear();
        
        coordenadas[] d = new coordenadas[4];
        d[0] = new coordenadas(1,1);
        d[1] = new coordenadas(1,-1);
        d[2] = new coordenadas(-1,1);
        d[3] = new coordenadas(-1,-1);
        
        for(int i=0;i<4;i++)
        {
            try
            {
            coordenadas q = this.getPosicion().add(d[i]);
            
            if(J.getFichaAt(q)==null) destinos.add(q);
                   
            coordenadas q2 = this.getPosicion().add(d[i].por(2));
            
            if(J.getFichaAt(q2)==null && J.getFichaAt(q)!=null && J.getFichaAt(q).getColor().equals(this.Rival())) 
            {
                destinos.add(q2);
                destinosCaptura.add(q2);
            }
           
            }
            catch(Exception e){} 
        }
    }
  
    public String getColor() { return color; }
  
    public String toString(){
        return "D"+this.getColor()+super.getPosicion().toString();
    }
  
}
