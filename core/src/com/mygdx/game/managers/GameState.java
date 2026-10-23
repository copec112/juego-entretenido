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

    // Unica instancia (se crea la primera vez que se pide)
    private static GameState instance;

    private int puntaje;
    private int vidas;
    private int nivel;

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
    }

    /** Game over: vuelve a vidas y nivel iniciales (el puntaje se mantiene). */
    public void reiniciarVidasYNivel() {
        vidas = VIDAS_INICIALES;
        nivel = 1;
    }

    public int getPuntaje() { return puntaje; }
    public int getVidas() { return vidas; }
    public int getNivel() { return nivel; }

    public void sumarPuntos(int puntos) { puntaje += puntos; }
    public void perderVida() { vidas--; }
    public void subirNivel() { nivel++; }
}
