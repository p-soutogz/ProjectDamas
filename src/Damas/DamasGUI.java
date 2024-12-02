package Damas;

import excepciones.*;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DamasGUI extends javax.swing.JFrame {
    
    public static ArrayList<coordenadas> Pointers = new ArrayList<>();
    private JLabel[][] c; 
    private JLabel statusLabel;
    public static Juego miPartida = new Juego();
    public static ImageIcon DamaBlanca;
    public static ImageIcon DamaNegra;
    public static ImageIcon ReinaBlanca;
    public static ImageIcon ReinaNegra;
    
    static
    {
    DamaBlanca = new ImageIcon("C:/Users/pablo/Desktop/Iconos damas/dama blanca.png");
    Image image = DamaBlanca.getImage();
    DamaBlanca = new ImageIcon(image.getScaledInstance(80, 80, Image.SCALE_SMOOTH));
    DamaNegra = new ImageIcon("C:/Users/pablo/Desktop/Iconos damas/dama negra.png");
    image = DamaNegra.getImage();
    DamaNegra = new ImageIcon(image.getScaledInstance(80, 80, Image.SCALE_SMOOTH));
    ReinaBlanca = new ImageIcon("C:/Users/pablo/Desktop/Iconos damas/reina blanca.png");
    image = ReinaBlanca.getImage();
    ReinaBlanca = new ImageIcon(image.getScaledInstance(80, 80, Image.SCALE_SMOOTH));
    ReinaNegra = new ImageIcon("C:/Users/pablo/Desktop/Iconos damas/reina negra.png");
    image = ReinaNegra.getImage();
    ReinaNegra = new ImageIcon(image.getScaledInstance(80, 80, Image.SCALE_SMOOTH));
    }
       
    public DamasGUI() {
        
        initComponents();

        // Inicializar Pointers con arrays vacíos de tamaño 2
             
    }
    
    private void initComponents() {
    // Inicialización de los componentes
    c = new javax.swing.JLabel[8][8];
    
    statusLabel = new JLabel("Pointers: []");
    statusLabel.setSize(400, 40);
    statusLabel.setFont(statusLabel.getFont().deriveFont(20.0f));
    
    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    
    JPanel jTablero = new javax.swing.JPanel();

    jTablero.setLayout(new java.awt.GridLayout(8, 8));
    jTablero.setPreferredSize(new java.awt.Dimension(600, 600)); 
    jTablero.setBorder(BorderFactory.createLineBorder(Color.BLACK, 10));
    
    for (int i = 0; i < 8; i++) {
    for (int j = 0; j < 8; j++) {
        final int row = i; 
        final int col = j;

        c[row][col] = new javax.swing.JLabel();
        c[row][col].setOpaque(true);
        
        if ((row + col) % 2 == 0) {
                c[row][col].setBackground(new Color(252, 239, 199)); 
            
        } else {
            c[row][col].setBackground(new Color(120, 78, 24));
        }

        c[row][col].setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLACK));

        
        c[row][col].addMouseListener(new GuardarClick(this,new coordenadas(row,col)));

        jTablero.add(c[row][col]);  
    }
}

    
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.add(statusLabel, java.awt.BorderLayout.NORTH); 
    mainPanel.add(jTablero, java.awt.BorderLayout.CENTER); 
    
   
    getContentPane().add(mainPanel);

    
    pack();
}
                          
    public void actualizarPointer(coordenadas values) {
        Ficha f;
        actualizarStatusLabel();
        
        if(Pointers.isEmpty() && miPartida.getFichaAt(values).isEmpty())
        {
            return;    
        }
       
        switch(Pointers.size()){
            
            case 1: 
                f=miPartida.getFichaAt(Pointers.get(0));
                f.calcularDestinos(miPartida);
                if((f.esValido(values) && !miPartida.isHasCapturado()) || (miPartida.isHasCapturado() && f.esValido(values) && f.esCaptura(values))){
                    Pointers.add(values); 
                    imprimirPointers();
                }
                else if(Pointers.get(0).equals(values) && !miPartida.isHasCapturado()) {
                    Pointers.clear();
                }
                actualizarStatusLabel();
                miPartida.getFichaAt(values).imprimirDestinos();  
                break;
            case 0:
                if(miPartida.isTurno(values))Pointers.add(values); 
                actualizarStatusLabel();
                miPartida.getFichaAt(values).calcularDestinos(miPartida);
                miPartida.getFichaAt(values).imprimirDestinos(); 
                //imprimirPointers();
                break;
        }
        
        if (Pointers.size()==2)
        {
            try{
                miPartida.modificarTablero(Pointers);
                Pointers.clear(); 
                miPartida.setHasCapturado(false);
            }
            catch(CapturadoException e){
                miPartida.setHasCapturado(true);
                System.out.print("CAPTURA!!!!!!!!!! \n");
                Pointers.clear();
                Pointers.add(e.getcoordenadas());
                miPartida.getFichaAt(e.getcoordenadas()).calcularDestinos(miPartida);
                miPartida.getFichaAt(e.getcoordenadas()).imprimirDestinos();
                actualizarStatusLabel();
            }
            finally{
                actualizarLabels();  
                actualizarStatusLabel();
            }            
        }   
    }
    
    private void imprimirPointers() {
        for (int i = 0; i < Pointers.size(); i++) {
            coordenadas pointer = Pointers.get(i);
            System.out.println("P" + (i + 1) + ": "+pointer.toString());
        }
    }
    
    private String PointersToString() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < Pointers.size(); i++) {
            str.append(Pointers.get(i).toString());
            str.append("  ");
        }
        return str.toString();
    }
    
    public void actualizarStatusLabel() {
        
        statusLabel.setText("Pointers = "+this.PointersToString() + "Juegan: "+miPartida.getTurno());
    }
    
    public void actualizarLabels()
    {
        for(int i=0;i<8;i++)
            {
                for(int j=0;j<8;j++)
                {
                    coordenadas aux = new coordenadas(i,j);
                    Ficha faux = miPartida.getFichaAt(aux);
                    
                    if(faux.getColor().equals("N") && faux instanceof Dama) {
                        c[i][j].setIcon(DamaNegra); 
                        
                    }
                    else if(faux.getColor().equals("B") && faux instanceof Dama){
                        c[i][j].setIcon(DamaBlanca);
                        
                    }
                    else if(faux.getColor().equals("N") && faux instanceof Reina){
                        c[i][j].setIcon(ReinaNegra);
                        
                    }
                    else if(faux.getColor().equals("B") && faux instanceof Reina){
                        //System.out.print("Ostiaaaaaa");
                        c[i][j].setIcon(ReinaBlanca);
                        
                    }
                    else {
                        c[i][j].setIcon(null);   
                    }
                }  
            }    
    }    
}


class GuardarClick extends MouseAdapter {
    
    DamasGUI D;
    coordenadas p;
    
    public GuardarClick (DamasGUI Da,coordenadas p)
    {
        D=Da;
        this.p=p;
    }
            
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                D.actualizarPointer(p);
            }
        }