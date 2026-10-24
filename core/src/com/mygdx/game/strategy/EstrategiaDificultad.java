package com.mygdx.game.strategy;

/**
 * Patron Strategy: define "como se vuelve mas dificil el juego" segun el puntaje.
 *
 * Cada implementacion es una estrategia intercambiable (facil, normal,
 * dificil). MyGdxGame (el contexto) solo conoce esta interfaz, asi que se
 * puede cambiar de dificultad mientras se juega sin tocar su codigo.
 */
public interface EstrategiaDificultad {

    /** Nombre del modo, para mostrarlo en pantalla. */
    String getNombre();

    /** Cuantas pelotas deben estar en juego con este puntaje. */
    int pelotasPara(int puntaje);

    /** Cuanta velocidad extra (pixeles por fotograma) tienen las pelotas con este puntaje. */
    int velocidadExtraPara(int puntaje);
}
