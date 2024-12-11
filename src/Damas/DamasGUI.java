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
    private JButton retroButton;
    private JButton CargarButton;
    private JButton GuardarButton;
    public static Juego miPartida = new Juego();
    public static ImageIcon DamaBlanca;
    public static ImageIcon DamaNegra;
    public static ImageIcon ReinaBlanca;
    public static ImageIcon ReinaNegra;
    
    static
    {
    DamaBlanca = new ImageIcon("Iconos damas/dama blanca.png");
    Image image = DamaBlanca.getImage();
    DamaBlanca = new ImageIcon(image.getScaledInstance(80, 80, Image.SCALE_SMOOTH));
    DamaNegra = new ImageIcon("Iconos damas/dama negra.png");
    image = DamaNegra.getImage();
    DamaNegra = new ImageIcon(image.getScaledInstance(80, 80, Image.SCALE_SMOOTH));
    ReinaBlanca = new ImageIcon("Iconos damas/reina blanca.png");
    image = ReinaBlanca.getImage();
    ReinaBlanca = new ImageIcon(image.getScaledInstance(80, 80, Image.SCALE_SMOOTH));
    ReinaNegra = new ImageIcon("Iconos damas/reina negra.png");
    image = ReinaNegra.getImage();
    ReinaNegra = new ImageIcon(image.getScaledInstance(80, 80, Image.SCALE_SMOOTH));
    }
       
    public DamasGUI() {
        
        initComponents();     
    }
    
    private void initComponents() {
    // Inicialización de los componentes
    c = new javax.swing.JLabel[8][8];
    
    statusLabel = new JLabel("Pointers: []");
    statusLabel.setFont(statusLabel.getFont().deriveFont(20.0f));
    
    retroButton = new JButton("Retroceder");
    retroButton.setFont(statusLabel.getFont().deriveFont(20.0f));
    retroButton.addMouseListener(new retroButtonClick(this));
    
    CargarButton = new JButton("Cargar");
    CargarButton.addMouseListener(new CargarButtonClick(this));
    CargarButton.setFont(statusLabel.getFont().deriveFont(20.0f));
    
    GuardarButton = new JButton("Guardar");
    GuardarButton.addMouseListener(new GuardarButtonClick(this));
    GuardarButton.setFont(statusLabel.getFont().deriveFont(20.0f));
    
    this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
  
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
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 3));
    
    buttonPanel.add(GuardarButton);
    buttonPanel.add(CargarButton);
    buttonPanel.add(retroButton);
    buttonPanel.add(statusLabel);
    
    mainPanel.add(buttonPanel, java.awt.BorderLayout.NORTH); 
    mainPanel.add(jTablero, java.awt.BorderLayout.CENTER); 
    
    getContentPane().add(mainPanel);
    pack();
}
                          
    public void actualizarPointer(coordenadas values) {
        Ficha aux;
        Ficha aux2;
        actualizarStatusLabel();
        
        switch(Pointers.size()){
            
            case 0:
                if(miPartida.isTurno(values)){
                    Pointers.add(values);
                    actualizarStatusLabel();
                    actualizarDestinosLabels(values);
                } 
                break;
            
            case 1: 
                aux=miPartida.getFichaAt(Pointers.get(0));
                aux2=miPartida.getFichaAt(values);
                aux.calcularDestinos(miPartida);
                if( (aux.esValido(values) && !aux.esCaptura(values) && !miPartida.puedesCapturar(aux)) || aux.esCaptura(values) || (miPartida.HasCapturado() && aux.esValido(values) && aux.esCaptura(values))){
                    Pointers.add(values); 
                    break;
                }
                else if(aux2!=null && aux.getColor().equals(aux2.getColor()) && !miPartida.HasCapturado()) {
                    actualizarLabels();
                    Pointers.clear();
                    Pointers.add(values);
                    actualizarDestinosLabels(values);
                }
                actualizarStatusLabel();
                break;
        }
        
        if (Pointers.size()==2)
        {
            try{
                miPartida.modificarTablero(Pointers);
                Pointers.clear(); 
                miPartida.setHasCapturado(false);
            }
            catch(VictoriaException e1){
                actualizarLabels();  
                this.TerminarPrograma(e1.getMessage());
            }
            catch(CapturadoException e2){
                miPartida.setHasCapturado(true);
                Pointers.clear();
                Pointers.add(e2.getcoordenadas());
                if(!Pointers.isEmpty())actualizarDestinosLabels(Pointers.get(0));
            }
            finally{
                actualizarLabels();  
                actualizarStatusLabel();
                if(!Pointers.isEmpty()&& Pointers.get(0)!=null) actualizarDestinosLabels(Pointers.get(0));
                //miPartida.Historial.append(miPartida.toString());
                System.out.print("////////////////"+"\n");
                System.out.print(miPartida.Historial);
            }            
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
                    c[i][j].setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLACK));
                    coordenadas aux = new coordenadas(i,j);
                    Ficha faux = miPartida.getFichaAt(aux);
                    if(faux==null) c[i][j].setIcon(null);  
                    else{
                        if(faux!=null && faux.getColor().equals("N") && faux instanceof Dama) {
                            c[i][j].setIcon(DamaNegra);   
                        }
                        else if(faux.getColor().equals("B") && faux instanceof Dama){
                            c[i][j].setIcon(DamaBlanca);                       
                        }
                        else if(faux.getColor().equals("N") && faux instanceof Reina){
                            c[i][j].setIcon(ReinaNegra);                       
                        }
                        else if(faux.getColor().equals("B") && faux instanceof Reina){
                            c[i][j].setIcon(ReinaBlanca);                       
                        }
                    }
                }  
            }    
    }    
    
    public void actualizarDestinosLabels(coordenadas p){   
        actualizarLabels();
        Ficha faux = miPartida.getFichaAt(p);
        faux.calcularDestinos(miPartida);
        int n = faux.destinos.size();
        int m = faux.destinosCaptura.size();
        int row,col;
        if(!miPartida.HasCapturado() && !miPartida.puedesCapturar(faux))
        {
            for(int i = 0 ; i<n; i++)
            {
                row=faux.destinos.get(i).x;
                col=faux.destinos.get(i).y;
                c[row][col].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 5));
            } 
        }
        else{
            for(int i = 0 ; i<m; i++)
            {
                row=faux.destinosCaptura.get(i).x;
                col=faux.destinosCaptura.get(i).y;
                c[row][col].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 5));
            }    
        }
    }
    
    private void TerminarPrograma(String str)
    {
        JOptionPane.showMessageDialog(
        this,
        str,
        "",
        JOptionPane.INFORMATION_MESSAGE
        );

        // Finalizar el programa después de mostrar el mensaje
        this.dispose(); // Liberar los recursos de la ventana
        System.exit(0); // Finalizar el programa
        
    }
}


//Implementacion de los liseners

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

class retroButtonClick extends MouseAdapter {
    
    DamasGUI D;
    
    public retroButtonClick (DamasGUI D)
    {
        this.D=D;
    }
    
    public void mouseClicked(java.awt.event.MouseEvent evt) {
         DamasGUI.miPartida.retoceder();
         D.actualizarLabels();
         D.actualizarStatusLabel();
         if(!DamasGUI.Pointers.isEmpty())D.actualizarDestinosLabels(DamasGUI.Pointers.get(0));
    }
}

class CargarButtonClick extends MouseAdapter {
    
    DamasGUI D;
    
    public CargarButtonClick (DamasGUI D)
    {
        this.D=D;
    }
    
    public void mouseClicked(java.awt.event.MouseEvent evt) {
         DamasGUI.miPartida.CargarPartida();
         D.actualizarLabels();
         D.actualizarStatusLabel();
    }
}

class GuardarButtonClick extends MouseAdapter {
    
    DamasGUI D;
    
    public GuardarButtonClick (DamasGUI D)
    {
        this.D=D;
    }
    
    public void mouseClicked(java.awt.event.MouseEvent evt) {
         DamasGUI.miPartida.GuardarPartida();
    }
}