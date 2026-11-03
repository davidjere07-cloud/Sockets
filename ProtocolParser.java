
public class ProtocolParser {

    public static Command parsear(String mensaje) {

        if (mensaje == null || mensaje.trim().isEmpty()) {
            throw new IllegalArgumentException("El comando está vacío.");
        }

        String[] partes = mensaje.trim().split("\\s+", 2);

        String tipo = partes[0].toUpperCase();

        String argumento = partes.length > 1 ? partes[1].trim() : "";

        switch (tipo) {

            case "CONNECT":
            case "JOIN":
            case "MSG":

                if (argumento.isEmpty()) {
                    throw new IllegalArgumentException(
                        "El comando " + tipo + " necesita un argumento.");
                }

                break;

            case "QUIT":
                if (!argumento.isEmpty()) {
                    throw new IllegalArgumentException(
                        "El comando QUIT no necesita argumentos.");
                }
                break;

            default:
                throw new IllegalArgumentException(
                    "Comando desconocido: " + tipo);
        }

        return new Command(tipo, argumento);
    }
}
