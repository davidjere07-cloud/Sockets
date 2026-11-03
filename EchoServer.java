
import java.io.*;
import java.net.*;

public class EchoServer {

    public static void main(String[] args) {

        int puerto = 8080;

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {

            System.out.println("Servidor iniciado en puerto " + puerto);
            System.out.println("Esperando conexión de un cliente...");

            Socket clientSocket = serverSocket.accept();

            System.out.println("Cliente conectado.");

            BufferedReader entrada = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream())
            );

            PrintWriter salida = new PrintWriter(
                clientSocket.getOutputStream(), true
            );

            String mensaje;

            while ((mensaje = entrada.readLine()) != null) {

                if ("CHAO".equalsIgnoreCase(mensaje)) {
                    System.out.println("Cliente desconectado.");
                    break;
                }

                System.out.println("Mensaje recibido: " + mensaje);

                salida.println("ECO: " + mensaje);
            }

            clientSocket.close();

        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }
}
