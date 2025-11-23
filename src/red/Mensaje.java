package red;

import java.io.Serializable;
import java.util.Map;

public class Mensaje implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tipo;
    private String remitente;
    private Map<String, Object> datos;

    public Mensaje(String tipo, String remitente, Map<String, Object> datos) {
        this.tipo = tipo;
        this.remitente = remitente;
        this.datos = datos;
    }

    public String getTipo() {
        return tipo;
    }

    public String getRemitente() {
        return remitente;
    }

    public Map<String, Object> getDatos() {
        return datos;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public void setDatos(Map<String, Object> datos) {
        this.datos = datos;
    }
}
