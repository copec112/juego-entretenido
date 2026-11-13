package com.mygdx.game.strategy;

/**
 * Patron Strategy: define como es la dificultad en una etapa del juego.
 *
 * Cada implementacion es una estrategia (Basica, Media, Avanzada, Experta)
 * que sabe APLICARSE al juego: asegura cuantas pelotas hay y que tan rapidas
 * son. El juego solo conoce esta interfaz: a medida que sube el puntaje
 * cambia de estrategia sola, sin tocar el resto del codigo.
 */
public interface EstrategiaDificultad {
    /** Nombre de la etapa. */
    String getNombre();

    /** Puntaje desde el cual esta estrategia pasa a usarse. */
    int getPuntajeMinimo();

    /** Aplica la dificultad de esta etapa al juego (pelotas y velocidad). */
    void aplicar(ControlPelotas juego);
}
