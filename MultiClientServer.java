

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class MultiClientServer {

    private static final int PUERTO = 8080;

    private static ExecutorService pool =
        Executors.newFixedThreadPool(10);

    private static RoomManager gestor = new RoomManager();

    public static void main(String[] args) {

        try (ServerSocket server = new ServerSocket(PUERTO)) {

            System.out.println("Servidor JSMS iniciado en puerto " + PUERTO);

            // Apagado ordenado del servidor
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {

                System.out.println("Apagando servidor...");

                gestor.apagarServidor();

                pool.shutdown();

                System.out.println("Servidor apagado correctamente.");
            }));

            while (true) {

                Socket socket = server.accept();

                System.out.println("Nuevo cliente conectado.");

                // Enviar el socket y el administrador de salas
                pool.execute(new ClientHandler(socket, gestor));
            }

        } catch (IOException e) {

            System.out.println("Error en el servidor: " + e.getMessage());

        } finally {
            pool.shutdown();
        }
    }
}
