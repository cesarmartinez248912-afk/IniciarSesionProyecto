package modelo;

import java.util.ArrayList;
import java.util.List;

public class GestorTurnos {

    private List<Jugador> jugadores;
    private int turnoActual;
    private boolean partidaIniciada;

    public GestorTurnos() {
        this.jugadores = new ArrayList<>();
        this.turnoActual = 0;
        this.partidaIniciada = false;
    }

    public void inicializar(List<Jugador> ordenJugadores) {
        this.jugadores = new ArrayList<>(ordenJugadores);
        this.turnoActual = 0;
        this.partidaIniciada = true;
    }

    public Jugador obtenerJugadorActual() {
        if (!partidaIniciada || jugadores.isEmpty()) return null;
        return jugadores.get(turnoActual);
    }

    public void avanzarTurno() {
        if (!partidaIniciada) return;
        turnoActual = (turnoActual + 1) % jugadores.size();
    }

    public boolean esTurnoDe(String nombreJugador) {
        Jugador actual = obtenerJugadorActual();
        return actual != null && actual.getNombre().equals(nombreJugador);
    }

    public boolean isPartidaIniciada() {
        return partidaIniciada;
    }

    public void finalizarPartida() {
        this.partidaIniciada = false;
    }
}