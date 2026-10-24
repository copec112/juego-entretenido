package com.mygdx.game.managers;

/**
 * Singleton que guarda el estado global de la partida:
 * puntaje, vidas y nivel.
 *
 * Asi cualquier clase del juego puede consultar o modificar el estado
 * con GameState.getInstance(), sin pasarlo de objeto en objeto y
 * garantizando que existe una unica copia.
 */
public class GameState {
    private static final int VIDAS_INICIALES = 3;
    private static final int MAX_VIDAS = 5;
    public static final int META = 2048; // puntaje que da la recompensa

    // Unica instancia (se crea la primera vez que se pide)
    private static GameState instance;

    private int puntaje;
    private int vidas;
    private int nivel;
    private int mejorPuntaje; // record de esta sesion (no se borra al reiniciar)
    private boolean metaAlcanzada; // true cuando la partida actual ya llego a la META

    // Constructor privado: nadie mas puede hacer "new GameState()"
    private GameState() {
        reiniciar();
    }

    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    /** Deja todo como al comenzar el juego. */
    public void reiniciar() {
        puntaje = 0;
        vidas = VIDAS_INICIALES;
        nivel = 1;
        metaAlcanzada = false;
    }

    public int getPuntaje() { return puntaje; }
    public int getVidas() { return vidas; }
    public int getNivel() { return nivel; }
    public int getMejorPuntaje() { return mejorPuntaje; }

    public void sumarPuntos(int puntos) { puntaje += puntos; }
    public void perderVida() { vidas--; }
    public void subirNivel() { nivel++; }

    /** Vida extra de recompensa (hasta un maximo de 5). */
    public void ganarVida() {
        if (vidas < MAX_VIDAS) {
            vidas++;
        }
    }

    public boolean isMetaAlcanzada() { return metaAlcanzada; }

    /** Devuelve true UNA sola vez por partida: la primera vez que el puntaje llega a la META. */
    public boolean verificarMeta() {
        if (!metaAlcanzada && puntaje >= META) {
            metaAlcanzada = true;
            return true;
        }
        return false;
    }

    /** Al terminar la partida guarda el mejor puntaje. Devuelve true si se batio el record. */
    public boolean terminarPartida() {
        boolean record = puntaje > mejorPuntaje;
        if (record) {
            mejorPuntaje = puntaje;
        }
        return record;
    }
}
