import java.io.*;
import java.net.*;
import java.util.Scanner;


public class ServidorTCP {
    public static void main(String[] args) {
        int puerto = 12345; // Puerto donde el servidor escuchará
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor escuchando en el puerto " + puerto);

            // Aceptar conexiones de clientes
            Socket socket = serverSocket.accept();
            System.out.println("Cliente conectado: " + socket.getInetAddress());

            // Crear flujo de entrada para leer datos del cliente
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
            
            String mensajeCliente;
            String respuestaServidor;
            salida.println("Hola cliente");           
            Scanner sc = new Scanner(System.in);
            
            do{
                mensajeCliente = entrada.readLine();
                System.out.println("Mensaje del cliente: " + mensajeCliente);  
                // Responder al cliente
                System.out.println("Introduce tu respuesta:");
                respuestaServidor=sc.nextLine();
                salida.println(respuestaServidor);
            }while(!mensajeCliente.equals("."));
            
            System.out.println("Fin de la comunicacion");
            
            // Cerrar conexión
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
