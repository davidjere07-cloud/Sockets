

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {

    private Socket socket;
    private RoomManager gestor;

    private String nombreUsuario;
    private String salaActual;

    private PrintWriter salida;

    public ClientHandler(Socket socket, RoomManager gestor) {
        this.socket = socket;
        this.gestor = gestor;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getSalaActual() {
        return salaActual;
    }

    public void setSalaActual(String sala) {
        this.salaActual = sala;
    }

    public synchronized void enviarMensajeDirecto(String mensaje) {
        if (salida != null) {
            salida.println(mensaje);
        }
    }

    @Override
    public void run() {

        try {
            socket.setSoTimeout(30000);
            socket.setKeepAlive(true);

            BufferedReader entrada = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
            );

            salida = new PrintWriter(
                socket.getOutputStream(), true
            );

            String mensaje;

            while ((mensaje = entrada.readLine()) != null) {

                try {
                    Command comando = ProtocolParser.parsear(mensaje);

                    String tipo = comando.getTipo();
                    String argumento = comando.getArgumento();

                    switch (tipo) {

                        case "CONNECT":

                            if (nombreUsuario != null) {
                                salida.println("401 ERROR: Ya estás conectado.");
                                break;
                            }

                            if (!gestor.registrarUsuario(argumento, this)) {
                                salida.println("401 ERROR: Usuario existente.");
                                break;
                            }

                            nombreUsuario = argumento;
                            salida.println("200 OK: Usuario conectado.");
                            break;

                        case "JOIN":

                            if (nombreUsuario == null) {
                                salida.println("401 ERROR: Primero debes conectarte.");
                                break;
                            }

                            gestor.unirseASala(argumento, this);
                            salida.println("JOINED " + argumento);
                            break;

                        case "MSG":

                            if (nombreUsuario == null || salaActual == null) {
                                salida.println("401 ERROR: Debes conectarte y entrar a una sala.");
                                break;
                            }

                            gestor.broadcast(
                                salaActual,
                                "BROADCAST [" + nombreUsuario + "]: " + argumento,
                                this
                            );

                            salida.println("200 OK: Mensaje enviado.");
                            break;

                        case "QUIT":

                            salida.println("200 OK: Sesión finalizada.");
                            return;
                    }

                } catch (IllegalArgumentException e) {
                    salida.println("ERROR: " + e.getMessage());
                }
            }

        } catch (SocketTimeoutException e) {
            System.out.println("Cliente inactivo. Timeout alcanzado.");

        } catch (SocketException e) {
            System.out.println("Conexión interrumpida.");

        } catch (IOException e) {
            System.out.println("Error de comunicación: " + e.getMessage());

        } finally {
            gestor.desconectar(this);

            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error al cerrar el socket.");
            }
        }
    }

    public void cerrarConexion() {
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Error al cerrar el socket.");
        }
    }
}
