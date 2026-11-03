
import java.io.*;
import java.net.*;

public class JSMSClient {

    public static void main(String[] args) {

        try (
            Socket socket = new Socket("localhost", 8080);
            BufferedReader teclado = new BufferedReader(
                new InputStreamReader(System.in));
            BufferedReader entrada = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            PrintWriter salida = new PrintWriter(
                socket.getOutputStream(), true)
        ) {

            System.out.println("Conectado al servidor JSMS.");

            Thread receptor = new Thread(() -> {

                try {
                    String respuesta;

                    while ((respuesta = entrada.readLine()) != null) {
                        System.out.println(respuesta);
                    }

                    System.out.println("Servidor desconectado.");

                } catch (IOException e) {
                    System.out.println("Error al recibir mensajes.");
                }
            });

            receptor.setDaemon(true);
            receptor.start();

            String mensaje;

            while ((mensaje = teclado.readLine()) != null) {

                salida.println(mensaje);

                if (mensaje.trim().equalsIgnoreCase("QUIT")) {
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
    }
}
