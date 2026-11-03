
import java.io.*;
import java.net.*;

public class EchoClient {
    public static void main(String[] args) {
        int puerto = 8080;

        try (
            Socket socket = new Socket("localhost", puerto);
            BufferedReader teclado = new BufferedReader(
                new InputStreamReader(System.in));
            BufferedReader entrada = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            PrintWriter salida = new PrintWriter(
                socket.getOutputStream(), true)
        ) {
            System.out.println("Conectado al servidor.");
            System.out.println("Escriba un mensaje (CHAO para salir):");

            String mensaje;

            while ((mensaje = teclado.readLine()) != null) {
                salida.println(mensaje);

                if ("CHAO".equalsIgnoreCase(mensaje)) {
                    break;
                }

                String respuesta = entrada.readLine();

                if (respuesta == null) {
                    System.out.println("El servidor se desconectó.");
                    break;
                }

                System.out.println("Servidor: " + respuesta);
            }

        } catch (IOException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
    }
}
