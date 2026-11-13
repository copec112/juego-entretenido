package com.mygdx.game.strategy;

/**
 * Lo que una estrategia de dificultad puede pedirle al juego.
 *
 * Las estrategias dependen de esta interfaz y no de la pantalla del juego
 * (inversion de dependencias): asi se pueden probar y reutilizar solas.
 */
public interface ControlPelotas {
    /** Cuantas pelotas hay en juego ahora. */
    int cantidadPelotas();

    /** Agrega una pelota nueva, quieta sobre la paleta (espera el ESPACIO). */
    void agregarPelotaQuieta();

    /** Fija la velocidad extra (en pixeles por fotograma) de todas las pelotas, presentes y futuras. */
    void fijarVelocidadExtra(int extra);
}
