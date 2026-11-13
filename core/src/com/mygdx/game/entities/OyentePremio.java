package com.mygdx.game.entities;

/**
 * Oyente (patron Observer) de la grilla de bloques: se avisa cuando una
 * fusion llega a BlockGrid.VALOR_PREMIO y debe caer un premio.
 *
 * Asi la grilla no tiene que devolver listas con sus datos internos: avisa
 * el evento y quien escucha decide que hacer (en el juego, crear el corazon).
 */
public interface OyentePremio {
    /** Un bloque llego al valor del premio; (x, y) es su esquina inferior izquierda. */
    void alGenerarPremio(int x, int y);
}
