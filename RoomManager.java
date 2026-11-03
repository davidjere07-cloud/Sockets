
import java.util.concurrent.*;

public class RoomManager {

    private final ConcurrentHashMap<String, CopyOnWriteArrayList<ClientHandler>> salas =
        new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, ClientHandler> usuarios =
        new ConcurrentHashMap<>();

    // Registrar un usuario
    public boolean registrarUsuario(String nombre, ClientHandler cliente) {
        return usuarios.putIfAbsent(nombre, cliente) == null;
    }

    // Unir un usuario a una sala
    public void unirseASala(String nombreSala, ClientHandler cliente) {

        salirDeSala(cliente);

        salas.computeIfAbsent(
            nombreSala,
            k -> new CopyOnWriteArrayList<>()
        ).add(cliente);

        cliente.setSalaActual(nombreSala);
    }

    // Enviar mensajes a todos los integrantes
    public void broadcast(String nombreSala, String mensaje,
                          ClientHandler emisor) {

        CopyOnWriteArrayList<ClientHandler> miembros =
            salas.get(nombreSala);

        if (miembros != null) {

            for (ClientHandler miembro : miembros) {

                if (miembro != emisor) {
                    miembro.enviarMensajeDirecto(mensaje);
                }
            }
        }
    }

    // Retirar un usuario de su sala
    public void salirDeSala(ClientHandler cliente) {

        String sala = cliente.getSalaActual();

        if (sala != null) {

            CopyOnWriteArrayList<ClientHandler> miembros =
                salas.get(sala);

            if (miembros != null) {
                miembros.remove(cliente);

                if (miembros.isEmpty()) {
                    salas.remove(sala, miembros);
                }
            }

            cliente.setSalaActual(null);
        }
    }

    // Desconectar un usuario y liberar recursos
    public void desconectar(ClientHandler cliente) {

        salirDeSala(cliente);

        String nombre = cliente.getNombreUsuario();

        if (nombre != null) {
            usuarios.remove(nombre, cliente);
        }
    }

    // Avisar a los usuarios antes del apagado
    public void apagarServidor() {

        for (ClientHandler cliente : usuarios.values()) {
            cliente.enviarMensajeDirecto(
                "SERVIDOR: El servidor se está apagando."
            );
        }

        for (ClientHandler cliente : usuarios.values()) {
            cliente.cerrarConexion();
        }
    }
}
