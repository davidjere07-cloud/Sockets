
public class Command {

    private String tipo;
    private String argumento;

    public Command(String tipo, String argumento) {
        this.tipo = tipo;
        this.argumento = argumento;
    }

    public String getTipo() {
        return tipo;
    }

    public String getArgumento() {
        return argumento;
    }
}
