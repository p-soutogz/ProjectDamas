package Damas;

import excepciones.*;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 *
 * @author pablo
 */

public class DamasGUI extends javax.swing.JFrame {
    
    public static ArrayList<coordenadas> Pointers = new ArrayList<>();
    private JLabel[][] casillas; 
    private JLabel statusLabel;
    private JButton AtrasButton;
    static Partida miPartida = new Partida();
    private static ImageIcon DamaBlanca;
    private static ImageIcon DamaNegra;
    private static ImageIcon ReinaBlanca;
    private static ImageIcon ReinaNegra;
    
    static{
    DamaBlanca = new ImageIcon("Iconos damas/damablanca.png");
    Image image = DamaBlanca.getImage();
    DamaBlanca = new ImageIcon(image.getScaledInstance(65, 65, Image.SCALE_SMOOTH));
    DamaNegra = new ImageIcon("Iconos damas/damanegra.png");
    image = DamaNegra.getImage();
    DamaNegra = new ImageIcon(image.getScaledInstance(65, 65, Image.SCALE_SMOOTH));
    ReinaBlanca = new ImageIcon("Iconos damas/reinablanca.png");
    image = ReinaBlanca.getImage();
    ReinaBlanca = new ImageIcon(image.getScaledInstance(65, 65, Image.SCALE_SMOOTH));
    ReinaNegra = new ImageIcon("Iconos damas/reinanegra.png");
    image = ReinaNegra.getImage();
    ReinaNegra = new ImageIcon(image.getScaledInstance(65, 65, Image.SCALE_SMOOTH));
    }
       
    public DamasGUI() {  
        initComponents();     
    }
    
    private void initComponents() {
    // Inicialización de los componentes
    casillas = new javax.swing.JLabel[8][8];
    
    statusLabel = new JLabel("Pointers: []");
    statusLabel.setFont(new Font("Arial", Font.PLAIN, 20));
    
    AtrasButton = new JButton("Atras");
    AtrasButton.setFont(new Font("Arial", Font.PLAIN, 20));
    AtrasButton.addMouseListener(new retroButtonClick(this));
    
    // Configurar el menú
    JMenuBar menuBar = new JMenuBar();
    menuBar.setFont(new Font("Arial", Font.PLAIN, 20)); // Fuente más grande para la barra de menú
    menuBar.setPreferredSize(new Dimension(100, 40)); // Aumenta la altura de la barra de menú
    
    JMenu archivoMenu = new JMenu("Menu");
    archivoMenu.setFont(new Font("Arial", Font.PLAIN, 18)); // Fuente más grande para el menú

    
    JMenuItem guardarMenuItem = new JMenuItem("Guardar Partida");
    guardarMenuItem.setFont(new Font("Arial", Font.PLAIN, 16)); // Fuente más grande para los elementos
    guardarMenuItem.addActionListener(e -> {
        // Acciones para el botón "Guardar"
        new GuardarButtonClick(this).mouseClicked(null); // Llama al evento relacionado
    });

    JMenuItem cargarMenuItem = new JMenuItem("Cargar Partida");
    cargarMenuItem.setFont(new Font("Arial", Font.PLAIN, 16)); // Fuente más grande para los elementos
    cargarMenuItem.addActionListener(e -> {
        // Acciones para el botón "Cargar"
        new CargarButtonClick(this).mouseClicked(null); // Llama al evento relacionado
    });
    
    JMenuItem nuevaPartidaMenuItem = new JMenuItem("Nueva Partida");
    nuevaPartidaMenuItem.setFont(new Font("Arial", Font.PLAIN, 16)); // Fuente más grande para los elementos
    nuevaPartidaMenuItem.addActionListener(e -> {
        // Acciones para el botón "Cargar"
        new NuevaPartidaButtonClick(this).mouseClicked(null); // Llama al evento relacionado
    });
    
    archivoMenu.add(nuevaPartidaMenuItem);
    archivoMenu.add(guardarMenuItem);
    archivoMenu.add(cargarMenuItem);
    menuBar.add(archivoMenu);

    this.setJMenuBar(menuBar); 
    
    this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
   
    //Configuarar rl tablero de casillas
    
    JPanel jTablero = new javax.swing.JPanel();
    jTablero.setLayout(new java.awt.GridLayout(8, 8));
    jTablero.setPreferredSize(new java.awt.Dimension(600, 600)); 
    jTablero.setBorder(BorderFactory.createLineBorder(Color.BLACK, 10));
  
    for (int i = 0; i < 8; i++) {
    for (int j = 0; j < 8; j++) {
        int row = i; 
        int col = j;

        casillas[row][col] = new javax.swing.JLabel();
        casillas[row][col].setOpaque(true);
        
        if ((row + col) % 2 == 0) {
                casillas[row][col].setBackground(new Color(252, 239, 199)); 

        } else {
            casillas[row][col].setBackground(new Color(120, 78, 24));
        }

        casillas[row][col].setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLACK));
        casillas[row][col].addMouseListener(new GuardarClick(this,new coordenadas(row,col)));
        casillas[row][col].setHorizontalAlignment(JLabel.CENTER); 
        casillas[row][col].setVerticalAlignment(JLabel.CENTER);
        jTablero.add(casillas[row][col]);  
    }
}
 
    JPanel mainPanel = new JPanel(new BorderLayout());
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 3));
    
    buttonPanel.add(AtrasButton);
    buttonPanel.add(statusLabel);
    
    mainPanel.add(buttonPanel, java.awt.BorderLayout.NORTH); 
    mainPanel.add(jTablero, java.awt.BorderLayout.CENTER); 
    
    getContentPane().add(mainPanel);
    pack();
}
    
    //Funcion que ira recogiendo o ignorando los click sobre las casillas de tablero
    public void actualizarPointer(coordenadas values) {
        Ficha aux,aux2;
        actualizarStatusLabel();
        switch(Pointers.size()){
            case 0: 
                //Si la primera casilla que seleccionas esta vacia ignoramos el click
                if(miPartida.isTurno(values)){
                    Pointers.add(values);
                    actualizarStatusLabel();
                    actualizarDestinosLabels(values);
                } 
                break;
            case 1: // Ya tenemos seleccionada una ficha 
                aux=miPartida.getFichaAt(Pointers.get(0));
                aux2=miPartida.getFichaAt(values);
                aux.calcularDestinos(miPartida);
                //Para recojer el segundo click verificamos que se cumplen las condiciones para realizar un movimiento valido
                if( (aux.esDestino(values) && !aux.esDestinoCaptura(values) && !miPartida.puedesCapturar()) || aux.esDestinoCaptura(values) 
                     || (miPartida.puedeSeguirCapturando() && aux.esDestino(values) && aux.esDestinoCaptura(values))){
                    Pointers.add(values); 
                    break;
                }
                //Añado esta linea para poder deseleccionar la ficha seleccionada por otra del mismo bando simpre que no este obligado a comer con la ficha 
                //ya seleccionada, es decir si puedeSeguirCapturando==false
                else if(aux2!=null && aux.getColor().equals(aux2.getColor()) && !miPartida.puedeSeguirCapturando()) {
                    actualizarCasillas();
                    Pointers.clear();
                    Pointers.add(values);
                    actualizarDestinosLabels(values);
                }
                actualizarStatusLabel();
                break;
        }
        //Ahora que tenemos los dos click intentamos realizar un movimiento y actualizamos los Pointers
        if (Pointers.size()==2){
            try{
                miPartida.modificarPartida(Pointers);
                Pointers.clear(); 
                miPartida.setPuedeSeguirCapturando(false);
            }
            catch(VictoriaException e1){
                actualizarCasillas();  
                this.TerminarPrograma(e1.getMessage());
            }
            catch(PuedoSeguirCapturandoException e2){
                miPartida.setPuedeSeguirCapturando(true);
                Pointers.clear();
                Pointers.add(e2.getcoordenadas());//Fijamos la coordenada de la ficha que debe seguir comiendo
                actualizarDestinosLabels(Pointers.get(0));
            }
            finally{
                actualizarCasillas();  
                actualizarStatusLabel();
                if(!Pointers.isEmpty()&& Pointers.get(0)!=null) actualizarDestinosLabels(Pointers.get(0));
            }            
        }   
    }
    
    //Metodo que actualiza la etiqueta de la parte superior que indica el turno
    public void actualizarStatusLabel() {
        if(miPartida.getTurno().equals("B"))statusLabel.setText( "Juegan Blancas");
        else statusLabel.setText( "Juegan Negras");
    }
    
    //Metodo que actualiza las labels de las casillas para que se reflejen los cambios en la partida
    public void actualizarCasillas(){
        for(int i=0;i<8;i++){
                for(int j=0;j<8;j++){   
                    casillas[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    coordenadas aux = new coordenadas(i,j);
                    Ficha faux = miPartida.getFichaAt(aux);
                    if(faux==null) casillas[i][j].setIcon(null);  
                    else{
                        if(faux!=null && faux.getColor().equals("N") && faux instanceof Dama) {
                            casillas[i][j].setIcon(DamaNegra);
                        }
                        else if(faux.getColor().equals("B") && faux instanceof Dama){
                            casillas[i][j].setIcon(DamaBlanca);                       
                        }
                        else if(faux.getColor().equals("N") && faux instanceof Reina){
                            casillas[i][j].setIcon(ReinaNegra);                       
                        }
                        else if(faux.getColor().equals("B") && faux instanceof Reina){
                            casillas[i][j].setIcon(ReinaBlanca);                       
                        }
                    }
                }  
            }    
    }    
   
    //Metodo que pinta el borde de las casillas a las que puede viajar la ficha situada en la casilla p
    public void actualizarDestinosLabels(coordenadas p){   
        actualizarCasillas();
        Ficha faux = miPartida.getFichaAt(p);
        if(faux==null) return;
        faux.calcularDestinos(miPartida);
        int row,col;
        if(!miPartida.puedeSeguirCapturando() && !miPartida.puedesCapturar()){
            for(int i = 0 ; i<faux.destinos.size(); i++)
            {
                row=faux.destinos.get(i).x;
                col=faux.destinos.get(i).y;
                casillas[row][col].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 5));
            } 
        }
        else{
            for(int i = 0 ; i<faux.destinosCaptura.size(); i++){
                row=faux.destinosCaptura.get(i).x;
                col=faux.destinosCaptura.get(i).y;
                casillas[row][col].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 5));
            }    
        }
    }
    
    private void TerminarPrograma(String str){
        JOptionPane.showMessageDialog(this,str,"",JOptionPane.INFORMATION_MESSAGE);
        // Finalizar el programa después de mostrar el mensaje
        this.dispose();
        System.exit(0); // Finalizar el programa
        
    }
}

//Implementacion de los distintos liseners liseners

class GuardarClick extends MouseAdapter {
    
    DamasGUI D;
    coordenadas p;
    
    public GuardarClick (DamasGUI Da,coordenadas p){
        D=Da;
        this.p=p;
    }
            
    public void mouseClicked(java.awt.event.MouseEvent evt) {
         D.actualizarPointer(p);
    }
}

class retroButtonClick extends MouseAdapter {
    
    DamasGUI D;
    
    public retroButtonClick (DamasGUI D){
        this.D=D;
    }
    
    public void mouseClicked(java.awt.event.MouseEvent evt) {
         D.miPartida.retoceder();
         D.actualizarCasillas();
         D.actualizarStatusLabel();
         if(!DamasGUI.Pointers.isEmpty())D.actualizarDestinosLabels(DamasGUI.Pointers.get(0));
    }
}

class CargarButtonClick extends MouseAdapter {
    
    DamasGUI D;
    
    public CargarButtonClick (DamasGUI D){
        this.D=D;
    }
     public void mouseClicked(java.awt.event.MouseEvent evt) {
         
        // Crear un selector de archivos
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar Partida Guardada");
        fileChooser.setCurrentDirectory(new File("PartidasGuardadas"));
             
        int userSelection = fileChooser.showOpenDialog(D);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            String nombreArchivo = fileToLoad.getAbsolutePath();
            
            try {
                DamasGUI.miPartida.CargarPartida(nombreArchivo);
                D.actualizarCasillas();
                D.actualizarStatusLabel();
                if (!DamasGUI.Pointers.isEmpty()) {
                    D.actualizarDestinosLabels(DamasGUI.Pointers.get(0));
                }   
            } catch (Exception e) {}
        }
    }
}

class GuardarButtonClick extends MouseAdapter {
    
    DamasGUI D;
    
    public GuardarButtonClick (DamasGUI D){
        this.D=D;
    }
    public void mouseClicked(java.awt.event.MouseEvent evt) {

        String nombreArchivo = JOptionPane.showInputDialog(D, "Introduce el nombre del archivo:", "Guardar Partida", JOptionPane.PLAIN_MESSAGE);
        // Comprobar se ha introducido un nombre y no ha cancelado el cuadro de diálogo
        if (nombreArchivo != null && !nombreArchivo.trim().isEmpty()) {
            DamasGUI.miPartida.GuardarPartida(nombreArchivo);
        } 
    }
}

class NuevaPartidaButtonClick extends MouseAdapter {
    
    DamasGUI D;

    public NuevaPartidaButtonClick (DamasGUI D){
        this.D=D;
    }
    
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        DamasGUI.miPartida.nuevaPartida();
        D.actualizarCasillas();
        DamasGUI.Pointers.clear();
    }
}

