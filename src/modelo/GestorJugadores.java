package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GestorJugadores {

    private List<Jugador> jugadores = new ArrayList<>();

    public synchronized void agregarJugador(Jugador j) {
        if (j == null) return;

        boolean existe = jugadores.stream().anyMatch(x -> x.getNombre().equalsIgnoreCase(j.getNombre()));
        if (!existe) {
            jugadores.add(j);
            System.out.println("[Gestor] Agregado: " + j.getNombre());
        } else {
            System.out.println("[Gestor] Ya existe jugador: " + j.getNombre());
        }
    }

    public synchronized void removerJugador(Jugador j) {
        if (j == null) return;
        jugadores.removeIf(x -> x.getNombre().equals(j.getNombre()));
        System.out.println("[Gestor] Removido: " + j.getNombre());
    }

    public synchronized void removerPorNombre(String nombre) {
        if (nombre == null) return;
        jugadores.removeIf(x -> x.getNombre().equals(nombre));
        System.out.println("[Gestor] Removido por nombre: " + nombre);
    }

    public synchronized List<Jugador> obtenerJugadores() {
        // devolvemos copia para evitar modificación externa
        return new ArrayList<>(jugadores);
    }

    public synchronized List<String> obtenerNombresJugadores() {
        return jugadores.stream().map(Jugador::getNombre).toList();
    }

    public synchronized int cantidadJugadores() {
        return jugadores.size();
    }

    public synchronized boolean esperarConfirmaciones(long timeoutMillis) {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < deadline) {
            boolean todos = jugadores.stream().allMatch(Jugador::haConfirmado);
            if (todos) return true;
            try { Thread.sleep(150); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
        }
        return false;
    }


    public synchronized void confirmarJugador(String nombre) {
        Optional<Jugador> opt = jugadores.stream().filter(j -> j.getNombre().equals(nombre)).findFirst();
        opt.ifPresent(j -> {
            j.setConfirmado(true);
            System.out.println("[Gestor] Confirmado: " + nombre);
        });
    }


    public synchronized void asignarOrdenTurnos(List<Jugador> orden) {
        this.jugadores = new ArrayList<>(orden);
    }

    public synchronized void resetearConfirmaciones() {
        jugadores.forEach(j -> j.setConfirmado(false));
    }


    public synchronized Jugador buscarPorNombre(String nombre) {
        return jugadores.stream().filter(j -> j.getNombre().equals(nombre)).findFirst().orElse(null);
    }
}
