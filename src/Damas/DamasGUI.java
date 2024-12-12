package Damas;

import excepciones.*;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File; 
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author pablo
 */

public class DamasGUI extends javax.swing.JFrame {
    
    public static ArrayList<coordenadas> Pointers = new ArrayList<>();
    private JLabel[][] c; 
    private JLabel statusLabel;
    private JButton AtrasButton;
    static Juego miPartida = new Juego();
    private static ImageIcon DamaBlanca;
    private static ImageIcon DamaNegra;
    private static ImageIcon ReinaBlanca;
    private static ImageIcon ReinaNegra;
    
    static
    {
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
    c = new javax.swing.JLabel[8][8];
    
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

    this.setJMenuBar(menuBar); // Establecer el menú en la ventana
    
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
        c[row][col].setHorizontalAlignment(JLabel.CENTER); 
        c[row][col].setVerticalAlignment(JLabel.CENTER);
        jTablero.add(c[row][col]);  
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
            }            
        }   
    }
       
    public void actualizarStatusLabel() {
        
        if(miPartida.getTurno().equals("B"))statusLabel.setText( "Juegan Blancas");
        else statusLabel.setText( "Juegan Negras");
    }
    
    public void actualizarLabels()
    {
        for(int i=0;i<8;i++)
            {
                for(int j=0;j<8;j++)
                {   
                    c[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
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
        int row,col;
        if(!miPartida.HasCapturado() && !miPartida.puedesCapturar(faux))
        {
            for(int i = 0 ; i<faux.destinos.size(); i++)
            {
                row=faux.destinos.get(i).x;
                col=faux.destinos.get(i).y;
                c[row][col].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 5));
            } 
        }
        else{
            for(int i = 0 ; i<faux.destinosCaptura.size(); i++)
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
         D.miPartida.retoceder();
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
        // Crear un selector de archivos
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar Partida Guardada");
        
        // Establecer el directorio predeterminado como la carpeta "partidas guardadas"
        fileChooser.setCurrentDirectory(new File("PartidasGuardadas"));
             
        int userSelection = fileChooser.showOpenDialog(D);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            String nombreArchivo = fileToLoad.getAbsolutePath();
            
            try {
                DamasGUI.miPartida.CargarPartida(nombreArchivo);
                D.actualizarLabels();
                D.actualizarStatusLabel();
                if (!DamasGUI.Pointers.isEmpty()) {
                    D.actualizarDestinosLabels(DamasGUI.Pointers.get(0));
                }   
            } catch (Exception e) {
                JOptionPane.showMessageDialog(D, "Error al cargar la partida: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

class GuardarButtonClick extends MouseAdapter {
    
    DamasGUI D;
    
    public GuardarButtonClick (DamasGUI D)
    {
        this.D=D;
    }
    
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        // Mostrar un cuadro de diálogo para que el usuario introduzca el nombre del archivo
        String nombreArchivo = JOptionPane.showInputDialog(D, "Introduce el nombre del archivo:", "Guardar Partida", JOptionPane.PLAIN_MESSAGE);
        
        // Comprobar si el usuario ha introducido un nombre y no ha cancelado el cuadro de diálogo
        if (nombreArchivo != null && !nombreArchivo.trim().isEmpty()) {
            DamasGUI.miPartida.GuardarPartida(nombreArchivo);
            JOptionPane.showMessageDialog(D, "Partida guardada como " + nombreArchivo, "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(D, "Nombre de archivo no válido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class NuevaPartidaButtonClick extends MouseAdapter {
    
    DamasGUI D;
    
    public NuevaPartidaButtonClick (DamasGUI D)
    {
        this.D=D;
    }
    
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        DamasGUI.miPartida.nuevaPartida();
        D.actualizarLabels();
        DamasGUI.Pointers.clear();
    }
}

