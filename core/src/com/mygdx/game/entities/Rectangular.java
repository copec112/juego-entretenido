package com.mygdx.game.entities;

/**
 * Cualquier cosa que ocupa un rectangulo en pantalla.
 *
 * Es la abstraccion minima que necesita quien quiere saber "donde esta" un
 * objeto (por ejemplo, VidaExtra para detectar a la paleta): asi depende de
 * esta interfaz y no de una clase concreta como Paddle.
 *
 * (x, y) siempre es la esquina inferior izquierda del rectangulo.
 */
public interface Rectangular {
    int getX();
    int getY();
    int getWidth();
    int getHeight();
}
