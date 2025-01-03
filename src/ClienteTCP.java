
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClienteTCP {

    public static void main(String[] args) {
        String host = "localhost"; // Dirección IP o nombre del servidor
        int puerto = 12345;        // Puerto del servidor
        try (Socket socket = new Socket(host, puerto)) {
            System.out.println("Conectado al servidor");

            // Crear flujo de salida para enviar datos al servidor
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String mensajeServidor;
            String respuestaCliente;
            Scanner sc = new Scanner(System.in);

            do {
                mensajeServidor = entrada.readLine();
                System.out.println("Mensaje del servidor: " + mensajeServidor);
                // Responder al cliente
                System.out.println("Introduce tu respuesta:");
                respuestaCliente = sc.nextLine();
                salida.println(respuestaCliente);
            } while (!mensajeServidor.equals("."));

            System.out.println("Fin de la comunicacion");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
