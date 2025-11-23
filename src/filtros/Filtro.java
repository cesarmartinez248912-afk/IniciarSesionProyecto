package filtros;

import red.Mensaje;

public interface Filtro {
    Mensaje procesar(Mensaje m);
}
