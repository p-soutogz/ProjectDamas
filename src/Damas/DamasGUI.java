package Damas;


import excepciones.*;

import java.util.ArrayList;
import java.io.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author pablo
 */
public class DamasGUI extends javax.swing.JFrame {

    private ArrayList<coordenadas> Pointers = new ArrayList<>();
    private Partida miPartida = new Partida();
    private JLabel[][] casillas;
    private JLabel TurnoLabel;
    private ImageIcon DamaBlanca;
    private ImageIcon DamaNegra;
    private ImageIcon ReinaBlanca;
    private ImageIcon ReinaNegra;

    public DamasGUI() {
        initComponents();
        this.actualizarCasillas();
        this.actualizarTurnoLabel();
    }

    private void initComponents() {
        // Inicialización de los componentes

        //Primero cargo las imagenes para las damas y reinas    
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

        casillas = new javax.swing.JLabel[8][8];

        TurnoLabel = new JLabel();
        TurnoLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        JButton AtrasButton = new JButton("Atras");
        AtrasButton.setFont(new Font("Arial", Font.PLAIN, 20));
        AtrasButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ArrayList<String> lines = new ArrayList<>(miPartida.getHistorial().lines().toList());
                coordenadas p, q;
                String movimiento;

                // Reinicio los atributos de la partida
                miPartida.initComponents();
                actualizarCasillas();
                actualizarTurnoLabel();

                if (lines.isEmpty()) {
                    return; // No hay jugadas en el historial
                }
                lines.remove(lines.size() - 1);
                if (lines.isEmpty()) {
                    return; // Solo había un movimiento
                }
                int i = 0;
                do {
                    Pointers.clear();
                    movimiento = lines.get(i);
                    p = new coordenadas(movimiento.charAt(0) - '0', movimiento.charAt(1) - '0');
                    q = new coordenadas(movimiento.charAt(2) - '0', movimiento.charAt(3) - '0');
                    Pointers.add(p);
                    Pointers.add(q);

                    try {
                        miPartida.modificarPartida(Pointers);
                        Pointers.clear();
                        miPartida.setPuedeSeguirCapturando(false);
                    } catch (VictoriaException e1) {

                    } catch (PuedoSeguirCapturandoException e2) {
                        miPartida.setPuedeSeguirCapturando(true);
                        Pointers.clear();
                        Pointers.add(e2.getcoordenadas()); // Fijar coordenada de ficha obligada a capturar
                    }
                    i++;
                } while (i < lines.size());

                // Actualizar interfaz
                actualizarCasillas();
                actualizarTurnoLabel();
                if (!Pointers.isEmpty()) {
                    actualizarDestinosLabels();
                }
            }
        });
        // Configurar el menú
        JMenuBar menuBar = new JMenuBar();
        menuBar.setFont(new Font("Arial", Font.PLAIN, 20));
        menuBar.setPreferredSize(new Dimension(100, 40));

        JMenu archivoMenu = new JMenu("Menu");
        archivoMenu.setFont(new Font("Arial", Font.PLAIN, 18));

        JMenuItem guardarMenuItem = new JMenuItem("Guardar Partida");
        guardarMenuItem.setFont(new Font("Arial", Font.PLAIN, 16));
        guardarMenuItem.addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Funcion para guardar partida
                String nombreArchivo = JOptionPane.showInputDialog(
                        guardarMenuItem, // Componente padre para el diálogo
                        "Introduce el nombre de tu partida:",
                        "Guardar Partida",
                        JOptionPane.PLAIN_MESSAGE
                );

                // Comprueba que se introdujo un nombre válido
                if (nombreArchivo != null && !nombreArchivo.trim().isEmpty()) {
                    File directorio = new File("PartidasGuardadas");
                    File archivoNuevo = new File(directorio, nombreArchivo);

                    try {
                        // Crea el directorio si no existe
                        if (!directorio.exists()) {
                            directorio.mkdirs();
                        }
                        // Crea el archivo si no existe
                        if (!archivoNuevo.exists()) {
                            archivoNuevo.createNewFile();
                            System.out.println("Partida guardada: " + archivoNuevo.getAbsolutePath());
                        } else {
                            JOptionPane.showMessageDialog(guardarMenuItem, "Esa partida ya existe, escoje otro nombre", "", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }

                        // Escribe en el archivo
                        try (FileWriter escritor = new FileWriter(archivoNuevo)) {
                            escritor.write(miPartida.getHistorial()); // Corrige la referencia a `miPartida`
                        }
                    } catch (IOException exc) {
                        JOptionPane.showMessageDialog(
                                guardarMenuItem,
                                "Error al guardar la partida: " + exc.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        }
        );

        JMenuItem cargarMenuItem = new JMenuItem("Cargar Partida");
        cargarMenuItem.setFont(new Font("Arial", Font.PLAIN, 16));
        cargarMenuItem.addActionListener(
            //Defino el listener CargarButtonClick aparte porque sino queda muy engorrosa la lectura    
            new CargarButtonClick(this)
        );

        JMenuItem nuevaPartidaMenuItem = new JMenuItem("Nueva Partida");
        nuevaPartidaMenuItem.setFont(new Font("Arial", Font.PLAIN, 16));
        nuevaPartidaMenuItem.addActionListener(e -> {
            miPartida.initComponents();
            actualizarCasillas();
            Pointers.clear();
        });

        archivoMenu.add(nuevaPartidaMenuItem);
        archivoMenu.add(guardarMenuItem);
        archivoMenu.add(cargarMenuItem);
        menuBar.add(archivoMenu);

        this.setJMenuBar(menuBar);

        this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        //Configuarar el tablero de casillas
        JPanel jTablero = new javax.swing.JPanel();
        jTablero.setLayout(new java.awt.GridLayout(8, 8));
        jTablero.setPreferredSize(new java.awt.Dimension(600, 600));
        jTablero.setBorder(BorderFactory.createLineBorder(Color.BLACK, 10));

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                casillas[i][j] = new javax.swing.JLabel();
                casillas[i][j].setOpaque(true);

                if ((i + j) % 2 == 0) {
                    casillas[i][j].setBackground(new Color(252, 239, 199));

                } else {
                    casillas[i][j].setBackground(new Color(120, 78, 24));
                }

                casillas[i][j].setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLACK));
                casillas[i][j].addMouseListener(new GuardarClick(new coordenadas(i, j)) {
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        actualizarPointer(p);
                    }
                });
                casillas[i][j].setHorizontalAlignment(JLabel.CENTER);
                casillas[i][j].setVerticalAlignment(JLabel.CENTER);
                jTablero.add(casillas[i][j]);
            }
        }

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 3));

        buttonPanel.add(AtrasButton);
        buttonPanel.add(TurnoLabel);

        mainPanel.add(buttonPanel, java.awt.BorderLayout.NORTH);
        mainPanel.add(jTablero, java.awt.BorderLayout.CENTER);

        getContentPane().add(mainPanel);
        pack();
    }

    //Funcion que irá recogiendo o ignorando los click sobre las casillas de tablero
    public void actualizarPointer(coordenadas values) {
        Ficha aux, aux2;
        actualizarTurnoLabel();
        switch (Pointers.size()) {
            case 0:
                //Si la primera casilla que seleccionas esta vacia ignoramos el click
                if (miPartida.isTurno(values)) {
                    Pointers.add(values);
                    actualizarTurnoLabel();
                    actualizarDestinosLabels();
                }
                break;
            case 1: // Ya tenemos seleccionada una ficha 
                aux = miPartida.getFichaAt(Pointers.get(0));
                aux2 = miPartida.getFichaAt(values);
                aux.calcularDestinos(miPartida);
                //Para recojer el segundo click verificamos que se cumplen las condiciones para realizar un movimiento valido
                if (aux.esDestinoCaptura(values) || (aux.esDestino(values) && (!miPartida.puedesCapturar() || miPartida.puedeSeguirCapturando()))) {
                    Pointers.add(values);
                    break;
                } /*Añado esta linea para poder deseleccionar la ficha seleccionada por otra del mismo bando simpre que 
                no este obligado a comer con la ficha ya seleccionada, es decir si puedeSeguirCapturando==false*/ else if (aux2 != null && aux.getColor().equals(aux2.getColor()) && !miPartida.puedeSeguirCapturando()) {
                    actualizarCasillas();
                    Pointers.clear();
                    Pointers.add(values);
                    actualizarDestinosLabels();
                }
                actualizarTurnoLabel();
                break;
        }
        //Ahora que tenemos los dos click intentamos realizar un movimiento y actualizamos los Pointers
        if (Pointers.size() == 2) {
            try {
                miPartida.modificarPartida(Pointers);
                Pointers.clear();
                miPartida.setPuedeSeguirCapturando(false);
            } catch (VictoriaException e1) {
                actualizarCasillas();
                this.TerminarPrograma(e1.getMessage());
            } catch (PuedoSeguirCapturandoException e2) {
                miPartida.setPuedeSeguirCapturando(true);
                Pointers.clear();
                Pointers.add(e2.getcoordenadas());//Fijamos la coordenada de la ficha que debe seguir comiendo
            } finally {
                actualizarCasillas();
                actualizarTurnoLabel();
                if (!Pointers.isEmpty() && Pointers.get(0) != null) {
                    actualizarDestinosLabels();
                }
            }
        }
    }

    //Metodo que actualiza la etiqueta de la parte superior que indica el turno
    public void actualizarTurnoLabel() {
        if (miPartida.getTurno().equals("B")) {
            TurnoLabel.setText("Juegan Blancas");
        } else {
            TurnoLabel.setText("Juegan Negras");
        }
    }

    //Metodo que actualiza las labels de las casillas para que se reflejen los cambios en la partida
    public void actualizarCasillas() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                casillas[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                coordenadas aux = new coordenadas(i, j);
                Ficha faux = miPartida.getFichaAt(aux);
                if (faux == null) {
                    casillas[i][j].setIcon(null);
                } else {
                    if (faux.getColor().equals("N") && faux instanceof Dama) {
                        casillas[i][j].setIcon(DamaNegra);
                    } else if (faux.getColor().equals("B") && faux instanceof Dama) {
                        casillas[i][j].setIcon(DamaBlanca);
                    } else if (faux.getColor().equals("N") && faux instanceof Reina) {
                        casillas[i][j].setIcon(ReinaNegra);
                    } else if (faux.getColor().equals("B") && faux instanceof Reina) {
                        casillas[i][j].setIcon(ReinaBlanca);
                    }
                }
            }
        }
    }

    //Metodo que pinta el borde de las casillas a las que puede viajar la ficha situada en la casilla p
    public void actualizarDestinosLabels() {
        coordenadas p = Pointers.get(0);
        actualizarCasillas();
        Ficha faux = miPartida.getFichaAt(p);
        if (faux == null) {
            return;
        }
        faux.calcularDestinos(miPartida);
        int row, col;
        if (!miPartida.puedeSeguirCapturando() && !miPartida.puedesCapturar()) {
            for (int i = 0; i < faux.destinos.size(); i++) {
                row = faux.destinos.get(i).getX();
                col = faux.destinos.get(i).getY();
                casillas[row][col].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 5));
            }
        } else {
            for (int i = 0; i < faux.destinosCaptura.size(); i++) {
                row = faux.destinosCaptura.get(i).getX();
                col = faux.destinosCaptura.get(i).getY();
                casillas[row][col].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 5));
            }
        }
    }

    //Metodo que cierra la ventana si algun jugador gana
    private void TerminarPrograma(String str) {
        JOptionPane.showMessageDialog(this, str, "", JOptionPane.INFORMATION_MESSAGE);
        // Finalizar el programa después de mostrar el mensaje
        this.dispose();
        System.exit(0); // Finalizar el programa

    }

    public Partida getMiPartida() {
        return miPartida;
    }

    public ArrayList<coordenadas> getPointers() {
        return Pointers;
    }
 
}

//Implementacion de los distintos liseners 
class GuardarClick extends MouseAdapter {

    coordenadas p;

    public GuardarClick(coordenadas p) {
        this.p = p;
    }
}

class CargarButtonClick implements ActionListener {

    DamasGUI GUI;

    public CargarButtonClick(DamasGUI D) {
        this.GUI = D;
    }

    public void CargarPartida(String partida) {
        //Funcion para cargar partida
        
        Partida miPartida = GUI.getMiPartida();
        ArrayList<coordenadas> pointers = GUI.getPointers();
        
        // Reinicio los atributos de mi partida
        miPartida.initComponents();

        ArrayList<String> lines = new ArrayList<>();
        File archivo = new File(partida);

        // Leer el archivo línea por línea
        try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    lines.add(linea);
                }
            }
        } catch (IOException e) {
            System.out.println("Ocurrió un error al leer el archivo: " + e.getMessage());
            return;
        }

        if (lines.isEmpty()) {
            System.out.println("El archivo está vacío.");
            return;
        }

        // Crear un temporizador para procesar los movimientos con un retraso, esto solo lo hago para crear un efecto visual bonito
        final int[] i = {0}; // Índice para acceder a las líneas, lo escribo así para que pueda acceder a el dentro de Timer y poder modificarlo.
        Timer timer = new Timer(300, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (i[0] < lines.size()) {
                    String movimiento = lines.get(i[0]);
                    if (movimiento.length() < 4) {
                        System.out.println("Línea inválida: " + movimiento);
                        ((Timer) e.getSource()).stop();
                        return;
                    }
                    coordenadas p = new coordenadas(movimiento.charAt(0) - '0', movimiento.charAt(1) - '0');
                    coordenadas q = new coordenadas(movimiento.charAt(2) - '0', movimiento.charAt(3) - '0');
                    pointers.clear();
                    pointers.add(p);
                    pointers.add(q);

                    try {
                        miPartida.modificarPartida(pointers);
                        pointers.clear();
                        miPartida.setPuedeSeguirCapturando(false);
                    } catch (VictoriaException e1) {
                    } catch (PuedoSeguirCapturandoException e2) {
                        miPartida.setPuedeSeguirCapturando(true);
                        pointers.clear();
                        pointers.add(e2.getcoordenadas());
                    } finally {
                        GUI.actualizarCasillas();
                        if (!pointers.isEmpty()) {
                            GUI.actualizarDestinosLabels();
                        }
                    }
                    i[0]++;
                } else {
                    ((Timer) e.getSource()).stop(); // Detener el temporizador al finalizar
                }
            }
        });

        timer.start(); // Iniciar el temporizador
    }

    public void actionPerformed(ActionEvent e) {

        // Crear un selector de archivos
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar Partida Guardada");
        fileChooser.setCurrentDirectory(new File("PartidasGuardadas"));

        int userSelection = fileChooser.showOpenDialog(GUI);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            String nombreArchivo = fileToLoad.getAbsolutePath();

            try {
                CargarPartida(nombreArchivo);
            } catch (Exception exc) {
            }

        }
    }

}




